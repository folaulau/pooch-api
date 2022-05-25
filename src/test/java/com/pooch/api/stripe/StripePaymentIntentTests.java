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
import com.pooch.api.dto.PaymentIntentParentCreateDTO;
import com.pooch.api.dto.PaymentIntentQuestCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.paymentmethod.PaymentMethod;
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
class StripePaymentIntentTests extends IntegrationTestConfiguration {

  @Autowired
  private MockMvc mockMvc;

  @Resource
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Filter springSecurityFilterChain;

  @MockBean
  private JwtTokenService jwtTokenService;

  @Captor
  private ArgumentCaptor<String> tokenCaptor;

  private String PARENT_TOKEN = "PARENT_TOKEN";
  private String PARENT_UUID = "PARENT_UUID";

  @Autowired
  @Qualifier(value = "stripeSecrets")
  private StripeSecrets stripeSecrets;

  @Autowired
  private TestEntityGeneratorService testEntityGeneratorService;

  @Autowired
  private StripePaymentIntentService stripePaymentIntentService;

  @Value("${booking.fee:10}")
  private Double bookingFee;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilters(springSecurityFilterChain).build();

    JwtPayload groomerJwtPayload = new JwtPayload();
    groomerJwtPayload.setUuid(PARENT_UUID);
    groomerJwtPayload.setRole(Authority.parent.name());

    Mockito.when(jwtTokenService.getPayloadByToken(PARENT_TOKEN)).thenReturn(groomerJwtPayload);
  }

//  @Test
//  void itShouldCreateQuestPaymentIntent_with_stripe_ready_groomer() throws Exception {
//
//    Groomer activeGroomer = testEntityGeneratorService.getStripeReadyDBGroomer();
//
//    double bookingCost = 245D;
//
//    // @formatter:off
//        PaymentIntentQuestCreateDTO paymentIntentCreateDTO = PaymentIntentQuestCreateDTO.builder()
//                .amount(bookingCost)
//                .savePaymentMethodForFutureUse(true)
//                .groomerUuid(activeGroomer.getUuid())
//                .build();
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent/booking")
//                .header("x-api-key", xApiKey.getMobileXApiKey())
//                .accept(MediaType.APPLICATION_JSON)
//                .characterEncoding("utf-8")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(ObjectUtils.toJson(paymentIntentCreateDTO));
//
//        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//
//        // @formatter:on
//    String contentAsString = result.getResponse().getContentAsString();
//
//    PaymentIntentDTO paymentIntentDTO =
//        objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});
//
//    double chargeAmount = bookingCost;
//
//    // 2.9% of chargeAmount + 30 cents
//    double stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
//        .multiply(BigDecimal.valueOf(chargeAmount)).add(BigDecimal.valueOf(0.3))
//        .setScale(2, RoundingMode.CEILING).doubleValue();
//
//    double totalCharge = bookingCost + bookingFee + stripeFee;
//
//    assertThat(paymentIntentDTO).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
//    assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
//    assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
//    assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
//    assertThat(paymentIntentDTO.getTotalChargeAtBooking()).isNotNull().isEqualTo(totalCharge);
//    assertThat(paymentIntentDTO.getTotalChargeAtDropOff()).isNotNull().isEqualTo(0);
//    assertThat(paymentIntentDTO.getSetupFutureUsage()).isNotNull().isEqualTo("off_session");
//    assertThat(paymentIntentDTO.getId()).isNotNull();
//  }
//
//  @Test
//  void itShouldCreateQuestPaymentIntent_with_stripe_not_ready_groomer() throws Exception {
//
//    Groomer activeGroomer = testEntityGeneratorService.getActiveDBGroomer();
//
//    double bookingCost = 245D;
//
//    // @formatter:off
//        PaymentIntentQuestCreateDTO paymentIntentCreateDTO = PaymentIntentQuestCreateDTO.builder()
//                .amount(bookingCost)
//                .savePaymentMethodForFutureUse(true)
//                .groomerUuid(activeGroomer.getUuid())
//                .build();
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent/booking")
//                .header("x-api-key", xApiKey.getMobileXApiKey())
//                .accept(MediaType.APPLICATION_JSON)
//                .characterEncoding("utf-8")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(ObjectUtils.toJson(paymentIntentCreateDTO));
//
//        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//
//        // @formatter:on
//    String contentAsString = result.getResponse().getContentAsString();
//
//    PaymentIntentDTO paymentIntentDTO =
//        objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});
//
//    // 2.9% of chargeAmount + 30 cents
//    double stripeFee = 0;
//
//    double totalCharge = bookingFee;
//
//    assertThat(paymentIntentDTO).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
//    assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
//    assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
//    assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
//    assertThat(paymentIntentDTO.getTotalChargeAtBooking()).isNotNull().isEqualTo(totalCharge);
//    assertThat(paymentIntentDTO.getTotalChargeAtDropOff()).isNotNull().isEqualTo(bookingCost);
//    assertThat(paymentIntentDTO.getSetupFutureUsage()).isNotNull().isEqualTo("off_session");
//    assertThat(paymentIntentDTO.getId()).isNotNull();
//  }

