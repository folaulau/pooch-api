package com.pooch.api.entity.booking;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.util.Pair;
import com.pooch.api.dto.PoochBookingCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.parent.Parent;

public interface BookingCalculatorService {

//  public BookingCostDetails generatePaymentIntentDetails(Groomer groomer, Double amount);
//
//  public Pair<Double, Double> calculateBookingCareServicesCost(Groomer groomer, Parent parent, Double pickUpCost,
//      Double dropOffCost, LocalDateTime startDateTime, LocalDateTime endDateTime,
//      Set<PoochBookingCreateDTO> pooches);
  
  public BookingCostDetails runCalculateBookingCareServicesCost(Groomer groomer, Parent parent, Double pickUpCost,
      Double dropOffCost, LocalDateTime startDateTime, LocalDateTime endDateTime,
      Set<PoochBookingCreateDTO> pooches);
}
