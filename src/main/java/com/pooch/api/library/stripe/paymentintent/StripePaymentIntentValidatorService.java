package com.pooch.api.library.stripe.paymentintent;
import com.pooch.api.dto.PaymentIntentQuestCreateDTO;
import com.pooch.api.entity.groomer.Groomer;

public interface StripePaymentIntentValidatorService {

    Groomer validateCreateQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentCreateDTO);
    
    Groomer validateUpdateQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentQuestUpdateDTO);
}
