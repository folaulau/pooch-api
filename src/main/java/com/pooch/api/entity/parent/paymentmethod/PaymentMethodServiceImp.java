package com.pooch.api.entity.parent.paymentmethod;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PaymentMethodCreateDTO;
import com.pooch.api.dto.PaymentMethodDTO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.stripe.customer.StripeCustomerService;
import com.pooch.api.library.stripe.paymentmethod.StripePaymentMethodService;
import com.pooch.api.library.stripe.setupintent.StripeSetupIntentService;
import com.pooch.api.utils.ObjectUtils;
import com.stripe.model.Customer;
import com.stripe.param.PaymentMethodCreateParams;

import lombok.extern.slf4j.Slf4j;

/**
 * Now as of 2021-11-15, we are only accepting Card Payment Method
 */
@Slf4j
@Service
public class PaymentMethodServiceImp implements PaymentMethodService {

  @Autowired
  private PaymentMethodDAO paymentMethodDAO;

  @Autowired
  private StripeCustomerService stripeCustomerService;

  @Autowired
  private StripePaymentMethodService stripePaymentMethodService;

  @Autowired
  private StripeSetupIntentService stripeSetupIntentService;

  @Autowired
  private PaymentMethodValidatorService paymentMethodValidatorService;

  @Autowired
  private EntityDTOMapper entityDTOMapper;


  @Override
  public PaymentMethod create(PaymentMethod paymentMethod) {
    log.debug("create(...)");
    return paymentMethodDAO.save(paymentMethod);
  }

  @Override
  public PaymentMethod update(PaymentMethod paymentMethod) {
    log.debug("update(...)");
    if (paymentMethod.getId() == null || paymentMethod.getUuid() == null) {
      return create(paymentMethod);
    }
    return paymentMethodDAO.save(paymentMethod);
  }

  @Override
  public List<PaymentMethod> update(Long accountId, String paymentMethodUuid,
      PaymentMethod newPaymentMethod) {
    log.debug("update(...)");

    Optional<PaymentMethod> optPaymentMethod = paymentMethodDAO.getByUuid(paymentMethodUuid);

    if (optPaymentMethod.isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG,
          "PaymentMethod not found for uuid=" + paymentMethodUuid);
    }

    PaymentMethod paymentMethod = optPaymentMethod.get();

    if (accountId != paymentMethod.getParent().getId()) {
      throw new ApiException("Payment Method does not belong to your account");
    }

    paymentMethod.setDeleted(true);
    this.update(paymentMethod);

    this.create(newPaymentMethod);

    return null;// this.getByAccountId(accountId);
  }

  @Override
  public List<PaymentMethod> getByParentId(Long accountId) {
    // TODO Auto-generated method stub
    return paymentMethodDAO.findByParentId(accountId);
  }

  @Override
  public List<PaymentMethod> getByParentUuid(String accountUuid) {
    // TODO Auto-generated method stub
    return paymentMethodDAO.findByParentUuid(accountUuid);
  }

  /**
   * add paymentMethod to Stripe<br>
   * add paymentMethod to DB<br>
   */
  @Override
  public PaymentMethod add(Parent parent, com.stripe.model.PaymentMethod stripePaymentMethod) {
    log.info("stripePaymentMethod={}", ObjectUtils.toJson(stripePaymentMethod));

    if (stripePaymentMethod.getCustomer() == null) {

      stripePaymentMethod = stripePaymentMethodService.attachToCustomer(stripePaymentMethod.getId(),
          parent.getStripeCustomerId());

      log.info("updated stripePaymentMethod={}", stripePaymentMethod.toJson());
    }

    PaymentMethod paymentMethod = null;

    if (stripePaymentMethod.getType()
        .equalsIgnoreCase(PaymentMethodCreateParams.Type.CARD.name())) {

      com.stripe.model.PaymentMethod.Card card = stripePaymentMethod.getCard();

      try {
        paymentMethod = new PaymentMethod();
        paymentMethod.setBrand(card.getBrand());
        paymentMethod.setParent(parent);
        paymentMethod.setLast4(card.getLast4());
        paymentMethod.setExpirationMonth(card.getExpMonth());
        paymentMethod.setExpirationYear(card.getExpYear());
        paymentMethod.setStripeId(stripePaymentMethod.getId());
        paymentMethod.setType(stripePaymentMethod.getType());

        paymentMethod = this.create(paymentMethod);

      } catch (Exception e) {
        log.warn("Exception, msg={}", e.getLocalizedMessage());
      }
    } else {
      log.warn("Stripe PaymentMethod type not found. stripePaymentMethod={}",
          stripePaymentMethod.toJson());
    }

    log.info("paymentMethod={}", ObjectUtils.toJson(paymentMethod));

    return paymentMethod;
  }

  @Override
  public PaymentMethod mapStripePaymentMethodToPaymentMethod(
      com.stripe.model.PaymentMethod stripePaymentMethod) {

    PaymentMethod paymentMethod = null;

    com.stripe.model.PaymentMethod.Card card = stripePaymentMethod.getCard();

    try {
      paymentMethod = new PaymentMethod();
      paymentMethod.setBrand(card.getBrand());
      paymentMethod.setLast4(card.getLast4());
      paymentMethod.setExpirationMonth(card.getExpMonth());
      paymentMethod.setExpirationYear(card.getExpYear());
      paymentMethod.setStripeId(stripePaymentMethod.getId());
      paymentMethod.setType(stripePaymentMethod.getType());

    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getLocalizedMessage());
    }

    log.info("paymentMethod={}", ObjectUtils.toJson(paymentMethod));

    return paymentMethod;
  }

  @Override
  public PaymentMethodDTO add(String parentUuid, PaymentMethodCreateDTO paymentMethodCreateDTO) {

    Pair<Parent, com.stripe.model.SetupIntent> pair = paymentMethodValidatorService
        .validateAddNewPaymentMethod(parentUuid, paymentMethodCreateDTO);

    Parent parent = pair.getFirst();
    com.stripe.model.SetupIntent setupIntent = pair.getSecond();


    com.stripe.model.PaymentMethod stripePaymentMethod =
        stripePaymentMethodService.getById(setupIntent.getPaymentMethod());

    PaymentMethod paymentMethod = add(parent, stripePaymentMethod);

    return entityDTOMapper.mapPaymentMethodToPaymentMethod(paymentMethod);
  }


}
