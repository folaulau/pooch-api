package com.pooch.api.library.stripe.paymentintent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.PaymentIntentCreateDTO;
import com.pooch.api.dto.PaymentIntentDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.utils.MathUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.net.RequestOptions;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripePaymentIntentServiceImp implements StripePaymentIntentService {

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets                       stripeSecrets;

    @Autowired
    private StripePaymentIntentValidatorService stripePaymentIntentValidatorService;

    @Override
    public PaymentIntent getById(String paymentIntentId) {

        Stripe.apiKey = stripeSecrets.getSecretKey();

        PaymentIntent paymentIntent = null;

        try {
            paymentIntent = PaymentIntent.retrieve(paymentIntentId);
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
    public PaymentIntent create(String accountId, BigDecimal amount) {
        Stripe.apiKey = stripeSecrets.getSecretKey();

        log.info("create({}, {})", accountId, amount);
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("payment_method_types", paymentMethodTypes);
        params.put("amount", amount.longValue() * 100);
        params.put("currency", "usd");
        params.put("application_fee_amount", 123);
        
        Map<String, Object> transferDataParams = new HashMap<>();
        transferDataParams.put("destination", accountId);
        params.put("transfer_data", transferDataParams);

        PaymentIntent paymentIntent = null;

        try {
            paymentIntent = PaymentIntent.create(params);
            System.out.println(paymentIntent.toJson());
        } catch (StripeException e) {
            log.warn("StripeException, msg={}", e.getMessage());
            throw new ApiException(e.getMessage(), "StripeException, msg="+e.getMessage());
        }

        return paymentIntent;
    }

    @Override
    public PaymentIntentDTO processNewPaymentIntent(PaymentIntentCreateDTO paymentIntentCreateDTO) {
        Groomer groomer = stripePaymentIntentValidatorService.validateProcessNewPaymentIntent(paymentIntentCreateDTO);
        // remember to remove acct_1KtIhI2ELI6szoyV and poochfolio is ready
        // use groomer's connected account id
        
        String acountId = "acct_1Kvuna2E8yUfNXhV";// Lucy Mullins
        PaymentIntent paymentIntent = create(acountId, BigDecimal.valueOf(paymentIntentCreateDTO.getAmount()));

        PaymentIntentDTO paymentIntentDTO = PaymentIntentDTO.builder()
                .amount((double)(paymentIntent.getAmount() / 100))
                .clientSecret(paymentIntent.getClientSecret())
                .groomerUuid(groomer.getUuid())
                .id(paymentIntent.getId())
                .build();
        
        return paymentIntentDTO;
    }

}
