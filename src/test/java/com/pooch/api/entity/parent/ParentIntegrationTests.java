package com.pooch.api.entity.parent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
import com.pooch.api.dto.AddressCreateUpdateDTO;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.CareServiceUpdateDTO;
import com.pooch.api.dto.GroomerCreateProfileDTO;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.GroomerUpdateDTO;
import com.pooch.api.dto.ParentCreateUpdateDTO;
import com.pooch.api.dto.ParentDTO;
import com.pooch.api.dto.ParentUpdateDTO;
import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.dto.VaccineCreateDTO;
import com.pooch.api.dto.VaccineDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Gender;
import com.pooch.api.entity.pooch.Training;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.library.firebase.FirebaseAuthService;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

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
                .characterEncoding("utf-8")
                .header("token", PARENT_TOKEN);

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        List<S3FileDTO> S3FileDTOs = objectMapper.readValue(contentAsString, new TypeReference<List<S3FileDTO>>() {});

        assertThat(S3FileDTOs).isNotNull();
        assertThat(S3FileDTOs.size()).isNotNull().isGreaterThan(0);

    }

    @Transactional
    @Test
    void itShouldUpdateJustProfile_valid() throws Exception {
        System.out.println("itShouldUpdateProfile_valid");
        // Given
        Parent parent = testEntityGeneratorService.getDBParent();

        // @formatter:off
        AddressCreateUpdateDTO address = AddressCreateUpdateDTO.builder()
                .state("CA")
                .street("222 Alta Ave")
                .city("Santa Monica")
                .zipcode("90402")
                .latitude(34.025070)
                .longitude(-118.507700).build();
        
        ParentUpdateDTO parentUpdateDTO = ParentUpdateDTO.builder()
                .uuid(parent.getUuid())
                .countryCode(1)
                .phoneNumber(3109944731L)
                .fullName("Folau Kaveinga")
                .address(address)
                .build();

        
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/parents/profile")
                .header("token", PARENT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(parentUpdateDTO));

        // @formatter:on

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        ParentDTO parentDTO = objectMapper.readValue(contentAsString, new TypeReference<ParentDTO>() {});

        assertThat(parentDTO).isNotNull();
        assertThat(parentDTO.getId()).isNotNull().isGreaterThan(0);
        assertThat(parentDTO.getUuid()).isNotNull();
        assertThat(parentDTO.getFullName()).isNotNull().isEqualTo("Folau Kaveinga");
        assertThat(parentDTO.getUuid()).isNotNull();
        assertThat(parentDTO.getPhoneNumber()).isNotNull().isEqualTo(3109944731L);
        assertThat(parentDTO.getCountryCode()).isNotNull().isEqualTo(1);
        assertThat(parentDTO.getAddress()).isNotNull();
        assertThat(parentDTO.getAddress().getId()).isNotNull().isGreaterThan(0);
        assertThat(parentDTO.getAddress().getStreet()).isNotNull().isEqualTo("222 Alta Ave");
        assertThat(parentDTO.getAddress().getCity()).isNotNull().isEqualTo("Santa Monica");
        assertThat(parentDTO.getAddress().getZipcode()).isNotNull().isEqualTo("90402");
        assertThat(parentDTO.getAddress().getState()).isNotNull().isEqualTo("CA");

    }

    @Transactional
    @Test
    void itShouldUpdatePooches_valid() throws Exception {
        System.out.println("itShouldUpdatePooches_valid");
        // Given
        Parent parent = testEntityGeneratorService.getDBParent();

        // @formatter:off
        ParentUpdateDTO parentUpdateDTO = ParentUpdateDTO.builder()
                .uuid(parent.getUuid())
                .build();
        
        PoochCreateUpdateDTO pooch = PoochCreateUpdateDTO.builder()
                .gender(Gender.Female)
                .dob(LocalDate.now().minusYears(2))
                .fullName("Simpa")
                .training(Training.Low)
                .spayed(true)
                .build();
        
        pooch.addFoodSchedule(FoodSchedule.Morning);
        pooch.addFoodSchedule(FoodSchedule.Night);
        
        pooch.addVaccine(VaccineCreateDTO.builder()
                .name("vitamin")
                .expireDate(LocalDateTime.now().plusMonths(3))
                .build());
        
        parentUpdateDTO.addPooch(pooch);        
        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/parents/profile")
                .header("token", PARENT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(parentUpdateDTO));

        // @formatter:on

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        ParentDTO parentDTO = objectMapper.readValue(contentAsString, new TypeReference<ParentDTO>() {});

        assertThat(parentDTO).isNotNull();
        assertThat(parentDTO.getId()).isNotNull().isGreaterThan(0);
        assertThat(parentDTO.getUuid()).isNotNull();
        assertThat(parentDTO.getPooches()).isNotNull();
        assertThat(parentDTO.getPooches().size()).isNotNull().isGreaterThan(0);

        assertThat(parentDTO.getPooches().get(0)).isNotNull();
        assertThat(parentDTO.getPooches().get(0).getId()).isNotNull().isGreaterThan(0);
        assertThat(parentDTO.getPooches().get(0).getFullName()).isNotNull().isEqualTo("Simpa");
        assertThat(parentDTO.getPooches().get(0).getSpayed()).isNotNull().isTrue();
        assertThat(parentDTO.getPooches().get(0).getFoodSchedule()).isNotNull().isEqualTo(Arrays.asList(FoodSchedule.Night, FoodSchedule.Morning));

        Set<VaccineDTO> vaccines = parentDTO.getPooches().get(0).getVaccines();

        assertThat(vaccines).isNotNull();
        assertThat(vaccines.size()).isGreaterThan(0);

        for (VaccineDTO vaccineDTO : vaccines) {
            assertThat(vaccineDTO).isNotNull();
            assertThat(vaccineDTO.getId()).isNotNull().isGreaterThan(0);
            assertThat(vaccineDTO.getExpireDate()).isNotNull();
            assertThat(vaccineDTO.getName()).isNotNull().isEqualTo("vitamin");
        }

        assertThat(parentDTO.getPooches().get(0).getGender()).isNotNull().isEqualTo(Gender.Female);
        assertThat(parentDTO.getPooches().get(0).getTraining()).isNotNull().isEqualTo(Training.Low);

    }

}
