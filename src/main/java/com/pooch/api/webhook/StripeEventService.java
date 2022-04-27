package com.pooch.api.webhook;

import java.util.Optional;

import org.springframework.scheduling.annotation.Async;

import com.stripe.model.Event;

public interface StripeEventService {

    Event getById(String id);

    Optional<Event> authorizeWebhookEvent(String body, String signature, String eventSigningSecret);

    @Async
    void processWebhookEvent(Event event);
}
