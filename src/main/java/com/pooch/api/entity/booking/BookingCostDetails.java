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
   * cost of the booking
   */
  private Double bookingCost;

  /**
   * Pooch fee
   */
  private Double bookingFee;

  /**
   * stripe fee on charge
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
   * total cost of booking
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
