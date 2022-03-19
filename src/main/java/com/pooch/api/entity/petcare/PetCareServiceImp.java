package com.pooch.api.entity.petcare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PetCareCreateDTO;
import com.pooch.api.dto.PetCareDTO;
import com.pooch.api.entity.petparent.PetParentDAO;
import com.pooch.api.entity.petsitter.PetSitterDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetCareServiceImp implements PetCareService {

    @Autowired
    private PetCareDAO              petCareDAO;

    @Autowired
    private EntityDTOMapper         entityDTOMapper;

    @Autowired
    private PetSitterDAO            petSitterDAO;

    @Autowired
    private PetParentDAO            petParentDAO;

    @Autowired
    private PetCareValidatorService petCareValidatorService;

    @Override
    public PetCareDTO book(PetCareCreateDTO petCareCreateDTO) {
        petCareValidatorService.validateBook(petCareCreateDTO);

        return null;
    }

}
