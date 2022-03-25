package com.pooch.api.entity.petcare;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.PetCareCreateDTO;
import com.pooch.api.dto.PetCreateDTO;
import com.pooch.api.dto.PetDTO;
import com.pooch.api.dto.PetParentUpdateDTO;
import com.pooch.api.dto.GroomerUuidDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.pet.Pet;
import com.pooch.api.entity.pet.PetDAO;
import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.entity.petparent.PetParentDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetCareValidatorServiceImp implements PetCareValidatorService {

    @Autowired
    private GroomerDAO petSitterDAO;

    @Autowired
    private PetParentDAO petParentDAO;

    @Autowired
    private PetDAO       petDAO;

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

        // String petSitterUuid = petCareCreateDTO.getPetSitterUuid();
        //
        // if (null == petSitterUuid || petSitterUuid.isEmpty()) {
        // throw new ApiException(ApiError.DEFAULT_MSG, "petSitter not found for uuid=" + petSitterUuid);
        // }
        //
        // Optional<PetSitter> optPetSitter = petSitterDAO.getByUuid(petSitterUuid);
        //
        // if (!optPetSitter.isPresent()) {
        // throw new ApiException(ApiError.DEFAULT_MSG, "petSitter not found for uuid=" + petSitterUuid);
        // }
        //
        // PetSitter petSitter = optPetSitter.get();

        /**
         * Pets
         */
        Set<PetCreateDTO> petCreateDTOs = petCareCreateDTO.getPets();
        for (PetCreateDTO petCreateDTO : petCreateDTOs) {
            String uuid = petCreateDTO.getUuid();

            if (uuid != null && !uuid.isEmpty()) {
                Optional<Pet> optPet = petDAO.getByUuid(uuid);

                if (!optPet.isPresent()) {
                    throw new ApiException(ApiError.DEFAULT_MSG, "Pet not found for uuid=" + uuid);
                } else {
                    Pet pet = optPet.get();

                    if (!petParent.getId().equals(pet.getPetParent().getId())) {
                        throw new ApiException(ApiError.DEFAULT_MSG, "Pet does not belong to petParent");
                    }
                }
            }
        }

    }

}
