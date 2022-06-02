package com.pooch.api.entity.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.Triple;
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
import com.pooch.api.utils.MathUtils;
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
  public Triple<Groomer, Parent, PaymentIntent> validateBook(BookingCreateDTO bookingCreateDTO) {

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

    /**
     * Pets
     */
    Set<PoochBookingCreateDTO> petCreateDTOs = bookingCreateDTO.getPooches();

    BookingCostDetails calculatedCostDetails =
        bookingCalculatorService.calculateBookingDetailCosts(
            BookingCalculatorSender.CREATE_BOOKING,
            groomer, parent, pickUpCost,
            dropOffCost, startDateTime, endDateTime, petCreateDTOs);

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

    if (!paymentIntent.getStatus().equalsIgnoreCase("requires_capture")) {
      if(paymentIntent.getStatus().equalsIgnoreCase("requires_payment_method")) {
        throw new ApiException("You need to add a Payment Method",
            "payment status should be requires_capture",
            "payment status=" + paymentIntent.getStatus());
      }
      
      throw new ApiException(ApiError.DEFAULT_MSG, "Payment has not been made",
          "stripe issue, booking payment", "payment status should be requires_capture",
          "payment status=" + paymentIntent.getStatus());
    }

    if (parent.getStripeCustomerId() != null && paymentIntent.getCustomer() != null
        && !parent.getStripeCustomerId().equalsIgnoreCase(paymentIntent.getCustomer())) {
      throw new ApiException(ApiError.DEFAULT_MSG, "You are charging a different parent",
          "stripe issue, booking payment",
          "parent stripeCustomerId is not the same as the customerId of the paymentIntent");
    }

    log.info("paymentIntent.getAmount={},paymentIntent.getAmountReceived={}",
        paymentIntent.getAmount(), paymentIntent.getAmountReceived());

    BookingCostDetails paymentIntentCostDetails = BookingCostDetails.fromJson(
        paymentIntent.getMetadata().get(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS));

    Double amountReceived = MathUtils.convertCentsToDollars(paymentIntent.getAmountReceived());
    Double amount = MathUtils.convertCentsToDollars(paymentIntent.getAmount());

    if (!amount.equals(calculatedCostDetails.getTotalChargeAtBooking())) {
      throw new ApiException("Incorrect payment value", "today's amountReceived: " + amountReceived,
          "today's amount: " + amount,
          "today's charge should be: " + calculatedCostDetails.getTotalChargeAtBooking(),
          "stripe paymentIntent.totalChargeAtBooking: "
              + paymentIntentCostDetails.getTotalChargeAtBooking());
    }


    if (!paymentIntentCostDetails.getTotalChargeAtBooking()
        .equals(calculatedCostDetails.getTotalChargeAtBooking())) {
      int numberOfDays = calculatedCostDetails.getNumberOfDays();
      throw new ApiException("Incorrect payment value",
          "today's charge: " + paymentIntentCostDetails.getTotalChargeAtBooking(),
          "today's charge should be: " + calculatedCostDetails.getTotalChargeAtBooking(),
          "formula: bookingCost + bookingFee + stripeFee + dropOffCost + pickUpCost",
          "calculated services bookingCost: " + calculatedCostDetails.getCareServicesCost(),
          "bookingFee: " + paymentIntentCostDetails.getBookingFee(),
          "stripeFee: " + paymentIntentCostDetails.getStripeFee(), "pickUpCost: " + pickUpCost,
          "dropOffCost: " + dropOffCost, "numberOfDays: " + numberOfDays);
    }

    if (!paymentIntentCostDetails.getTotalChargeAtDropOff()
        .equals(calculatedCostDetails.getTotalChargeAtDropOff())) {
      throw new ApiException("Incorrect payment value",
          "TotalChargeAtDropOff is " + paymentIntentCostDetails.getTotalChargeAtDropOff(),
          "TotalChargeAtDropOff should be " + calculatedCostDetails.getTotalChargeAtDropOff());
    }

    return Triple.of(groomer, parent, paymentIntent);
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
