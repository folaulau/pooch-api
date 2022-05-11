package com.pooch.api.entity.groomer.careservice.type;

import java.util.List;

import com.pooch.api.dto.GroomerServiceCategoryDTO;

public interface GroomerServiceTypeService {

    List<GroomerServiceCategoryDTO> getAllGroomerServiceTypes();
    
    List<GroomerServiceCategory> getTopServiceTypes(Long count);
    
    GroomerServiceCategory getByName(String name);
}
