package com.pooch.api.entity.phonenumber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.pooch.api.entity.parent.Parent;
import com.pooch.api.utils.TestEntityGeneratorService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.library.twilio.sms.SmsService;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class PhoneNumberIntegrationTests extends IntegrationTestConfiguration {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private SmsService smsService;

  @Autowired
  private PhoneNumberVerificationRepository phoneNumberVerificationRepository;

  @Autowired
  private TestEntityGeneratorService testEntityGeneratorService;

  @Autowired
  private PhoneNumberService phoneNumberService;

  @Captor
  private ArgumentCaptor<PhoneNumberVerification> phoneNumberVerificationCaptor;

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

    ApiDefaultResponseDTO dtoResponse =
        phoneNumberService.requestVerification(parent, phoneNumberRequestVerificationDTO);


    log.info("dtoResponse={}", ObjectUtils.toJson(dtoResponse));

    assertThat(dtoResponse).isNotNull();
    assertThat(dtoResponse.getMessage()).isNotNull();

    // --verify
    // --assert

  }

  @Transactional
  @Test
  void itShouldRequestPhoneNumberVerification_and_verifyWithCode() throws Exception {

    Parent parent = testEntityGeneratorService.getDBParent();
    // Given
    int countryCode = 1;
    PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO =
        new PhoneNumberVerificationCreateDTO();
    phoneNumberRequestVerificationDTO.setCountryCode(countryCode);

    long phoneNumber = 3109934731L;
    phoneNumberRequestVerificationDTO.setPhoneNumber(phoneNumber);

    Mockito.when(smsService.sendSMS(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyString()))
        .thenReturn("good");

    ApiDefaultResponseDTO dtoResponse =
        phoneNumberService.requestVerification(parent, phoneNumberRequestVerificationDTO);
    
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

    PhoneNumberVerification savedPhoneNumberVerification = optVerification.get();

    PhoneNumberVerificationUpdateDTO phoneNumberVerificationUpdateDTO =
        new PhoneNumberVerificationUpdateDTO();
    phoneNumberVerificationUpdateDTO.setCountryCode(1);
    phoneNumberVerificationUpdateDTO.setPhoneNumber(phoneNumber);
    phoneNumberVerificationUpdateDTO.setCode(savedPhoneNumberVerification.getVerificationCode());


   PhoneNumberVerification phoneNumberVerification =  phoneNumberService.verifyNumberWithCode(parent, phoneNumberVerificationUpdateDTO);

    log.info("phoneNumberVerificationDTO={}", ObjectUtils.toJson(phoneNumberVerification));

    assertThat(phoneNumberVerification).isNotNull();
    assertThat(phoneNumberVerification.getPhoneNumber()).isEqualTo(phoneNumber);
    assertThat(phoneNumberVerification.getPhoneVerified()).isTrue();

  }
}
