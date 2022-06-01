package com.pooch.api.entity.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.test.util.ReflectionTestUtils;
import com.pooch.api.dto.BookingCareServiceCreateDTO;
import com.pooch.api.dto.PoochBookingCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.utils.TestEntityGeneratorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BookingCalculatorServiceTests {

  @InjectMocks
  BookingCalculatorService calculatorService = new BookingCalculatorServiceImp();

  TestEntityGeneratorService testEntityGeneratorService = new TestEntityGeneratorService();

  @Mock
  private CareServiceDAO careServiceDAO;
  
  private double bookingFee = 10.0;

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(calculatorService, "bookingFee", bookingFee);
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

  }

  @Test
  void itShouldGeneratePaymentIntentDetails_with_1_care_service_and_stripe_not_setup() {

    Groomer groomer = testEntityGeneratorService.getActiveGroomer();
    groomer.setId(1L);

    Parent parent = testEntityGeneratorService.getParent();
    parent.setId(1L);

    double pickUpCost = 20;
    double dropOffCost = 20;

    int numOfDays = 1;
    LocalDateTime startDateTime = LocalDateTime.of(2022, 6, 10, 8, 0);
    LocalDateTime endDateTime = startDateTime.plusDays(numOfDays);

    Set<PoochBookingCreateDTO> pooches = new HashSet<>();

    int numOfPooches = 1;

    String careService1_UUID = "careService1_UUID";

    Mockito.when(careServiceDAO.getByUuidAndGroomer(careService1_UUID, groomer.getId()))
        .thenReturn(Optional.of(CareService.builder().name("Grooming").largePrice(35.0)
            .serviceLarge(true).uuid(careService1_UUID).build()));

    for (int i = 0; i < numOfPooches; i++) {
      PoochBookingCreateDTO pooch = new PoochBookingCreateDTO();
      pooch.addService(
          BookingCareServiceCreateDTO.builder().size("large").uuid(careService1_UUID).build());
      pooches.add(pooch);
    }


    BookingCostDetails bookingCostDetails = calculatorService.calculateBookingDetailCosts(
        BookingCalculatorSender.CREATE_PAYMENTINTENT,
        groomer, parent, pickUpCost, dropOffCost, startDateTime, endDateTime, pooches);

    assertThat(bookingCostDetails).isNotNull();
    assertThat(bookingCostDetails.getGroomerStripeReady()).isFalse();
    assertThat(bookingCostDetails.getStripeFee()).isEqualTo(0.0);
    assertThat(bookingCostDetails.getBookingFee()).isEqualTo(bookingFee);
    assertThat(bookingCostDetails.getTotalChargeAtBooking()).isEqualTo(10.0);
    assertThat(bookingCostDetails.getTotalChargeAtDropOff()).isEqualTo(75.0);
    assertThat(bookingCostDetails.getCareServicesCost()).isEqualTo(35.0);
    assertThat(bookingCostDetails.getDropOffCost()).isEqualTo(20.0);
    assertThat(bookingCostDetails.getPickUpCost()).isEqualTo(20.0);
    assertThat(bookingCostDetails.getTotalAmount()).isEqualTo(85.0);
    assertThat(bookingCostDetails.getNumberOfDays()).isEqualTo(numOfDays);

  }

  @Test
  void itShouldGeneratePaymentIntentDetails_with_multiple_care_services_and_stripe_not_setup() {

    Groomer groomer = testEntityGeneratorService.getActiveGroomer();
    groomer.setId(1L);


    Parent parent = testEntityGeneratorService.getParent();
    parent.setId(1L);

    double pickUpCost = 15;
    double dropOffCost = 15;

    int numOfDays = 2;
    LocalDateTime startDateTime = LocalDateTime.of(2022, 6, 10, 8, 0);
    LocalDateTime endDateTime = startDateTime.plusDays(numOfDays);

    Set<PoochBookingCreateDTO> pooches = new HashSet<>();

    int numOfPooches = 1;

    String careService1_UUID = "careService1_UUID";
    String careService2_UUID = "careService2_UUID";

    Mockito.when(careServiceDAO.getByUuidAndGroomer(careService1_UUID, groomer.getId()))
        .thenReturn(Optional.of(CareService.builder().name("Grooming").largePrice(35.0)
            .serviceLarge(true).uuid(careService1_UUID).build()));

    Mockito.when(careServiceDAO.getByUuidAndGroomer(careService2_UUID, groomer.getId()))
        .thenReturn(Optional.of(CareService.builder().name("Nail Clipping").mediumPrice(23.0)
            .serviceMedium(true).uuid(careService1_UUID).build()));

    for (int i = 0; i < numOfPooches; i++) {
      PoochBookingCreateDTO pooch = new PoochBookingCreateDTO();
      pooch.addService(
          BookingCareServiceCreateDTO.builder().size("large").uuid(careService1_UUID).build());
      pooch.addService(
          BookingCareServiceCreateDTO.builder().size("medium").uuid(careService2_UUID).build());
      pooches.add(pooch);
    }


    BookingCostDetails bookingCostDetails = calculatorService.calculateBookingDetailCosts(
        BookingCalculatorSender.CREATE_PAYMENTINTENT,
        groomer, parent, pickUpCost, dropOffCost, startDateTime, endDateTime, pooches);

    assertThat(bookingCostDetails).isNotNull();
    assertThat(bookingCostDetails.getGroomerStripeReady()).isFalse();
    assertThat(bookingCostDetails.getNumberOfDays()).isEqualTo(numOfDays);
    assertThat(bookingCostDetails.getStripeFee()).isEqualTo(0.0);
    assertThat(bookingCostDetails.getBookingFee()).isEqualTo(bookingFee);
    assertThat(bookingCostDetails.getTotalChargeAtBooking()).isEqualTo(10.0);
    assertThat(bookingCostDetails.getTotalChargeAtDropOff()).isEqualTo(146.0);
    assertThat(bookingCostDetails.getCareServicesCost()).isEqualTo(116.0);
    assertThat(bookingCostDetails.getDropOffCost()).isEqualTo(15.0);
    assertThat(bookingCostDetails.getPickUpCost()).isEqualTo(15.0);
    assertThat(bookingCostDetails.getTotalAmount()).isEqualTo(156.0);

  }



  @Test
  void itShouldGeneratePaymentIntentDetails_with_multiple_care_services_and_stripe_setup() {

    Groomer groomer = testEntityGeneratorService.getActiveGroomer();
    groomer.setId(1L);
    groomer.setStripeDetailsSubmitted(true);
    groomer.setStripeChargesEnabled(true);
    groomer.setStripePayoutsEnabled(true);

    Parent parent = testEntityGeneratorService.getParent();
    parent.setId(1L);

    double pickUpCost = 15;
    double dropOffCost = 15;

    int numOfDays = 2;
    LocalDateTime startDateTime = LocalDateTime.of(2022, 6, 10, 8, 0);
    LocalDateTime endDateTime = startDateTime.plusDays(numOfDays);

    Set<PoochBookingCreateDTO> pooches = new HashSet<>();

    int numOfPooches = 1;

    String careService1_UUID = "careService1_UUID";
    String careService2_UUID = "careService2_UUID";

    Mockito.when(careServiceDAO.getByUuidAndGroomer(careService1_UUID, groomer.getId()))
        .thenReturn(Optional.of(CareService.builder().name("Grooming").largePrice(35.0)
            .serviceLarge(true).uuid(careService1_UUID).build()));

    Mockito.when(careServiceDAO.getByUuidAndGroomer(careService2_UUID, groomer.getId()))
        .thenReturn(Optional.of(CareService.builder().name("Nail Clipping").mediumPrice(23.0)
            .serviceMedium(true).uuid(careService1_UUID).build()));

    for (int i = 0; i < numOfPooches; i++) {
      PoochBookingCreateDTO pooch = new PoochBookingCreateDTO();
      pooch.addService(
          BookingCareServiceCreateDTO.builder().size("large").uuid(careService1_UUID).build());
      pooch.addService(
          BookingCareServiceCreateDTO.builder().size("medium").uuid(careService2_UUID).build());
      pooches.add(pooch);
    }


    BookingCostDetails bookingCostDetails = calculatorService.calculateBookingDetailCosts(
        BookingCalculatorSender.CREATE_PAYMENTINTENT,
        groomer, parent, pickUpCost, dropOffCost, startDateTime, endDateTime, pooches);

    assertThat(bookingCostDetails).isNotNull();
    assertThat(bookingCostDetails.getGroomerStripeReady()).isTrue();
    assertThat(bookingCostDetails.getNumberOfDays()).isEqualTo(numOfDays);
    assertThat(bookingCostDetails.getStripeFee()).isEqualTo(4.53);
    assertThat(bookingCostDetails.getBookingFee()).isEqualTo(bookingFee);
    assertThat(bookingCostDetails.getCareServicesCost()).isEqualTo(116.0);
    assertThat(bookingCostDetails.getDropOffCost()).isEqualTo(15.0);
    assertThat(bookingCostDetails.getPickUpCost()).isEqualTo(15.0);
    assertThat(bookingCostDetails.getTotalChargeAtBooking()).isEqualTo(160.53);
    assertThat(bookingCostDetails.getTotalChargeAtDropOff()).isEqualTo(0.0);
    assertThat(bookingCostDetails.getTotalAmount()).isEqualTo(160.53);

  }

}
