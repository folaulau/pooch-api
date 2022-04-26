package com.pooch.api.entity.groomer.careservice.type;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerServiceTypeServiceImp implements GroomerServiceTypeService {

    @Autowired
    private GroomerServiceCategoryRepository groomerServiceCategoryRepository;

    @Override
    public List<GroomerServiceCategory> getAllGroomerServiceTypes() {
        return groomerServiceCategoryRepository.findAll();
    }

}
