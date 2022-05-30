package com.pooch.api.entity.booking.pooch;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Gender;
import com.pooch.api.entity.pooch.PoochSize;
import com.pooch.api.entity.pooch.Training;
import com.pooch.api.entity.pooch.vaccine.Vaccine;
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
@Table(name = DatabaseTableNames.BookingPooch)
public class BookingPooch implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "booking_pooch_id", nullable = false, updatable = false, unique = true)
  private Long bookingPoochId;

  @Column(name = "id")
  private Long id;

  @Column(name = "uuid")
  private String uuid;


  @JsonIgnoreProperties(value = {"pooches"})
  @ManyToOne(cascade = CascadeType.DETACH)
  @JoinColumn(name = "booking_id")
  private Booking booking;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "breed")
  private String breed;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender")
  private Gender gender;

  @Enumerated(EnumType.STRING)
  @Column(name = "training")
  private Training training;

  @Column(name = "dob")
  private LocalDate dob;

  @Column(name = "weight")
  private Double weight;

  /**
   * calculated from weight
   */
  @Column(name = "size")
  private String size;

  @Column(name = "spayed")
  private Boolean spayed;

  @Column(name = "neutered")
  private Boolean neutered;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "booking_pooch_id", nullable = true)
  private Set<Vaccine> vaccines;

  @ElementCollection
  @CollectionTable(name = "booking_pooch_food_schedule",
      joinColumns = {@JoinColumn(name = "booking_pooch_id")})
  private Set<FoodSchedule> foodSchedule;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public void setWeight(Double weight) {
    this.weight = weight;

    this.setSize(size);
  }

  /**
   * set when weight is set
   */
  public void setSize(String size) {
    this.size = PoochSize.getSizeByWeight(weight);
  }

  @PrePersist
  private void preCreate() {}

}
