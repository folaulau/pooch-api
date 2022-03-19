package com.pooch.api.entity.petcare;

import com.pooch.api.dto.PetCareCreateDTO;

public interface PetCareValidatorService {

    void validateBook(PetCareCreateDTO petCareCreateDTO);

}
