package com.pooch.api.elastic.groomer;

import com.pooch.api.dto.CustomPage;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceRepository;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Repository
@Slf4j
public class GroomerESDAOImp implements GroomerESDAO {

  @Autowired private RestHighLevelClient restHighLevelClient;

  @Autowired private GroomerESRepository groomerESRepository;

  @Autowired private CareServiceRepository careServiceRepository;

  @Autowired private EntityDTOMapper entityDTOMapper;

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
    return null;
  }
}
