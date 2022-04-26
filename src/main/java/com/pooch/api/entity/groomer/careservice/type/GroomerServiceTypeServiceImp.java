package com.pooch.api.entity.groomer.careservice.type;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.GroomerServiceCategoryDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerServiceTypeServiceImp implements GroomerServiceTypeService {

    @Autowired
    private GroomerServiceCategoryRepository groomerServiceCategoryRepository;

    @Autowired
    private EntityDTOMapper                  entityDTOMapper;

    @Override
    public List<GroomerServiceCategoryDTO> getAllGroomerServiceTypes() {
        List<GroomerServiceCategory> categories = groomerServiceCategoryRepository.findAll();

        return entityDTOMapper.mapGroomerServiceCategorysToGroomerServiceCategoryDTOs(categories);
    }

}
