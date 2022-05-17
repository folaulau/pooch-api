package com.pooch.api.entity.booking;

import com.pooch.api.entity.groomer.Groomer;

public interface BookingCalculatorService {

    public BookingCostDetails generatePaymentIntentDetails(Groomer groomer, Double amount);
}
