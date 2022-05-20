package com.pooch.api.library.stripe.paymentmethod;

import org.elasticsearch.core.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentMethodAttachParams;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripePaymentMethodServiceImp implements StripePaymentMethodService {

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets stripeSecrets;

    @Value("${spring.profiles.active}")
    private String        env;

    @Override
    public PaymentMethod getById(String id) {
        PaymentMethod paymentMethod = null;
        try {
            paymentMethod = PaymentMethod.retrieve(id);
        } catch (StripeException e) {
            log.warn("StripeException - getById, msg={}", e.getMessage());
        }

        return paymentMethod;
    }

    @Override
    public PaymentMethod attachToCustomer(String paymentMethodId, String customerId) {
        PaymentMethod paymentMethod = null;

        try {
            paymentMethod = PaymentMethod.retrieve(paymentMethodId);

            paymentMethod.attach(PaymentMethodAttachParams.builder()
                    .setCustomer(customerId).build());

        } catch (Exception e) {
            log.warn("StripeException - attachToCustomer, msg={}", e.getMessage());
        }

        return paymentMethod;
    }

    @Override
    public PaymentMethod detachFromCustomer(String paymentMethodId) {
        PaymentMethod paymentMethod = null;

        try {
            paymentMethod = PaymentMethod.retrieve(paymentMethodId);

            paymentMethod.detach();

        } catch (Exception e) {
            log.warn("StripeException - detachFromCustomer, msg={}", e.getMessage());
        }

        return paymentMethod;
    }
}
