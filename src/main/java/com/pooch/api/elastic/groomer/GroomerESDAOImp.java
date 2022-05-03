package com.pooch.api.elastic.groomer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pooch.api.dto.CustomPage;
import com.pooch.api.dto.CustomSort;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.GroomerSearchParamsDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.groomer.GroomerSearchSorting;
import com.pooch.api.entity.groomer.GroomerStatus;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceRepository;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class GroomerESDAOImp implements GroomerESDAO {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient   restHighLevelClient;

    @Autowired
    private GroomerESRepository   groomerESRepository;

    @Autowired
    private CareServiceRepository careServiceRepository;

    @Autowired
    private EntityDTOMapper       entityDTOMapper;

    @PostConstruct
    public void setup() {
        // try {
        // restHighLevelClient.indices().delete(new DeleteIndexRequest("groomer"), RequestOptions.DEFAULT);
        // } catch (IOException e) {
        // log.warn("IOException with deleting groomer index. msg={}", e.getLocalizedMessage());
        // }
    }

    @Override
    public void save(GroomerES groomerES) {
        log.info("groomerES={}", ObjectUtils.toJson(groomerES));
        
        groomerES.populateGeoPoints();

        groomerES = groomerESRepository.save(groomerES);

        log.info("saved groomerES={}", ObjectUtils.toJson(groomerES));
    }

    @Override
    public CustomPage<GroomerES> search(GroomerSearchParamsDTO filters) {
        SearchRequest searchRequest = new SearchRequest("groomer");
        searchRequest.allowPartialSearchResults(true);
        searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen());

        filters = populateSearchFilterAndSortDefaultValues(filters);

        int pageNumber = filters.getPageNumber();
        int pageSize = filters.getPageSize();
        int radius = filters.getDistance();
        double latitude = filters.getLatitude();
        double longitude = filters.getLongitude();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from((int) (pageNumber * pageSize));
        searchSourceBuilder.size(Math.toIntExact(pageSize));
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        /**
         * fetch only a few fields
         */
        searchSourceBuilder.fetchSource(new String[]{"*"}, new String[]{});
        // searchSourceBuilder.fetchSource(new FetchSourceContext(true, new String[]{"*"}, new String[]{"cards"}));
        /**
         * Filter
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // termQuery is case sensitive
        boolQuery.filter(QueryBuilders.termQuery("status.keyword", GroomerStatus.ACTIVE.name()));

        boolQuery.filter(QueryBuilders.nestedQuery("addresses",
                QueryBuilders.geoDistanceQuery("addresses.location").point(latitude, longitude).distance(radius, DistanceUnit.MILES).geoDistance(GeoDistance.ARC), ScoreMode.None));

        /**
         * CareServices
         */
        Set<String> careServiceNames = filters.getCareServiceNames();

        Set<String> poochSizes = filters.getPoochSizes();

        Double minPrice = filters.getMinPrice();
        Double maxPrice = filters.getMaxPrice();

        searchSourceBuilder.query(boolQuery);

        String searchPhrase = filters.getSearchPhrase();

        /**
         * Sorting
         */

        filters.getSorts().stream().forEach(sorting -> {
            // distance
            if (sorting.getProperty().equalsIgnoreCase(GroomerSearchSorting.distance.name())) {
                addGeoLocationSorting(searchSourceBuilder, latitude, longitude, radius);

                // rating
            } else if (sorting.getProperty().equalsIgnoreCase(GroomerSearchSorting.rating.name())) {
                addRatingSorting(searchSourceBuilder);
            }
        });

        searchRequest.source(searchSourceBuilder);
        searchRequest.preference("nested-address");

        if (searchSourceBuilder.sorts() != null && searchSourceBuilder.sorts().size() > 0) {
            log.info("\n{\n\"query\":{}, \"sort\":{}\n}", searchSourceBuilder.query().toString(), searchSourceBuilder.sorts().toString());
        } else {
            log.info("\n{\n\"query\":{}\n}", searchSourceBuilder.query().toString());
        }

        SearchResponse searchResponse = null;
        List<GroomerES> groomers = Arrays.asList();
        long totalHits = 0;

        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            totalHits = searchResponse.getHits().getTotalHits().value;
            log.info("isTimedOut={}, totalShards={}, totalHits={}", searchResponse.isTimedOut(), searchResponse.getTotalShards(), totalHits);

            groomers = getResponseResult(searchResponse.getHits(), new GeoPoint(latitude, longitude), radius);

            log.info("groomers={}", ObjectUtils.toJson(groomers));

        } catch (IOException e) {
            log.warn("IOException, msg={}", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return new CustomPage<>(new PageImpl<>(groomers, PageRequest.of(pageNumber, pageSize), totalHits));
    }

    private void addGeoLocationSorting(SearchSourceBuilder searchSourceBuilder, Double lat, Double lon, int radius) {
        searchSourceBuilder.sort(new GeoDistanceSortBuilder("addresses.location", lat, lon).order(SortOrder.ASC)
                .setNestedSort(new NestedSortBuilder("addresses").setFilter(QueryBuilders.geoDistanceQuery("addresses.location").point(lat, lon).distance(radius, DistanceUnit.MILES))));
    }

    private void addRatingSorting(SearchSourceBuilder searchSourceBuilder) {
        searchSourceBuilder.sort(new FieldSortBuilder("rating").order(SortOrder.DESC));
    }

    private GroomerSearchParamsDTO populateSearchFilterAndSortDefaultValues(GroomerSearchParamsDTO filters) {

        if (filters.getPageNumber() == null || filters.getPageNumber() < 0) {
            filters.setPageNumber(0);
        }

        if (filters.getPageSize() == null || filters.getPageSize() <= 0) {
            filters.setPageSize(25);
        }

        if (filters.getDistance() == null || filters.getDistance() <= 0) {
            filters.setDistance(5);
        }

        /**
         * https://www.zillow.com/homedetails/2401-Ocean-Front-Walk-Venice-CA-90291/20443655_zpid/<br>
         * lat: 33.982635, lon: -118.469807
         */

        Double latitude = filters.getLatitude();
        Double longitude = filters.getLongitude();

        /**
         * for MVP, users might not allow their geo location to be had, use venice beach
         */
        if (latitude == null || longitude == null) {
            filters.setLatitude(33.982635);
            filters.setLongitude(-118.469807);
        }

        if (filters.getSorts() == null || filters.getSorts().size() == 0) {
            filters.setSorts(Arrays.asList(new CustomSort(GroomerSearchSorting.distance.name())));
        }

        return filters;
    }

    private List<GroomerES> getResponseResult(SearchHits searchHits, GeoPoint searchLocation, int radius) {

        Iterator<SearchHit> it = searchHits.iterator();

        List<GroomerES> searchResults = new ArrayList<>();

        while (it.hasNext()) {
            SearchHit searchHit = it.next();
            log.info("sourceAsString={}", searchHit.getSourceAsString());
            try {

                GroomerES obj = ObjectUtils.getObjectMapper().readValue(searchHit.getSourceAsString(), new TypeReference<GroomerES>() {});
                // log.info("obj={}", ObjectUtils.toJson(obj));

                obj.filterOutUnreachableLocations(searchLocation, radius);

                searchResults.add(obj);
            } catch (IOException e) {
                log.warn("IOException, msg={}", e.getLocalizedMessage());
            }
        }

        return searchResults;

    }

}
