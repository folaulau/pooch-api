package com.pooch.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class PaymentIntentDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String clientSecret;

  private String id;

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

  // for paymentMethod to use in future
  private String setupFutureUsage;

  private String status;

  /**
   * customer to see all paymentMethods
   */
  private String ephemeralKey;

}
