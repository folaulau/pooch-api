package com.pooch.api.entity.pooch.care;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PocchCareDAOImp implements PoochCareDAO {

    @Autowired
    private PoochCareRepository petCareRepository;
}
