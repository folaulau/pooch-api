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
import com.pooch.api.dto.AddressCreateUpdateDTO;
import com.pooch.api.dto.CareServiceUpdateDTO;
import com.pooch.api.dto.GroomerCreateProfileDTO;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.SettingsUpdateDTO;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class GroomerSettingsUpdateTests extends IntegrationTestConfiguration {

  @Autowired
  private MockMvc mockMvc;

  @Resource
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Filter springSecurityFilterChain;

  @MockBean
  private JwtTokenService jwtTokenService;

  @Captor
  private ArgumentCaptor<String> tokenCaptor;

  private String GROOMER_TOKEN = "GROOMER_TOKEN";
  private String GROOMER_UUID = "GROOMER_UUID";

  @Autowired
  private TestEntityGeneratorService testEntityGeneratorService;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilters(springSecurityFilterChain).build();

    JwtPayload groomerJwtPayload = new JwtPayload();
    groomerJwtPayload.setUuid(GROOMER_UUID);
    groomerJwtPayload.setRole(UserType.groomer.name());

    Mockito.when(jwtTokenService.getPayloadByToken(GROOMER_TOKEN)).thenReturn(groomerJwtPayload);
  }

  @Transactional
  @Test
  void itShouldUpdateSettings_valid() throws Exception {
    System.out.println("itShouldUpdateSettings_valid");
    // Given
    Groomer groomer = testEntityGeneratorService.getDBFirebaseGroomer();
    String email = RandomGeneratorUtils.getRandomEmail();
    SettingsUpdateDTO settingsUpdateDTO = SettingsUpdateDTO.builder().uuid(groomer.getUuid())
        .email(email).password("Test1234!").businessName("Grooming").firstName("John")
        .lastName("Doe").countryCode(1).phoneNumber(3109934731L).build();

    // @formatter:on
    // When
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.put("/groomers/settings").header("token", GROOMER_TOKEN)
            .accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .contentType(MediaType.APPLICATION_JSON).content(ObjectUtils.toJson(settingsUpdateDTO));

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    GroomerDTO groomerDTO =
        objectMapper.readValue(contentAsString, new TypeReference<GroomerDTO>() {});

    assertThat(groomerDTO).isNotNull();
    assertThat(groomerDTO.getId()).isNotNull().isGreaterThan(0);
    assertThat(groomerDTO.getUuid()).isNotNull();
    assertThat(groomerDTO.getEmail()).isNotNull().isEqualTo(email);
    assertThat(groomerDTO.getFirstName()).isNotNull().isEqualTo("John");
    assertThat(groomerDTO.getLastName()).isNotNull().isEqualTo("Doe");
    assertThat(groomerDTO.getBusinessName()).isNotNull().isEqualTo("Grooming");
    assertThat(groomerDTO.getPhoneNumber()).isNotNull().isEqualTo(3109934731L);

  }

}
