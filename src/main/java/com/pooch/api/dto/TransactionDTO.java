package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.UserType;
import com.pooch.api.entity.booking.transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@JsonInclude(value = Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  private Long id;

  private String uuid;

  private TransactionType type;

  private Double bookingCost;

  private Double bookingFee;

  private Double stripeFee;

  private Double totalChargeAtBooking;

  private Double totalChargeAtDropOff;

  private String description;

  private Double amount;

  private UserType cancelUserType;

  private Long cancelUserId;

  private Double refundedAmount;

  private Double nonRefundedAmount;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
