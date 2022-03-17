package com.pooch.api.entity.pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetValidatorServiceImp implements PetValidatorService {

    @Autowired
    private PetDAO petDAO;
}
