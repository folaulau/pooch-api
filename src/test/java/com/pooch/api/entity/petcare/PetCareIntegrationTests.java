package com.pooch.api.entity.petcare;

import static org.junit.jupiter.api.Assertions.*;

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
import com.pooch.api.dto.PetParentCreateDTO;
import com.pooch.api.dto.PetParentUpdateDTO;
import com.pooch.api.dto.PetSitterUuidDTO;
import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.entity.petparent.PetParentDAO;
import com.pooch.api.entity.petparent.PetParentIntegrationTests;
import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.entity.petsitter.PetSitterDAO;
import com.pooch.api.utils.ObjectUtils;

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

        PetParent petParent = testEntityGeneratorService.getDBPetParent();

        PetParentUpdateDTO petParentDTO = entityDTOMapper.mapPetParentToPetParentUpdateDTO(petParent);

        petCareCreateDTO.setPetParent(petParentDTO);

        PetSitter petSitter = testEntityGeneratorService.getDBPetSitter();

        petCareCreateDTO.setPetSitterUuid(petSitter.getUuid());

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
