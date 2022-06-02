package com.pooch.api.entity.booking.transaction;

import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.booking.BookingCostDetails;

public interface TransactionService {

    Transaction addBookingInitialPayment(Booking booking, BookingCostDetails bookingCostDetails);

    Transaction addBookingCancellation(Booking booking, Double refundedAmount,
        Double nonRefundedAmount);
}
