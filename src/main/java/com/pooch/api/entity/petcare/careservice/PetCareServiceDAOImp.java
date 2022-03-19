package com.pooch.api.entity.petcare.careservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PetCareServiceDAOImp implements PetCareServiceDAO {

    @Autowired
    private PetCareServiceRepository petCareServiceRepository;

    @Override
    public PetCareService save(PetCareService petCareService) {
        return petCareServiceRepository.saveAndFlush(petCareService);
    }
}
