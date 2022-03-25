package com.pooch.api.security;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.security.jwt.JwtPayload;

public interface AuthenticationService {

    AuthenticationResponseDTO authenticate(PetParent petParent);

    AuthenticationResponseDTO authenticate(Groomer petSitter);

    boolean authorizeRequest(String token, JwtPayload jwtPayload);

    boolean logOutUser(String token);
}
