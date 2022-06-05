package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class PaymentIntentParentCreateDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String groomerUuid;

  private String paymentIntentId;

  private String parentUuid;

  private String paymentMethodUuid;

  private Boolean savePaymentMethodForFutureUse;


  /**
   * ====== Fields to calculate payment =======
   */
  private Double pickUpCost;

  private Double dropOffCost;

  private LocalDateTime startDateTime;

  private LocalDateTime endDateTime;

  private List<PoochBookingCreateDTO> pooches;

  /**
   * ====== Fields to calculate payment ends ===
   */

  public void addPooch(PoochBookingCreateDTO pooch) {
    if (this.pooches == null) {
      this.pooches = new ArrayList<>();
    }
    this.pooches.add(pooch);
  }
}
