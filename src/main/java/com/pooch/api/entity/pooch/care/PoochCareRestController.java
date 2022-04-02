package com.pooch.api.entity.pooch.care;

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
import com.pooch.api.dto.PoochCareCreateDTO;
import com.pooch.api.dto.PoochCareDTO;
import com.pooch.api.entity.parent.ParentRestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "PoochCares", description = "Pooch Care Operations")
@RestController
@RequestMapping("/poochcares")
public class PoochCareRestController {

    @Autowired
    private PoochCareService poochCareService;

    @Operation(summary = "Book Pooch Care", description = "book a pooch care")
    @PostMapping(value = "/book")
    public ResponseEntity<PoochCareDTO> book(@RequestHeader(name = "token", required = true) String token, @RequestBody PoochCareCreateDTO petCareCreateDTO) {
        log.info("book");

        PoochCareDTO petCareDTO = poochCareService.book(petCareCreateDTO);

        return new ResponseEntity<>(petCareDTO, OK);
    }
}
