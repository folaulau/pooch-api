package com.pooch.api.entity.groomer;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

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
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.role.Role;

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
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.Groomer + " SET deleted = 'T' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.Groomer, indexes = {@Index(columnList = "uuid"),
    @Index(columnList = "email"), @Index(columnList = "deleted")})
public class Groomer implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "business_name")
  private String businessName;

  @NotEmpty
  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "email_verified")
  private Boolean emailVerified;

  @Enumerated(EnumType.STRING)
  @Column(name = "sign_up_status")
  private GroomerSignUpStatus signUpStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private GroomerStatus status;

  /**
   * Social platforms(facebook, google, etc) don't give email<br>
   * Now create a temp email for now
   */
  @Column(name = "email_temp")
  private boolean emailTemp;

  @Column(name = "country_code")
  private Integer countryCode;

  @Column(name = "phone_number")
  private Long phoneNumber;

  @Column(name = "phone_number_verified")
  private Boolean phoneNumberVerified;

  /**
   * 5 star rating
   */
  @Column(name = "rating")
  private Double rating;

  @Column(name = "offered_pick_up")
  private Boolean offeredPickUp;

  @Column(name = "offered_drop_off")
  private Boolean offeredDropOff;

  @Column(name = "charge_per_mile")
  private Double chargePerMile;

  @Column(name = "number_of_occupancy")
  private Long numberOfOccupancy;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  @Column(name = "description")
  private String description;

  @Column(name = "instant_booking", nullable = false, columnDefinition = "boolean default false")
  private Boolean instantBooking;

  /**
   * switch for market place listing(show up in search)
   */
  @Column(name = "listing", columnDefinition = "boolean default false")
  private Boolean listing;

  /**
   * ======== Stripe =========
   */

  @Column(name = "stripe_connected_account_id")
  private String stripeConnectedAccountId;

  /**
   * Stripe account.capabilities.card_payments<br>
   * active or inactive
   */
  @Column(name = "stripe_accept_card_payments")
  private String stripeAcceptCardPayments;

  @Column(name = "stripe_details_submitted", columnDefinition = "boolean default false")
  private Boolean stripeDetailsSubmitted;

  /**
   * two fields indicate complete: charges_enabled and payouts_enabled
   */

  /**
   * Stripe account.charges_enabled
   */
  @Column(name = "stripe_charges_enabled", columnDefinition = "boolean default false")
  private Boolean stripeChargesEnabled;

  /**
   * Stripe.account.payouts_enabled
   */
  @Column(name = "stripe_payouts_enabled", columnDefinition = "boolean default false")
  private Boolean stripePayoutsEnabled;

  /**
   * Time Groomer's shop opens
   */
  @Column(name = "open_time")
  private LocalTime openTime;

  /**
   * Time Groomer's shop closes
   */
  @Column(name = "close_time")
  private LocalTime closeTime;

  /**
   * Operational Days
   */
  @Column(name = "operate_monday", columnDefinition = "boolean default false")
  private Boolean operateMonday;

  @Column(name = "operate_tuesday", columnDefinition = "boolean default false")
  private Boolean operateTuesday;

  @Column(name = "operate_wednesday", columnDefinition = "boolean default false")
  private Boolean operateWednesday;

  @Column(name = "operate_thursday", columnDefinition = "boolean default false")
  private Boolean operateThursday;

  @Column(name = "operate_friday", columnDefinition = "boolean default false")
  private Boolean operateFriday;

  @Column(name = "operate_saturday", columnDefinition = "boolean default false")
  private Boolean operateSaturday;

  @Column(name = "operate_sunday", columnDefinition = "boolean default false")
  private Boolean operateSunday;

  @JsonIgnoreProperties(value = {"groomers"})
  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "groomer_roles", joinColumns = {@JoinColumn(name = "groomer_id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id")})
  private Set<Role> roles;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @JsonIgnoreProperties(value = {"groomer"})
  @OneToOne(cascade = CascadeType.ALL, mappedBy = "groomer")
  private Address address;

  public void addRole(Role role) {
    if (this.roles == null) {
      this.roles = new HashSet<>();
    }
    this.roles.add(role);
  }

  public String getRoleAsString() {
    if (this.roles == null) {
      return null;
    }
    return this.roles.stream().findFirst().get().getUserType().name();
  }

  public boolean isAllowedToLogin() {
    return GroomerStatus.isAllowedToLogin(status);
  }

  public String getFullName() {
    StringBuilder str = new StringBuilder();
    if (this.firstName != null && !this.firstName.isEmpty()) {
      str.append(this.firstName);
    }

    if (this.lastName != null && !this.lastName.isEmpty()) {
      if (!str.toString().isBlank()) {
        str.append(" ");
      }
      str.append(this.lastName);
    }

    return str.toString();
  }

  public void setOperationsForWeekDaysOnly() {
    operateMonday = true;
    operateTuesday = true;
    operateWednesday = true;
    operateThursday = true;
    operateFriday = true;
    operateSaturday = false;
    operateSunday = false;
  }

  public void setOperationsForEveryDay() {
    operateMonday = true;
    operateTuesday = true;
    operateWednesday = true;
    operateThursday = true;
    operateFriday = true;
    operateSaturday = true;
    operateSunday = true;
  }

  public boolean isActive() {
    return status.equals(GroomerStatus.ACTIVE);
  }

  public boolean isStripeReady() {
    return (this.stripeDetailsSubmitted != null && this.stripeDetailsSubmitted)
        && (this.stripeChargesEnabled != null && this.stripeChargesEnabled)
        && (this.stripePayoutsEnabled != null && this.stripePayoutsEnabled);
  }

  @PrePersist
  private void preCreate() {
    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "groomer-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
    }

    if (this.offeredDropOff == null) {
      this.offeredDropOff = false;
    }

    if (this.offeredPickUp == null) {
      this.offeredPickUp = false;
    }

    if (this.instantBooking == null) {
      this.instantBooking = false;
    }

    if (this.listing == null) {
      this.listing = false;
    }

    /**
     * by default set to true
     */

    if (this.instantBooking == null) {
      this.instantBooking = false;
    }

    if (operateMonday == null) {
      operateMonday = true;
    }

    if (operateTuesday == null) {
      operateTuesday = true;
    }

    if (operateWednesday == null) {
      operateWednesday = true;
    }

    if (operateThursday == null) {
      operateThursday = true;
    }

    if (operateFriday == null) {
      operateFriday = true;
    }

    if (operateSaturday == null) {
      operateSaturday = false;
    }

    if (operateSunday == null) {
      operateSunday = false;
    }
    
    /**
     * UTC 8am
     */
    if(openTime==null) {
      openTime = LocalTime.of(14, 0);
    }
    
    /**
     * UTC 5pm
     */
    if(closeTime==null) {
      closeTime = LocalTime.of(22, 0);
    }

  }

  @PreUpdate
  private void preUpdate() {}

  public boolean checkOperationByDay(LocalDate date) {
    DayOfWeek dayOfWeek = date.getDayOfWeek();

    if (DayOfWeek.MONDAY.equals(dayOfWeek) && operateMonday != null && operateMonday) {
      return true;
    } else if (DayOfWeek.TUESDAY.equals(dayOfWeek) && operateTuesday != null && operateTuesday) {
      return true;
    } else if (DayOfWeek.WEDNESDAY.equals(dayOfWeek) && operateWednesday != null
        && operateWednesday) {
      return true;
    } else if (DayOfWeek.THURSDAY.equals(dayOfWeek) && operateThursday != null && operateThursday) {
      return true;
    } else if (DayOfWeek.FRIDAY.equals(dayOfWeek) && operateFriday != null && operateFriday) {
      return true;
    } else if (DayOfWeek.SATURDAY.equals(dayOfWeek) && operateSaturday != null && operateSaturday) {
      return true;
    } else if (DayOfWeek.SUNDAY.equals(dayOfWeek) && operateSunday != null && operateSunday) {
      return true;
    }

    return false;
  }

}
