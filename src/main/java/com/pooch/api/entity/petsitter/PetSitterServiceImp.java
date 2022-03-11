package com.pooch.api.entity.petsitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetSitterServiceImp implements PetSitterService {

    @Autowired
    private PetSitterDAO petSitterDAO;
}
