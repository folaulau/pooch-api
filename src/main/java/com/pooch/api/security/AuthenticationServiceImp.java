package com.pooch.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.security.jwt.JwtTokenService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationServiceImp implements AuthenticationService {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public AuthenticationResponseDTO authenticate(PetParent petParent) {
        String jwt = jwtTokenService.generateUserToken(petParent);

        AuthenticationResponseDTO auth = new AuthenticationResponseDTO();
        auth.setToken(jwt);
        auth.setEmail(petParent.getEmail());
        auth.setFullName(petParent.getFullName());
        auth.setUuid(petParent.getUuid());

        return auth;
    }

}
