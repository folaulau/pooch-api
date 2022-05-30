package com.pooch.api.library.stripe.paymentintent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethod;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethodDAO;
import com.pooch.api.exception.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PaymentIntentDTO;
import com.pooch.api.dto.PaymentIntentParentCreateDTO;
import com.pooch.api.dto.PaymentIntentQuestCreateDTO;
import com.pooch.api.entity.booking.BookingCalculatorService;
import com.pooch.api.entity.booking.BookingCostDetails;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.stripe.StripeMetadataService;
import com.pooch.api.library.stripe.customer.StripeCustomerService;
import com.pooch.api.utils.MathUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.model.Transfer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import com.stripe.param.TransferCreateParams;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripePaymentIntentServiceImp implements StripePaymentIntentService {

  @Autowired
  @Qualifier(value = "stripeSecrets")
  private StripeSecrets stripeSecrets;

  @Autowired
  private StripePaymentIntentValidatorService stripePaymentIntentValidatorService;

  @Value("${spring.profiles.active}")
  private String env;

  @Value("${booking.fee:10}")
  private Double bookingFee;

  @Autowired
  private BookingCalculatorService bookingCalculatorService;

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Autowired
  private StripeCustomerService stripeCustomerService;

  @Autowired
  private PaymentMethodDAO paymentMethodDAO;

  @Override
  public PaymentIntent getById(String paymentIntentId) {

    Stripe.apiKey = stripeSecrets.getSecretKey();

    PaymentIntent paymentIntent = null;

    try {
      paymentIntent = PaymentIntent.retrieve(paymentIntentId);
      log.info("paymentIntent={}", paymentIntent.toJson());
    } catch (StripeException e) {
      log.warn("StripeException - getById, msg={}, userMessage={}, stripeErrorMessage={}",
          e.getLocalizedMessage(), e.getUserMessage(), e.getStripeError().getMessage());
    }

    return paymentIntent;
  }

  @Override
  public PaymentIntentCollection getPaymentIntentsByCustomerId(String customerId, long limit,
      String startingAfter) {

    Stripe.apiKey = stripeSecrets.getSecretKey();

    Map<String, Object> params = new HashMap<>();
    params.put("customer", customerId);

    /**
     * https://stripe.com/docs/api/invoices/list#list_invoices-limit<br/>
     * default to 20
     */
    if (limit <= 0 || limit > 100) {
      params.put("limit", 20);
    } else {
      params.put("limit", limit);
    }

    if (null != startingAfter && startingAfter.length() > 0) {
      params.put("starting_after", startingAfter);
    }

    PaymentIntentCollection paymentIntentCollection = null;

    try {
      paymentIntentCollection = PaymentIntent.list(params);
    } catch (StripeException e) {
      log.warn("StripeException - getPaymentIntentsByCustomerId, localMessage={}, userMessage={}",
          e.getLocalizedMessage(), e.getUserMessage());
    }

    return paymentIntentCollection;
  }

  // @Override
  // public PaymentIntentDTO createQuestPaymentIntent(
  // PaymentIntentQuestCreateDTO paymentIntentCreateDTO) {
  // Stripe.apiKey = stripeSecrets.getSecretKey();
  //
  // Groomer groomer = stripePaymentIntentValidatorService
  // .validateCreateQuestPaymentIntent(paymentIntentCreateDTO);
  //
  // // Customer customer = stripeCustomerService.createPlaceHolderCustomer();
  //
  // BookingCostDetails costDetails = bookingCalculatorService.generatePaymentIntentDetails(groomer,
  // paymentIntentCreateDTO.getAmount());
  //
  // long totalChargeAsCents = BigDecimal.valueOf(costDetails.getTotalChargeAtBooking())
  // .multiply(BigDecimal.valueOf(100)).longValue();
  //
//    //@formatter:off
//        com.stripe.param.PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
//                .addPaymentMethodType("card")
//                .setAmount(totalChargeAsCents)
//                .setCurrency("usd")
//                .putMetadata(StripeMetadataService.env, env)
//                .putMetadata(StripeMetadataService.PAYMENT_PURPOSE, StripeMetadataService.PAYMENT_PURPOSE_BOOKING_INITIAL_PAYMENT)
//                .putMetadata(StripeMetadataService.PAYMENTINTENT_GROOMER_UUID, groomer.getUuid())
//                .putMetadata(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS, costDetails.toJson())
//                .setTransferGroup("group-" + UUID.randomUUID().toString());
//        // @formatter:on
  //
  // // if (customer != null) {
  // // builder.setCustomer(customer.getId());
  // // }
  //
  // if (paymentIntentCreateDTO.getSavePaymentMethodForFutureUse() != null
  // && paymentIntentCreateDTO.getSavePaymentMethodForFutureUse()) {
  // builder.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);
  // }
  //
  // PaymentIntentCreateParams createParams = builder.build();
  //
  // PaymentIntent paymentIntent = null;
  //
  // try {
  // paymentIntent = PaymentIntent.create(createParams);
  // System.out.println(paymentIntent.toJson());
  // } catch (StripeException e) {
  // log.warn("StripeException - createQuestPaymentIntent, msg={}", e.getMessage());
  // throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
  // }
  //
  // double stripeChargeAmount = BigDecimal.valueOf(paymentIntent.getAmount())
  // .divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
  //
  // PaymentIntentDTO paymentIntentDTO =
  // entityDTOMapper.mapBookingCostDetailsToPaymentIntentDTO(costDetails);
  // paymentIntentDTO.setId(paymentIntent.getId());
  // paymentIntentDTO.setTotalChargeAtBooking(stripeChargeAmount);
  // paymentIntentDTO.setClientSecret(paymentIntent.getClientSecret());
  // paymentIntentDTO.setSetupFutureUsage(paymentIntent.getSetupFutureUsage());
  //
  // return paymentIntentDTO;
  // }
  //
  // @Override
  // public PaymentIntentDTO updateQuestPaymentIntent(
  // PaymentIntentQuestCreateDTO paymentIntentQuestUpdateDTO) {
  // Stripe.apiKey = stripeSecrets.getSecretKey();
  //
  // Groomer groomer = stripePaymentIntentValidatorService
  // .validateUpdateQuestPaymentIntent(paymentIntentQuestUpdateDTO);
  //
  // BookingCostDetails costDetails = bookingCalculatorService.generatePaymentIntentDetails(groomer,
  // paymentIntentQuestUpdateDTO.getAmount());
  //
  // long totalChargeAsCents = BigDecimal.valueOf(costDetails.getTotalChargeAtBooking())
  // .multiply(BigDecimal.valueOf(100)).longValue();
  //
  // PaymentIntent paymentIntent = null;
  //
  // try {
  // paymentIntent = PaymentIntent.retrieve(paymentIntentQuestUpdateDTO.getPaymentIntentId());
  // System.out.println(paymentIntent.toJson());
  //
//      // @formatter:off
//
//            com.stripe.param.PaymentIntentUpdateParams.Builder builder = com.stripe.param.PaymentIntentUpdateParams.builder()
//                    .setAmount(totalChargeAsCents)
//                    .putMetadata(StripeMetadataService.PAYMENT_PURPOSE, StripeMetadataService.PAYMENT_PURPOSE_BOOKING_INITIAL_PAYMENT)
//                    .putMetadata(StripeMetadataService.PAYMENTINTENT_GROOMER_UUID, groomer.getUuid())
//                    .putMetadata(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS, costDetails.toJson());
//
//            if (paymentIntentQuestUpdateDTO.getSavePaymentMethodForFutureUse() != null && paymentIntentQuestUpdateDTO.getSavePaymentMethodForFutureUse()) {
//                builder.setSetupFutureUsage(PaymentIntentUpdateParams.SetupFutureUsage.OFF_SESSION);
//            }
//
//            com.stripe.param.PaymentIntentUpdateParams updateParams = builder.build();
//            // @formatter:on
  //
  // paymentIntent = paymentIntent.update(updateParams);
  //
  // System.out.println(paymentIntent.toJson());
  // } catch (StripeException e) {
  // log.warn("StripeException - updateQuestPaymentIntent, msg={}", e.getMessage());
  // throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
  // }
  //
  // double stripeChargeAmount = BigDecimal.valueOf(paymentIntent.getAmount())
  // .divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
  //
  // PaymentIntentDTO paymentIntentDTO =
  // entityDTOMapper.mapBookingCostDetailsToPaymentIntentDTO(costDetails);
  // paymentIntentDTO.setId(paymentIntent.getId());
  // paymentIntentDTO.setTotalChargeAtBooking(stripeChargeAmount);
  // paymentIntentDTO.setClientSecret(paymentIntent.getClientSecret());
  // paymentIntentDTO.setSetupFutureUsage(paymentIntent.getSetupFutureUsage());
  //
  // return paymentIntentDTO;
  // }

  @Override
  public boolean transferFundsToGroomer(PaymentIntent pi, Groomer groomer) {
    Stripe.apiKey = stripeSecrets.getSecretKey();

    PaymentIntent paymentIntent = null;

    try {
      paymentIntent = PaymentIntent.retrieve(pi.getId());

      String transferGroup = paymentIntent.getTransferGroup();

      BookingCostDetails costDetails = BookingCostDetails.fromJson(
          paymentIntent.getMetadata().get(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS));

      BigDecimal bookingCost = BigDecimal.valueOf(costDetails.getBookingCost());

      List<com.stripe.model.Charge> charges = paymentIntent.getCharges().getData();

      com.stripe.model.Charge charge = charges.get(charges.size() - 1);

      /**
       * make sure TransferGroup is the same from the payment intent
       */
      TransferCreateParams transferParams = TransferCreateParams.builder()
          .setAmount(bookingCost.multiply(BigDecimal.valueOf(100)).longValue()).setCurrency("usd")
          .setDestination(groomer.getStripeConnectedAccountId())

          // https://stripe.com/docs/connect/charges-transfers#transfer-availability
          .setSourceTransaction(charge.getId()).setTransferGroup(transferGroup).build();

      System.out.println("transferParams: " + transferParams.toMap().toString());

      Transfer transfer = Transfer.create(transferParams);

      System.out.println("transfer: " + transfer.toJson());

    } catch (StripeException e) {
      log.warn("StripeException - transferFundsToGroomer, msg={}", e.getMessage());

      return false;
    }

    return false;
  }

  @Override
  public PaymentIntentDTO createParentPaymentIntent(
      PaymentIntentParentCreateDTO paymentIntentParentDTO) {
    Stripe.apiKey = stripeSecrets.getSecretKey();

    Pair<Groomer, Parent> pair = stripePaymentIntentValidatorService
        .validateCreateParentPaymentIntent(paymentIntentParentDTO);

    Groomer groomer = pair.getFirst();
    Parent parent = pair.getSecond();

    PaymentMethod paymentMethod =
        paymentMethodDAO.getByUuid(paymentIntentParentDTO.getPaymentMethodUuid()).orElse(null);

    // Customer customer = stripeCustomerService.createPlaceHolderCustomer();

    BookingCostDetails costDetails = bookingCalculatorService.generatePaymentIntentDetails(groomer,
        paymentIntentParentDTO.getAmount());

    long totalChargeAsCents = BigDecimal.valueOf(costDetails.getTotalChargeAtBooking())
        .multiply(BigDecimal.valueOf(100)).longValue();

    //@formatter:off
        com.stripe.param.PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                .addPaymentMethodType("card")
                .setAmount(totalChargeAsCents)
                .setCurrency("usd")
                .putMetadata(StripeMetadataService.env, env)
                .putMetadata(StripeMetadataService.PAYMENT_PURPOSE, StripeMetadataService.PAYMENT_PURPOSE_BOOKING_INITIAL_PAYMENT)
                .putMetadata(StripeMetadataService.PAYMENTINTENT_GROOMER_UUID, groomer.getUuid())
                .putMetadata(StripeMetadataService.PAYMENTINTENT_PARENT_UUID, parent.getUuid())
                .putMetadata(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS, costDetails.toJson())
                .setTransferGroup("group-" + UUID.randomUUID().toString());
        // @formatter:on

    if (parent.getStripeCustomerId() != null) {
      builder.setCustomer(parent.getStripeCustomerId());
    }

    if (paymentMethod != null && paymentMethod.getStripeId() != null
        && !paymentMethod.getStripeId().trim().isEmpty()) {
      builder.setPaymentMethod(paymentMethod.getStripeId());
    } else {
      if (paymentIntentParentDTO.getSavePaymentMethodForFutureUse() != null
          && paymentIntentParentDTO.getSavePaymentMethodForFutureUse()) {
        builder.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);
      }
    }

    PaymentIntentCreateParams createParams = builder.build();

    PaymentIntent paymentIntent = null;

    try {
      paymentIntent = PaymentIntent.create(createParams);
      System.out.println("createParentPaymentIntent paymentIntent=" + paymentIntent.toJson());
    } catch (StripeException e) {
      log.warn("StripeException - createParentPaymentIntent, msg={}", e.getMessage());
      throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
    }

    double stripeChargeAmount = BigDecimal.valueOf(paymentIntent.getAmount())
        .divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

    PaymentIntentDTO paymentIntentDTO =
        entityDTOMapper.mapBookingCostDetailsToPaymentIntentDTO(costDetails);
    paymentIntentDTO.setId(paymentIntent.getId());
    paymentIntentDTO.setTotalChargeAtBooking(stripeChargeAmount);
    paymentIntentDTO.setClientSecret(paymentIntent.getClientSecret());
    paymentIntentDTO.setSetupFutureUsage(paymentIntent.getSetupFutureUsage());

    return paymentIntentDTO;
  }

  @Override
  public PaymentIntentDTO updateParentPaymentIntent(
      PaymentIntentParentCreateDTO paymentIntentParentDTO) {
    Stripe.apiKey = stripeSecrets.getSecretKey();

    Pair<Groomer, Parent> pair = stripePaymentIntentValidatorService
        .validateUpdateParentPaymentIntent(paymentIntentParentDTO);

    Groomer groomer = pair.getFirst();
    Parent parent = pair.getSecond();

    PaymentMethod paymentMethod =
        paymentMethodDAO.getByUuid(paymentIntentParentDTO.getPaymentMethodUuid()).orElse(null);

    BookingCostDetails costDetails = bookingCalculatorService.generatePaymentIntentDetails(groomer,
        paymentIntentParentDTO.getAmount());

    long totalChargeAsCents = BigDecimal.valueOf(costDetails.getTotalChargeAtBooking())
        .multiply(BigDecimal.valueOf(100)).longValue();

    PaymentIntent paymentIntent = null;

    try {
      paymentIntent = PaymentIntent.retrieve(paymentIntentParentDTO.getPaymentIntentId());
      System.out.println(paymentIntent.toJson());

      // @formatter:off

            com.stripe.param.PaymentIntentUpdateParams.Builder builder = com.stripe.param.PaymentIntentUpdateParams.builder()
                    .setAmount(totalChargeAsCents)
                    .putMetadata(StripeMetadataService.PAYMENT_PURPOSE, StripeMetadataService.PAYMENT_PURPOSE_BOOKING_INITIAL_PAYMENT)
                    .putMetadata(StripeMetadataService.PAYMENTINTENT_GROOMER_UUID, groomer.getUuid())
                    .putMetadata(StripeMetadataService.PAYMENTINTENT_PARENT_UUID, parent.getUuid())
                    .putMetadata(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS, costDetails.toJson());

            if (parent.getStripeCustomerId() != null) {
                builder.setCustomer(parent.getStripeCustomerId());
            }
            
            if (paymentMethod != null && paymentMethod.getStripeId() != null && !paymentMethod.getStripeId().trim().isEmpty()) {
                builder.setPaymentMethod(paymentMethod.getStripeId());
            } else {
                if (paymentIntentParentDTO.getSavePaymentMethodForFutureUse() != null && paymentIntentParentDTO.getSavePaymentMethodForFutureUse()) {
                    builder.setSetupFutureUsage(PaymentIntentUpdateParams.SetupFutureUsage.OFF_SESSION);
                }
            }

            com.stripe.param.PaymentIntentUpdateParams updateParams = builder.build();
            // @formatter:on

      paymentIntent = paymentIntent.update(updateParams);

      System.out.println("updateParentPaymentIntent paymentIntent=" + paymentIntent.toJson());
    } catch (StripeException e) {
      log.warn("StripeException - updateParentPaymentIntent, msg={}", e.getMessage());
      throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
    }

    double stripeChargeAmount = BigDecimal.valueOf(paymentIntent.getAmount())
        .divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

    PaymentIntentDTO paymentIntentDTO =
        entityDTOMapper.mapBookingCostDetailsToPaymentIntentDTO(costDetails);
    paymentIntentDTO.setId(paymentIntent.getId());
    paymentIntentDTO.setTotalChargeAtBooking(stripeChargeAmount);
    paymentIntentDTO.setClientSecret(paymentIntent.getClientSecret());
    paymentIntentDTO.setSetupFutureUsage(paymentIntent.getSetupFutureUsage());

    return paymentIntentDTO;
  }

  @Override
  public PaymentIntent confirm(String paymentIntentId) {
    Stripe.apiKey = stripeSecrets.getSecretKey();

    PaymentIntent paymentIntent = getById(paymentIntentId);

    paymentIntent = confirm(paymentIntent);

    return paymentIntent;
  }

  @Override
  public PaymentIntent confirm(PaymentIntent paymentIntent) {
    Stripe.apiKey = stripeSecrets.getSecretKey();

    try {
      paymentIntent = paymentIntent.confirm();

      log.info("confirmed paymentIntent={}", paymentIntent.toJson());

    } catch (StripeException e) {
      log.warn("StripeException - getById, msg={}, userMessage={}, stripeErrorMessage={}",
          e.getLocalizedMessage(), e.getUserMessage(), e.getStripeError().getMessage());
      throw new ApiException("Unable to confirm payment", e.getUserMessage());
    }

    return paymentIntent;
  }
}
