package com.pooch.api.library.stripe.paymentintent;

import com.pooch.api.dto.PaymentIntentCreateDTO;
import com.pooch.api.entity.groomer.Groomer;

public interface StripePaymentIntentValidatorService {

    Groomer validateProcessNewPaymentIntent(PaymentIntentCreateDTO paymentIntentCreateDTO);
}
