package com.pooch.api.library.stripe.setupintent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pooch.api.dto.SetupIntentConfirmDTO;
import com.pooch.api.dto.SetupIntentCreateDTO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripeSetupIntentValidatorServiceImp implements StripeSetupIntentValidatorService {

  @Autowired
  private ParentDAO parentDAO;

  @Override
  public Parent validateCreateSetupIntent(SetupIntentCreateDTO setupIntentCreateDTO) {

    String parentUuid = setupIntentCreateDTO.getParentUuid();

    if (parentUuid == null || parentUuid.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "parentUuid is required");
    }

    Parent parent = parentDAO.getByUuid(parentUuid).orElseThrow(
        () -> new ApiException(ApiError.DEFAULT_MSG, "parent not found for uuid=" + parentUuid));

    return parent;
  }

  @Override
  public Parent validateConfirmSetupIntent(SetupIntentConfirmDTO setupIntentConfirmDTO) {

    String parentUuid = setupIntentConfirmDTO.getParentUuid();

    if (parentUuid == null || parentUuid.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "parentUuid is required");
    }


    String setupIntentId = setupIntentConfirmDTO.getSetupIntentId();

    if (setupIntentId == null || setupIntentId.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "setupIntentId is required");
    }

    Parent parent = parentDAO.getByUuid(parentUuid).orElseThrow(
        () -> new ApiException(ApiError.DEFAULT_MSG, "parent not found for uuid=" + parentUuid));

    return parent;
  }

}
