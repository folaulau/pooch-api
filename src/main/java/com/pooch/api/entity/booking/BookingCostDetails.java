package com.pooch.api.entity.booking;

import java.io.Serializable;
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
  private Double bookingCost;

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
   * amount charge now depending on Groomer's Stripe status
   */
  private Double totalChargeAtBooking;

  /**
   * amount charge at drop off depending on Groomer's Stripe status
   */
  private Double totalChargeAtDropOff;

  /**
   * total cost of booking<br>
   * bookingCost + bookingFee + stripeFee
   */
  private Double totalAmount;

  public String toJson() {
    return ObjectUtils.toJson(this);
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
