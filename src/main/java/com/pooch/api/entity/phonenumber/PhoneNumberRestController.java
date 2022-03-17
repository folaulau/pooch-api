package com.pooch.api.entity.phonenumber;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.entity.petsitter.PetSitterRestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Phonenumbers", description = "Phone Numbers")
@Slf4j
@RestController
@RequestMapping("/phonenumbers")
public class PhoneNumberRestController {

    @Autowired
    private PhoneNumberService phoneNumberService;

    @Operation(summary = "Request Phone Number Verification", description = "request verification")
    @PostMapping(value = "/request-verification")
    public ResponseEntity<ApiDefaultResponseDTO> requestPhoneNumberVerification(@RequestHeader(name = "x-api-key", required = true) String xApiKey,
            @RequestBody PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO) {
        log.info("requestPhoneNumberVerification");

        ApiDefaultResponseDTO response = phoneNumberService.requestVerification(phoneNumberRequestVerificationDTO);

        return new ResponseEntity<>(response, OK);
    }
    
    @Operation(summary = "Verify Phone Number", description = "verify phone number with code")
    @PutMapping(value = "/verification")
    public ResponseEntity<PhoneNumberVerificationDTO> verifyPhoneNumberWithCode(@RequestHeader(name = "x-api-key", required = true) String xApiKey,
            @RequestBody PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO) {
        log.info("verifyPhoneNumberWithCode");

        PhoneNumberVerificationDTO response = phoneNumberService.verifyNumberWithCode(phoneNumberVerificationDTO);

        return new ResponseEntity<>(response, OK);
    }
}
