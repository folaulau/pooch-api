package com.pooch.api.library.stripe.setupintent;

import com.pooch.api.dto.SetupIntentConfirmDTO;
import com.pooch.api.dto.SetupIntentCreateDTO;
import com.pooch.api.entity.parent.Parent;

public interface StripeSetupIntentValidatorService {

  Parent validateCreateSetupIntent(SetupIntentCreateDTO setupIntentCreateDTO);

  Parent validateConfirmSetupIntent(SetupIntentConfirmDTO setupIntentConfirmDTO);

}
