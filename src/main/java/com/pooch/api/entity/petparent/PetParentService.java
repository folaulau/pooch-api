package com.pooch.api.entity.petparent;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;

public interface PetParentService {

    AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);
}
