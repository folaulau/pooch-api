package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Gender;
import com.pooch.api.entity.pooch.Training;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(value = Include.NON_NULL)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoochBookingCreateDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String uuid;

  private Set<BookingCareServiceCreateDTO> requestedCareServices;

  public void addService(BookingCareServiceCreateDTO service) {
    if (this.requestedCareServices == null) {
      this.requestedCareServices = new HashSet<>();
    }
    this.requestedCareServices.add(service);
  }
}
