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

  PaymentIntent confirm(String paymentIntentId);

  PaymentIntent confirm(PaymentIntent paymentIntent);

  // PaymentIntentDTO createQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentCreateDTO);

  // PaymentIntentDTO updateQuestPaymentIntent(PaymentIntentQuestCreateDTO
  // paymentIntentQuestUpdateDTO);

  PaymentIntentCollection getPaymentIntentsByCustomerId(String customerId, long limit,
      String startingAfter);

  boolean transferFundsToGroomerOnBookingInitialPayment(PaymentIntent paymentIntent, Groomer groomer);
  
  boolean transferFundsToGroomer(PaymentIntent paymentIntent, Groomer groomer, com.stripe.model.Charge charge);

  PaymentIntentDTO createParentPaymentIntent(PaymentIntentParentCreateDTO paymentIntentParentDTO);

  PaymentIntentDTO updateParentPaymentIntent(PaymentIntentParentCreateDTO paymentIntentParentDTO);

  PaymentIntent capture(PaymentIntent paymentIntent);
  
  PaymentIntent capture(String paymentIntentId);

}
