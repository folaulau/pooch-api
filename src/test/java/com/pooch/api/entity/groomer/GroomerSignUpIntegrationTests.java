package com.pooch.api.entity.groomer;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.transaction.Transactional;
import javax.validation.Valid;
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
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.AddressCreateUpdateDTO;
import com.pooch.api.dto.CareServiceUpdateDTO;
import com.pooch.api.dto.CustomPage;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.GroomerCreateListingDTO;
import com.pooch.api.dto.GroomerCreateProfileDTO;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.GroomerSearchParamsDTO;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.entity.s3file.S3FileDAO;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class GroomerSignUpIntegrationTests extends IntegrationTestConfiguration {

  @Autowired
  private MockMvc mockMvc;

  @Resource
  private WebApplicationContext webApplicationContext;

  @Autowired
  private S3FileDAO s3FileDAO;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Filter springSecurityFilterChain;

  @Autowired
  private GroomerESRepository groomerESRepository;

  @MockBean
  private JwtTokenService jwtTokenService;

  @Captor
  private ArgumentCaptor<String> tokenCaptor;

  @Autowired
  private GroomerService groomerService;

  @Autowired
  private EntityDTOMapper entityDTOMapper;

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
  void itShouldRunSignUpFlow_to_market_place_valid_as_6_23_22() throws Exception {
    System.out.println("itShouldRunSignUpFlow_valid");
    // Given
    Groomer groomer = testEntityGeneratorService.getDBGroomer();
    GroomerCreateProfileDTO groomerUpdateDTO = new GroomerCreateProfileDTO();
    groomerUpdateDTO.setUuid(groomer.getUuid());
    groomerUpdateDTO.setFirstName("Folau");
    groomerUpdateDTO.setLastName("Kaveinga");
    groomerUpdateDTO.setBusinessName("Folau Dev");
    groomerUpdateDTO.setPhoneNumber(3109934731L);

    AddressCreateUpdateDTO address =
        AddressCreateUpdateDTO.builder().state("CA").street("222 Alta Ave").city("Santa Monica")
            .zipcode("90402").latitude(34.025070).longitude(-118.507700).build();
    groomerUpdateDTO.setAddress(address);

    groomerUpdateDTO.addCareService(CareServiceUpdateDTO.builder().name("Grooming").build());
    groomerUpdateDTO.addCareService(CareServiceUpdateDTO.builder().name("Dog Daycare").build());
    groomerUpdateDTO.addCareService(CareServiceUpdateDTO.builder().name("Overnight").build());

    // @formatter:on
    // When
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.put("/groomers/create-profile").header("token", GROOMER_TOKEN)
            .accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .contentType(MediaType.APPLICATION_JSON).content(ObjectUtils.toJson(groomerUpdateDTO));

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    GroomerDTO groomerDTO =
        objectMapper.readValue(contentAsString, new TypeReference<GroomerDTO>() {});

    assertThat(groomerDTO).isNotNull();
    assertThat(groomerDTO.getId()).isNotNull().isGreaterThan(0);
    assertThat(groomerDTO.getUuid()).isNotNull();
    assertThat(groomerDTO.getFirstName()).isNotNull().isEqualTo("Folau");
    assertThat(groomerDTO.getLastName()).isNotNull().isEqualTo("Kaveinga");
    assertThat(groomerDTO.getDescription()).isNotNull().isEqualTo("Test description");
    assertThat(groomerDTO.getAddress()).isNotNull();
    assertThat(groomerDTO.getCareServices()).isNotNull();
    assertThat(groomerDTO.getCareServices().size()).isEqualTo(3);
    assertThat(groomerDTO.isListing()).isNotNull().isFalse();
    assertThat(groomerDTO.getStatus()).isNotNull().isEqualTo(GroomerStatus.SIGNING_UP);

    GroomerCreateListingDTO groomerCreateListingDTO =
        GroomerCreateListingDTO.builder().description("Test description").chargePerMile(5.50)
            .instantBooking(true).numberOfOccupancy(20L).offeredDropOff(true).offeredPickUp(true)
            .uuid(groomer.getUuid()).build();

    Set<CareServiceUpdateDTO> careServices =
        entityDTOMapper.mapCareServiceDTOsToCareServiceUpdateDTOs(groomerDTO.getCareServices());

    careServices = careServices.stream().map(cs -> {
      cs.setServiceSmall(true);
      cs.setSmallPrice(RandomGeneratorUtils.getDoubleWithin(10, 20));


      cs.setServiceMedium(true);
      cs.setMediumPrice(RandomGeneratorUtils.getDoubleWithin(21, 40));


      cs.setServiceLarge(true);
      cs.setLargePrice(RandomGeneratorUtils.getDoubleWithin(41, 80));

      return cs;
    }).collect(Collectors.toSet());


    groomerCreateListingDTO.setCareServices(careServices);

    requestBuilder = MockMvcRequestBuilders.put("/groomers/create-listing")
        .header("token", GROOMER_TOKEN).accept(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8").contentType(MediaType.APPLICATION_JSON)
        .content(ObjectUtils.toJson(groomerCreateListingDTO));

    result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    contentAsString = result.getResponse().getContentAsString();

    groomerDTO = objectMapper.readValue(contentAsString, new TypeReference<GroomerDTO>() {});

    assertThat(groomerDTO).isNotNull();
    assertThat(groomerDTO.getId()).isNotNull().isGreaterThan(0);
    assertThat(groomerDTO.getUuid()).isNotNull();
    assertThat(groomerDTO.getFirstName()).isNotNull().isEqualTo("Folau");
    assertThat(groomerDTO.getLastName()).isNotNull().isEqualTo("Kaveinga");
    assertThat(groomerDTO.getDescription()).isNotNull().isEqualTo("Test description");
    assertThat(groomerDTO.getAddress()).isNotNull();
    assertThat(groomerDTO.getCareServices()).isNotNull();
    assertThat(groomerDTO.getCareServices().size()).isEqualTo(3);
    assertThat(groomerDTO.isListing()).isNotNull().isTrue();
    assertThat(groomerDTO.getStatus()).isNotNull().isEqualTo(GroomerStatus.PENDING_STRIPE);



    /**
     * check if groomer is in market place
     */
    GroomerES groomerES = entityDTOMapper.mapGroomerDTOToGroomerES(groomerDTO);
    groomerES = groomerESRepository.save(groomerES);


    GroomerSearchParamsDTO filters = new GroomerSearchParamsDTO();
    filters.setLatitude(address.getLatitude());
    filters.setLongitude(address.getLongitude());
    filters.setDistance(1);

    CustomPage<GroomerES> searchResult = groomerService.search(filters);

    System.out.println("search result");
    System.out.println(searchResult.toString());

    assertThat(searchResult).isNotNull();
    // groomer 1 and 2
    assertThat(searchResult.getTotalElements()).isGreaterThanOrEqualTo(1);

    assertThat(searchResult.getContent()).isNotNull();

    boolean groomerInList = searchResult.getContent().stream().filter(gm -> {
      if (gm.getId().equals(groomer.getId())) {
        return true;
      }
      return false;
    }).findFirst().isPresent();

    assertThat(groomerInList).isNotNull().isTrue();

  }

}
