package com.pooch.api.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.BankAccount;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentMethodCreateParams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StripeTokenService {

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets      stripeSecrets;

    private final List<String> creditCards = Arrays.asList("4242424242424242", "4000056655665556", "5555555555554444", "2223003122003222", "5200828282828210", "5105105105105100", "378282246310005",
            "371449635398431", "6011111111111117", "6011000990139424", "30569309025904", "38520000023237", "3566002020360505", "6200000000000005");

    private final List<String> firstnames  = Arrays.asList("Taylor", "Bailey", "Josiah", "Clay", "Archie", "Nicolas", "Lucero", "Davin", "Odell", "Princeton", "Garcia", "Amando", "Ernesto", "Lorena",
            "Maya", "Osborn", "Sancho", "Ruben", "Nick", "Jorge", "Dominique", "Elvis", "Kendra", "Cristian", "Jayden", "Lisa", "Regan", "Jabari", "Emma", "Ryann", "Luna", "Janelle", "Kelsey",
            "Briana", "Richard", "Gracelyn", "Willie", "Sidney", "Sean", "Skyler", "Rolando", "Stephanie", "Terrell", "Karla", "Javier", "Kimberly", "Macy", "Irene");

    @Async
    public Future<StripeToken> generateTokenByCardNumbers(String name, String numbers) {
        log.debug("generating a test token from stripe...");

        Stripe.apiKey = stripeSecrets.getSecretKey();

        Map<String, Object> tokenParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();
        int thisYear = LocalDate.now().getYear();
        int thisMonth = LocalDate.now().getMonthValue();

        cardParams.put("number", numbers);
        cardParams.put("exp_month", thisMonth);
        cardParams.put("exp_year", RandomGeneratorUtils.getIntegerWithin(thisYear + 1, thisYear + 10));
        cardParams.put("cvc", "314");

        if (name == null || name.length() <= 0) {
            name = this.firstnames.get(RandomGeneratorUtils.getIntegerWithin(0, firstnames.size() - 1));
        }
        cardParams.put("name", name);

        tokenParams.put("card", cardParams);

        Token token = null;
        try {

            long start = System.currentTimeMillis();
            token = Token.create(tokenParams);
            long end = System.currentTimeMillis();
            long restCallTimeTaken = end - start;
            log.debug("Stripe api call to create token took, {} milliseconds -> {} seconds", restCallTimeTaken, (restCallTimeTaken / 1000));
        } catch (StripeException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        // log.debug("Token: {}",token.getId());
        // log.debug("Token body: {}",token.toJson());
        return new AsyncResult<>(new StripeToken(token.getId(), StripeToken.VALID));
    }

    public StripeToken getCreditCardTokenFromStripe(String name) {
        log.debug("generating a test token from stripe...");

        Stripe.apiKey = stripeSecrets.getSecretKey();

        Map<String, Object> tokenParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();
        int thisYear = LocalDate.now().getYear();
        int thisMonth = LocalDate.now().getMonthValue();

        cardParams.put("number", creditCards.get(RandomGeneratorUtils.getIntegerWithin(0, creditCards.size() - 1)));
        cardParams.put("exp_month", thisMonth);
        cardParams.put("exp_year", RandomGeneratorUtils.getIntegerWithin(thisYear + 1, thisYear + 10));
        cardParams.put("cvc", "314");

        if (name == null || name.length() <= 0) {
            name = this.firstnames.get(RandomGeneratorUtils.getIntegerWithin(0, firstnames.size() - 1));
        }
        cardParams.put("name", name);

        tokenParams.put("card", cardParams);

        Token token = null;
        try {

            long start = System.currentTimeMillis();
            token = Token.create(tokenParams);
            long end = System.currentTimeMillis();
            long restCallTimeTaken = end - start;
            log.info("Stripe api call to create token took, {} milliseconds -> {} seconds", restCallTimeTaken, (restCallTimeTaken / 1000));
        } catch (StripeException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        // log.debug("Token: {}",token.getId());
        log.info("Token body: {}", token.toJson());
        return new StripeToken(token.getId(), token.getCard().getId());
    }

    @Async
    public Future<StripeToken> getDeclinedCardTokenFromStripe(String name) {
        log.debug("generating a test token from stripe...");

        Stripe.apiKey = stripeSecrets.getSecretKey();

        String expiredCard = "4000000000000002";

        Map<String, Object> tokenParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();
        int thisYear = LocalDate.now().getYear();
        int thisMonth = LocalDate.now().getMonthValue();

        cardParams.put("number", expiredCard);
        cardParams.put("exp_month", thisMonth);
        cardParams.put("exp_year", RandomGeneratorUtils.getIntegerWithin(thisYear + 1, thisYear + 10));
        cardParams.put("cvc", "314");
        if (name == null || name.length() <= 0) {
            name = this.firstnames.get(RandomGeneratorUtils.getIntegerWithin(0, firstnames.size() - 1));
        }
        cardParams.put("name", name);

        tokenParams.put("card", cardParams);

        Token token = null;
        try {

            long start = System.currentTimeMillis();
            token = Token.create(tokenParams);
            long end = System.currentTimeMillis();
            long restCallTimeTaken = end - start;
            log.debug("Stripe api call to create token took, {} milliseconds -> {} seconds", restCallTimeTaken, (restCallTimeTaken / 1000));
        } catch (StripeException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        log.debug("Token: {}", token.getId());
        // log.debug("Token body: {}",token);
        return new AsyncResult<>(new StripeToken(token.getId(), StripeToken.VALID));
    }

    @Async
    public Future<StripeToken> getDebitCardTokenFromStripe(String name) {
        log.debug("generating a test token from stripe...");

        Stripe.apiKey = stripeSecrets.getSecretKey();

        Map<String, Object> tokenParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();
        int thisYear = LocalDate.now().getYear();
        int thisMonth = LocalDate.now().getMonthValue();

        cardParams.put("number", creditCards.get(RandomGeneratorUtils.getIntegerWithin(0, creditCards.size() - 1)));
        cardParams.put("exp_month", thisMonth);
        cardParams.put("exp_year", RandomGeneratorUtils.getIntegerWithin(thisYear + 1, thisYear + 10));
        cardParams.put("cvc", "314");
        if (name == null || name.length() <= 0) {
            name = this.firstnames.get(RandomGeneratorUtils.getIntegerWithin(0, firstnames.size() - 1));
        }
        cardParams.put("name", name);

        tokenParams.put("card", cardParams);

        Token token = null;
        try {

            long start = System.currentTimeMillis();
            token = Token.create(tokenParams);
            long end = System.currentTimeMillis();
            long restCallTimeTaken = end - start;
            log.debug("Stripe api call to create token took, {} milliseconds -> {} seconds", restCallTimeTaken, (restCallTimeTaken / 1000));
        } catch (StripeException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        log.debug("Token: {}", token.getId());
        // log.debug("Token body: {}",token);
        return new AsyncResult<>(new StripeToken(token.getId(), StripeToken.VALID));
    }

    @Async
    public Future<Boolean> validateStripeToken(String stripeToken) {
        log.debug("validating token from stripe...");

        Stripe.apiKey = stripeSecrets.getSecretKey();

        Token token = null;
        try {
            long start = System.currentTimeMillis();
            token = Token.retrieve(stripeToken);
            long end = System.currentTimeMillis();
            long restCallTimeTaken = end - start;
            log.debug("Stripe api call to retrieve token took, {} milliseconds -> {} seconds", restCallTimeTaken, (restCallTimeTaken / 1000));
        } catch (StripeException e) {
            log.error(e.getLocalizedMessage(), e);
            log.error("Token could not be retrieve from Stripe, msg: {}", e.getLocalizedMessage());
            return new AsyncResult<>(false);
        }
        log.debug("Token: {}, used: {}", token.getId(), token.getUsed());
        return (token.getUsed()) ? new AsyncResult<>(false) : new AsyncResult<>(true);
    }

    public Token retrieve(String tokenStr) {
        log.debug("retrieving token details from stripe...");
        Stripe.apiKey = stripeSecrets.getSecretKey();
        Token token = null;
        try {
            token = Token.retrieve(tokenStr);
        } catch (StripeException e) {
            log.error("Token could not be retrieve from Stripe, msg: {}", e.getLocalizedMessage());
        }
        return token;
    }

    @Async
    public Future<StripeToken> generateTokenByBankNumbers(String accountNumber, String routingNumber) {
        log.debug("generating a test token from stripe...");

        Stripe.apiKey = stripeSecrets.getSecretKey();

        Map<String, Object> tokenParams = new HashMap<>();
        Map<String, Object> bankParams = new HashMap<>();
        bankParams.put("account_number", accountNumber);
        bankParams.put("country", "US");
        bankParams.put("currency", "usd");
        bankParams.put("routing_number", routingNumber);
        bankParams.put("account_holder_name", "Sidecar test");
        bankParams.put("account_holder_type", "individual");

        tokenParams.put("bank_account", bankParams);

        Token token = null;
        try {

            long start = System.currentTimeMillis();
            token = Token.create(tokenParams);
            long end = System.currentTimeMillis();
            long restCallTimeTaken = end - start;
            log.debug("Stripe api call to create token took, {} milliseconds -> {} seconds", restCallTimeTaken, (restCallTimeTaken / 1000));
        } catch (StripeException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return new AsyncResult<>(new StripeToken(token.getId(), StripeToken.VALID));
    }

    /**
     * for testing only
     * 
     * @param sourceId
     */
    @Async
    public void verify(String customerId, String paymentId) {
        log.debug("generating a test token from stripe...");

        Stripe.apiKey = stripeSecrets.getSecretKey();

        try {
            Customer customer = Customer.retrieve(customerId);

            BankAccount bankAccount = (BankAccount) customer.getSources().retrieve(paymentId);

            List<Object> amounts = new ArrayList<>();
            amounts.add(32);
            amounts.add(45);
            Map<String, Object> params = new HashMap<>();
            params.put("amounts", amounts);

            bankAccount.verify(params);
        } catch (StripeException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public PaymentMethod getCardPaymentMethod() {
        Stripe.apiKey = stripeSecrets.getSecretKey();

        PaymentMethodCreateParams.Builder builder = PaymentMethodCreateParams.builder();

        int currentYr = LocalDate.now().getYear() + 1;
        int futureYr = currentYr + 20;
        
        builder.setType(PaymentMethodCreateParams.Type.CARD);
        builder.setCard(PaymentMethodCreateParams.CardDetails.builder()
                .setCvc(RandomGeneratorUtils.getLongWithin(100, 999)+"")
                .setExpMonth(RandomGeneratorUtils.getLongWithin(1, 11))
                .setNumber(creditCards.get(RandomGeneratorUtils.getIntegerWithin(0, creditCards.size() - 1)))
                .setExpYear(RandomGeneratorUtils.getLongWithin(currentYr, futureYr))
                .build());

        PaymentMethodCreateParams createParams = builder.build();
        PaymentMethod paymentMethod = null;
        
        try {
            paymentMethod = PaymentMethod.create(createParams);
        } catch (StripeException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        return paymentMethod;
    }
}
