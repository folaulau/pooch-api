package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String groomerUuid;

  private String paymentIntentId;

  private ParentCreateUpdateDTO parent;

//  private Set<PoochCreateUpdateDTO> pooches;
  
  private Set<PoochBookingCreateDTO> pooches;

//  /**
//   * careServices apply to all pooch
//   */
//  private Set<BookingCareServiceDTO> careServices;

  /**
   * care services with prices
   */
  private LocalDateTime pickUpDateTime;

  private LocalDateTime dropOffDateTime;

  private LocalDateTime startDateTime;

  private LocalDateTime endDateTime;

  private Boolean agreedToContracts;

  private Double pickUpCost;

  private Double dropOffCost;

//  public void addService(BookingCareServiceDTO service) {
//    if (this.careServices == null) {
//      this.careServices = new HashSet<>();
//    }
//    this.careServices.add(service);
//  }
}
