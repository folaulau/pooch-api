package com.pooch.api.entity.groomer;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.GroomerUpdateDTO;

public interface GroomerService {

    AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);

    GroomerDTO updateProfile(GroomerUpdateDTO petSitterUpdateDTO);
}
