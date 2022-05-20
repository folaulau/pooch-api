package com.pooch.api.library.stripe.paymentintent;
import com.pooch.api.dto.PaymentIntentParentCreateDTO;
import com.pooch.api.dto.PaymentIntentQuestCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import org.springframework.data.util.Pair;

public interface StripePaymentIntentValidatorService {

    Groomer validateCreateQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentCreateDTO);
    
    Groomer validateUpdateQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentQuestUpdateDTO);

    Pair<Groomer, Parent> validateCreateParentPaymentIntent(PaymentIntentParentCreateDTO paymentIntentParentDTO);

    Pair<Groomer, Parent> validateUpdateParentPaymentIntent(PaymentIntentParentCreateDTO paymentIntentParentDTO);
}
