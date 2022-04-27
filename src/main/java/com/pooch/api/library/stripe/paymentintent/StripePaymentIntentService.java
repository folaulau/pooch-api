package com.pooch.api.library.stripe.paymentintent;

import java.math.BigDecimal;

import com.pooch.api.dto.PaymentIntentCreateDTO;
import com.pooch.api.dto.PaymentIntentDTO;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;

public interface StripePaymentIntentService {

    PaymentIntent getById(String paymentIntentId);

    PaymentIntent create(String accountId, BigDecimal amount);

    PaymentIntentCollection getPaymentIntentsByCustomerId(String customerId, long limit, String startingAfter);

    PaymentIntentDTO processNewPaymentIntent(PaymentIntentCreateDTO paymentIntentCreateDTO);
}
