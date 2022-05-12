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
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.CareServiceUpdateDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.GroomerCreateProfileDTO;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.GroomerUpdateDTO;
import com.pooch.api.entity.parent.ParentIntegrationTests;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class GroomerIntegrationTests extends IntegrationTestConfiguration {

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

    private String                     GROOMER_TOKEN = "GROOMER_TOKEN";
    private String                     GROOMER_UUID  = "GROOMER_UUID";

    @Autowired
    private TestEntityGeneratorService testEntityGeneratorService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        JwtPayload groomerJwtPayload = new JwtPayload();
        groomerJwtPayload.setUuid(GROOMER_UUID);
        groomerJwtPayload.setRole(Authority.groomer.name());

        Mockito.when(jwtTokenService.getPayloadByToken(GROOMER_TOKEN)).thenReturn(groomerJwtPayload);
    }

    @Transactional
    @Test
    void itShouldUpdateProfile_valid() throws Exception {
        System.out.println("itShouldUpdateProfile_valid");
        // Given
        Groomer groomer = testEntityGeneratorService.getDBGroomer();
        GroomerCreateProfileDTO groomerUpdateDTO = new GroomerCreateProfileDTO();
        groomerUpdateDTO.setUuid(groomer.getUuid());
        groomerUpdateDTO.setFirstName("Folau");
        groomerUpdateDTO.setLastName("Kaveinga");
        groomerUpdateDTO.setBusinessName("Folau Dev");
        groomerUpdateDTO.setPhoneNumber(3109934731L);

        AddressCreateUpdateDTO address = AddressCreateUpdateDTO.builder().state("CA").street("222 Alta Ave").city("Santa Monica").zipcode("90402").latitude(34.025070).longitude(-118.507700).build();
        groomerUpdateDTO.setAddress(address);

        groomerUpdateDTO.addCareService(CareServiceUpdateDTO.builder().name("Overnight").build());

        // @formatter:on
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/groomers/create-profile")
                .header("token", GROOMER_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(groomerUpdateDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        GroomerDTO groomerDTO = objectMapper.readValue(contentAsString, new TypeReference<GroomerDTO>() {});

        assertThat(groomerDTO).isNotNull();
        assertThat(groomerDTO.getId()).isNotNull().isGreaterThan(0);
        assertThat(groomerDTO.getUuid()).isNotNull();
        assertThat(groomerDTO.getFirstName()).isNotNull().isEqualTo("Folau");
        assertThat(groomerDTO.getLastName()).isNotNull().isEqualTo("Kaveinga");
        assertThat(groomerDTO.getDescription()).isNotNull().isEqualTo("Test description");
        assertThat(groomerDTO.getAddresses()).isNotNull();
        assertThat(groomerDTO.getAddresses().size()).isEqualTo(2);
        assertThat(groomerDTO.getCareServices()).isNotNull();
        assertThat(groomerDTO.getCareServices().size()).isEqualTo(1);

    }

}
