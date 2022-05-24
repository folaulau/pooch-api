package com.pooch.api.entity.booking;

import java.time.LocalDateTime;
import java.util.Set;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.careservice.CareService;

public interface BookingCalculatorService {

  public BookingCostDetails generatePaymentIntentDetails(Groomer groomer, Double amount);

  public Double calculateBookingCareServicesCost(Groomer groomer, Set<CareService> careServices,
      LocalDateTime startDateTime, LocalDateTime endDateTime);
}
