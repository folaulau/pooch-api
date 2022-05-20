package com.pooch.api.entity.paymentmethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import com.pooch.api.dto.PaymentMethodCreateDTO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.stripe.setupintent.StripeSetupIntentService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentMethodValidatorServiceImp implements PaymentMethodValidatorService {

  @Autowired
  private ParentDAO parentDAO;

  @Autowired
  private StripeSetupIntentService stripeSetupIntentService;

  @Override
  public Pair<Parent, com.stripe.model.SetupIntent> validateAddNewPaymentMethod(String parentUuid,
      PaymentMethodCreateDTO paymentMethodCreateDTO) {


    if (parentUuid == null || parentUuid.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "parentUuid is required");
    }

    String setupIntentId = paymentMethodCreateDTO.getSetupIntentId();

    if (setupIntentId == null || setupIntentId.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "setupIntentId is required");
    }


    Parent parent = parentDAO.getByUuid(parentUuid).orElseThrow(
        () -> new ApiException("Parent not found", "parent not found for uuid=" + parentUuid));

    com.stripe.model.SetupIntent setupIntent =
        stripeSetupIntentService.verifyForAddPaymentMethod(setupIntentId);

    if (!parent.getStripeCustomerId().equalsIgnoreCase(setupIntent.getCustomer())) {
      throw new ApiException("PaymentMethod is invalid",
          "setupIntent customer is different from parent customer");
    }

    return Pair.of(parent, setupIntent);
  }



}
