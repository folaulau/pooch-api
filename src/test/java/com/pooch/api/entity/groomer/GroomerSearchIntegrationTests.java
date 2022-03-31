package com.pooch.api.entity.groomer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.TestEntityGeneratorService;
import com.pooch.api.dto.*;
import com.pooch.api.elastic.groomer.GroomerESDAO;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AutoConfigureMockMvc
public class GroomerSearchIntegrationTests extends IntegrationTestConfiguration {

  @Autowired private MockMvc mockMvc;

  @Resource private WebApplicationContext webApplicationContext;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private Filter springSecurityFilterChain;

  @MockBean private JwtTokenService jwtTokenService;

  @Autowired private GroomerESRepository groomerESRepository;

  @Captor private ArgumentCaptor<String> tokenCaptor;

  private String GROOMER_TOKEN = "GROOMER_TOKEN";
  private String GROOMER_UUID = "GROOMER_UUID";

  @Autowired private TestEntityGeneratorService testEntityGeneratorService;
  @Autowired private EntityDTOMapper entityDTOMapper;

  private List<GroomerES> groomers = null;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(springSecurityFilterChain)
            .build();

    JwtPayload groomerJwtPayload = new JwtPayload();
    groomerJwtPayload.setUuid(GROOMER_UUID);
    groomerJwtPayload.setRole(Authority.groomer.name());

    Mockito.when(jwtTokenService.getPayloadByToken(GROOMER_TOKEN)).thenReturn(groomerJwtPayload);

    groomers =
        List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).stream()
            .map(
                add -> {
                  GroomerES groomer =
                      entityDTOMapper.mapGroomerEntityToGroomerES(
                          testEntityGeneratorService.getGroomer());
                  groomer.setId(RandomGeneratorUtils.getLongWithin(1000, 1000000));
                  groomer.populateGeoPoints();
                  return groomerESRepository.save(groomer);
                })
            .collect(Collectors.toList());
  }

  @Disabled
  @Transactional
  @Test
  void itShouldSearch_valid() throws Exception {
    // Given
    // @formatter:on
    // When

    // address: 1625 Centinela Ave APT J, Santa Monica, CA 90404
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/groomers/search")
            .header("token", GROOMER_TOKEN)
            .accept(MediaType.APPLICATION_JSON)
                .queryParam("lat","34.0737122")
                .queryParam("lon","-118.197979")

            .contentType(MediaType.APPLICATION_JSON);

    MvcResult result =
        this.mockMvc
            .perform(requestBuilder)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    CustomPage<GroomerES> searchResult =
        objectMapper.readValue(contentAsString, new TypeReference<CustomPage<GroomerES>>() {});

    assertThat(searchResult).isNotNull();
  }

  @AfterEach
  void cleanUp() {
    groomers.stream()
        .forEach(
            groomer -> {
              groomerESRepository.deleteById(groomer.getId());
            });
  }
}
