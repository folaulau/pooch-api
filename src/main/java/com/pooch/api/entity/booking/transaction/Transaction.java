package com.pooch.api.entity.booking.transaction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.UserType;
import com.pooch.api.entity.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.Transaction + " SET deleted = 'T' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.Transaction,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class Transaction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  /**
   * Check this enum for which fields belong to which type
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TransactionType type;

  /**
   * cost of the booking
   */
  @Column(name = "booking_cost")
  private Double bookingCost;

  /**
   * Pooch fee
   */
  @Column(name = "booking_fee")
  private Double bookingFee;

  /**
   * stripe fee on charge
   */
  @Column(name = "stripe_fee")
  private Double stripeFee;

  /**
   * amount charge now depending on Groomer's Stripe status
   */
  @Column(name = "total_charge_at_booking")
  private Double totalChargeAtBooking;

  /**
   * amount charge at drop off depending on Groomer's Stripe status
   */
  @Column(name = "total_charge_at_drop_off")
  private Double totalChargeAtDropOff;

  @Column(name = "description", nullable = true)
  private String description;

  @Column(name = "amount", nullable = true)
  private Double amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "cancel_user_type", nullable = true)
  private UserType cancelUserType;

  @Column(name = "cancel_user_id", nullable = true)
  private Long cancelUserId;

  @Column(name = "refunded_amount", nullable = true)
  private Double refundedAmount;

  @Column(name = "non_refunded_amount", nullable = true)
  private Double nonRefundedAmount;

  @ManyToOne(cascade = CascadeType.DETACH)
  @JoinColumn(name = "booking_id")
  private Booking booking;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;


  @PrePersist
  private void preCreate() {
    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "transaction-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
    }
  }
}
