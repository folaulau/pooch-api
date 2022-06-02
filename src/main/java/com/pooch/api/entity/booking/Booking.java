package com.pooch.api.entity.booking;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.elastic.repo.AddressES;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.UserType;
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.booking.careservice.BookingCareService;
import com.pooch.api.entity.booking.pooch.BookingPooch;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethod;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.vaccine.Vaccine;
import com.pooch.api.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.Booking + " SET deleted = 'T' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.Booking,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class Booking implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  @ManyToOne(cascade = CascadeType.DETACH)
  @JoinColumn(name = "parent_id")
  private Parent parent;

  @ManyToOne(cascade = CascadeType.DETACH)
  @JoinColumn(name = "groomer_id")
  private Groomer groomer;

  // @ManyToMany(cascade = {CascadeType.DETACH})
  // @JoinTable(name = "booking_pooches", joinColumns = @JoinColumn(name = "booking_id"),
  // inverseJoinColumns = @JoinColumn(name = "pooch_id"))
  @JsonIgnoreProperties(value = {"booking"})
  @OneToMany(cascade = {CascadeType.DETACH}, mappedBy = "booking")
  private Set<BookingPooch> pooches;

  // @OneToMany(cascade = {CascadeType.ALL})
  // @JoinTable(name = "booking_careservices", joinColumns = @JoinColumn(name = "booking_id"),
  // inverseJoinColumns = @JoinColumn(name = "booking_care_service_id"))
  @JsonIgnoreProperties(value = {"booking"})
  @OneToMany(cascade = {CascadeType.DETACH}, mappedBy = "booking")
  private Set<BookingCareService> careServices;


  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private BookingStatus status;

  @Column(name = "pick_up_date_time", nullable = true)
  private LocalDateTime pickUpDateTime;

  @Column(name = "drop_off_date_time", nullable = true)
  private LocalDateTime dropOffDateTime;

  @Column(name = "pick_up_cost")
  private Double pickUpCost;

  @Column(name = "drop_off_cost")
  private Double dropOffCost;

  @Column(name = "start_date_time", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "end_date_time", nullable = false)
  private LocalDateTime endDateTime;

  @Column(name = "stripe_payment_intent_id", nullable = true)
  private String stripePaymentIntentId;

  /**
   * Id of Transfer of bookingCost from pooch to the groomer's connected account<br>
   * Use to refund money back
   */
  @Column(name = "stripe_payment_intent_transfer_id", nullable = true)
  private String stripePaymentIntentTransferId;


  // services cost + pick up cost + drop off cost
  @Column(name = "booking_cost")
  private Double bookingCost;

  @Column(name = "booking_fee")
  private Double bookingFee;

  @Column(name = "stripeFee")
  private Double stripeFee;

  @Column(name = "total_amount")
  private Double totalAmount;

  @Column(name = "total_charge_at_booking")
  private Double totalChargeAtBooking;

  @Column(name = "total_charge_at_drop_off")
  private Double totalChargeAtDropOff;

  @Column(name = "checked_in", nullable = true)
  private Boolean checkedIn;

  @Column(name = "checked_out", nullable = true)
  private Boolean checkedOut;

  @Column(name = "checked_in_at", nullable = true)
  private LocalDateTime checkedInAt;

  @Column(name = "checked_out_at", nullable = true)
  private LocalDateTime checkedOutAt;

  /**
   * ======== Cancellation ========
   */
  @Column(name = "cancelled_at", nullable = true)
  private LocalDateTime cancelledAt;

  /**
   * user type of who cancelled booking
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "cancel_user_type", nullable = true)
  private UserType cancelUserType;

  /**
   * user id of who cancelled booking
   */
  @Column(name = "cancel_user_id", nullable = true)
  private Long cancelUserId;

  /**
   * amount refunded on cancellation
   */
  @Column(name = "cancellation_refunded_amount")
  private Double cancellationRefundedAmount;

  /**
   * amount non refunded on cancellation
   */
  @Column(name = "cancellation_non_refunded_amount")
  private Double cancellationNonRefundedAmount;

  /**
   * ======== Cancellation End =====
   */

  @Embedded
  private BookingPaymentMethod paymentMethod;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public void addPooch(BookingPooch pooch) {
    if (this.pooches == null) {
      this.pooches = new HashSet<>();
    }
    this.pooches.add(pooch);
  }

  public void addCareService(BookingCareService bookingCareService) {
    if (this.careServices == null) {
      this.careServices = new HashSet<>();
    }
    this.careServices.add(bookingCareService);
  }


  public void populateBookingCostDetails(BookingCostDetails costDetails) {
    this.setBookingCost(costDetails.getCareServicesCost());
    this.setBookingFee(costDetails.getBookingFee());
    this.setTotalAmount(costDetails.getTotalAmount());
    this.setStripeFee(costDetails.getStripeFee());
    this.setTotalChargeAtBooking(costDetails.getTotalChargeAtBooking());
    this.setTotalChargeAtDropOff(costDetails.getTotalChargeAtDropOff());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.id).append(this.uuid).toHashCode();

    // return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Booking other = (Booking) obj;
    return new EqualsBuilder().append(this.id, other.id).append(this.uuid, other.uuid).isEquals();
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return ToStringBuilder.reflectionToString(this);
  }

  @PrePersist
  private void preCreate() {
    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "booking-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
    }

    if (checkedIn == null) {
      checkedIn = false;
    }

    if (checkedOut == null) {
      checkedIn = false;
    }

  }

  public String toJson() {
    return ObjectUtils.toJson(this);
  }


}
