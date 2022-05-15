package com.pooch.api.entity.phonenumber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

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
    private MockMvc                                 mockMvc;

    @Autowired
    private ObjectMapper                            objectMapper;

    @MockBean
    private SmsService                              smsService;

    @Autowired
    private PhoneNumberVerificationRepository       phoneNumberVerificationRepository;

    @Captor
    private ArgumentCaptor<PhoneNumberVerification> phoneNumberVerificationCaptor;

    @Transactional
    @Test
    void itShouldRequestPhoneNumberVerification_valid() throws Exception {
        // Given

        PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO = new PhoneNumberVerificationCreateDTO();
        phoneNumberRequestVerificationDTO.setCountryCode(1);

        long phoneNumber = 3109934731L;
        phoneNumberRequestVerificationDTO.setPhoneNumber(phoneNumber);

        Mockito.when(smsService.sendSMS(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyString())).thenReturn("good");

        // When

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/phonenumbers/request-verification")
                .header("x-api-key", "test-token")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(phoneNumberRequestVerificationDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        ApiDefaultResponseDTO dtoResponse = objectMapper.readValue(contentAsString, new TypeReference<ApiDefaultResponseDTO>() {});
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
        int countryCode = 1;
        PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO = new PhoneNumberVerificationCreateDTO();
        phoneNumberRequestVerificationDTO.setCountryCode(countryCode);

        long phoneNumber = 3109934731L;
        phoneNumberRequestVerificationDTO.setPhoneNumber(phoneNumber);

        Mockito.when(smsService.sendSMS(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyString())).thenReturn("good");

        // When

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/phonenumbers/request-verification")
                .header("x-api-key", "test-token")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(phoneNumberRequestVerificationDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        ApiDefaultResponseDTO dtoResponse = objectMapper.readValue(contentAsString, new TypeReference<ApiDefaultResponseDTO>() {});
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
        Optional<PhoneNumberVerification> optVerification = phoneNumberVerificationRepository.findFirstByOrderByIdDesc();

        if (!optVerification.isPresent()) {
            throw new RuntimeException("PhoneNumberVerification not found");
        }

        PhoneNumberVerification phoneNumberVerification = optVerification.get();

        PhoneNumberVerificationUpdateDTO phoneNumberVerificationUpdateDTO = new PhoneNumberVerificationUpdateDTO();
        phoneNumberVerificationUpdateDTO.setCountryCode(1);
        phoneNumberVerificationUpdateDTO.setPhoneNumber(phoneNumber);
        phoneNumberVerificationUpdateDTO.setCode(phoneNumberVerification.getVerificationCode());

        // When

        requestBuilder = MockMvcRequestBuilders.put("/phonenumbers/verification")
                .header("x-api-key", "test-token")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(phoneNumberVerificationUpdateDTO));

        result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        contentAsString = result.getResponse().getContentAsString();

        PhoneNumberVerificationDTO phoneNumberVerificationDTO = objectMapper.readValue(contentAsString, new TypeReference<PhoneNumberVerificationDTO>() {});
        // Then

        log.info("phoneNumberVerificationDTO={}", ObjectUtils.toJson(phoneNumberVerificationDTO));

        assertThat(phoneNumberVerificationDTO).isNotNull();
        assertThat(phoneNumberVerificationDTO.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(phoneNumberVerificationDTO.getPhoneVerified()).isTrue();

    }
}
