package com.pooch.api.elastic.groomer;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GroomerESDAOImp implements GroomerESDAO {

  @Autowired private RestHighLevelClient restHighLevelClient;

  @Autowired private GroomerESRepository groomerESRepository;


  @Async
  @Override
  public void save(GroomerES groomerES) {
    log.info("groomerES={}", ObjectUtils.toJson(groomerES));

    groomerES.populateGeoPoints();
    groomerES = groomerESRepository.save(groomerES);

    log.info("saved groomerES={}", ObjectUtils.toJson(groomerES));
  }
}
