package com.pooch.api.security;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.entity.petparent.PetParent;

public interface AuthenticationService {

    AuthenticationResponseDTO authenticate(PetParent petParent);
}
