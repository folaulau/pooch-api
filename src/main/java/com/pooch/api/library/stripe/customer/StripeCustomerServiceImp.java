package com.pooch.api.library.stripe.customer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.stripe.StripeMetadataService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import lombok.extern.slf4j.Slf4j;

/**
 * Parent Customer is created when paymentIntent is requested, refer to
 */
@Service
@Slf4j
public class StripeCustomerServiceImp implements StripeCustomerService {

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets stripeSecrets;

    @Value("${spring.profiles.active}")
    private String        env;

    @Override
    public Customer createPlaceHolderCustomer() {
        Stripe.apiKey = stripeSecrets.getSecretKey();

        //@formatter:off
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setMetadata(Map.of(StripeMetadataService.env,env))
                .setName("pooch parent").build();
        //@formatter:on

        Customer customer = null;

        try {
            customer = Customer.create(customerParams);
        } catch (Exception e) {
            log.warn("StripeException - createQuestPaymentIntent, customer, msg={}", e.getMessage());
        }

        return customer;
    }

    @Override
    public Customer getById(String id) {
        Stripe.apiKey = stripeSecrets.getSecretKey();

        Customer customer = null;

        try {
            customer = Customer.retrieve(id);
        } catch (Exception e) {
            log.warn("StripeException - createQuestPaymentIntent, customer, msg={}", e.getMessage());
        }

        return customer;
    }

    @Override
    public Customer updateParentDetails(Parent parent) {

        String id = parent.getStripeCustomerId();

        if (id == null) {
            log.info("updateParentDetails of parentId={}, status={} but stripeCustomerId is null", parent.getId(), parent.getStatus());
            return null;
        }

        Stripe.apiKey = stripeSecrets.getSecretKey();

        Customer customer = null;

        // @formatter:off
 
        CustomerUpdateParams.Builder builder = CustomerUpdateParams.builder()
                .putMetadata(StripeMetadataService.env, env)
                .setName(parent.getFullName())
                .setEmail(parent.getEmail());

        // @formatter:on

        if (parent.getPhoneNumber() != null) {
            builder.setPhone(parent.getPhoneNumber() + "");
        }

        Address address = parent.getAddress();

        if (address != null) {
            builder.setAddress(CustomerUpdateParams.Address.builder()
                    .setCity(address.getCity())
                    .setCountry(address.getCountry())
                    .setLine1(address.getStreet())
                    .setPostalCode(address.getZipcode())
                    .setState(address.getState())
                    .build());
        }

        CustomerUpdateParams updateParams = builder.build();

        try {
            customer = Customer.retrieve(parent.getStripeCustomerId());

            customer = customer.update(updateParams);

        } catch (StripeException e) {
            log.warn("StripeException - updateParentDetails, msg={}, userMessage={}, stripeErrorMessage={}", e.getLocalizedMessage(), e.getUserMessage(), e.getStripeError().getMessage());
        }

        return customer;
    }

}
