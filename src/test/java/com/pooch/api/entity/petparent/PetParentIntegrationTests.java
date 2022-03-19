package com.pooch.api.entity.petparent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

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
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.entity.phonenumber.PhoneNumberVerification;
import com.pooch.api.firebase.FirebaseAuthService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class PetParentIntegrationTests extends IntegrationTestConfiguration {

    @Autowired
    private MockMvc                mockMvc;

    @Autowired
    private ObjectMapper           objectMapper;

    @MockBean
    private FirebaseAuthService    firebaseAuthService;

    @Autowired
    private FirebaseAuth           firebaseAuth;

    @Captor
    private ArgumentCaptor<String> tokenCaptor;

    @Transactional
    @Test
    void itShouldAuthenticatePetParent_valid() throws Exception {
        // Given
        AuthenticatorDTO authenticatorDTO = new AuthenticatorDTO();
        authenticatorDTO.setToken("token");

        CreateRequest request = new CreateRequest().setEmail(RandomGeneratorUtils.getRandomEmail())
                .setEmailVerified(false)
                .setPassword("Test1234!")
                .setPhoneNumber("+1" + RandomGeneratorUtils.getRandomPhone())
                .setDisplayName(RandomGeneratorUtils.getRandomFullName())
                .setPhotoUrl("http://www.example.com/12345678/photo.png")
                .setDisabled(false);
        // @formatter:on

        UserRecord userRecord = firebaseAuth.createUser(request);

        // When
        Mockito.when(firebaseAuthService.verifyAndGetUser(Mockito.anyString())).thenReturn(userRecord);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/petparents/authenticate")
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

        Mockito.verify(firebaseAuthService).verifyAndGetUser(tokenCaptor.capture());

        String token = tokenCaptor.getValue();

        assertThat(token).isNotNull().isEqualTo("token");

    }

}
