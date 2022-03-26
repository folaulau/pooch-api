package com.pooch.api.entity.parent;

import static org.assertj.core.api.Assertions.assertThat;
import javax.transaction.Transactional;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.library.firebase.FirebaseAuthService;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class ParentAuthIntegrationTests extends IntegrationTestConfiguration {

    @Autowired
    private MockMvc                mockMvc;

    @Autowired
    private ObjectMapper           objectMapper;

    @Captor
    private ArgumentCaptor<String> tokenCaptor;

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
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/parents/authenticate")
                .header("x-api-key", "test-token")
                .accept(MediaType.APPLICATION_JSON)
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
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/parents/authenticate")
                .header("x-api-key", "test-token")
                .accept(MediaType.APPLICATION_JSON)
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

        requestBuilder = MockMvcRequestBuilders.post("/parents/authenticate")
                .header("x-api-key", "test-token")
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
