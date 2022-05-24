package com.pooch.api.entity.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCareServiceDTO;
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

  @Override
  public void validateBook(BookingCreateDTO bookingCreateDTO) {
    ParentCreateUpdateDTO parentCreateUpdateDTO = bookingCreateDTO.getParent();

    if (parentCreateUpdateDTO == null) {
      throw new ApiException(ApiError.DEFAULT_MSG, "petParent is required");
    }

    String parentUuid = parentCreateUpdateDTO.getUuid();

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

    if (dropOffCost != null && dropOffCost < 0) {
      throw new ApiException(ApiError.DEFAULT_MSG, "dropOffCost must greater than or equal to 0");
    }

    Double pickUpCost = bookingCreateDTO.getPickUpCost();

    if (pickUpCost != null && pickUpCost < 0) {
      throw new ApiException(ApiError.DEFAULT_MSG, "pickUpCost must greater than or equal to 0");
    }

    LocalTime openTime = groomer.getOpenTime();

    LocalTime closeTime = groomer.getCloseTime();

    LocalDateTime startDateTime = bookingCreateDTO.getStartDateTime();

    if (startDateTime == null) {
      throw new ApiException(ApiError.DEFAULT_MSG, "startDateTime is required");
    }

    if (startDateTime.isBefore(LocalDateTime.now())) {
      throw new ApiException(ApiError.DEFAULT_MSG, "startDateTime must be in the future");
    }

    LocalDateTime endDateTime = bookingCreateDTO.getEndDateTime();

    if (endDateTime == null) {
      throw new ApiException(ApiError.DEFAULT_MSG, "endDateTime is required");
    }

    if (endDateTime.isBefore(LocalDateTime.now())) {
      throw new ApiException(ApiError.DEFAULT_MSG, "endDateTime must be in the future");
    }

    if (startDateTime.isAfter(endDateTime)) {
      throw new ApiException(ApiError.DEFAULT_MSG,
          "endDateTime must be greater than startDateTime");
    }

    int numberOfDays = 0;

    LocalDateTime countFromStartDateTime = startDateTime;

    do {

      numberOfDays++;
      countFromStartDateTime = countFromStartDateTime.plusDays(1);

    } while (countFromStartDateTime.isAfter(endDateTime));

    System.out.println("numberOfDays: " + numberOfDays);


    /**
     * Pets
     */
    Set<PoochBookingCreateDTO> petCreateDTOs = bookingCreateDTO.getPooches();

    BigDecimal totalBookingCost = BigDecimal.valueOf(0.0);

    if (petCreateDTOs == null || petCreateDTOs.size() <= 0) {
      throw new ApiException(ApiError.DEFAULT_MSG, "Add a pooch", "pooches are empty");
    }

    for (PoochBookingCreateDTO petCreateDTO : petCreateDTOs) {
      String poochUuid = petCreateDTO.getUuid();

      if (poochUuid != null && !poochUuid.trim().isEmpty()) {

        Pooch pooch = poochDAO.getByUuid(poochUuid).orElseThrow(
            () -> new ApiException(ApiError.DEFAULT_MSG, "Pooch not found for uuid=" + poochUuid));

        if (!parent.getId().equals(pooch.getParent().getId())) {
          throw new ApiException(ApiError.DEFAULT_MSG, "Pooch does not belong to Parent",
              "pooch has to belong to his/her parent");
        }
      }

      Set<BookingCareServiceDTO> services = petCreateDTO.getCareServices();

      if (services == null || services.size() <= 0) {
        throw new ApiException(ApiError.DEFAULT_MSG, "Add a service",
            "services are empty for pooch=" + petCreateDTO.getFullName());
      }

      for (BookingCareServiceDTO service : services) {
        String careServiceUuid = service.getUuid();

        if (careServiceUuid == null || careServiceUuid.trim().isEmpty()) {
          throw new ApiException(ApiError.DEFAULT_MSG, "service.uuid is required");
        }

        String size = service.getSize();

        if (!PoochSize.isValidSize(size)) {
          throw new ApiException(ApiError.DEFAULT_MSG, "service.size is invalid",
              "valid sizes: " + PoochSize.sizes);
        }

        Optional<CareService> optService =
            careServiceDAO.getByUuidAndGroomer(careServiceUuid, groomer.getId());

        if (!optService.isPresent()) {
          throw new ApiException(ApiError.DEFAULT_MSG,
              "service.uuid belongs to a different groomer");
        }

        CareService careService = optService.get();

        Double careServicePrice = careService.getPriceBySize(size);

        /**
         * calculate careService price per day
         */
        careServicePrice = careServicePrice * numberOfDays;

        System.out.println("careServicePrice: " + careServicePrice);

        totalBookingCost = totalBookingCost.add(BigDecimal.valueOf(careServicePrice));

      }


    }

    if (dropOffCost != null && dropOffCost >= 0) {
      totalBookingCost = totalBookingCost.add(BigDecimal.valueOf(dropOffCost));
    }

    if (pickUpCost != null && pickUpCost >= 0) {
      totalBookingCost = totalBookingCost.add(BigDecimal.valueOf(pickUpCost));
    }

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
          "payment status=" + paymentIntent.getStatus());
    }

    if (parent.getStripeCustomerId() != null
        && !parent.getStripeCustomerId().equalsIgnoreCase(paymentIntent.getCustomer())) {
      throw new ApiException(ApiError.DEFAULT_MSG, "You are charging a different parent",
          "parent stripeCustomerId is not the same as the customerId of the paymentIntent");
    }

    BookingCostDetails costDetails = BookingCostDetails.fromJson(
        paymentIntent.getMetadata().get(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS));

    /**
     * validate booking fees
     */
    System.out.println("totalBookingCost: " + totalBookingCost.doubleValue());


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
