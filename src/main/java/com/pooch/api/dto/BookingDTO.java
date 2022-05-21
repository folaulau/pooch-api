package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.booking.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(value = Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long id;

  private String uuid;

  private ParentDTO parent;

  private Set<PoochDTO> pooches;

  private GroomerDTO groomer;

  private BookingPaymentMethodDTO paymentMethod;

  private BookingStatus status;

  private LocalDateTime pickUpDateTime;

  private LocalDateTime dropOffDateTime;

  private LocalDateTime startDateTime;

  private LocalDateTime endDateTime;

  private Double bookingCost;

  private Double bookingFee;

  private Double stripeFee;

  private Double totalAmount;

  private Double totalChargeBooking;

  private Double totalChargeAtDropOff;

}
