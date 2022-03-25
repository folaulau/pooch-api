package com.pooch.api.entity.parent;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;

public interface ParentService {

    AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);
}
