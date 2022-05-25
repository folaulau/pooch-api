package com.pooch.api.entity.booking;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.ParentCreateUpdateDTO;
import com.pooch.api.dto.ParentDTO;
import com.pooch.api.dto.PoochBookingCreateDTO;
import com.pooch.api.dto.BookingCareServiceDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.BookingDTO;
import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.dto.VaccineCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Gender;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochSize;
import com.pooch.api.entity.pooch.Training;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.library.stripe.paymentintent.StripePaymentIntentService;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.MathUtils;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;
import com.stripe.model.PaymentIntent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class BookingIntegrationTests extends IntegrationTestConfiguration {

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

  @Autowired
  private GroomerDAO petSitterDAO;

  @Autowired
  private ParentDAO petParentDAO;

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Autowired
  private TestEntityGeneratorService testEntityGeneratorService;

  private String TEST_PETPARENT_TOKEN = "TEST_PETPARENT_TOKEN";
  private String TEST_PETSITTER_TOKEN = "TEST_PETSITTER_TOKEN";

  private String TEST_PETPARENT_UUID = "TEST_PETPARENT_UUID";
  private String TEST_PETSITTER_UUID = "TEST_PETSITTER_UUID";

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilters(springSecurityFilterChain).build();

    JwtPayload petParentJwtPayload = new JwtPayload();
    petParentJwtPayload.setUuid(TEST_PETPARENT_UUID);
    petParentJwtPayload.setRole(Authority.parent.name());

    Mockito.when(jwtTokenService.getPayloadByToken(TEST_PETPARENT_TOKEN))
        .thenReturn(petParentJwtPayload);

    JwtPayload petSitterJwtPayload = new JwtPayload();
    petSitterJwtPayload.setUuid(TEST_PETSITTER_UUID);
    petSitterJwtPayload.setRole(Authority.parent.name());

    Mockito.when(jwtTokenService.getPayloadByToken(TEST_PETSITTER_TOKEN))
        .thenReturn(petSitterJwtPayload);
  }

  /**
   * First time parent makes a booking
   */
  // @Transactional
  @Test
  void itShouldMakeBooking_first_time_valid() throws Exception {
    // Given
    BookingCreateDTO bookingCreateDTO = new BookingCreateDTO();

    bookingCreateDTO.setStartDateTime(LocalDateTime.now().plusDays(1));
    bookingCreateDTO.setEndDateTime(LocalDateTime.now().plusDays(3));

    /**
     * Pet Parent
     */
    Parent petParent = testEntityGeneratorService.getDBParent();
    Pooch pooch1 = testEntityGeneratorService.getDBPooch(petParent);


    Double bookingCost =
        MathUtils.getTwoDecimalPlaces(RandomGeneratorUtils.getDoubleWithin(20, 300));

    String paymentMethodId = testEntityGeneratorService.getPaymentMethod(petParent.getFullName());

    com.stripe.model.PaymentIntent paymentIntent =
        testEntityGeneratorService.createAndConfirmPaymentIntent(bookingCost, paymentMethodId);


    ParentCreateUpdateDTO petParentDTO =
        entityDTOMapper.mapParentToParentCreateUpdateDTO(petParent);

    bookingCreateDTO.setParent(petParentDTO);

    bookingCreateDTO.setAgreedToContracts(true);

    bookingCreateDTO.setPaymentIntentId(paymentIntent.getId());

    /**
     * Pet Sitter
     */
    Groomer groomer = testEntityGeneratorService.getDBGroomer();

    bookingCreateDTO.setGroomerUuid(groomer.getUuid());

    // @formatter:off

        CareService careService= testEntityGeneratorService.getDBCareService(groomer);
        
//        bookingCreateDTO.addService(BookingCareServiceDTO.builder()
//                .size(PoochSize.medium)
//                .uuid(careService.getUuid())
//                .build());
        
        // @formatter:on

    // com.stripe.model.PaymentIntent paymentIntent = new com.stripe.model.PaymentIntent();
    // paymentIntent.setStatus("succeeded");
    // Mockito.when(stripePaymentIntentService.getById(Mockito.anyString())).thenReturn(paymentIntent);

    /**
     * Pets
     */
    Set<PoochBookingCreateDTO> petCreateDTOs = new HashSet<>();
    for (int i = 0; i < 1; i++) {
      PoochBookingCreateDTO petCreateDTO = new PoochBookingCreateDTO();
      petCreateDTO.setUuid(pooch1.getUuid());
      petCreateDTO.addService(BookingCareServiceDTO.builder().size(PoochSize.medium)
          .uuid(careService.getUuid()).build());

      petCreateDTOs.add(petCreateDTO);
    }

    bookingCreateDTO.setPooches(petCreateDTOs);
    // When

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/bookings/book").header("token", TEST_PETPARENT_TOKEN)
            .accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .contentType(MediaType.APPLICATION_JSON).content(ObjectUtils.toJson(bookingCreateDTO));

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    BookingDTO bookingDTO =
        objectMapper.readValue(contentAsString, new TypeReference<BookingDTO>() {});

    assertThat(bookingDTO).isNotNull();
    assertThat(bookingDTO.getId()).isNotNull().isGreaterThan(0);
    assertThat(bookingDTO.getUuid()).isNotNull();
    assertThat(bookingDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);
  }

  /**
   * 
   * @throws Exception
   */
  @Transactional
  @Test
  void itShouldMakeBooking_second_time_valid() throws Exception {
    // Given
    BookingCreateDTO bookingCreateDTO = new BookingCreateDTO();

    bookingCreateDTO.setStartDateTime(LocalDateTime.now().plusDays(1));
    bookingCreateDTO.setEndDateTime(LocalDateTime.now().plusDays(3));

    /**
     * Pet Parent
     */
    Parent petParent = testEntityGeneratorService.getDBParent();
    Pooch pooch1 = testEntityGeneratorService.getDBPooch(petParent);

    Double bookingCost =
        MathUtils.getTwoDecimalPlaces(RandomGeneratorUtils.getDoubleWithin(20, 300));

    com.stripe.model.Customer customer = testEntityGeneratorService.createCustomer(petParent);

    String paymentMethodId = testEntityGeneratorService.addPaymentMethodToCustomer(customer);

    com.stripe.model.PaymentIntent paymentIntent = testEntityGeneratorService
        .createAndConfirmPaymentIntent(bookingCost, paymentMethodId, customer.getId());

    ParentCreateUpdateDTO petParentDTO =
        entityDTOMapper.mapParentToParentCreateUpdateDTO(petParent);

    bookingCreateDTO.setParent(petParentDTO);

    bookingCreateDTO.setAgreedToContracts(true);

    bookingCreateDTO.setPaymentIntentId(paymentIntent.getId());

    /**
     * Pet Sitter
     */
    Groomer groomer = testEntityGeneratorService.getDBGroomer();

    bookingCreateDTO.setGroomerUuid(groomer.getUuid());

    // @formatter:off

        CareService careService= testEntityGeneratorService.getDBCareService(groomer);
      
        
        // @formatter:on

    // com.stripe.model.PaymentIntent paymentIntent = new com.stripe.model.PaymentIntent();
    // paymentIntent.setStatus("succeeded");
    // Mockito.when(stripePaymentIntentService.getById(Mockito.anyString())).thenReturn(paymentIntent);

    /**
     * Pets
     */
    Set<PoochBookingCreateDTO> petCreateDTOs = new HashSet<>();
    for (int i = 0; i < 1; i++) {
      PoochBookingCreateDTO petCreateDTO = new PoochBookingCreateDTO();
      petCreateDTO.setUuid(pooch1.getUuid());
      petCreateDTO.addService(BookingCareServiceDTO.builder().size(PoochSize.medium)
          .uuid(careService.getUuid()).build());

      petCreateDTOs.add(petCreateDTO);
    }

    bookingCreateDTO.setPooches(petCreateDTOs);
    // When

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/bookings/book").header("token", TEST_PETPARENT_TOKEN)
            .accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .contentType(MediaType.APPLICATION_JSON).content(ObjectUtils.toJson(bookingCreateDTO));

    MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    BookingDTO bookingDTO =
        objectMapper.readValue(contentAsString, new TypeReference<BookingDTO>() {});

    assertThat(bookingDTO).isNotNull();
    assertThat(bookingDTO.getId()).isNotNull().isGreaterThan(0);
    assertThat(bookingDTO.getUuid()).isNotNull();
    assertThat(bookingDTO.getBookingCost()).isNotNull().isEqualTo(bookingCost);

  }


}
