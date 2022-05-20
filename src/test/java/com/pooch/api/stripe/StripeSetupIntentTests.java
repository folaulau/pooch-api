package com.pooch.api.stripe;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
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
import com.pooch.api.dto.SetupIntentConfirmDTO;
import com.pooch.api.dto.SetupIntentCreateDTO;
import com.pooch.api.dto.SetupIntentDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.paymentmethod.PaymentMethod;
import com.pooch.api.entity.paymentmethod.PaymentMethodDAO;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.aws.secretsmanager.XApiKey;
import com.pooch.api.library.stripe.paymentintent.StripePaymentIntentService;
import com.pooch.api.library.stripe.setupintent.StripeSetupIntentService;
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
class StripeSetupIntentTests extends IntegrationTestConfiguration {

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

  @Autowired
  @Qualifier(value = "xApiKey")
  private XApiKey xApiKey;

  @Captor
  private ArgumentCaptor<String> tokenCaptor;

  private String PARENT_TOKEN = "PARENT_TOKEN";
  private String PARENT_UUID = "PARENT_UUID";

  @Autowired
  @Qualifier(value = "stripeSecrets")
  private StripeSecrets stripeSecrets;

  @Autowired
  private PaymentMethodDAO paymentMethodDAO;

  @Autowired
  private TestEntityGeneratorService testEntityGeneratorService;

  @Autowired
  private StripeSetupIntentService stripeSetupIntentService;

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

  @Test
  void itShouldAdd_paymentMethod() throws Exception {

    Parent parent = testEntityGeneratorService.getDBParentWithStripeCustomer();

    SetupIntentCreateDTO setupIntentCreateDTO =
        SetupIntentCreateDTO.builder().parentUuid(parent.getUuid()).build();

    String paymentMethodId = testEntityGeneratorService.getPaymentMethod("Folau Kaveinga");

    SetupIntentDTO setupIntentDTO = stripeSetupIntentService.create(setupIntentCreateDTO);

    com.stripe.model.SetupIntent setupIntent = testEntityGeneratorService
        .addPaymentMethodAndConfirmSetupIntent(setupIntentDTO.getId(), paymentMethodId);

    log.info("setupIntent={}", setupIntent.toJson());

    SetupIntentDTO updatedSetupIntentDTO =
        stripeSetupIntentService.confirmSetupIntent(SetupIntentConfirmDTO.builder()
            .parentUuid(parent.getUuid()).setupIntentId(setupIntent.getId()).build());

    log.info("updatedSetupIntentDTO={}", ObjectUtils.toJson(updatedSetupIntentDTO));

    assertThat(updatedSetupIntentDTO).isNotNull();
    assertThat(updatedSetupIntentDTO.getPaymentUuid()).isNotNull();
    assertThat(updatedSetupIntentDTO.getId()).isNotNull();
    assertThat(updatedSetupIntentDTO.getStatus()).isNotNull().isEqualTo("succeeded");


    List<PaymentMethod> paymentMethods = paymentMethodDAO.findByParentId(parent.getId());

    log.info("paymentMethods={}", ObjectUtils.toJson(paymentMethods));

    boolean present = paymentMethods.stream()
        .filter(pm -> pm.getUuid().equalsIgnoreCase(updatedSetupIntentDTO.getPaymentUuid()))
        .findFirst().isPresent();

    assertThat(present).isTrue();

  }

}
