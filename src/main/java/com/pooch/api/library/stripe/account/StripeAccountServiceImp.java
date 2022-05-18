package com.pooch.api.library.stripe.account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.net.RequestOptions;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.AccountLinkCreateParams.Type;
import com.stripe.param.AccountRetrieveParams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StripeAccountServiceImp implements StripeAccountService {

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets stripeSecrets;

    @Value("${spring.profiles.active}")
    private String        env;

    @Value("${poochfolio.web.app.uri}")
    private String        poochfolioUrl;

    @Override
    public Account create(Groomer groomer) {
        Stripe.apiKey = stripeSecrets.getSecretKey();

        Address address = groomer.getAddress();
        
        // @formatter:off

        Map<String,String> metadata = new HashMap<>();
        metadata.put("env", env);
        metadata.put("id", groomer.getId()+"");
        metadata.put("uuid", groomer.getUuid());
        
        AccountCreateParams params =
                AccountCreateParams
                  .builder()
                  .setEmail(groomer.getEmail())
                  .setMetadata(metadata)
                  .setCountry("US")
                  .setType(AccountCreateParams.Type.EXPRESS)
                  .setCapabilities(
                    AccountCreateParams.Capabilities
                      .builder()
                      .setCardPayments(
                        AccountCreateParams.Capabilities.CardPayments
                          .builder()
                          .setRequested(true)
                          .build()
                      )
                      .setTransfers(
                        AccountCreateParams.Capabilities.Transfers
                          .builder()
                          .setRequested(true)
                          .build()
                      )
                      .build()
                  )
                  .setBusinessType(AccountCreateParams.BusinessType.COMPANY)
                  .setCompany(com.stripe.param.AccountCreateParams.Company.builder()
                          .setPhone(groomer.getPhoneNumber()+"")
                          .setName(groomer.getBusinessName())
                          .setAddress(com.stripe.param.AccountCreateParams.Company.Address.builder()
                                  .setCity(address.getCity())
                                  .setCountry("US")
                                  .setState(address.getState())
                                  .setPostalCode(address.getZipcode())
                                  .setLine1(address.getStreet())
                                  .build())
                          
                          .build())
                  .setBusinessProfile(
                          AccountCreateParams.BusinessProfile
                          .builder()
                          .setName(groomer.getBusinessName())
                          .setProductDescription("Pooch or Dog Care")
                          .build()
                  )
                  .build();
        // @formatter:on

        com.stripe.model.Account account = null;

        try {
            account = com.stripe.model.Account.create(params);
            System.out.println("account=" + account.toJson());
        } catch (StripeException e) {
            log.warn("StripeException, msg={}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return account;
    }

    @Override
    public Account getById(String id) {
        Stripe.apiKey = stripeSecrets.getSecretKey();

        com.stripe.model.Account account = null;
        RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(id).build();
        try {
            account = com.stripe.model.Account.retrieve(AccountRetrieveParams.builder().build(), requestOptions);
            System.out.println("account=" + account.toJson());
        } catch (StripeException e) {
            log.warn("StripeException, msg={}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return account;
    }

    @Override
    public AccountLink getByAccountId(String accountId, Type type, String host) {
        Stripe.apiKey = stripeSecrets.getSecretKey();

        StringBuilder url = new StringBuilder();

        /**
         * host
         */
        if (host != null && host.trim().length() > 0) {
            url.append(host);
        } else {
            url.append(this.poochfolioUrl);
        }

        /**
         * path
         */
        String refreshUrl = url.toString() + "/dashboard/payment-method";
        String returnUrl = url.toString() + "/dashboard/payment-method?from=stripe";

        AccountLinkCreateParams params = AccountLinkCreateParams.builder().setAccount(accountId).setRefreshUrl(refreshUrl).setReturnUrl(returnUrl).setType(type).build();

        AccountLink accountLink = null;
        try {
            accountLink = AccountLink.create(params);

            System.out.println("accountLink=" + accountLink.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountLink;
    }

}
