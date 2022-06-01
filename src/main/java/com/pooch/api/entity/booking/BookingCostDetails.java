package com.pooch.api.entity.booking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.utils.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class BookingCostDetails implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * cost of the booking<br>
   * all service costs for all the days
   */
  private Double careServicesCost;

  /**
   * Pooch fee. Flat fee taken by Pooch Platform
   */
  private Double bookingFee;

  /**
   * stripe fee on charge<br>
   * 2.9% of bookingCost + 30 cents which is charged to the parent<br>
   */
  private Double stripeFee;

  /**
   * amount charge now depending on Groomer's Stripe status<br>
   * if groomer is Stripe ready? = all costs, else = just booking fee<br>
   */
  // private Double totalChargeAtBooking;

  /**
   * amount charge at drop off depending on Groomer's Stripe status<br>
   * if groomer is Stripe ready? = 0, else = (all costs - (bookingFee + stripeFee))<br>
   */
  // private Double totalChargeAtDropOff;

  /**
   * start date and time of the booking
   */
  private LocalDateTime startDateTime;

  /**
   * end date and time of the booking
   */
  private LocalDateTime endDateTime;

  /**
   * number of days charged for services<br>
   * difference between endDateTime and startDateTime
   */
  private Integer numberOfDays;

  private Double dropOffCost;

  private Double pickUpCost;

  private Boolean groomerStripeReady;

  public String toJson() {
    return ObjectUtils.toJson(this);
  }

  /**
   * careServicesCost + pickUpCost + dropOffCost
   * 
   * @return
   */
  public Double getBookingCost() {
    BigDecimal bookingCost = BigDecimal.valueOf(0.0);

    bookingCost = bookingCost.add(BigDecimal.valueOf(careServicesCost));

    if (pickUpCost != null) {
      bookingCost = bookingCost.add(BigDecimal.valueOf(pickUpCost));
    }

    if (dropOffCost != null) {
      bookingCost = bookingCost.add(BigDecimal.valueOf(dropOffCost));
    }

    return bookingCost.doubleValue();
  }

  public Double getTotalChargeAtBooking() {
    BigDecimal chargeAtBooking = BigDecimal.valueOf(0.0);

    if (groomerStripeReady != null && groomerStripeReady) {
      chargeAtBooking = chargeAtBooking.add(BigDecimal.valueOf(getTotalAmount()));

    } else {
      chargeAtBooking = chargeAtBooking.add(BigDecimal.valueOf(bookingFee));
    }

    return chargeAtBooking.doubleValue();
  }

  public Double getTotalChargeAtDropOff() {
    BigDecimal chargeAtBooking = BigDecimal.valueOf(0.0);


    if (groomerStripeReady == null || groomerStripeReady == false) {
      chargeAtBooking = chargeAtBooking.add(BigDecimal.valueOf(getBookingCost()));
    }

    return chargeAtBooking.doubleValue();
  }


  /**
   * total cost of booking<br>
   * bookingCost + bookingFee + stripeFee
   */
  public Double getTotalAmount() {
    BigDecimal amount = BigDecimal.valueOf(0.0);

    amount = amount.add(BigDecimal.valueOf(careServicesCost));

    if (pickUpCost != null) {
      amount = amount.add(BigDecimal.valueOf(pickUpCost));
    }

    if (dropOffCost != null) {
      amount = amount.add(BigDecimal.valueOf(dropOffCost));
    }

    if (stripeFee != null) {
      amount = amount.add(BigDecimal.valueOf(stripeFee));
    }

    if (bookingFee != null) {
      amount = amount.add(BigDecimal.valueOf(bookingFee));
    }

    return amount.doubleValue();
  }

  public static BookingCostDetails fromJson(String json) {
    if (json == null || json.trim().isEmpty()) {
      return null;
    }

    try {
      return ObjectUtils.getObjectMapper().readValue(json,
          new TypeReference<BookingCostDetails>() {});
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }

}
