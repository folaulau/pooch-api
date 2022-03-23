package com.pooch.api.entity.petsitter;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.PetSitterDTO;
import com.pooch.api.dto.PetSitterUpdateDTO;
import com.pooch.api.utils.ObjectUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "PetSitters", description = "Pet Sitter Operations")
@Slf4j
@RestController
@RequestMapping("/petsitters")
public class PetSitterRestController {

    @Autowired
    private PetSitterService petSitterService;

    @Operation(summary = "Authenticate", description = "sign up or sign in")
    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestHeader(name = "x-api-key", required = true) String xApiKey, @RequestBody AuthenticatorDTO authenticatorDTO) {
        log.info("authenticate={}", ObjectUtils.toJson(authenticatorDTO));

        AuthenticationResponseDTO authenticationResponseDTO = petSitterService.authenticate(authenticatorDTO);

        return new ResponseEntity<>(authenticationResponseDTO, OK);
    }

    @Operation(summary = "Update Profile", description = "update profile")
    @PutMapping(value = "/profile")
    public ResponseEntity<PetSitterDTO> update(@RequestHeader(name = "token", required = true) String token, @RequestBody PetSitterUpdateDTO petSitterUpdateDTO) {
        log.info("petSitterUpdateDTO={}", ObjectUtils.toJson(petSitterUpdateDTO));

        PetSitterDTO petSitterDTO = petSitterService.updateProfile(petSitterUpdateDTO);

        return new ResponseEntity<>(petSitterDTO, OK);
    }

}
