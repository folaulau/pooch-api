package com.pooch.api.entity.petcare;

import com.pooch.api.dto.PetCareCreateDTO;
import com.pooch.api.dto.PetCareDTO;

public interface PetCareService {

    PetCareDTO book(PetCareCreateDTO petCareCreateDTO);
}
