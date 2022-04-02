package com.pooch.api.entity.booking;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.BookingDTO;
import com.pooch.api.entity.parent.ParentRestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Bookings", description = "Booking Operations")
@RestController
@RequestMapping("/bookings")
public class BookingRestController {

    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Make booking", description = "make a booking")
    @PostMapping(value = "/book")
    public ResponseEntity<BookingDTO> book(@RequestHeader(name = "token", required = true) String token, @RequestBody BookingCreateDTO bookingCreateDTO) {
        log.info("book");

        BookingDTO bookingDTO = bookingService.book(bookingCreateDTO);

        return new ResponseEntity<>(bookingDTO, OK);
    }
}
