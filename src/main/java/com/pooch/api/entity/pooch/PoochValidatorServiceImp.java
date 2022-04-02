package com.pooch.api.entity.pooch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PoochValidatorServiceImp implements PoochValidatorService {

    @Autowired
    private PoochDAO poochDAO;
}
