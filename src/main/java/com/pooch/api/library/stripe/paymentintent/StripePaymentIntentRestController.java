package com.pooch.api.library.stripe.paymentintent;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.PaymentIntentCreateDTO;
import com.pooch.api.dto.PaymentIntentDTO;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.xapikey.XApiKeyService;
import com.stripe.model.PaymentIntent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Stripe PaymentIntents", description = "Stripe PaymentIntents")
@Slf4j
@RestController
public class StripePaymentIntentRestController {

    @Autowired
    private StripePaymentIntentService stripePaymentIntentService;

    @Autowired
    private XApiKeyService             xApiKeyService;

    @Operation(summary = "Get Stripe Payment Intent", description = "get stripe payment intent")
    @PostMapping(value = "/stripe/paymentintent")
    public ResponseEntity<PaymentIntentDTO> processNewPaymentIntent(@RequestHeader(name = "x-api-key", required = true) String xApiKey, @RequestBody PaymentIntentCreateDTO paymentIntentCreateDTO) {
        log.info("processNewPaymentIntent={}", ObjectUtils.toJson(paymentIntentCreateDTO));

        xApiKeyService.validate(xApiKey);
        
        PaymentIntentDTO paymentIntent = stripePaymentIntentService.processNewPaymentIntent(paymentIntentCreateDTO);

        return new ResponseEntity<>(paymentIntent, OK);
    }
}
