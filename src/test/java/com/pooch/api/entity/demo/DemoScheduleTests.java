package com.pooch.api.entity.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.DemoCreateDTO;
import com.pooch.api.dto.DemoDTO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiSubError;
import com.pooch.api.library.aws.secretsmanager.XApiKey;
import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class DemoScheduleTests extends IntegrationTestConfiguration {

    @Autowired
    private MockMvc                mockMvc;

    @Autowired
    private ObjectMapper           objectMapper;

    @Captor
    private ArgumentCaptor<String> tokenCaptor;

    @Autowired
    @Qualifier(value = "xApiKey")
    private XApiKey                xApiKey;

    @Transactional
    @Test
    void itShouldScheduleDemo_valid() throws Exception {
        // Given

        DemoCreateDTO demoCreateDTO = DemoCreateDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@gmail.com")
                .companyName("Dog Grooming")
                .companyWebsite("doggrooming.com")
                .marketingCommunicationConsent(true)
                .numberOfPetsPerDay("< 0")
                .phoneNumber("3109934731")
                .services(Set.of("Day Care"))
                .build();

        // @formatter:on
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/demos/schedule")
                .header("x-api-key", xApiKey.getMobileXApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(demoCreateDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        DemoDTO demoDTO = objectMapper.readValue(contentAsString, new TypeReference<DemoDTO>() {});

        assertThat(demoDTO).isNotNull();
        assertThat(demoDTO.getId()).isNotNull().isGreaterThan(0);
        assertThat(demoDTO.getUuid()).isNotNull();

    }

    @Transactional
    @Test
    void itShouldNotScheduleDemo_invalid() throws Exception {
        // Given

        DemoCreateDTO demoCreateDTO = DemoCreateDTO.builder().build();

        // @formatter:off
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/demos/schedule")
                .header("x-api-key", xApiKey.getMobileXApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(demoCreateDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
        // @formatter:on

        String contentAsString = result.getResponse().getContentAsString();

        ApiError apiError = objectMapper.readValue(contentAsString, new TypeReference<ApiError>() {});

        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).isEqualTo("Something went wrong");

        List<ApiSubError> errors = apiError.getErrors();

        assertThat(errors).isNotNull().isNotEmpty();

        assertThat(errors.size()).isEqualTo(7);

        assertThat(errors.stream().filter(er -> er.getMessage().equalsIgnoreCase("firstName must not be empty")).findFirst().isPresent()).isTrue();
        assertThat(errors.stream().filter(er -> er.getMessage().equalsIgnoreCase("lastName must not be empty")).findFirst().isPresent()).isTrue();
        assertThat(errors.stream().filter(er -> er.getMessage().equalsIgnoreCase("phoneNumber must not be empty")).findFirst().isPresent()).isTrue();
        assertThat(errors.stream().filter(er -> er.getMessage().equalsIgnoreCase("email must not be empty")).findFirst().isPresent()).isTrue();
        assertThat(errors.stream().filter(er -> er.getMessage().equalsIgnoreCase("services must not be empty")).findFirst().isPresent()).isTrue();
        assertThat(errors.stream().filter(er -> er.getMessage().equalsIgnoreCase("companyName must not be empty")).findFirst().isPresent()).isTrue();
        assertThat(errors.stream().filter(er -> er.getMessage().equalsIgnoreCase("numberOfPetsPerDay must not be empty")).findFirst().isPresent()).isTrue();

    }

}
