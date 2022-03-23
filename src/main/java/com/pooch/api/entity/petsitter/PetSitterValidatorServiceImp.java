package com.pooch.api.entity.petsitter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.PetSitterUpdateDTO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetSitterValidatorServiceImp implements PetSitterValidatorService {

    @Autowired
    private PetSitterDAO petSitterDAO;

    @Override
    public PetSitter validateUpdateProfile(PetSitterUpdateDTO petSitterUpdateDTO) {
        String uuid = petSitterUpdateDTO.getUuid();

        if (uuid == null || !uuid.isEmpty()) {
            throw new ApiException(ApiError.FAILURE, "uuid is empty. uuid=" + uuid);
        }

        Optional<PetSitter> optPetSitter = petSitterDAO.getByUuid(uuid);

        if (!optPetSitter.isPresent()) {
            throw new ApiException(ApiError.FAILURE, "PetSitter not found for uuid=" + uuid);
        }

        PetSitter petSitter = optPetSitter.get();

        return petSitter;
    }

}
