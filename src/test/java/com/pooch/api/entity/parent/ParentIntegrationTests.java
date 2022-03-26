package com.pooch.api.entity.parent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.TestEntityGeneratorService;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.GroomerUpdateDTO;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.library.firebase.FirebaseAuthService;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class ParentIntegrationTests extends IntegrationTestConfiguration {

    @Autowired
    private MockMvc                    mockMvc;

    @Resource
    private WebApplicationContext      webApplicationContext;

    @Autowired
    private ObjectMapper               objectMapper;

    @Autowired
    private Filter                     springSecurityFilterChain;

    @MockBean
    private JwtTokenService            jwtTokenService;

    @Captor
    private ArgumentCaptor<String>     tokenCaptor;

    private String                     PARENT_TOKEN = "PARENT_TOKEN";
    private String                     PARENT_UUID  = "PARENT_UUID";

    @Autowired
    private TestEntityGeneratorService testEntityGeneratorService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        JwtPayload groomerJwtPayload = new JwtPayload();
        groomerJwtPayload.setUuid(PARENT_UUID);
        groomerJwtPayload.setRole(Authority.parent.name());

        Mockito.when(jwtTokenService.getPayloadByToken(PARENT_TOKEN)).thenReturn(groomerJwtPayload);
    }

    @Transactional
    @Test
    void itShouldUploadProfileImages_valid() throws Exception {
        // Given
        Parent parent = testEntityGeneratorService.getDBParent();

        // @formatter:on
        // When
        MockMultipartFile firstFile = new MockMultipartFile("images", "note1.png", MediaType.TEXT_PLAIN_VALUE, "Hello, World!1".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("images", "note2.png", MediaType.TEXT_PLAIN_VALUE, "Hello, World!2".getBytes());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/parents/" + parent.getUuid() + "/profile/images")
                .file(firstFile)
                .file(secondFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("token", PARENT_TOKEN);

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        List<S3FileDTO> S3FileDTOs = objectMapper.readValue(contentAsString, new TypeReference<List<S3FileDTO>>() {});

        assertThat(S3FileDTOs).isNotNull();
        assertThat(S3FileDTOs.size()).isNotNull().isGreaterThan(0);

    }

}
