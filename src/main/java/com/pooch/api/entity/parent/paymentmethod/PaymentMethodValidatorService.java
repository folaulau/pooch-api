package com.pooch.api.entity.parent.paymentmethod;

import org.springframework.data.util.Pair;
import com.pooch.api.dto.PaymentMethodCreateDTO;
import com.pooch.api.entity.parent.Parent;

public interface PaymentMethodValidatorService {

  Pair<Parent, com.stripe.model.SetupIntent> validateAddNewPaymentMethod(String parentUuid,
      PaymentMethodCreateDTO paymentMethodCreateDTO);

}
