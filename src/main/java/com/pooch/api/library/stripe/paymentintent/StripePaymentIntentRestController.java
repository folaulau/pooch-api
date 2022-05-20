package com.pooch.api.library.stripe.paymentintent;

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

    @Operation(summary = "Get Stripe Payment Intent for initial booking payment", description = "This is for booking initial payment. amount represents the total cost of all of services that Pooch Parent is requesting.<br>Get stripe payment intent<br>stripe fee: 2.9% + 30c per payment")
    @PostMapping(value = "/stripe/paymentintent/booking")
    public ResponseEntity<PaymentIntentDTO> createPaymentIntentForBooking(@RequestHeader(name = "x-api-key", required = true) String xApiKey, @RequestBody PaymentIntentQuestCreateDTO paymentIntentCreateDTO) {
        log.info("createPaymentIntent={}", ObjectUtils.toJson(paymentIntentCreateDTO));

        xApiKeyService.validate(xApiKey);

        String paymentIntentId = paymentIntentCreateDTO.getPaymentIntentId();

        PaymentIntentDTO paymentIntent = null;

        if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
            paymentIntent = stripePaymentIntentService.createQuestPaymentIntent(paymentIntentCreateDTO);
        } else {
            // update paymentIntent
            paymentIntent = stripePaymentIntentService.updateQuestPaymentIntent(paymentIntentCreateDTO);
        }

        return new ResponseEntity<>(paymentIntent, OK);
    }
    
    @Operation(summary = "Get Stripe Parent Payment Intent for initial booking payment", description = "This is for booking initial payment. It's for Pooch parents that are authenticated. This endpoint allows parents to their existing payment methods to make payments with. amount represents the total cost of all of services that Pooch Parent is requesting. <br>Get stripe payment intent<br>stripe fee: 2.9% + 30c per payment")
    @PostMapping(value = "/stripe/parent/paymentintent/booking")
    public ResponseEntity<PaymentIntentDTO> createParentPaymentIntentForBooking(@RequestHeader(name = "token", required = true) String token, @RequestBody PaymentIntentParentCreateDTO paymentIntentParentDTO) {
        log.info("createParentPaymentIntent={}", ObjectUtils.toJson(paymentIntentParentDTO));

        String paymentIntentId = paymentIntentParentDTO.getPaymentIntentId();

        PaymentIntentDTO paymentIntent = null;

        if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
            paymentIntent = stripePaymentIntentService.createParentPaymentIntent(paymentIntentParentDTO);
        } else {
            // update paymentIntent
            paymentIntent = stripePaymentIntentService.updateParentPaymentIntent(paymentIntentParentDTO);
        }

        return new ResponseEntity<>(paymentIntent, OK);
    }

    // @Operation(summary = "Update Stripe Payment Intent", description = "update stripe payment intent")
    // @PutMapping(value = "/stripe/paymentintent")
    // public ResponseEntity<PaymentIntentDTO> updatePaymentIntent(@RequestHeader(name = "x-api-key", required = true)
    // String xApiKey, @RequestBody PaymentIntentQuestUpdateDTO paymentIntentQuestUpdateDTO) {
    // log.info("updatePaymentIntent={}", ObjectUtils.toJson(paymentIntentQuestUpdateDTO));
    //
    // xApiKeyService.validate(xApiKey);
    //
    // PaymentIntentDTO paymentIntent =
    // stripePaymentIntentService.updateQuestPaymentIntent(paymentIntentQuestUpdateDTO);
    //
    // return new ResponseEntity<>(paymentIntent, OK);
    // }
}
