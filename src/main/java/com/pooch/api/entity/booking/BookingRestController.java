
package com.pooch.api.entity.booking;

import static org.springframework.http.HttpStatus.OK;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.BookingApprovalDTO;
import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCheckInDTO;
import com.pooch.api.dto.BookingCheckOutDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.BookingDTO;
import com.pooch.api.entity.parent.ParentRestController;
import com.pooch.api.xapikey.XApiKeyService;

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

    @Operation(summary = "Make Booking", description = "make a booking")
    @PostMapping(value = "/book")
    public ResponseEntity<BookingDTO> book(@RequestHeader(name = "token", required = true) String token, @RequestBody BookingCreateDTO bookingCreateDTO) {
        log.info("book");

        BookingDTO bookingDTO = bookingService.book(bookingCreateDTO);

        return new ResponseEntity<>(bookingDTO, OK);
    }

    @Operation(summary = "Cancel Booking", description = "cancel a booking")
    @PutMapping(value = "/cancel")
    public ResponseEntity<BookingDTO> cancel(@RequestHeader(name = "token", required = true) String token, @RequestBody BookingCancelDTO bookingCancelDTO) {
        log.info("cancel, bookingCancelDTO={}", bookingCancelDTO.toString());

        BookingDTO bookingDTO = bookingService.cancel(bookingCancelDTO);

        return new ResponseEntity<>(bookingDTO, OK);
    }
    
    @Operation(summary = "Check In Booking", description = "check in a booking")
    @PutMapping(value = "/checkin")
    public ResponseEntity<BookingDTO> checkIn(@RequestHeader(name = "token", required = true) String token, @Valid @RequestBody BookingCheckInDTO checkInDTO) {
        log.info("checkIn");

        BookingDTO bookingDTO = bookingService.checkIn(checkInDTO);

        return new ResponseEntity<>(bookingDTO, OK);
    }
    
    @Operation(summary = "Check Out Booking", description = "check out a booking")
    @PutMapping(value = "/checkout")
    public ResponseEntity<BookingDTO> checkOut(@RequestHeader(name = "token", required = true) String token, @Valid @RequestBody BookingCheckOutDTO checkOutDTO) {
        log.info("checkOut");

        BookingDTO bookingDTO = bookingService.checkOut(checkOutDTO);

        return new ResponseEntity<>(bookingDTO, OK);
    }
    
    @Operation(summary = "Groomer Approval", description = "Groomer approves booking after a parent has made a booking that requires approval")
    @PutMapping(value = "/groomer/approval")
    public ResponseEntity<BookingDTO> approve(@RequestHeader(name = "token", required = true) String token, @Valid @RequestBody BookingApprovalDTO bookingApprovalDTO) {
        log.info("approve");

        BookingDTO bookingDTO = bookingService.approve(bookingApprovalDTO);

        return new ResponseEntity<>(bookingDTO, OK);
    }
}
