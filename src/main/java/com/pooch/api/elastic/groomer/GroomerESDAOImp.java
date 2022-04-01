package com.pooch.api.elastic.groomer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pooch.api.dto.CustomPage;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        // remove when ready
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest("groomer"), RequestOptions.DEFAULT);
        } catch (IOException e) {
        }
    }

    @Async
    @Override
    public void save(GroomerES groomerES) {
        log.info("groomerES={}", ObjectUtils.toJson(groomerES));

        try {
            Set<CareService> careServices = careServiceRepository.findByGroomerId(groomerES.getId());
            groomerES.setCareServices(entityDTOMapper.mapCareServicesToCareServiceESs(careServices));
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
        }

        groomerES.populateGeoPoints();

        groomerES = groomerESRepository.save(groomerES);

        log.info("saved groomerES={}", ObjectUtils.toJson(groomerES));
    }

    @Override
    public CustomPage<GroomerES> search(Long pageNumber, Long pageSize, Long lat, Long lon, String searchPhrase) {
        SearchRequest searchRequest = new SearchRequest("groomer");
        searchRequest.allowPartialSearchResults(true);
        searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen());

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from((int) (pageNumber * pageSize));
        searchSourceBuilder.size(Math.toIntExact(pageSize));
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        /**
         * fetch only a few fields
         */
        searchSourceBuilder.fetchSource(new String[]{"*"}, new String[]{"cards"});
        // searchSourceBuilder.fetchSource(new FetchSourceContext(true, new String[]{"*"}, new String[]{"cards"}));
        /**
         * Query with bool
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        /**
         * Lehi skate park: 40.414897, -111.881186<br>
         * get locations/addresses close to skate park(from a radius).<br>
         * The geo_distance filter can work with multiple locations / points per document. Once a single location /
         * point matches the filter, the document will be included in the filter.<br>
         */
        boolQuery.filter(QueryBuilders.geoDistanceQuery("addresses.location").point(40.414897, -111.881186).distance(1, DistanceUnit.MILES).geoDistance(GeoDistance.ARC));

        searchSourceBuilder.query(QueryBuilders.nestedQuery("addresses", boolQuery, ScoreMode.None));

        searchRequest.source(searchSourceBuilder);

        searchRequest.preference("nested-address");

        if (searchSourceBuilder.sorts() != null && searchSourceBuilder.sorts().size() > 0) {
            log.info("\n{\n\"query\":{}, \"sort\":{}\n}", searchSourceBuilder.query().toString(), searchSourceBuilder.sorts().toString());
        } else {
            log.info("\n{\n\"query\":{}\n}", searchSourceBuilder.query().toString());
        }

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            log.info("isTimedOut={}, totalShards={}, totalHits={}", searchResponse.isTimedOut(), searchResponse.getTotalShards(), searchResponse.getHits().getTotalHits().value);

            List<GroomerES> groomers = getResponseResult(searchResponse.getHits());

            log.info("groomers={}", ObjectUtils.toJson(groomers));

        } catch (IOException e) {
            log.warn("IOException, msg={}", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return null;
    }

    private List<GroomerES> getResponseResult(SearchHits searchHits) {

        Iterator<SearchHit> it = searchHits.iterator();

        List<GroomerES> searchResults = new ArrayList<>();

        while (it.hasNext()) {
            SearchHit searchHit = it.next();
            log.info("sourceAsString={}", searchHit.getSourceAsString());
            try {

                GroomerES obj = ObjectUtils.getObjectMapper().readValue(searchHit.getSourceAsString(), new TypeReference<GroomerES>() {});
                // log.info("obj={}", ObjectUtils.toJson(obj));

                searchResults.add(obj);
            } catch (IOException e) {
                log.warn("IOException, msg={}", e.getLocalizedMessage());
            }
        }

        return searchResults;

    }
}
