package com.pooch.api.entity.groomer.careservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CareServiceDAOImp implements CareServiceDAO {

    @Autowired
    private CareServiceRepository careServiceRepository;

    @Override
    public CareService save(CareService careService) {
        return careServiceRepository.saveAndFlush(careService);
    }
}
