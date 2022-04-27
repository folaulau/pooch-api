package com.pooch.api.webhook;

import static org.springframework.http.HttpStatus.OK;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.stripe.model.Event;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Webhook Receiver", description = "Webhook Receiver")
@Slf4j
@RestController
@RequestMapping("/webhooks/receivers")
public class StripeWebhookRestController {

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets      stripeSecrets;

    @Autowired
    private StripeEventService stripeEventService;

    @Operation(summary = "Stripe Webhook Receiver", description = "stripe webhook receiver")
    @PostMapping(value = "/stripe")
    public ResponseEntity<ApiDefaultResponseDTO> stripeWebhookReceiver(@RequestHeader("Stripe-Signature") String stripeSignature, @RequestBody String object) {
        log.info("event object {}", object);

        Optional<Event> optEvent = stripeEventService.authorizeWebhookEvent(object, stripeSignature, stripeSecrets.getWebhookSigningSecret());

        if (optEvent.isPresent() == false) {
            log.warn("Webhook authorization did not pass, stripeWebhookSigningSecret={}", stripeSecrets.getWebhookSigningSecret());
            throw new ApiException("Webhook authorization did not pass");
        }

        stripeEventService.processWebhookEvent(optEvent.get());

        return new ResponseEntity<>(new ApiDefaultResponseDTO("good"), OK);
    }
}
