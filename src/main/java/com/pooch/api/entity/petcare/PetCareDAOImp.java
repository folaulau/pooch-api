package com.pooch.api.entity.petcare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PetCareDAOImp implements PetCareDAO {

    @Autowired
    private PetCareRepository petCareRepository;
}
