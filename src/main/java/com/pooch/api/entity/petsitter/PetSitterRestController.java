package com.pooch.api.entity.petsitter;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "PetSitters", description = "Pet Sitter Operations")
@Slf4j
@RestController
@RequestMapping("/petsitters")
public class PetSitterRestController {

    @Operation(summary = "Sign Up", description = "sign up")
    @PostMapping(value = "/signup")
    public ResponseEntity<String> signUp(@RequestHeader(name = "x-api-key", required = true) String xApiKey) {
        log.info("sign up");

        return new ResponseEntity<>("Looks Good", OK);
    }

}
