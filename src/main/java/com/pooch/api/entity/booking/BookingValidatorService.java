package com.pooch.api.entity.booking;

import com.pooch.api.dto.*;
import org.apache.commons.lang3.tuple.Triple;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.stripe.model.PaymentIntent;

public interface BookingValidatorService {

    Triple<Groomer, Parent, PaymentIntent> validateBook(BookingCreateDTO petCareCreateDTO);

    Booking validateCancel(BookingCancelDTO bookingCancelDTO);

    Booking validateCheckIn(BookingCheckInDTO bookingCheckInDTO);

    Booking validateCheckOut(BookingCheckOutDTO bookingCheckOutDTO);

    Booking validateApproval(BookingApprovalDTO bookingApprovalDTO);
}
