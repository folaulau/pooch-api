package com.pooch.api.entity.groomer;

import com.pooch.api.dto.GroomerUpdateDTO;

public interface GroomerValidatorService {

    Groomer validateUpdateProfile(GroomerUpdateDTO petSitterUpdateDTO);
}
