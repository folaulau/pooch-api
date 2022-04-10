package com.pooch.api.entity.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.ParentCreateUpdateDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.PoochCreateDTO;
import com.pooch.api.dto.VaccineCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Gender;
import com.pooch.api.entity.pooch.Training;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class BookingIntegrationTests extends IntegrationTestConfiguration {

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

    @Autowired
    private GroomerDAO                 petSitterDAO;

    @Autowired
    private ParentDAO                  petParentDAO;

    @Autowired
    private EntityDTOMapper            entityDTOMapper;

    @Autowired
    private TestEntityGeneratorService testEntityGeneratorService;

    private String                     TEST_PETPARENT_TOKEN = "TEST_PETPARENT_TOKEN";
    private String                     TEST_PETSITTER_TOKEN = "TEST_PETSITTER_TOKEN";

    private String                     TEST_PETPARENT_UUID  = "TEST_PETPARENT_UUID";
    private String                     TEST_PETSITTER_UUID  = "TEST_PETSITTER_UUID";

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        JwtPayload petParentJwtPayload = new JwtPayload();
        petParentJwtPayload.setUuid(TEST_PETPARENT_UUID);
        petParentJwtPayload.setRole(Authority.parent.name());

        Mockito.when(jwtTokenService.getPayloadByToken(TEST_PETPARENT_TOKEN)).thenReturn(petParentJwtPayload);

        JwtPayload petSitterJwtPayload = new JwtPayload();
        petSitterJwtPayload.setUuid(TEST_PETSITTER_UUID);
        petSitterJwtPayload.setRole(Authority.parent.name());

        Mockito.when(jwtTokenService.getPayloadByToken(TEST_PETSITTER_TOKEN)).thenReturn(petSitterJwtPayload);
    }

    /**
     * 
     * @throws Exception
     */
    @Transactional
    @Test
    void itShouldMakeBooking_valid() throws Exception {
        // Given
        BookingCreateDTO petCareCreateDTO = new BookingCreateDTO();

        /**
         * Pet Parent
         */
        Parent petParent = testEntityGeneratorService.getDBParent();

        ParentCreateUpdateDTO petParentDTO = entityDTOMapper.mapParentToParentCreateUpdateDTO(petParent);

        petCareCreateDTO.setParent(petParentDTO);

        /**
         * Pet Sitter
         */
        Groomer groomer = testEntityGeneratorService.getDBGroomer();

        petCareCreateDTO.setGroomerUuid(groomer.getUuid());

        /**
         * Pets
         */
        Set<PoochCreateDTO> petCreateDTOs = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            PoochCreateDTO petCreateDTO = new PoochCreateDTO();
            petCreateDTO.setDob(LocalDate.now().minusMonths(RandomGeneratorUtils.getLongWithin(6, 36)));
            petCreateDTO.setBreed("Bulldog");
            petCreateDTO.setFullName(RandomGeneratorUtils.getRandomFullName());
            petCreateDTO.setGender(Gender.Female);
            petCreateDTO.setSpayed(true);
            petCreateDTO.setTraining(Training.Medium);
            petCreateDTO.setWeight(15D);
            petCreateDTO.addFoodSchedule(FoodSchedule.Morning);
            petCreateDTO.addFoodSchedule(FoodSchedule.Night);
            petCreateDTO.addVaccine(new VaccineCreateDTO(LocalDateTime.now().plusMonths(2), "vac1"));
            petCreateDTO.addVaccine(new VaccineCreateDTO(LocalDateTime.now().plusMonths(6), "vac2"));
            petCreateDTOs.add(petCreateDTO);
        }

        petCareCreateDTO.setPooches(petCreateDTOs);
        // When

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bookings/book")
                .header("token", TEST_PETPARENT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(petCareCreateDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        // Then
        // --verify
        // --assert

    }
}
