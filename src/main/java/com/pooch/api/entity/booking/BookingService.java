package com.pooch.api.entity.booking;

import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCheckInDTO;
import com.pooch.api.dto.BookingCheckOutDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.BookingDTO;

public interface BookingService {

    BookingDTO book(BookingCreateDTO petCareCreateDTO);

    BookingDTO cancel(BookingCancelDTO bookingCancelDTO);

    BookingDTO checkIn(BookingCheckInDTO bookingCheckInDTO);

    BookingDTO checkOut(BookingCheckOutDTO bookingCheckOutDTO);
}
