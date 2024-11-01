package com.pooch.api.entity.parent;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.ParentCancellationRequestDTO;
import com.pooch.api.dto.ParentDTO;
import com.pooch.api.dto.ParentUpdateDTO;
import com.pooch.api.dto.PaymentMethodCreateDTO;
import com.pooch.api.dto.PaymentMethodDTO;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethodService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.xapikey.XApiKeyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Parents", description = "Parent Operations")
@RestController
@RequestMapping("/parents")
public class ParentRestController {

    @Autowired
    private ParentService        parentService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private XApiKeyService       xApiKeyService;

    @Operation(summary = "Authenticate", description = "sign up or sign in")
    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestHeader(name = "x-api-key", required = true) String xApiKey, @RequestBody AuthenticatorDTO authenticatorDTO) {
        // log.info("authenticate={}", ObjectUtils.toJson(authenticatorDTO));

        xApiKeyService.validateForPoochAppMobile(xApiKey);

        AuthenticationResponseDTO authenticationResponseDTO = parentService.authenticate(authenticatorDTO);

        return new ResponseEntity<>(authenticationResponseDTO, OK);
    }

    @Operation(summary = "Upload Profile Image", description = "Upload profile image. All other profile images will have mainProfileImage as false")
    @PostMapping(value = "/{uuid}/profile/image", consumes = {"multipart/form-data"})
    public ResponseEntity<S3FileDTO> uploadProfileImage(@RequestHeader(name = "token", required = true) String token, @PathVariable String uuid, @RequestParam(name = "image") MultipartFile image) {
        log.info("uploadProfileImages, uuid={}", uuid);

        S3FileDTO s3FileDTO = parentService.uploadProfileImage(uuid, image);

        return new ResponseEntity<>(s3FileDTO, OK);
    }

    @Operation(summary = "Update Profile", description = "update profile")
    @PutMapping(value = "/profile")
    public ResponseEntity<ParentDTO> updateProfile(@RequestHeader(name = "token", required = true) String token, @RequestBody ParentUpdateDTO parentUpdateDTO) {
        log.info("parentUpdateDTO={}", ObjectUtils.toJson(parentUpdateDTO));

        ParentDTO parentDTO = parentService.updateProfile(parentUpdateDTO);

        return new ResponseEntity<>(parentDTO, OK);
    }

    @Operation(summary = "Sign Out", description = "sign out")
    @DeleteMapping(value = "/signout")
    public ResponseEntity<ApiDefaultResponseDTO> signOut(@RequestHeader(name = "token", required = true) String token) {
        log.info("signOut={}", token);

        parentService.signOut(token);

        return new ResponseEntity<>(new ApiDefaultResponseDTO(ApiDefaultResponseDTO.SUCCESS), OK);
    }

    @Operation(summary = "Add PaymentMethod", description = "Add PaymentMethod with setupIntent. This endpoint assumes that the setupIntent has been added the PaymentMethod from the UI")
    @PostMapping(value = "/{uuid}/paymentmethod")
    public ResponseEntity<PaymentMethodDTO> addPaymentMethod(@RequestHeader(name = "token", required = true) String token, @PathVariable String uuid,
            @RequestBody PaymentMethodCreateDTO paymentMethodCreateDTO) {
        log.info("addPaymentMethod={}", ObjectUtils.toJson(paymentMethodCreateDTO));

        PaymentMethodDTO paymentMethodDTO = paymentMethodService.add(uuid, paymentMethodCreateDTO);

        return new ResponseEntity<>(paymentMethodDTO, OK);
    }

    @Operation(summary = "Request Phone Number Verification", description = "Request phone number verification.")
    @PostMapping(value = "/{uuid}/phonenumber/request-verification")
    public ResponseEntity<ApiDefaultResponseDTO> requestPhoneNumberVerification(@RequestHeader(name = "token", required = true) String token, @PathVariable String uuid,
            @RequestBody PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO) {
        log.info("requestPhoneNumberVerification, phoneNumberRequestVerificationDTO={}", ObjectUtils.toJson(phoneNumberRequestVerificationDTO));

        ApiDefaultResponseDTO response = parentService.requestPhoneNumberVerification(uuid, phoneNumberRequestVerificationDTO);

        return new ResponseEntity<>(response, OK);
    }

    @Operation(summary = "Verify Phone Number", description = "verify phone number with code")
    @PutMapping(value = "/{uuid}/phonenumber/verification")
    public ResponseEntity<PhoneNumberVerificationDTO> verifyPhoneNumberWithCode(@RequestHeader(name = "token", required = true) String token, @PathVariable String uuid,
            @RequestBody PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO) {
        log.info("verifyPhoneNumberWithCode");

        PhoneNumberVerificationDTO response = parentService.verifyNumberWithCode(uuid, phoneNumberVerificationDTO);

        return new ResponseEntity<>(response, OK);
    }

    @Operation(summary = "Cancel Account", description = "cancel account")
    @PutMapping(value = "/cancel")
    public ResponseEntity<ApiDefaultResponseDTO> cancelAccount(@RequestHeader(name = "token", required = true) String token, @RequestBody ParentCancellationRequestDTO cancellationRequest) {
        log.info("cancelAccount");

        boolean result = parentService.cancelAccount(cancellationRequest);

        return new ResponseEntity<>(new ApiDefaultResponseDTO("Your account has been cancelled"), OK);
    }
}