//  @Test
//  void itShouldUpdateQuestPaymentIntent_with_stripe_ready_groomer() throws Exception {
//
//    Groomer activeGroomer = testEntityGeneratorService.getStripeReadyDBGroomer();
//
//    double bookingCost = 245D;
//
//    // @formatter:off
//        PaymentIntentQuestCreateDTO paymentIntentCreateDTO = PaymentIntentQuestCreateDTO.builder()
//                .amount(bookingCost)
//                .groomerUuid(activeGroomer.getUuid())
//                .savePaymentMethodForFutureUse(true)
//                .build();
//
//        PaymentIntentDTO paymentIntentDTO = stripePaymentIntentService.createQuestPaymentIntent(paymentIntentCreateDTO);
//
//        // 2.9% of chargeAmount + 30 cents
//        double stripeFee = BigDecimal.valueOf(2.9)
//                .divide(BigDecimal.valueOf(100))
//                .multiply(BigDecimal.valueOf(bookingCost))
//                .add(BigDecimal.valueOf(0.3))
//                .setScale(2, RoundingMode.CEILING)
//                .doubleValue();
//
//        double totalCharge = bookingCost + bookingFee + stripeFee;
//        
//        assertThat(paymentIntentDTO).isNotNull();
//        assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
//        assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
//        assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
//        assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
//        assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
//        assertThat(paymentIntentDTO.getTotalChargeAtBooking()).isNotNull().isEqualTo(totalCharge);
//        assertThat(paymentIntentDTO.getTotalChargeAtDropOff()).isNotNull().isEqualTo(0);
//        assertThat(paymentIntentDTO.getSetupFutureUsage()).isNotNull().isEqualTo("off_session");
//        assertThat(paymentIntentDTO.getId()).isNotNull();
//
//        bookingCost = (bookingCost+20);
//        
//        PaymentIntentQuestCreateDTO paymentIntentQuestUpdateDTO = PaymentIntentQuestCreateDTO.builder()
//                .paymentIntentId(paymentIntentDTO.getId())
//                .groomerUuid(activeGroomer.getUuid())
//                .amount(bookingCost)
//                .build();
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent/booking")
//                .header("x-api-key", xApiKey.getMobileXApiKey())
//                .accept(MediaType.APPLICATION_JSON)
//                .characterEncoding("utf-8")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(ObjectUtils.toJson(paymentIntentQuestUpdateDTO));
//
//        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//
//        // @formatter:on
//    String contentAsString = result.getResponse().getContentAsString();
//
//    paymentIntentDTO =
//        objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});
//
//    // 2.9% of chargeAmount + 30 cents
//    stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
//        .multiply(BigDecimal.valueOf(bookingCost)).add(BigDecimal.valueOf(0.3))
//        .setScale(2, RoundingMode.CEILING).doubleValue();
//
//    totalCharge = bookingCost + bookingFee + stripeFee;
//
//    assertThat(paymentIntentDTO).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
//    assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
//    assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
//    assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
//    assertThat(paymentIntentDTO.getTotalChargeAtBooking()).isNotNull().isEqualTo(totalCharge);
//    assertThat(paymentIntentDTO.getTotalChargeAtDropOff()).isNotNull().isEqualTo(0);
//    assertThat(paymentIntentDTO.getSetupFutureUsage()).isNotNull().isEqualTo("off_session");
//    assertThat(paymentIntentDTO.getId()).isNotNull();
//  }

