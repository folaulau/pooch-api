package com.pooch.api.stripe;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.PaymentIntentDTO;
import com.pooch.api.dto.PaymentIntentQuestCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.aws.secretsmanager.XApiKey;
import com.pooch.api.library.stripe.paymentintent.StripePaymentIntentService;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.MathUtils;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
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

    @Autowired
    @Qualifier(value = "xApiKey")
    private XApiKey                    xApiKey;

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

    @Value("${booking.fee:10}")
    private Double                     bookingFee;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        JwtPayload groomerJwtPayload = new JwtPayload();
        groomerJwtPayload.setUuid(PARENT_UUID);
        groomerJwtPayload.setRole(Authority.parent.name());

        Mockito.when(jwtTokenService.getPayloadByToken(PARENT_TOKEN)).thenReturn(groomerJwtPayload);
    }

    @Disabled
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

        // PaymentIntent paymentIntent = stripePaymentIntentService.create(account.getId(), BigDecimal.valueOf(50));
        //
        // assertThat(paymentIntent).isNotNull();
    }

    @Test
    void itShouldCreateQuestPaymentIntent() throws Exception {

        Groomer activeGroomer = testEntityGeneratorService.getActiveDBGroomer();

        double bookingCost = 245D;

        // @formatter:off
        PaymentIntentQuestCreateDTO paymentIntentCreateDTO = PaymentIntentQuestCreateDTO.builder()
                .amount(bookingCost)
                .groomerUuid(activeGroomer.getUuid())
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent")
                .header("x-api-key", xApiKey.getMobileXApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(paymentIntentCreateDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        // @formatter:on
        String contentAsString = result.getResponse().getContentAsString();

        PaymentIntentDTO paymentIntentDTO = objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});

        double chargeAmount = bookingCost + bookingFee;

        // 2.9% of chargeAmount + 30 cents
        double stripeFee = BigDecimal.valueOf(2.9)
                .divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(chargeAmount))
                .add(BigDecimal.valueOf(0.3))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();

        double totalCharge = bookingCost + bookingFee + stripeFee;

        assertThat(paymentIntentDTO).isNotNull();
        assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
        assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
        assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
        assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
        assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
        assertThat(paymentIntentDTO.getTotalAmount()).isNotNull().isEqualTo(totalCharge);
        assertThat(paymentIntentDTO.getId()).isNotNull();
    }

    @Test
    void itShouldUpdateQuestPaymentIntent() throws Exception {

        Groomer activeGroomer = testEntityGeneratorService.getActiveDBGroomer();

        double bookingCost = 245D;

        // @formatter:off
        PaymentIntentQuestCreateDTO paymentIntentCreateDTO = PaymentIntentQuestCreateDTO.builder()
                .amount(bookingCost)
                .groomerUuid(activeGroomer.getUuid())
                .build();

        PaymentIntentDTO paymentIntentDTO = stripePaymentIntentService.createQuestPaymentIntent(paymentIntentCreateDTO);

        double chargeAmount = bookingCost + bookingFee;

        // 2.9% of chargeAmount + 30 cents
        double stripeFee = BigDecimal.valueOf(2.9)
                .divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(chargeAmount))
                .add(BigDecimal.valueOf(0.3))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();

        double totalCharge = bookingCost + bookingFee + stripeFee;
        
        assertThat(paymentIntentDTO).isNotNull();
        assertThat(paymentIntentDTO.getId()).isNotNull();
        assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
        assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
        assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
        assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
        assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
        assertThat(paymentIntentDTO.getTotalAmount()).isNotNull().isEqualTo(totalCharge);

        bookingCost = (bookingCost+20);
        
        PaymentIntentQuestCreateDTO paymentIntentQuestUpdateDTO = PaymentIntentQuestCreateDTO.builder()
                .paymentIntentId(paymentIntentDTO.getId())
                .groomerUuid(activeGroomer.getUuid())
                .amount(bookingCost)
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent")
                .header("x-api-key", xApiKey.getMobileXApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(paymentIntentQuestUpdateDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        // @formatter:on
        String contentAsString = result.getResponse().getContentAsString();

        paymentIntentDTO = objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});

        chargeAmount = bookingCost + bookingFee;

        // 2.9% of chargeAmount + 30 cents
        stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(chargeAmount)).add(BigDecimal.valueOf(0.3)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        totalCharge = bookingCost + bookingFee + stripeFee;

        assertThat(paymentIntentDTO).isNotNull();
        assertThat(paymentIntentDTO.getId()).isNotNull();
        assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
        assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
        assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
        assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
        assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
        assertThat(paymentIntentDTO.getTotalAmount()).isNotNull().isEqualTo(totalCharge);
    }

}
