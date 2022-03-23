package com.pooch.api.entity.petsitter;

import com.pooch.api.dto.PetSitterUpdateDTO;

public interface PetSitterValidatorService {

    PetSitter validateUpdateProfile(PetSitterUpdateDTO petSitterUpdateDTO);
}
