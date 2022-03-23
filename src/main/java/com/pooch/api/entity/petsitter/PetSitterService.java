package com.pooch.api.entity.petsitter;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.PetSitterDTO;
import com.pooch.api.dto.PetSitterUpdateDTO;

public interface PetSitterService {

    AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);

    PetSitterDTO updateProfile(PetSitterUpdateDTO petSitterUpdateDTO);
}
