package com.pooch.api.library.stripe.paymentintent;

import java.math.BigDecimal;
import com.pooch.api.dto.PaymentIntentDTO;
import com.pooch.api.dto.PaymentIntentParentCreateDTO;
import com.pooch.api.dto.PaymentIntentQuestCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;

public interface StripePaymentIntentService {

    PaymentIntent getById(String paymentIntentId);

//    PaymentIntentDTO createQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentCreateDTO);

//    PaymentIntentDTO updateQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentQuestUpdateDTO);

    PaymentIntentCollection getPaymentIntentsByCustomerId(String customerId, long limit, String startingAfter);

    boolean transferFundsToGroomer(PaymentIntent paymentIntent, Groomer groomer);

    PaymentIntentDTO createParentPaymentIntent(PaymentIntentParentCreateDTO paymentIntentParentDTO);

    PaymentIntentDTO updateParentPaymentIntent(PaymentIntentParentCreateDTO paymentIntentParentDTO);

}
