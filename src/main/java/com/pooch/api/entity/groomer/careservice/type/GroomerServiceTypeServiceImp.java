package com.pooch.api.entity.groomer.careservice.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.GroomerServiceCategoryDTO;

import lombok.extern.slf4j.Slf4j;

@DependsOn({"careServiceTypeDataLoader"})// load date first
@Service
@Slf4j
public class GroomerServiceTypeServiceImp implements GroomerServiceTypeService {

    @Autowired
    private GroomerServiceCategoryRepository                groomerServiceCategoryRepository;

    @Autowired
    private EntityDTOMapper                                 entityDTOMapper;

    public final static Map<String, GroomerServiceCategory> dict = new HashMap<>();

    @PostConstruct
    public void init() {
        List<GroomerServiceCategory> categories = groomerServiceCategoryRepository.findAll();

        categories.stream().forEach(serviceCategory -> {
            dict.put(serviceCategory.getName(), serviceCategory);
        });
    }

    @Override
    public List<GroomerServiceCategoryDTO> getAllGroomerServiceTypes() {
        if (!dict.isEmpty()) {
            return entityDTOMapper.mapGroomerServiceCategorysToGroomerServiceCategoryDTOs(dict.values().stream().collect(Collectors.toList()));
        }

        List<GroomerServiceCategory> categories = groomerServiceCategoryRepository.findAll();

        categories.stream().forEach(serviceCategory -> {
            dict.put(serviceCategory.getName(), serviceCategory);
        });

        return entityDTOMapper.mapGroomerServiceCategorysToGroomerServiceCategoryDTOs(categories);
    }

    @Override
    public GroomerServiceCategory getByName(String name) {
        if (dict.containsKey(name)) {
            return dict.get(name);
        }
        GroomerServiceCategory groomerServiceCategory = groomerServiceCategoryRepository.findByName(name).orElse(null);
        if (groomerServiceCategory != null) {
            dict.put(groomerServiceCategory.getName(), groomerServiceCategory);
        }
        return groomerServiceCategory;
    }

}
