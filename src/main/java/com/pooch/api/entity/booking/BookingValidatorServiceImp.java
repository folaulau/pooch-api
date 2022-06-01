package com.pooch.api.entity.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCareServiceCreateDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.dto.ParentUpdateDTO;
import com.pooch.api.dto.PoochBookingCreateDTO;
import com.pooch.api.dto.GroomerUuidDTO;
import com.pooch.api.dto.ParentCreateUpdateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochDAO;
import com.pooch.api.entity.pooch.PoochSize;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.stripe.StripeMetadataService;
import com.pooch.api.library.stripe.paymentintent.StripePaymentIntentService;
import com.stripe.model.PaymentIntent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingValidatorServiceImp implements BookingValidatorService {

  @Autowired
  private GroomerDAO groomerDAO;

  @Autowired
  private ParentDAO parentDAO;

  @Autowired
  private PoochDAO poochDAO;

  @Autowired
  private BookingDAO bookingDAO;

  @Autowired
  private CareServiceDAO careServiceDAO;

  @Autowired
  private StripePaymentIntentService stripePaymentIntentService;

  @Autowired
  private BookingCalculatorService bookingCalculatorService;

  @Override
  public void validateBook(BookingCreateDTO bookingCreateDTO) {

    String parentUuid = bookingCreateDTO.getParentUuid();

    if (parentUuid == null || parentUuid.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "parent.uuid is required");
    }

    Parent parent = parentDAO.getByUuid(parentUuid).orElseThrow(
        () -> new ApiException(ApiError.DEFAULT_MSG, "parent not found for uuid=" + parentUuid));

    String groomerUuid = bookingCreateDTO.getGroomerUuid();

    if (groomerUuid == null || groomerUuid.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "groomerUuid is required");
    }

    Groomer groomer = groomerDAO.getByUuid(groomerUuid).orElseThrow(
        () -> new ApiException("Groomer not found", "groomer not found for uuid=" + groomerUuid));

    if (bookingCreateDTO.getAgreedToContracts() == null) {
      throw new ApiException(ApiError.DEFAULT_MSG, "Please agree to contracts",
          "agreedToContracts is null");
    }


    Double dropOffCost = bookingCreateDTO.getDropOffCost();

    Double pickUpCost = bookingCreateDTO.getPickUpCost();

    LocalDateTime startDateTime = bookingCreateDTO.getStartDateTime();

    LocalDateTime endDateTime = bookingCreateDTO.getEndDateTime();

    endDateTime = endDateTime.withSecond(0).withNano(0);

    int numberOfDays = 0;

    LocalDateTime countFromStartDateTime = startDateTime;

    do {
      numberOfDays++;
      countFromStartDateTime = countFromStartDateTime.plusDays(1);
    } while (countFromStartDateTime.isBefore(endDateTime));

    /**
     * Pets
     */
    Set<PoochBookingCreateDTO> petCreateDTOs = bookingCreateDTO.getPooches();

    Pair<Double, Double> bookingCosts = bookingCalculatorService.calculateBookingCareServicesCost(
        groomer, parent, pickUpCost, dropOffCost, startDateTime, endDateTime, petCreateDTOs);

    Double calculatedBookingCost = bookingCosts.getFirst();
    Double totalBookingCost = bookingCosts.getSecond();

    String paymentIntentId = bookingCreateDTO.getPaymentIntentId();

    if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "PaymentIntentId is required");
    }

    com.stripe.model.PaymentIntent paymentIntent =
        stripePaymentIntentService.getById(paymentIntentId);

    if (paymentIntent == null) {
      log.info("paymentIntent is null");
      throw new ApiException(ApiError.DEFAULT_MSG,
          "PaymentIntent not found for id=" + paymentIntentId);
    }

    if (!paymentIntent.getStatus().equalsIgnoreCase("succeeded")) {
      throw new ApiException(ApiError.DEFAULT_MSG, "Payment has not been made",
          "stripe issue, booking payment",
          "payment status should be succeeded",
          "payment status=" + paymentIntent.getStatus());
    }

    if (parent.getStripeCustomerId() != null && paymentIntent.getCustomer() != null
        && !parent.getStripeCustomerId().equalsIgnoreCase(paymentIntent.getCustomer())) {
      throw new ApiException(ApiError.DEFAULT_MSG, "You are charging a different parent",
          "stripe issue, booking payment",
          "parent stripeCustomerId is not the same as the customerId of the paymentIntent");
    }

    BookingCostDetails costDetails = BookingCostDetails.fromJson(
        paymentIntent.getMetadata().get(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS));


    if (groomer.isStripeReady()) {
      /**
       * totalChargeAtBooking = all<br>
       * totalChargeAtDropOff = 0<br>
       * bookingCost + bookingFee + stripeFee <br>
       */

      if (!costDetails.getTotalChargeAtBooking().equals(totalBookingCost.doubleValue())) {
        throw new ApiException("Incorrect payment value",
            "today's charge: " + costDetails.getTotalChargeAtBooking(),
            "today's charge should be: " + totalBookingCost.doubleValue(),
            "formula: bookingCost + bookingFee + stripeFee + dropOffCost + pickUpCost",
            "calculated bookingCost: " + calculatedBookingCost.doubleValue(),
            "bookingFee: " + costDetails.getBookingFee(),
            "stripeFee: " + costDetails.getStripeFee(), "pickUpCost: " + pickUpCost,
            "dropOffCost: " + dropOffCost, "numberOfDays: " + numberOfDays);
      }

      if (costDetails.getTotalChargeAtDropOff() != null
          && !(costDetails.getTotalChargeAtDropOff().equals(0.0D)
              || costDetails.getTotalChargeAtDropOff().equals(0D))) {
        throw new ApiException("Incorrect payment value",
            "TotalChargeAtDropOff should be 0 but it's " + costDetails.getTotalChargeAtDropOff());
      }


    } else {
      /**
       * booking fee
       */
      if (!costDetails.getBookingFee().equals(costDetails.getTotalChargeAtBooking())) {
        throw new ApiException(ApiError.DEFAULT_MSG,
            "today's charge should be just the booking fee");
      }

      BigDecimal calculatedChargeAtDropOff = BigDecimal.valueOf(0.0);
      /**
       * calculatedBookingCost + dropOffCost + pickUpCost
       */
      if (dropOffCost != null && dropOffCost >= 0) {
        calculatedChargeAtDropOff = calculatedChargeAtDropOff.add(BigDecimal.valueOf(dropOffCost));
      }

      if (pickUpCost != null && pickUpCost >= 0) {
        calculatedChargeAtDropOff = calculatedChargeAtDropOff.add(BigDecimal.valueOf(pickUpCost));
      }

      calculatedChargeAtDropOff =
          calculatedChargeAtDropOff.add(BigDecimal.valueOf(calculatedBookingCost));

      if (!costDetails.getTotalChargeAtDropOff().equals(calculatedChargeAtDropOff.doubleValue())) {
        throw new ApiException("Incorrect payment value",
            "chargeAtDropOff: " + costDetails.getTotalChargeAtDropOff(),
            "chargeAtDropOff should be: " + calculatedChargeAtDropOff.doubleValue(),
            "formula: bookingCost + dropOffCost + pickUpCost",
            "calculated bookingCost: " + calculatedBookingCost.doubleValue(),
            "pickUpCost: " + pickUpCost, "dropOffCost: " + dropOffCost,
            "numberOfDays: " + numberOfDays);
      }

    }
  }

  @Override
  public Booking validateCancel(BookingCancelDTO bookingCancelDTO) {
    // TODO Auto-generated method stub
    String uuid = bookingCancelDTO.getUuid();
    if (uuid == null) {
      throw new ApiException(ApiError.DEFAULT_MSG, "uuid is required");
    }

    Optional<Booking> optBooking = bookingDAO.getByUuid(uuid);

    if (!optBooking.isPresent()) {
      throw new ApiException("Booking not found", "booking not found for uuid=" + uuid);
    }

    Booking booking = optBooking.get();

    return booking;
  }

}
