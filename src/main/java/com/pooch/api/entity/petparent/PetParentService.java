package com.pooch.api.entity.petparent;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.PetCareBookingDTO;

public interface PetParentService {

    AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);

    void bookPetCare(PetCareBookingDTO petCareBookingDTO);
}
