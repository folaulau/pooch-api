package com.pooch.api.entity.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import com.pooch.api.dto.BookingCareServiceCreateDTO;
import com.pooch.api.dto.PoochBookingCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochDAO;
import com.pooch.api.entity.pooch.PoochSize;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.stripe.StripeMetadataService;
import com.pooch.api.utils.MathUtils;
import com.pooch.api.utils.ObjectUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Service
@Slf4j
public class BookingCalculatorServiceImp implements BookingCalculatorService {

  @Value("${spring.profiles.active}")
  private String env;

  @Value("${booking.fee:10}")
  private Double bookingFee;

  @Autowired
  private PoochDAO poochDAO;

  @Autowired
  private CareServiceDAO careServiceDAO;


  @Override
  public BookingCostDetails calculateBookingDetailCosts(BookingCalculatorSender sender,
      Groomer groomer, Parent parent, Double pickUpCost, Double dropOffCost,
      LocalDateTime startDateTime, LocalDateTime endDateTime,List<PoochBookingCreateDTO> pooches) {

    if (dropOffCost != null && dropOffCost < 0) {
      throw new ApiException(ApiError.DEFAULT_MSG, "dropOffCost must greater than or equal to 0");
    }

    if (pickUpCost != null && pickUpCost < 0) {
      throw new ApiException(ApiError.DEFAULT_MSG, "pickUpCost must greater than or equal to 0");
    }

    BookingCostDetails bookingCostDetails = new BookingCostDetails();
    bookingCostDetails.setDropOffCost(dropOffCost);
    bookingCostDetails.setPickUpCost(pickUpCost);
    bookingCostDetails.setEndDateTime(endDateTime);
    bookingCostDetails.setStartDateTime(startDateTime);
    bookingCostDetails.setGroomerStripeReady(groomer.isStripeReady());

    int numberOfDays = calculateNumberOfDays(groomer, startDateTime, endDateTime);

    bookingCostDetails.setNumberOfDays(numberOfDays);

    Double careServicesCost =
        calculateCareServicesCost(sender, groomer, parent, numberOfDays, pooches);

    bookingCostDetails.setCareServicesCost(careServicesCost);

    bookingCostDetails = calculateStripeFees(bookingCostDetails, groomer);

    log.info("bookingCostDetails={}", bookingCostDetails.toJson());

    return bookingCostDetails;
  }

  /**
   * careServicesCost
   */
  private Double calculateCareServicesCost(BookingCalculatorSender sender, Groomer groomer,
      Parent parent, int numberOfDays, List<PoochBookingCreateDTO> petCreateDTOs) {

    log.info("calculateBookingCareServicesCost(..)");

    log.info("petCreateDTOs={}", ObjectUtils.toJson(petCreateDTOs));

    BigDecimal careServicesBookingCost = BigDecimal.valueOf(0.0);


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

      } else if (sender.equals(BookingCalculatorSender.CREATE_BOOKING)) {
        throw new ApiException(ApiError.DEFAULT_MSG, "pooch uuid is required");
      }

      Set<BookingCareServiceCreateDTO> services = petCreateDTO.getRequestedCareServices();

      if (services == null || services.size() <= 0) {
        throw new ApiException(ApiError.DEFAULT_MSG, "Add a service",
            "services are empty for pooch uuid=" + petCreateDTO.getUuid());
      }

      for (BookingCareServiceCreateDTO service : services) {
        String careServiceUuid = service.getUuid();

        if (careServiceUuid == null || careServiceUuid.trim().isEmpty()) {
          throw new ApiException(ApiError.DEFAULT_MSG, "service.uuid is required");
        }

        String size = service.getSize();

        if (!PoochSize.isValidSize(size)) {
          throw new ApiException(ApiError.DEFAULT_MSG, "service.size is invalid",
              "valid sizes: " + PoochSize.sizes);
        }

        CareService careService =
            careServiceDAO.getByUuidAndGroomer(careServiceUuid, groomer.getId())
                .orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG,
                    "service.uuid belongs to a different groomer"));

        if (!careService.isSizeServiced(size)) {
          throw new ApiException(ApiError.DEFAULT_MSG, "Groomer does not off service for this size",
              "size: " + size, "serviceName: " + careService.getName());
        }

        Double careServicePricePerDay = careService.getPriceBySize(size);

        /**
         * calculate careService price per day
         */
        Double careServicePrice = careServicePricePerDay * numberOfDays;

        log.info("careService.name={}, numberOfDays={}, careServicePricePerDay={}, careServicePrice={}",
            careService.getName(), numberOfDays, careServicePricePerDay, careServicePrice);

        careServicesBookingCost = careServicesBookingCost.add(BigDecimal.valueOf(careServicePrice));

      }


    }

    log.info("careServicesBookingCost={}", careServicesBookingCost);

    // calculatedBookingCost, totalBookingCost
    return careServicesBookingCost.doubleValue();

  }

  private int calculateNumberOfDays(Groomer groomer, LocalDateTime startDateTime,
      LocalDateTime endDateTime) {
    LocalTime openTime = groomer.getOpenTime();

    // openTime = openTime.withSecond(0).withNano(0);

    LocalTime closeTime = groomer.getCloseTime();

    // closeTime = closeTime.withSecond(0).withNano(0);

    if (startDateTime == null) {
      throw new ApiException(ApiError.DEFAULT_MSG, "startDateTime is required");
    }

    if (startDateTime.isBefore(LocalDateTime.now())) {
      throw new ApiException(ApiError.DEFAULT_MSG, "startDateTime must be in the future");
    }

    startDateTime = startDateTime.withSecond(0).withNano(0);

    // if(!openTime.equals(startDateTime.toLocalTime())) {
    // throw new ApiException(ApiError.DEFAULT_MSG, "startDateTime is required");
    // }

    if (endDateTime == null) {
      throw new ApiException(ApiError.DEFAULT_MSG, "endDateTime is required");
    }

    if (endDateTime.isBefore(LocalDateTime.now())) {
      throw new ApiException(ApiError.DEFAULT_MSG, "endDateTime must be in the future");
    }

    endDateTime = endDateTime.withSecond(0).withNano(0);

    if (startDateTime.isAfter(endDateTime)) {
      throw new ApiException(ApiError.DEFAULT_MSG,
          "endDateTime must be greater than startDateTime");
    }

    int numberOfDays = 0;

    LocalDateTime countFromStartDateTime = startDateTime;

    do {

      numberOfDays++;
      countFromStartDateTime = countFromStartDateTime.plusDays(1);

    } while (countFromStartDateTime.isBefore(endDateTime));

    log.info("numberOfDays={}, startDateTime={}, endDateTime={}", numberOfDays, startDateTime,
        endDateTime);

    return numberOfDays;
  }

  private BookingCostDetails calculateStripeFees(BookingCostDetails costDetails, Groomer groomer) {

    // $10 booking fee
    double bookingCost = MathUtils.getTwoDecimalPlaces(costDetails.getBookingCost());

    // 2.9% of chargeAmount + 30 cents
    // put bookingCost on the parent, 10 booking is on pooch account
    double stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(bookingCost)).add(BigDecimal.valueOf(0.3))
        .setScale(2, RoundingMode.HALF_UP).doubleValue();

    if (!groomer.isStripeReady()) {
      stripeFee = 0;
    }

    costDetails.setBookingFee(bookingFee);
    costDetails.setStripeFee(stripeFee);

    log.info("calculateStripeFee bookingFee={}, stripeFee={}", bookingFee, stripeFee);

    return costDetails;
  }



}
