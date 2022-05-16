package com.pooch.api.library.stripe.paymentintent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.pooch.api.dto.PaymentIntentDTO;
import com.pooch.api.dto.PaymentIntentQuestCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.stripe.StripeMetadataService;
import com.pooch.api.utils.MathUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripePaymentIntentServiceImp implements StripePaymentIntentService {

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets                       stripeSecrets;

    @Autowired
    private StripePaymentIntentValidatorService stripePaymentIntentValidatorService;

    @Value("${spring.profiles.active}")
    private String                              env;

    @Value("${booking.fee:10}")
    private Double                              bookingFee;

    @Override
    public PaymentIntent getById(String paymentIntentId) {

        Stripe.apiKey = stripeSecrets.getSecretKey();

        PaymentIntent paymentIntent = null;

        try {
            paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            log.info("paymentIntent={}", paymentIntent.toJson());
        } catch (StripeException e) {
            log.warn("StripeException, msg={}, userMessage={}, stripeErrorMessage={}", e.getLocalizedMessage(), e.getUserMessage(), e.getStripeError().getMessage());
        }

        return paymentIntent;
    }

    @Override
    public PaymentIntentCollection getPaymentIntentsByCustomerId(String customerId, long limit, String startingAfter) {

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
            log.warn("getPaymentIntentsByCustomerId, StripeException, localMessage={}, userMessage={}", e.getLocalizedMessage(), e.getUserMessage());
        }

        return paymentIntentCollection;
    }

    @Override
    public PaymentIntentDTO createQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentCreateDTO) {
        Stripe.apiKey = stripeSecrets.getSecretKey();

        Groomer groomer = stripePaymentIntentValidatorService.validateCreateQuestPaymentIntent(paymentIntentCreateDTO);

        //@formatter:off
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setMetadata(Map.of(StripeMetadataService.env,env))
                .setName("pooch parent").build();
        //@formatter:on

        Customer customer = null;

        try {
            customer = Customer.create(customerParams);
        } catch (Exception e) {
            log.warn("StripeException, customer, msg={}", e.getMessage());
        }

        // $10 booking fee
        long bookingFeeAsCents = BigDecimal.valueOf(bookingFee).multiply(BigDecimal.valueOf(100)).longValue();

        double bookingCost = paymentIntentCreateDTO.getAmount().doubleValue();

        bookingCost = MathUtils.getTwoDecimalPlaces(bookingCost);

        double chargeAmount = bookingCost + bookingFee;
        long chargeAmountAsCents = BigDecimal.valueOf(chargeAmount).multiply(BigDecimal.valueOf(100)).longValue();
        // 2.9% of chargeAmount + 30 cents
        double stripeFee = BigDecimal.valueOf(2.9)
                .divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(chargeAmount))
                .add(BigDecimal.valueOf(0.3))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();

        long stripeFeeAsCents = BigDecimal.valueOf(stripeFee).multiply(BigDecimal.valueOf(100)).longValue();
        double totalCharge = chargeAmount + stripeFee;
        long totalChargeAsCents = BigDecimal.valueOf(totalCharge).multiply(BigDecimal.valueOf(100)).longValue();

        log.info("createQuestPaymentIntent -> bookingFee={}, bookingCost={}, chargeAmount={}, stripeFee={}, totalCharge={}", bookingFee, bookingCost, chargeAmount, stripeFee, totalCharge);
        log.info("createQuestPaymentIntent -> bookingFeeAsCents={}c, stripeFeeAsCents={}c, stripeFeeAmount={}c, totalChargeAsCents={}c", bookingFeeAsCents, chargeAmountAsCents, stripeFeeAsCents,
                totalChargeAsCents);

        //@formatter:off
        com.stripe.param.PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                .addPaymentMethodType("card")
                .setAmount(totalChargeAsCents)
                .setCurrency("usd")
                .putMetadata(StripeMetadataService.env, env)
                .putMetadata(StripeMetadataService.PAYMENTINTENT_GROOMER_UUID, groomer.getUuid())
                .setTransferGroup("group-" + UUID.randomUUID().toString());
        // @formatter:on

        if (customer != null) {
            builder.setCustomer(customer.getId());
        }

        if (paymentIntentCreateDTO.getSavePaymentMethodForFutureUse() != null && paymentIntentCreateDTO.getSavePaymentMethodForFutureUse()) {
            builder.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);
        }

        PaymentIntentCreateParams createParams = builder.build();

        PaymentIntent paymentIntent = null;

        try {
            paymentIntent = PaymentIntent.create(createParams);
            System.out.println(paymentIntent.toJson());
        } catch (StripeException e) {
            log.warn("StripeException, msg={}", e.getMessage());
            throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
        }

        double stripeAmount = BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        // @formatter:off
        PaymentIntentDTO paymentIntentDTO = PaymentIntentDTO.builder()
                .totalAmount(stripeAmount)
                .bookingCost(bookingCost)
                .stripeFee(stripeFee)
                .bookingFee(bookingFee)
                .clientSecret(paymentIntent.getClientSecret())
                .id(paymentIntent.getId())
                .setupFutureUsage(paymentIntent.getSetupFutureUsage())
                .build();
        // @formatter:off
        
        return paymentIntentDTO;
    }

    @Override
    public PaymentIntentDTO updateQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentQuestUpdateDTO) {
        Stripe.apiKey = stripeSecrets.getSecretKey();
        
        Groomer groomer = stripePaymentIntentValidatorService.validateUpdateQuestPaymentIntent(paymentIntentQuestUpdateDTO);

        PaymentIntent paymentIntent = null;

        // $10 booking fee
        long bookingFeeAsCents = BigDecimal.valueOf(bookingFee).multiply(BigDecimal.valueOf(100)).longValue();
        
        double bookingCost = paymentIntentQuestUpdateDTO.getAmount().doubleValue();
        
        bookingCost = MathUtils.getTwoDecimalPlaces(bookingCost);
        
        double chargeAmount = bookingCost + bookingFee;
        long chargeAmountAsCents = BigDecimal.valueOf(chargeAmount).multiply(BigDecimal.valueOf(100)).longValue();
        // 2.9% of chargeAmount + 30 cents
        double stripeFee = BigDecimal.valueOf(2.9)
                .divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(chargeAmount))
                .add(BigDecimal.valueOf(0.3)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        
        long stripeFeeAsCents = BigDecimal.valueOf(stripeFee).multiply(BigDecimal.valueOf(100)).longValue();
        double totalCharge = chargeAmount + stripeFee;
        long totalChargeAsCents = BigDecimal.valueOf(totalCharge).multiply(BigDecimal.valueOf(100)).longValue();

        log.info("updateQuestPaymentIntent -> bookingFee={}, chargeAmount={}, stripeFee={}, totalCharge={}", bookingFee, chargeAmount, stripeFee, totalCharge);
        log.info("updateQuestPaymentIntent -> bookingFeeAsCents={}c, stripeFeeAsCents={}c, stripeFeeAmount={}c, totalChargeAsCents={}c", bookingFeeAsCents, chargeAmountAsCents, stripeFeeAsCents,
                totalChargeAsCents);

        try {
            paymentIntent = PaymentIntent.retrieve(paymentIntentQuestUpdateDTO.getPaymentIntentId());
            System.out.println(paymentIntent.toJson());

            // @formatter:off
      
            com.stripe.param.PaymentIntentUpdateParams.Builder builder = com.stripe.param.PaymentIntentUpdateParams.builder()
                    .setAmount(totalChargeAsCents)
                    .putMetadata(StripeMetadataService.PAYMENTINTENT_GROOMER_UUID, groomer.getUuid());
            
            if (paymentIntentQuestUpdateDTO.getSavePaymentMethodForFutureUse() != null && paymentIntentQuestUpdateDTO.getSavePaymentMethodForFutureUse()) {
                builder.setSetupFutureUsage(PaymentIntentUpdateParams.SetupFutureUsage.OFF_SESSION);
            }
            
            com.stripe.param.PaymentIntentUpdateParams updateParams = builder.build();
            // @formatter:on

            paymentIntent = paymentIntent.update(updateParams);

            System.out.println(paymentIntent.toJson());
        } catch (StripeException e) {
            log.warn("StripeException, msg={}", e.getMessage());
            throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
        }

        double stripeAmount = BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        // @formatter:off

        PaymentIntentDTO paymentIntentDTO = PaymentIntentDTO.builder()
                .totalAmount(stripeAmount)
                .bookingCost(bookingCost)
                .stripeFee(stripeFee)
                .bookingFee(bookingFee)
                .clientSecret(paymentIntent.getClientSecret())
                .setupFutureUsage(paymentIntent.getSetupFutureUsage())
                .id(paymentIntent.getId())
                .build();
        // @formatter:on

        return paymentIntentDTO;
    }
}
