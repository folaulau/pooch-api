package com.pooch.api.entity.petcare;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.PetCareCreateDTO;
import com.pooch.api.dto.PetParentUpdateDTO;
import com.pooch.api.dto.PetSitterUuidDTO;
import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.entity.petparent.PetParentDAO;
import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.entity.petsitter.PetSitterDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetCareValidatorServiceImp implements PetCareValidatorService {

    @Autowired
    private PetSitterDAO petSitterDAO;

    @Autowired
    private PetParentDAO petParentDAO;

    @Override
    public void validateBook(PetCareCreateDTO petCareCreateDTO) {
        PetParentUpdateDTO petParentUpdateDTO = petCareCreateDTO.getPetParent();

        if (petParentUpdateDTO == null) {
            throw new ApiException(ApiError.DEFAULT_MSG, "petParent is required");
        }

        String petParentUuid = petParentUpdateDTO.getUuid();

        Optional<PetParent> optPetParent = petParentDAO.getByUuid(petParentUuid);

        if (!optPetParent.isPresent()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "petParent not found for uuid=" + petParentUuid);
        }

        PetParent petParent = optPetParent.get();

        String petSitterUuid = petCareCreateDTO.getPetSitterUuid();

        if (null == petSitterUuid || petSitterUuid.isEmpty()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "petSitter not found for uuid=" + petSitterUuid);
        }

        Optional<PetSitter> optPetSitter = petSitterDAO.getByUuid(petSitterUuid);

        if (!optPetSitter.isPresent()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "petSitter not found for uuid=" + petSitterUuid);
        }

        PetSitter petSitter = optPetSitter.get();

    }

}
