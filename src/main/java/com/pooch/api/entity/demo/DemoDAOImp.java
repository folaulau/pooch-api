package com.pooch.api.entity.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DemoDAOImp implements DemoDAO {

    @Autowired
    private DemoRepository demoRepository;

    @Override
    public Demo save(Demo demo) {
        return demoRepository.saveAndFlush(demo);
    }

}
