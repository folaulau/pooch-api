package com.pooch.api.entity.groomer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.GroomerUpdateDTO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerValidatorServiceImp implements GroomerValidatorService {

    @Autowired
    private GroomerDAO groomerDAO;

    @Override
    public Groomer validateUpdateProfile(GroomerUpdateDTO petSitterUpdateDTO) {
        String uuid = petSitterUpdateDTO.getUuid();

        if (uuid == null || !uuid.isEmpty()) {
            throw new ApiException(ApiError.FAILURE, "uuid is empty. uuid=" + uuid);
        }

        Optional<Groomer> optPetSitter = groomerDAO.getByUuid(uuid);

        if (!optPetSitter.isPresent()) {
            throw new ApiException(ApiError.FAILURE, "PetSitter not found for uuid=" + uuid);
        }

        Groomer petSitter = optPetSitter.get();

        return petSitter;
    }

}
