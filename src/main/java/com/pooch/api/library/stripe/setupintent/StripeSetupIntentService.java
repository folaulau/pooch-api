package com.pooch.api.library.stripe.setupintent;

import com.pooch.api.dto.PaymentMethodDTO;
import com.pooch.api.dto.SetupIntentConfirmDTO;
import com.pooch.api.dto.SetupIntentCreateDTO;
import com.pooch.api.dto.SetupIntentDTO;

public interface StripeSetupIntentService {

  SetupIntentDTO create(SetupIntentCreateDTO setupIntentCreateDTO);

  SetupIntentDTO confirmSetupIntent(SetupIntentConfirmDTO setupIntentConfirmDTO);
}
