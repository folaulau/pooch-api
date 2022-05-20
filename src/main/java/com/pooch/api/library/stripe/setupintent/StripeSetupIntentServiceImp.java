package com.pooch.api.library.stripe.setupintent;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PaymentMethodDTO;
import com.pooch.api.dto.SetupIntentConfirmDTO;
import com.pooch.api.dto.SetupIntentCreateDTO;
import com.pooch.api.dto.SetupIntentDTO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.paymentmethod.PaymentMethodService;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.stripe.StripeMetadataService;
import com.pooch.api.library.stripe.paymentmethod.StripePaymentMethodService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;
import com.stripe.param.SetupIntentCreateParams;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripeSetupIntentServiceImp implements StripeSetupIntentService {

  @Autowired
  @Qualifier(value = "stripeSecrets")
  private StripeSecrets stripeSecrets;

  @Value("${spring.profiles.active}")
  private String env;


  @Autowired
  private EntityDTOMapper entityDTOMapper;


  @Autowired
  private StripeSetupIntentValidatorService stripeSetupIntentValidatorService;

  @Autowired
  private StripePaymentMethodService stripePaymentMethodService;


  @Autowired
  private PaymentMethodService paymentMethodService;


  /**
   * SetupIntent to add a payment method
   */
  @Override
  public SetupIntentDTO create(SetupIntentCreateDTO setupIntentCreateDTO) {

    Parent parent =
        stripeSetupIntentValidatorService.validateCreateSetupIntent(setupIntentCreateDTO);


    Stripe.apiKey = stripeSecrets.getSecretKey();

    // @formatter:off

    SetupIntentCreateParams params = SetupIntentCreateParams.builder()
        .setCustomer(parent.getStripeCustomerId())
        .putMetadata(StripeMetadataService.env, env)
        .setUsage(SetupIntentCreateParams.Usage.OFF_SESSION).build();
    
    // @formatter:on

    SetupIntent setupIntent = null;
    try {
      setupIntent = SetupIntent.create(params);
      log.info("setupIntent={}", setupIntent.toJson());
    } catch (StripeException e) {
      log.warn("StripeException - create, localMessage={}, userMessage={}", e.getLocalizedMessage(),
          e.getUserMessage());
      throw new ApiException(e.getMessage());
    }

    SetupIntentDTO setupIntentDTO =
        SetupIntentDTO.builder().clientSecret(setupIntent.getClientSecret()).id(setupIntent.getId())
            .status(setupIntent.getStatus()).build();

    return setupIntentDTO;
  }


  @Override
  public SetupIntentDTO confirmSetupIntent(SetupIntentConfirmDTO setupIntentConfirmDTO) {

    Stripe.apiKey = stripeSecrets.getSecretKey();

    Parent parent =
        stripeSetupIntentValidatorService.validateConfirmSetupIntent(setupIntentConfirmDTO);


    SetupIntent setupIntent = null;
    try {
      setupIntent = SetupIntent.retrieve(setupIntentConfirmDTO.getSetupIntentId());

      log.info("setupIntent={}", setupIntent.toJson());
    } catch (StripeException e) {
      log.warn("StripeException - create, localMessage={}, userMessage={}", e.getLocalizedMessage(),
          e.getUserMessage());
      throw new ApiException(e.getMessage());
    }


    if (Arrays.asList("canceled").contains(setupIntent.getStatus())) {
      throw new ApiException(ApiError.DEFAULT_MSG, "status=" + setupIntent.getStatus());
    }

    PaymentMethod pm = stripePaymentMethodService.getById(setupIntent.getPaymentMethod());

    com.pooch.api.entity.paymentmethod.PaymentMethod paymentMethod =
        paymentMethodService.add(parent, pm);

    return SetupIntentDTO.builder().paymentUuid(paymentMethod.getUuid())
        .clientSecret(setupIntent.getClientSecret()).id(setupIntent.getId())
        .status(setupIntent.getStatus()).build();
  }


}
