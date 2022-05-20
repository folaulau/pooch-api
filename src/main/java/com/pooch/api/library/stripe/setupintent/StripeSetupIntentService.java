package com.pooch.api.library.stripe.setupintent;

import com.pooch.api.dto.PaymentMethodDTO;
import com.pooch.api.dto.SetupIntentConfirmDTO;
import com.pooch.api.dto.SetupIntentCreateDTO;
import com.pooch.api.dto.SetupIntentDTO;
import com.stripe.model.SetupIntent;

public interface StripeSetupIntentService {

  SetupIntentDTO create(SetupIntentCreateDTO setupIntentCreateDTO);
  
  SetupIntent verifyForAddPaymentMethod(String id);

//  SetupIntentDTO confirmSetupIntent(SetupIntentConfirmDTO setupIntentConfirmDTO);
}
