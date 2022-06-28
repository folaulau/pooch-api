package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class GroomerDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String uuid;

  private String firstName;

  private String lastName;

  private String businessName;

  private String email;

  private Boolean emailVerified;

  private boolean emailTemp;

  private Long phoneNumber;

  private Boolean phoneNumberVerified;

  private Integer rating;

  private Boolean offeredPickUp;

  private Boolean offeredDropOff;

  private Double chargePerMile;

  private Long numberOfOccupancy;

  private String description;

  private boolean instantBooking;

  private GroomerSignUpStatus signUpStatus;

  private GroomerStatus status;

  private Set<CareServiceDTO> careServices;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private AddressDTO address;

  private boolean listing;

  private LocalTime openTime;

  private LocalTime closeTime;

  private Boolean operateMonday;

  private Boolean operateTuesday;

  private Boolean operateWednesday;

  private Boolean operateThursday;

  private Boolean operateFriday;

  private Boolean operateSaturday;

  private Boolean operateSunday;

  public void addCareService(CareServiceDTO careServiceDTO) {
    if (this.careServices == null) {
      this.careServices = new HashSet<>();
    }
    this.careServices.add(careServiceDTO);
  }

}
