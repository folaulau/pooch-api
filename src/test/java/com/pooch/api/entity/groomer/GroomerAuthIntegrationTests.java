package com.pooch.api.entity.groomer;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.pooch.api.library.aws.secretsmanager.XApiKey;
import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class GroomerAuthIntegrationTests extends IntegrationTestConfiguration {

    @Autowired
    private MockMvc                mockMvc;

    @Autowired
    private ObjectMapper           objectMapper;

    @Captor
    private ArgumentCaptor<String> tokenCaptor;

    @Autowired
    @Qualifier(value = "xApiKey")
    private XApiKey                xApiKey;

    @Autowired
    private FirebaseRestClient     firebaseRestClient;

    @Transactional
    @Test
    void itShouldSignUp_valid() throws Exception {
        // Given

        String email = RandomGeneratorUtils.getRandomEmail();
        String password = "Test1234!";

        FirebaseAuthResponse authResponse = firebaseRestClient.signUp(email, password);

        AuthenticatorDTO authenticatorDTO = new AuthenticatorDTO();
        authenticatorDTO.setToken(authResponse.getIdToken());

        // @formatter:on
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/groomers/authenticate")
                .header("x-api-key", xApiKey.getMobileXApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(authenticatorDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        AuthenticationResponseDTO dtoResponse = objectMapper.readValue(contentAsString, new TypeReference<AuthenticationResponseDTO>() {});

        assertThat(dtoResponse).isNotNull();
        assertThat(dtoResponse.getToken()).isNotNull();
        assertThat(dtoResponse.getUuid()).isNotNull();

    }

    @Transactional
    @Test
    void itShouldSignIn_valid() throws Exception {
        /**
         * Sign up
         */
        String email = RandomGeneratorUtils.getRandomEmail();
        String password = "Test1234!";

        FirebaseAuthResponse authResponse = firebaseRestClient.signUp(email, password);

        AuthenticatorDTO authenticatorDTO = new AuthenticatorDTO();
        authenticatorDTO.setToken(authResponse.getIdToken());

        // @formatter:on
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/groomers/authenticate")
                .header("x-api-key", xApiKey.getWebXApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(authenticatorDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        AuthenticationResponseDTO dtoResponse = objectMapper.readValue(contentAsString, new TypeReference<AuthenticationResponseDTO>() {});

        assertThat(dtoResponse).isNotNull();
        assertThat(dtoResponse.getToken()).isNotNull();
        assertThat(dtoResponse.getUuid()).isNotNull();

        /**
         * Sign in
         */
        authResponse = firebaseRestClient.signIn(email, password);

        authenticatorDTO = new AuthenticatorDTO();
        authenticatorDTO.setToken(authResponse.getIdToken());

        requestBuilder = MockMvcRequestBuilders.post("/groomers/authenticate")
                .header("x-api-key", xApiKey.getWebXApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(authenticatorDTO));

        result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        contentAsString = result.getResponse().getContentAsString();

        dtoResponse = objectMapper.readValue(contentAsString, new TypeReference<AuthenticationResponseDTO>() {});

        assertThat(dtoResponse).isNotNull();
        assertThat(dtoResponse.getToken()).isNotNull();
        assertThat(dtoResponse.getUuid()).isNotNull();
    }

}