//  @Test
//  void itShouldUpdateQuestPaymentIntent_with_stripe_not_ready_groomer() throws Exception {
//
//    Groomer activeGroomer = testEntityGeneratorService.getActiveDBGroomer();
//
//    double bookingCost = 245D;
//
//    // @formatter:off
//        PaymentIntentQuestCreateDTO paymentIntentCreateDTO = PaymentIntentQuestCreateDTO.builder()
//                .amount(bookingCost)
//                .savePaymentMethodForFutureUse(true)
//                .groomerUuid(activeGroomer.getUuid())
//                .build();
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent/booking")
//                .header("x-api-key", xApiKey.getMobileXApiKey())
//                .accept(MediaType.APPLICATION_JSON)
//                .characterEncoding("utf-8")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(ObjectUtils.toJson(paymentIntentCreateDTO));
//
//        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//
//        // @formatter:on
//    String contentAsString = result.getResponse().getContentAsString();
//
//    PaymentIntentDTO paymentIntentDTO =
//        objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});
//
//    // 2.9% of chargeAmount + 30 cents
//    double stripeFee = 0;
//
//    double totalCharge = bookingFee;
//
//    assertThat(paymentIntentDTO).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
//    assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
//    assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
//    assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
//    assertThat(paymentIntentDTO.getTotalChargeAtBooking()).isNotNull().isEqualTo(totalCharge);
//    assertThat(paymentIntentDTO.getTotalChargeAtDropOff()).isNotNull().isEqualTo(bookingCost);
//    assertThat(paymentIntentDTO.getSetupFutureUsage()).isNotNull().isEqualTo("off_session");
//    assertThat(paymentIntentDTO.getId()).isNotNull();
//
//    PaymentIntentQuestCreateDTO paymentIntentQuestUpdateDTO =
//        PaymentIntentQuestCreateDTO.builder().paymentIntentId(paymentIntentDTO.getId())
//            .groomerUuid(activeGroomer.getUuid()).amount(bookingCost).build();
//
//    requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent/booking")
//        .header("x-api-key", xApiKey.getMobileXApiKey()).accept(MediaType.APPLICATION_JSON)
//        .characterEncoding("utf-8").contentType(MediaType.APPLICATION_JSON)
//        .content(ObjectUtils.toJson(paymentIntentQuestUpdateDTO));
//
//    result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
//        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//
//    // @formatter:on
//    contentAsString = result.getResponse().getContentAsString();
//
//    paymentIntentDTO =
//        objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});
//
//    // 2.9% of chargeAmount + 30 cents
//    stripeFee = 0;
//
//    totalCharge = bookingFee;
//
//    assertThat(paymentIntentDTO).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
//    assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
//    assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
//    assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
//    assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
//    assertThat(paymentIntentDTO.getTotalChargeAtBooking()).isNotNull().isEqualTo(totalCharge);
//    assertThat(paymentIntentDTO.getTotalChargeAtDropOff()).isNotNull().isEqualTo(bookingCost);
//    assertThat(paymentIntentDTO.getSetupFutureUsage()).isNotNull().isEqualTo("off_session");
//    assertThat(paymentIntentDTO.getId()).isNotNull();
//  }

  @Test
  void itShouldCreateParentPaymentIntent_with_stripe_ready_groomer() throws Exception {

    Groomer activeGroomer = testEntityGeneratorService.getStripeReadyDBGroomer();

    Parent parent = testEntityGeneratorService.getDBParentWithStripeCustomer();

    PaymentMethod pm = testEntityGeneratorService.addPaymentMethod(parent);

    double bookingCost = 245D;

    // @formatter:off
        PaymentIntentParentCreateDTO paymentIntentCreateDTO = PaymentIntentParentCreateDTO.builder()
                .amount(bookingCost)
                .savePaymentMethodForFutureUse(true)
                .groomerUuid(activeGroomer.getUuid())
                .parentUuid(parent.getUuid())
                .paymentMethodUuid(pm.getUuid())
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent/booking")
                .header("token", PARENT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(paymentIntentCreateDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        // @formatter:on
    String contentAsString = result.getResponse().getContentAsString();

    PaymentIntentDTO paymentIntentDTO =
        objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});

    double chargeAmount = bookingCost;

    // 2.9% of chargeAmount + 30 cents
    double stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(chargeAmount)).add(BigDecimal.valueOf(0.3))
        .setScale(2, RoundingMode.CEILING).doubleValue();

    double totalCharge = bookingCost + bookingFee + stripeFee;

    assertThat(paymentIntentDTO).isNotNull();
    assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
    assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
    assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
    assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
    assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
    assertThat(paymentIntentDTO.getTotalChargeAtBooking()).isNotNull().isEqualTo(totalCharge);
    assertThat(paymentIntentDTO.getTotalChargeAtDropOff()).isNotNull().isEqualTo(0);
    assertThat(paymentIntentDTO.getId()).isNotNull();
  }

  @Test
  void itShouldUpdateParentPaymentIntent_with_stripe_ready_groomer() throws Exception {

    Groomer activeGroomer = testEntityGeneratorService.getStripeReadyDBGroomer();

    Parent parent = testEntityGeneratorService.getDBParentWithStripeCustomer();

    PaymentMethod pm = testEntityGeneratorService.addPaymentMethod(parent);

    double bookingCost = 245D;

    // @formatter:off
        PaymentIntentParentCreateDTO paymentIntentCreateDTO = PaymentIntentParentCreateDTO.builder()
                .amount(bookingCost)
                .savePaymentMethodForFutureUse(true)
                .groomerUuid(activeGroomer.getUuid())
                .parentUuid(parent.getUuid())
                .paymentMethodUuid(pm.getUuid())
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent/booking")
                .header("token", PARENT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(paymentIntentCreateDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        // @formatter:on
    String contentAsString = result.getResponse().getContentAsString();

    PaymentIntentDTO paymentIntentDTO =
        objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});

    double chargeAmount = bookingCost;

    // 2.9% of chargeAmount + 30 cents
    double stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(chargeAmount)).add(BigDecimal.valueOf(0.3))
        .setScale(2, RoundingMode.CEILING).doubleValue();

    double totalCharge = bookingCost + bookingFee + stripeFee;

    assertThat(paymentIntentDTO).isNotNull();
    assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
    assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
    assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
    assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
    assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
    assertThat(paymentIntentDTO.getTotalChargeAtBooking()).isNotNull().isEqualTo(totalCharge);
    assertThat(paymentIntentDTO.getTotalChargeAtDropOff()).isNotNull().isEqualTo(0);
    assertThat(paymentIntentDTO.getId()).isNotNull();


    // @formatter:off

    bookingCost = 265D;
    
     paymentIntentCreateDTO = PaymentIntentParentCreateDTO.builder()
            .amount(bookingCost)
            .paymentIntentId(paymentIntentDTO.getId())           
            .savePaymentMethodForFutureUse(true)
            .groomerUuid(activeGroomer.getUuid())
            .parentUuid(parent.getUuid())
            .paymentMethodUuid(pm.getUuid())
            .build();

     requestBuilder = MockMvcRequestBuilders.post("/stripe/paymentintent/booking")
            .header("token", PARENT_TOKEN)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectUtils.toJson(paymentIntentCreateDTO));

     result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

      contentAsString = result.getResponse().getContentAsString();

      paymentIntentDTO =
         objectMapper.readValue(contentAsString, new TypeReference<PaymentIntentDTO>() {});
     
    // @formatter:on


    chargeAmount = bookingCost;

    // 2.9% of chargeAmount + 30 cents
    stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(chargeAmount)).add(BigDecimal.valueOf(0.3))
        .setScale(2, RoundingMode.CEILING).doubleValue();

    totalCharge = bookingCost + bookingFee + stripeFee;

    assertThat(paymentIntentDTO).isNotNull();
    assertThat(paymentIntentDTO.getClientSecret()).isNotNull();
    assertThat(paymentIntentDTO.getClientSecret().length()).isGreaterThan(0);
    assertThat(paymentIntentDTO.getBookingFee()).isNotNull().isEqualTo(bookingFee);
    assertThat(paymentIntentDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
    assertThat(paymentIntentDTO.getTotalChargeAtBooking()).isNotNull().isEqualTo(totalCharge);
    assertThat(paymentIntentDTO.getTotalChargeAtDropOff()).isNotNull().isEqualTo(0);
    assertThat(paymentIntentDTO.getId()).isNotNull();
    assertThat(paymentIntentDTO.getStripeFee()).isNotNull().isEqualTo(stripeFee);
  }

}
