package com.pooch.api.entity.parent;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.transaction.Transactional;

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
import org.springframework.mock.web.MockMultipartFile;
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
import com.pooch.api.dto.AddressCreateUpdateDTO;
import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.ParentCancellationRequestDTO;
import com.pooch.api.dto.ParentDTO;
import com.pooch.api.dto.ParentUpdateDTO;
import com.pooch.api.dto.PaymentMethodCreateDTO;
import com.pooch.api.dto.PaymentMethodDTO;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.dto.SetupIntentCreateDTO;
import com.pooch.api.dto.SetupIntentDTO;
import com.pooch.api.dto.VaccineCreateDTO;
import com.pooch.api.dto.VaccineDTO;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethod;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethodDAO;
import com.pooch.api.entity.phonenumber.PhoneNumberVerification;
import com.pooch.api.entity.phonenumber.PhoneNumberVerificationRepository;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Gender;
import com.pooch.api.entity.pooch.Training;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.entity.s3file.S3FileDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.library.aws.secretsmanager.XApiKey;
import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.library.stripe.setupintent.StripeSetupIntentService;
import com.pooch.api.library.twilio.sms.SmsService;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class ParentCancellationTests extends IntegrationTestConfiguration {

    @Autowired
    private MockMvc                           mockMvc;

    @Resource
    private WebApplicationContext             webApplicationContext;

    @Autowired
    private ObjectMapper                      objectMapper;

    @Autowired
    private S3FileDAO                         s3FileDAO;

    @Autowired
    @Qualifier(value = "xApiKey")
    private XApiKey                           xApiKey;

    @Autowired
    private FirebaseRestClient                firebaseRestClient;

    @Autowired
    private PhoneNumberVerificationRepository phoneNumberVerificationRepository;

    @Autowired
    private Filter                            springSecurityFilterChain;

    @MockBean
    private SmsService                        smsService;

    @Autowired
    private PaymentMethodDAO                  paymentMethodDAO;

    @Autowired
    private ParentDAO                         parentDAO;

    @MockBean
    private JwtTokenService                   jwtTokenService;

    @Captor
    private ArgumentCaptor<String>            tokenCaptor;

    private String                            PARENT_TOKEN = "PARENT_TOKEN";
    private String                            PARENT_UUID  = "PARENT_UUID";

    @Autowired
    private TestEntityGeneratorService        testEntityGeneratorService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        JwtPayload groomerJwtPayload = new JwtPayload();
        groomerJwtPayload.setUuid(PARENT_UUID);
        groomerJwtPayload.setRole(UserType.parent.name());

        Mockito.when(jwtTokenService.getPayloadByToken(PARENT_TOKEN)).thenReturn(groomerJwtPayload);
    }

    @Transactional
    @Test
    void itShouldCancelAccount_valid() throws Exception {
        System.out.println("itShouldCancelAccount_valid");

        String email = RandomGeneratorUtils.getRandomEmail();
        String password = "Test1234!";

        FirebaseAuthResponse authResponse = firebaseRestClient.signUp(email, password);

        AuthenticatorDTO authenticatorDTO = new AuthenticatorDTO();
        authenticatorDTO.setToken(authResponse.getIdToken());

        // @formatter:on
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/parents/authenticate")
                .header("x-api-key", xApiKey.getMobileXApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(authenticatorDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        AuthenticationResponseDTO dtoResponse = objectMapper.readValue(contentAsString, new TypeReference<AuthenticationResponseDTO>() {});

        System.out.println("dtoResponse: " + dtoResponse.toString());

        assertThat(dtoResponse).isNotNull();
        assertThat(dtoResponse.getUuid()).isNotNull();

        String uuid = dtoResponse.getUuid();

        // Given

        ParentCancellationRequestDTO cancellationRequestDTO = ParentCancellationRequestDTO.builder().uuid(uuid).reason("I am using another site").build();

        // When
        requestBuilder = MockMvcRequestBuilders.put("/parents/cancel")
                .header("token", PARENT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(cancellationRequestDTO));

        // @formatter:on

        result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        contentAsString = result.getResponse().getContentAsString();

        ApiDefaultResponseDTO apiResponse = objectMapper.readValue(contentAsString, new TypeReference<ApiDefaultResponseDTO>() {});

        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getMessage()).isNotNull().isEqualTo("Your account has been cancelled");

        Optional<Parent> optParent = parentDAO.getByUuid(uuid);

        Parent cancelledParent = optParent.get();

        assertThat(cancelledParent).isNotNull();
        assertThat(cancelledParent.getStatus()).isNotNull().isEqualTo(ParentStatus.CANCELLED);
        assertThat(cancelledParent.getCancelledAt()).isNotNull();

        /**
         * Sign in
         */
        authResponse = firebaseRestClient.signIn(email, password);

        authenticatorDTO = new AuthenticatorDTO();
        authenticatorDTO.setToken(authResponse.getIdToken());

        requestBuilder = MockMvcRequestBuilders.post("/parents/authenticate")
                .header("x-api-key", xApiKey.getUtilityXApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(authenticatorDTO));

        result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        contentAsString = result.getResponse().getContentAsString();

        ApiError apiError = objectMapper.readValue(contentAsString, new TypeReference<ApiError>() {});

        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).isNotNull().isEqualTo("Your account is not active. Please contact our support team.");

    }

}
