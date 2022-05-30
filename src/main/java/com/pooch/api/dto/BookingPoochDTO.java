package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Gender;
import com.pooch.api.entity.pooch.Pooch;
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
public class BookingPoochDTO implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private Long bookingPoochId;

  private Long id;

  private String uuid;

  private String fullName;

  private String breed;

  private Gender gender;

  private Training training;

  private LocalDate dob;

  private Double weight;

  private String size;

  private Boolean spayed;

  private Boolean neutered;

  private List<FoodSchedule> foodSchedule;

  private Set<VaccineDTO> vaccines;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
