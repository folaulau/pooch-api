package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.UserType;
import com.pooch.api.entity.booking.BookingStatus;
import com.pooch.api.entity.booking.careservice.BookingCareService;
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

  private Set<BookingPoochDTO> pooches;

  private Set<BookingCareServiceDTO> careServices;

  private GroomerDTO groomer;

  private BookingPaymentMethodDTO paymentMethod;

  private BookingStatus status;

  private LocalDateTime pickUpDateTime;

  private Double pickUpCost;

  private LocalDateTime dropOffDateTime;

  private Double dropOffCost;

  private LocalDateTime startDateTime;

  private LocalDateTime endDateTime;

  private Double bookingCost;

  private Double bookingFee;

  private Double stripeFee;

  private Double totalAmount;

  private Double totalChargeAtBooking;

  private Double totalChargeAtDropOff;

  private Set<TransactionDTO> transactions;

  private String stripePaymentIntentTransferId;

  private UserType cancelUserType;

  private Long cancelUserId;

  private Double cancellationRefundedAmount;

  private Double cancellationNonRefundedAmount;

  private LocalDateTime cancelledAt;

  public void addTransaction(TransactionDTO transaction) {
    if (this.transactions == null) {
      this.transactions = new HashSet<>();
    }
    this.transactions.add(transaction);
  }

}
