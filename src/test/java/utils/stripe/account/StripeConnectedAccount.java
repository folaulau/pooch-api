package utils.stripe.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.pooch.api.config.LocalAwsConfig;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class StripeConnectedAccount {

    static TestEntityGeneratorService generator          = new TestEntityGeneratorService();

    private static String             stripeApiSecretKey = "sk_test_51KaS6tCRM62QoG6s4BbiiVE9zm0nO1144oWkKhKyFiPZAUuXw3OQe6X24b7FYG7ha7WRurseQQiOLlyoOPOwelUt00pS3kP3rZ";

    public static void main(String[] args) {
        LocalAwsConfig awsConfig = new LocalAwsConfig();

        uploadToPublicFolder();

    }

    public static void uploadToPublicFolder() {

        Stripe.apiKey = stripeApiSecretKey;

        Map<String, Object> cardPayments = new HashMap<>();
        cardPayments.put("requested", true);

        Map<String, Object> transfers = new HashMap<>();
        transfers.put("requested", true);

        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("card_payments", cardPayments);
        capabilities.put("transfers", transfers);

        Groomer groomer = generator.getGroomer();

        Map<String, Object> company = new HashMap<>();
        company.put("name", groomer.getBusinessName());
        company.put("phone", groomer.getPhoneNumber());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("env", "local");

        // terms of service
        Map<String, Object> termsOfService = new HashMap<>();
        termsOfService.put("date", Instant.now().getEpochSecond());
        termsOfService.put("ip", "174.52.151.8");
        termsOfService.put("user_agent", "Chrome/{Chrome Rev} Mobile Safari/{WebKit Rev}");

        Map<String, Object> params = new HashMap<>();
        params.put("type", "custom");
        params.put("country", "US");
        params.put("business_type", "company");
        params.put("company", company);
        params.put("metadata", metadata);
        params.put("tos_acceptance", termsOfService);
        params.put("email", RandomGeneratorUtils.getRandomEmail());
        params.put("capabilities", capabilities);
        Account account = null;
        try {
            account = Account.create(params);

            System.out.println(account.toJson());
        } catch (StripeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        ArrayList paymentMethodTypes = new ArrayList();
        paymentMethodTypes.add("card");
        params = new HashMap<>();
        params.put("payment_method_types", paymentMethodTypes);
        params.put("amount", 1000);
        params.put("currency", "usd");

        RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(account.getId()).build();

        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params, requestOptions);
            System.out.println(paymentIntent.toJson());
        } catch (StripeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
