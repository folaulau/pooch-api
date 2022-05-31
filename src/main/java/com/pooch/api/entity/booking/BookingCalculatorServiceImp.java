package com.pooch.api.entity.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

import lombok.extern.slf4j.Slf4j;

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
  public BookingCostDetails generatePaymentIntentDetails(Groomer groomer, Double amount) {

    BookingCostDetails costDetails = new BookingCostDetails();

    // $10 booking fee
    double bookingCost = MathUtils.getTwoDecimalPlaces(amount);

    // 2.9% of chargeAmount + 30 cents
    // put bookingCost on the parent, 10 booking is on pooch account
    double stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(bookingCost)).add(BigDecimal.valueOf(0.3))
        .setScale(2, RoundingMode.CEILING).doubleValue();


    double totalChargeToday = 0;
    double totalChargeAtDropOff = 0;
    double totalAmount = 0;

    if (groomer.isStripeReady()) {
      totalChargeToday = BigDecimal.valueOf(bookingCost).add(BigDecimal.valueOf(stripeFee))
          .add(BigDecimal.valueOf(bookingFee)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
      totalChargeAtDropOff = 0;
    } else {
      totalChargeToday = bookingFee;
      totalChargeAtDropOff = bookingCost;
      stripeFee = 0;
    }

    totalAmount = (totalChargeToday + totalChargeAtDropOff);

    costDetails.setBookingCost(bookingCost);
    costDetails.setBookingFee(bookingFee);
    costDetails.setStripeFee(stripeFee);
    costDetails.setTotalChargeAtBooking(totalChargeToday);
    costDetails.setTotalChargeAtDropOff(totalChargeAtDropOff);
    costDetails.setTotalAmount(totalAmount);

    log.info("groomer={}", ObjectUtils.toJson(groomer));

    log.info(
        "generatePaymentIntentDetails -> groomer.isStripeReady={}, bookingFee={}, bookingCost={}, totalChargeToday={}, stripeFee={}, totalChargeAtDropOff={}, totalAmount={}",
        groomer.isStripeReady(), bookingFee, bookingCost, totalChargeToday, stripeFee,
        totalChargeAtDropOff, totalAmount);

    return costDetails;
  }

  @Override
  public Pair<Double, Double> calculateBookingCareServicesCost(Groomer groomer, Parent parent,
      Double pickUpCost, Double dropOffCost, LocalDateTime startDateTime, LocalDateTime endDateTime,
      Set<PoochBookingCreateDTO> petCreateDTOs) {

    if (dropOffCost != null && dropOffCost < 0) {
      throw new ApiException(ApiError.DEFAULT_MSG, "dropOffCost must greater than or equal to 0");
    }

    if (pickUpCost != null && pickUpCost < 0) {
      throw new ApiException(ApiError.DEFAULT_MSG, "pickUpCost must greater than or equal to 0");
    }

    LocalTime openTime = groomer.getOpenTime();

    LocalTime closeTime = groomer.getCloseTime();

    if (startDateTime == null) {
      throw new ApiException(ApiError.DEFAULT_MSG, "startDateTime is required");
    }

    if (startDateTime.isBefore(LocalDateTime.now())) {
      throw new ApiException(ApiError.DEFAULT_MSG, "startDateTime must be in the future");
    }

    startDateTime = startDateTime.withSecond(0).withNano(0);

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


    BigDecimal totalBookingCost = BigDecimal.valueOf(0.0);
    BigDecimal calculatedBookingCost = BigDecimal.valueOf(0.0);


    if (petCreateDTOs == null || petCreateDTOs.size() <= 0) {
      throw new ApiException(ApiError.DEFAULT_MSG, "Add a pooch", "pooches are empty");
    }


    for (PoochBookingCreateDTO petCreateDTO : petCreateDTOs) {

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

        calculatedBookingCost = calculatedBookingCost.add(BigDecimal.valueOf(careServicePrice));

      }


    }

    /**
     * ============= Booking Cost Details ================
     */
    if (dropOffCost != null && dropOffCost >= 0) {
      totalBookingCost = totalBookingCost.add(BigDecimal.valueOf(dropOffCost));
    }

    if (pickUpCost != null && pickUpCost >= 0) {
      totalBookingCost = totalBookingCost.add(BigDecimal.valueOf(pickUpCost));
    }

    totalBookingCost = totalBookingCost.add(calculatedBookingCost);

    // calculatedBookingCost, totalBookingCost
    return Pair.of(MathUtils.getTwoDecimalPlaces(calculatedBookingCost.doubleValue()),
        MathUtils.getTwoDecimalPlaces(totalBookingCost.doubleValue()));

  }

}
