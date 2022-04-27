package com.pooch.api.webhook;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripeEventServiceImp implements StripeEventService {

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets stripeSecrets;

    @Value("${spring.profiles.active}")
    private String        env;

    @Override
    public Event getById(String id) {

        Stripe.apiKey = stripeSecrets.getSecretKey();

        Event event = null;
        try {
            event = Event.retrieve(id);
        } catch (StripeException e) {
            log.warn("StripeException, msg={}, userMessage={}", e.getLocalizedMessage(), e.getUserMessage());
        }
        return event;
    }

    @Override
    public Optional<Event> authorizeWebhookEvent(String body, String signature, String eventSigningSecret) {

        Stripe.apiKey = stripeSecrets.getSecretKey();

        Event event = null;
        try {
            event = Webhook.constructEvent(body, signature, eventSigningSecret);

        } catch (SignatureVerificationException e) {
            log.error("SignatureVerificationException, msg: {}", e.getLocalizedMessage());

        } catch (Exception e) {
            log.error("Exception, msg: {}", e.getLocalizedMessage());

        }
        return Optional.ofNullable(event);
    }

    // Webhookevent controller
    @Override
    public void processWebhookEvent(Event event) {
        Optional<StripeObject> optStripeObject = event.getDataObjectDeserializer().getObject();

        if (!optStripeObject.isPresent()) {
            return;
        }

        if (event.getType().equals("customer.subscription.updated")) {
            Subscription subscription = (Subscription) optStripeObject.get();

            log.info("subscription={}", subscription.toJson());

            // accountService.updateOnStripeSubscriptionUpdate(subscription);
        }
    }

}
