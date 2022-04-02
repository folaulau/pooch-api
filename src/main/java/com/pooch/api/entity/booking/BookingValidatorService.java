package com.pooch.api.entity.booking;

import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCreateDTO;

public interface BookingValidatorService {

    void validateBook(BookingCreateDTO petCareCreateDTO);

    Booking validateCancel(BookingCancelDTO bookingCancelDTO);

}
