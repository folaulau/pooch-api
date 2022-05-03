package com.pooch.api.stripe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.PaymentIntentCreateDTO;
import com.pooch.api.dto.PaymentIntentDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.stripe.paymentintent.StripePaymentIntentService;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
class StripeConnectedAccountTests extends IntegrationTestConfiguration {

    @Autowired
    private MockMvc                    mockMvc;

    @Resource
    private WebApplicationContext      webApplicationContext;

    @Autowired
    private ObjectMapper               objectMapper;

    @Autowired
    private Filter                     springSecurityFilterChain;

    @MockBean
    private JwtTokenService            jwtTokenService;

    @Captor
    private ArgumentCaptor<String>     tokenCaptor;

    private String                     PARENT_TOKEN = "PARENT_TOKEN";
    private String                     PARENT_UUID  = "PARENT_UUID";

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets              stripeSecrets;

    @Autowired
    private TestEntityGeneratorService testEntityGeneratorService;

    @Autowired
    private StripePaymentIntentService stripePaymentIntentService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        JwtPayload groomerJwtPayload = new JwtPayload();
        groomerJwtPayload.setUuid(PARENT_UUID);
        groomerJwtPayload.setRole(Authority.parent.name());

        Mockito.when(jwtTokenService.getPayloadByToken(PARENT_TOKEN)).thenReturn(groomerJwtPayload);
    }

    @Test
    void createConnectedAccountAndPaymentIntent() {
        Stripe.apiKey = stripeSecrets.getSecretKey();

        Map<String, Object> cardPayments = new HashMap<>();
        cardPayments.put("requested", true);

        Map<String, Object> transfers = new HashMap<>();
        transfers.put("requested", true);

        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("card_payments", cardPayments);
        capabilities.put("transfers", transfers);

        Groomer groomer = testEntityGeneratorService.getGroomer();

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

        assertThat(account).isNotNull();

        PaymentIntent paymentIntent = stripePaymentIntentService.create(account.getId(), BigDecimal.valueOf(50));

        assertThat(paymentIntent).isNotNull();
    }

    @Test
    void itShouldProcessNewPaymentIntent() throws Exception {

        Groomer activeGroomer = testEntityGeneratorService.getActiveDBGroomer();

        // @formatter:on
        PaymentIntentCreateDTO paymentIntentCreateDTO = PaymentIntentCreateDTO.builder().amount(245D).groomerUuid(activeGroomer.getUuid()).savePaymentMethodForFutureUse(true).build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(paymentIntentCreateDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        // @formatter:off
        String contentAsString = result.getResponse().getContentAsString();

        PaymentIntentDTO paymentIntentDTO = objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});

        assertThat(paymentIntentDTO).isNotNull();
        assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
        assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
        assertThat(paymentIntentDTO.getAmount()).isNotNull().isEqualTo(245);
        assertThat(paymentIntentDTO.getId()).isNotNull();
    }

}