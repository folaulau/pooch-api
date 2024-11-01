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
import com.pooch.api.library.stripe.setupintent.StripeSetupIntentService;
import com.pooch.api.library.twilio.sms.SmsService;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class ParentIntegrationTests extends IntegrationTestConfiguration {

  @Autowired
  private MockMvc mockMvc;

  @Resource
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private S3FileDAO s3FileDAO;

  @Autowired
  private PhoneNumberVerificationRepository phoneNumberVerificationRepository;


  @Autowired
  private Filter springSecurityFilterChain;

  @MockBean
  private SmsService smsService;

  @Autowired
  private PaymentMethodDAO paymentMethodDAO;

  @Autowired
  private ParentDAO parentDAO;

  @MockBean
  private JwtTokenService jwtTokenService;


  @Autowired
  private StripeSetupIntentService stripeSetupIntentService;

  @Captor
  private ArgumentCaptor<String> tokenCaptor;

  private String PARENT_TOKEN = "PARENT_TOKEN";
  private String PARENT_UUID = "PARENT_UUID";

  @Autowired
  private TestEntityGeneratorService testEntityGeneratorService;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilters(springSecurityFilterChain).build();

    JwtPayload groomerJwtPayload = new JwtPayload();
    groomerJwtPayload.setUuid(PARENT_UUID);
    groomerJwtPayload.setRole(UserType.parent.name());

    Mockito.when(jwtTokenService.getPayloadByToken(PARENT_TOKEN)).thenReturn(groomerJwtPayload);
  }

  @Transactional
  @Test
  void itShouldUploadProfileImage_valid() throws Exception {
    // Given
    Parent parent = testEntityGeneratorService.getDBParent();

    // @formatter:on
    // When
    MockMultipartFile firstFile = new MockMultipartFile("image", "note1.png",
        MediaType.TEXT_PLAIN_VALUE, "Hello, World!1".getBytes());
    // MockMultipartFile secondFile = new MockMultipartFile("images", "note2.png",
    // MediaType.TEXT_PLAIN_VALUE, "Hello, World!2".getBytes());

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .multipart("/parents/" + parent.getUuid() + "/profile/image").file(firstFile)
        // .file(secondFile)
        .contentType(MediaType.MULTIPART_FORM_DATA).characterEncoding("utf-8")
        .header("token", PARENT_TOKEN);

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    S3FileDTO S3FileDTO =
        objectMapper.readValue(contentAsString, new TypeReference<S3FileDTO>() {});

    assertThat(S3FileDTO).isNotNull();
    assertThat(S3FileDTO.getId()).isNotNull().isGreaterThan(0);

    long profileImageCount = s3FileDAO.countProfileImages(parent);
    assertThat(profileImageCount).isNotNull().isEqualTo(1);
    
    requestBuilder = MockMvcRequestBuilders
        .multipart("/parents/" + parent.getUuid() + "/profile/image").file(firstFile)
        // .file(secondFile)
        .contentType(MediaType.MULTIPART_FORM_DATA).characterEncoding("utf-8")
        .header("token", PARENT_TOKEN);

     result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

     contentAsString = result.getResponse().getContentAsString();

     S3FileDTO =
        objectMapper.readValue(contentAsString, new TypeReference<S3FileDTO>() {});

    assertThat(S3FileDTO).isNotNull();
    assertThat(S3FileDTO.getId()).isNotNull().isGreaterThan(0);

     profileImageCount = s3FileDAO.countProfileImages(parent);
    assertThat(profileImageCount).isNotNull().isEqualTo(1);
  }

  @Transactional
  @Test
  void itShouldUpdateJustProfile_valid() throws Exception {
    System.out.println("itShouldUpdateProfile_valid");
    // Given
    Parent parent = testEntityGeneratorService.getDBParent();

    // @formatter:off
        AddressCreateUpdateDTO address = AddressCreateUpdateDTO.builder()
                .state("CA")
                .street("222 Alta Ave")
                .city("Santa Monica")
                .zipcode("90402")
                .latitude(34.025070)
                .longitude(-118.507700).build();
        
        ParentUpdateDTO parentUpdateDTO = ParentUpdateDTO.builder()
                .uuid(parent.getUuid())
                .countryCode(1)
                .phoneNumber(3109944731L)
                .fullName("Folau Kaveinga")
                .address(address)
                .build();

        
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/parents/profile")
                .header("token", PARENT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(parentUpdateDTO));

        // @formatter:on

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    ParentDTO parentDTO =
        objectMapper.readValue(contentAsString, new TypeReference<ParentDTO>() {});

    assertThat(parentDTO).isNotNull();
    assertThat(parentDTO.getId()).isNotNull().isGreaterThan(0);
    assertThat(parentDTO.getUuid()).isNotNull();
    assertThat(parentDTO.getFullName()).isNotNull().isEqualTo("Folau Kaveinga");
    assertThat(parentDTO.getUuid()).isNotNull();
    assertThat(parentDTO.getPhoneNumber()).isNotNull().isEqualTo(3109944731L);
    assertThat(parentDTO.getCountryCode()).isNotNull().isEqualTo(1);
    assertThat(parentDTO.getAddress()).isNotNull();
    assertThat(parentDTO.getAddress().getId()).isNotNull().isGreaterThan(0);
    assertThat(parentDTO.getAddress().getStreet()).isNotNull().isEqualTo("222 Alta Ave");
    assertThat(parentDTO.getAddress().getCity()).isNotNull().isEqualTo("Santa Monica");
    assertThat(parentDTO.getAddress().getZipcode()).isNotNull().isEqualTo("90402");
    assertThat(parentDTO.getAddress().getState()).isNotNull().isEqualTo("CA");

  }

  @Transactional
  @Test
  void itShouldUpdatePooches_valid() throws Exception {
    System.out.println("itShouldUpdatePooches_valid");
    // Given
    Parent parent = testEntityGeneratorService.getDBParent();

    // @formatter:off
        ParentUpdateDTO parentUpdateDTO = ParentUpdateDTO.builder()
                .uuid(parent.getUuid())
                .build();
        
        PoochCreateUpdateDTO pooch = PoochCreateUpdateDTO.builder()
                .gender(Gender.Female)
                .dob(LocalDate.now().minusYears(2))
                .fullName("Simpa")
                .training(Training.Low)
                .spayed(true)
                .build();
        
        pooch.addFoodSchedule(FoodSchedule.Morning);
        pooch.addFoodSchedule(FoodSchedule.Night);
        
        pooch.addVaccine(VaccineCreateDTO.builder()
                .name("vitamin")
                .expireDate(LocalDateTime.now().plusMonths(3))
                .build());
        
        parentUpdateDTO.addPooch(pooch);        
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/parents/profile")
                .header("token", PARENT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(parentUpdateDTO));

        // @formatter:on

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    ParentDTO parentDTO =
        objectMapper.readValue(contentAsString, new TypeReference<ParentDTO>() {});

    assertThat(parentDTO).isNotNull();
    assertThat(parentDTO.getId()).isNotNull().isGreaterThan(0);
    assertThat(parentDTO.getUuid()).isNotNull();
    assertThat(parentDTO.getPooches()).isNotNull();
    assertThat(parentDTO.getPooches().size()).isNotNull().isGreaterThan(0);

    assertThat(parentDTO.getPooches().get(0)).isNotNull();
    assertThat(parentDTO.getPooches().get(0).getId()).isNotNull().isGreaterThan(0);
    assertThat(parentDTO.getPooches().get(0).getFullName()).isNotNull().isEqualTo("Simpa");
    assertThat(parentDTO.getPooches().get(0).getSpayed()).isNotNull().isTrue();

    // Arrays.asList(FoodSchedule.Night, FoodSchedule.Morning)
    assertThat(parentDTO.getPooches().get(0).getFoodSchedule()).isNotNull()
        .contains(FoodSchedule.Morning, FoodSchedule.Night);
    Set<VaccineDTO> vaccines = parentDTO.getPooches().get(0).getVaccines();

    assertThat(vaccines).isNotNull();
    assertThat(vaccines.size()).isGreaterThan(0);

    for (VaccineDTO vaccineDTO : vaccines) {
      assertThat(vaccineDTO).isNotNull();
      assertThat(vaccineDTO.getId()).isNotNull().isGreaterThan(0);
      assertThat(vaccineDTO.getExpireDate()).isNotNull();
      assertThat(vaccineDTO.getName()).isNotNull().isEqualTo("vitamin");
    }

    assertThat(parentDTO.getPooches().get(0).getGender()).isNotNull().isEqualTo(Gender.Female);
    assertThat(parentDTO.getPooches().get(0).getTraining()).isNotNull().isEqualTo(Training.Low);

  }

  @Transactional
  @Test
  void itShouldAddPaymentMethod_valid() throws Exception {

    Parent parent = testEntityGeneratorService.getDBParentWithStripeCustomer();

    SetupIntentCreateDTO setupIntentCreateDTO =
        SetupIntentCreateDTO.builder().parentUuid(parent.getUuid()).build();

    String paymentMethodId = testEntityGeneratorService.getPaymentMethod("Folau Kaveinga");

    SetupIntentDTO setupIntentDTO = stripeSetupIntentService.create(setupIntentCreateDTO);

    com.stripe.model.SetupIntent setupIntent = testEntityGeneratorService
        .addPaymentMethodAndConfirmSetupIntent(setupIntentDTO.getId(), paymentMethodId);

    log.info("setupIntent={}", setupIntent.toJson());

    // @formatter:off
    PaymentMethodCreateDTO paymentMethodCreateDTO = PaymentMethodCreateDTO.builder().setupIntentId(setupIntent.getId()).build();
    
    // When
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/parents/"+parent.getUuid()+"/paymentmethod")
            .header("token", PARENT_TOKEN)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectUtils.toJson(paymentMethodCreateDTO));

    // @formatter:on

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    PaymentMethodDTO paymentMethodDTO =
        objectMapper.readValue(contentAsString, new TypeReference<PaymentMethodDTO>() {});


    log.info("paymentMethod={}", ObjectUtils.toJson(paymentMethodDTO));

    assertThat(paymentMethodDTO).isNotNull();
    assertThat(paymentMethodDTO.getUuid()).isNotNull();
    assertThat(paymentMethodDTO.getId()).isNotNull();


    List<PaymentMethod> paymentMethods = paymentMethodDAO.findByParentId(parent.getId());

    log.info("paymentMethods={}", ObjectUtils.toJson(paymentMethods));

    boolean present = paymentMethods.stream()
        .filter(pm -> pm.getUuid().equalsIgnoreCase(paymentMethodDTO.getUuid())).findFirst()
        .isPresent();

    assertThat(present).isTrue();
  }

  @Transactional
  @Test
  void itShouldRequestPhoneNumberVerification_valid() throws Exception {
    // Given

    Parent parent = testEntityGeneratorService.getDBParent();

    PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO =
        new PhoneNumberVerificationCreateDTO();
    phoneNumberRequestVerificationDTO.setCountryCode(1);

    long phoneNumber = 3109934731L;
    phoneNumberRequestVerificationDTO.setPhoneNumber(phoneNumber);

    Mockito.when(smsService.sendSMS(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyString()))
        .thenReturn("good");

    // When

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/parents/" + parent.getUuid() + "/phonenumber/request-verification")
        .header("token", PARENT_TOKEN).accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
        .contentType(MediaType.APPLICATION_JSON)
        .content(ObjectUtils.toJson(phoneNumberRequestVerificationDTO));

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    ApiDefaultResponseDTO dtoResponse =
        objectMapper.readValue(contentAsString, new TypeReference<ApiDefaultResponseDTO>() {});
    // Then

    log.info("dtoResponse={}", ObjectUtils.toJson(dtoResponse));

    assertThat(dtoResponse).isNotNull();
    assertThat(dtoResponse.getMessage()).isNotNull();

    // --verify
    // --assert

  }

  @Transactional
  @Test
  void itShouldRequestPhoneNumberVerification_and_verifyWithCode() throws Exception {
    // Given
    Parent parent = testEntityGeneratorService.getDBParent();


    int countryCode = 1;
    PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO =
        new PhoneNumberVerificationCreateDTO();
    phoneNumberRequestVerificationDTO.setCountryCode(countryCode);

    long phoneNumber = 3109934731L;
    phoneNumberRequestVerificationDTO.setPhoneNumber(phoneNumber);

    Mockito.when(smsService.sendSMS(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyString()))
        .thenReturn("good");

    // When

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/parents/" + parent.getUuid() + "/phonenumber/request-verification")
        .header("token", PARENT_TOKEN).accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
        .contentType(MediaType.APPLICATION_JSON)
        .content(ObjectUtils.toJson(phoneNumberRequestVerificationDTO));

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    ApiDefaultResponseDTO dtoResponse =
        objectMapper.readValue(contentAsString, new TypeReference<ApiDefaultResponseDTO>() {});
    // Then

    log.info("dtoResponse={}", ObjectUtils.toJson(dtoResponse));

    assertThat(dtoResponse).isNotNull();
    assertThat(dtoResponse.getMessage()).isNotNull();

    /**
     * Verify with number
     */

    /**
     * get the last row
     */
    Optional<PhoneNumberVerification> optVerification =
        phoneNumberVerificationRepository.findFirstByOrderByIdDesc();

    if (!optVerification.isPresent()) {
      throw new RuntimeException("PhoneNumberVerification not found");
    }

    PhoneNumberVerification phoneNumberVerification = optVerification.get();

    PhoneNumberVerificationUpdateDTO phoneNumberVerificationUpdateDTO =
        new PhoneNumberVerificationUpdateDTO();
    phoneNumberVerificationUpdateDTO.setCountryCode(1);
    phoneNumberVerificationUpdateDTO.setPhoneNumber(phoneNumber);
    phoneNumberVerificationUpdateDTO.setCode(phoneNumberVerification.getVerificationCode());

    // When

    requestBuilder =
        MockMvcRequestBuilders.put("/parents/" + parent.getUuid() + "/phonenumber/verification")
            .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .header("token", PARENT_TOKEN).accept(MediaType.APPLICATION_JSON)
            .content(ObjectUtils.toJson(phoneNumberVerificationUpdateDTO));

    result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    contentAsString = result.getResponse().getContentAsString();

    PhoneNumberVerificationDTO phoneNumberVerificationDTO =
        objectMapper.readValue(contentAsString, new TypeReference<PhoneNumberVerificationDTO>() {});
    // Then

    log.info("phoneNumberVerificationDTO={}", ObjectUtils.toJson(phoneNumberVerificationDTO));

    assertThat(phoneNumberVerificationDTO).isNotNull();
    assertThat(phoneNumberVerificationDTO.getPhoneNumber()).isEqualTo(phoneNumber);
    assertThat(phoneNumberVerificationDTO.getPhoneVerified()).isTrue();

    Optional<Parent> optParent = parentDAO.getByUuid(parent.getUuid());

    assertThat(optParent.isPresent()).isTrue();
    Parent savedParent = optParent.get();

    assertThat(savedParent.getPhoneNumber()).isEqualTo(phoneNumberVerificationDTO.getPhoneNumber());
    assertThat(savedParent.getPhoneNumberVerified()).isTrue();
  }

}
