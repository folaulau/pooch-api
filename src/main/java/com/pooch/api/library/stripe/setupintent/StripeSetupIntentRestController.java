package com.pooch.api.library.stripe.setupintent;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.PaymentIntentDTO;
import com.pooch.api.dto.PaymentIntentParentCreateDTO;
import com.pooch.api.dto.PaymentIntentQuestCreateDTO;
import com.pooch.api.dto.PaymentMethodDTO;
import com.pooch.api.dto.SetupIntentConfirmDTO;
import com.pooch.api.dto.SetupIntentCreateDTO;
import com.pooch.api.dto.SetupIntentDTO;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.xapikey.XApiKeyService;
import com.stripe.model.PaymentIntent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Stripe SetupIntents", description = "Stripe SetupIntents")
@Slf4j
@RestController
public class StripeSetupIntentRestController {

  @Autowired
  private StripeSetupIntentService stripeSetupIntentService;


  @Operation(summary = "Get Stripe Setup Intent to add a Payment Method", description = "")
  @PostMapping(value = "/stripe/setupintent")
  public ResponseEntity<SetupIntentDTO> createSetupIntent(
      @RequestHeader(name = "token", required = true) String token,
      @RequestBody SetupIntentCreateDTO setupIntentCreateDTO) {
    log.info("createSetupIntent={}", ObjectUtils.toJson(setupIntentCreateDTO));

    SetupIntentDTO setupIntentDTO = stripeSetupIntentService.create(setupIntentCreateDTO);

    return new ResponseEntity<>(setupIntentDTO, OK);
  }

//  @Operation(summary = "Confirm Stripe Setup Intent to add a Payment Method",
//      description = "After confirming the setupIntent, the paymentMethod will be saved")
//  @PostMapping(value = "/stripe/setupintent/confirm")
//  public ResponseEntity<SetupIntentDTO> confirmSetupContent(
//      @RequestHeader(name = "token", required = true) String token,
//      @RequestBody SetupIntentConfirmDTO setupIntentConfirmDTO) {
//    log.info("createSetupIntent={}", ObjectUtils.toJson(setupIntentConfirmDTO));
//
//    SetupIntentDTO setupIntentDTO =
//        stripeSetupIntentService.confirmSetupIntent(setupIntentConfirmDTO);
//
//    return new ResponseEntity<>(setupIntentDTO, OK);
//  }

}
