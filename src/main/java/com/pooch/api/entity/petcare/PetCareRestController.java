package com.pooch.api.entity.petcare;

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
import com.pooch.api.dto.PetCareCreateDTO;
import com.pooch.api.dto.PetCareDTO;
import com.pooch.api.entity.petparent.PetParentRestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "PetCares", description = "Pet Care Operations")
@RestController
@RequestMapping("/petcares")
public class PetCareRestController {

    @Autowired
    private PetCareService petCareService;

    @Operation(summary = "Book Pet Care", description = "book a pet care")
    @PostMapping(value = "/book")
    public ResponseEntity<PetCareDTO> book(@RequestHeader(name = "token", required = true) String token, @RequestBody PetCareCreateDTO petCareCreateDTO) {
        log.info("book");

        PetCareDTO petCareDTO = petCareService.book(petCareCreateDTO);

        return new ResponseEntity<>(petCareDTO, OK);
    }
}
