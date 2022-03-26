package com.pooch.api.entity.groomer;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.entity.parent.ParentIntegrationTests;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.firebase.FirebaseAuthResponse;
import com.pooch.api.firebase.FirebaseRestClient;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
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

    @MockBean
    private JwtTokenService        jwtTokenService;

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
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/groomers/authenticate")
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
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/groomers/authenticate")
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

        requestBuilder = MockMvcRequestBuilders.post("/groomers/authenticate")
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
