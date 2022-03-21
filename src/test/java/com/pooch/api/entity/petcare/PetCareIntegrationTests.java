package com.pooch.api.entity.petcare;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.stream;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.EntityGenerator;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.TestEntityGeneratorService;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PetCareCreateDTO;
import com.pooch.api.dto.PetCreateDTO;
import com.pooch.api.dto.PetParentCreateDTO;
import com.pooch.api.dto.PetParentUpdateDTO;
import com.pooch.api.dto.PetSitterUuidDTO;
import com.pooch.api.dto.VaccineCreateDTO;
import com.pooch.api.entity.pet.Breed;
import com.pooch.api.entity.pet.FoodSchedule;
import com.pooch.api.entity.pet.Gender;
import com.pooch.api.entity.pet.Training;
import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.entity.petparent.PetParentDAO;
import com.pooch.api.entity.petparent.PetParentIntegrationTests;
import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.entity.petsitter.PetSitterDAO;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class PetCareIntegrationTests extends IntegrationTestConfiguration {

    @Autowired
    private MockMvc                    mockMvc;

    @Autowired
    private ObjectMapper               objectMapper;

    @Autowired
    private PetSitterDAO               petSitterDAO;

    @Autowired
    private PetParentDAO               petParentDAO;

    @Autowired
    private EntityDTOMapper            entityDTOMapper;

    @Autowired
    private TestEntityGeneratorService testEntityGeneratorService;

    /**
     * 
     * @throws Exception
     */
    @Transactional
    @Test
    void itShouldBookPetCare_valid() throws Exception {
        // Given
        PetCareCreateDTO petCareCreateDTO = new PetCareCreateDTO();

        /**
         * Pet Parent
         */
        PetParent petParent = testEntityGeneratorService.getDBPetParent();

        PetParentUpdateDTO petParentDTO = entityDTOMapper.mapPetParentToPetParentUpdateDTO(petParent);

        petCareCreateDTO.setPetParent(petParentDTO);

        /**
         * Pet Sitter
         */
        PetSitter petSitter = testEntityGeneratorService.getDBPetSitter();

        petCareCreateDTO.setPetSitterUuid(petSitter.getUuid());

        /**
         * Pets
         */
        Set<PetCreateDTO> petCreateDTOs = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            PetCreateDTO petCreateDTO = new PetCreateDTO();
            petCreateDTO.setDob(LocalDate.now().minusMonths(RandomGeneratorUtils.getLongWithin(6, 36)));
            petCreateDTO.setBreed(Breed.Bulldog);
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

        petCareCreateDTO.setPets(petCreateDTOs);

        // When

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/petcares/book")
                .header("token", "test-token")
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
