package com.pooch.api.entity.groomer;

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
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.GroomerUpdateDTO;
import com.pooch.api.utils.ObjectUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Groomers", description = "Groomer Operations")
@Slf4j
@RestController
@RequestMapping("/groomers")
public class GroomerRestController {

    @Autowired
    private GroomerService groomerService;

    @Operation(summary = "Authenticate", description = "sign up or sign in")
    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestHeader(name = "x-api-key", required = true) String xApiKey, @RequestBody AuthenticatorDTO authenticatorDTO) {
        log.info("authenticate={}", ObjectUtils.toJson(authenticatorDTO));

        AuthenticationResponseDTO authenticationResponseDTO = groomerService.authenticate(authenticatorDTO);

        return new ResponseEntity<>(authenticationResponseDTO, OK);
    }

    @Operation(summary = "Update Profile", description = "update profile")
    @PutMapping(value = "/profile")
    public ResponseEntity<GroomerDTO> update(@RequestHeader(name = "token", required = true) String token, @RequestBody GroomerUpdateDTO groomerUpdateDTO) {
        log.info("groomerUpdateDTO={}", ObjectUtils.toJson(groomerUpdateDTO));

        GroomerDTO groomerDTO = groomerService.updateProfile(groomerUpdateDTO);

        return new ResponseEntity<>(groomerDTO, OK);
    }

}
