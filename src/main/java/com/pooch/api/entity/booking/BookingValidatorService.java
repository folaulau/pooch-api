package com.pooch.api.entity.booking;

import org.apache.commons.lang3.tuple.Triple;
import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCheckInDTO;
import com.pooch.api.dto.BookingCheckOutDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.stripe.model.PaymentIntent;

public interface BookingValidatorService {

    Triple<Groomer, Parent, PaymentIntent> validateBook(BookingCreateDTO petCareCreateDTO);

    Booking validateCancel(BookingCancelDTO bookingCancelDTO);

    Booking validateCheckIn(BookingCheckInDTO bookingCheckInDTO);

    Booking validateCheckOut(BookingCheckOutDTO bookingCheckOutDTO);

}
