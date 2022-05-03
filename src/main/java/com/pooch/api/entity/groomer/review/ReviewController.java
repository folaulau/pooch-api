package com.pooch.api.entity.groomer.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.dto.ReviewCreateDTO;
import com.pooch.api.dto.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Reviews", description = "Review Operations")
@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Add Groomer Review", description = "add groomer review")
    @PostMapping("/groomer")
    public ResponseEntity<ReviewDTO> addGroomerReview(@RequestHeader(name = "token", required = true) String token, @RequestBody ReviewCreateDTO review) {

        ReviewDTO reviewDTO = reviewService.add(review);

        return new ResponseEntity<>(reviewDTO, HttpStatus.OK);
    }

    // @Operation(summary = "Authenticate", description = "sign up or sign in")
    // @PostMapping(value = "/authenticate")
    // public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestHeader(name = "x-api-key", required = true)
    // String xApiKey, @RequestBody AuthenticatorDTO authenticatorDTO) {
    // log.info("authenticate={}", ObjectUtils.toJson(authenticatorDTO));
    //
    // AuthenticationResponseDTO authenticationResponseDTO = groomerService.authenticate(authenticatorDTO);
    //
    // return new ResponseEntity<>(authenticationResponseDTO, OK);
    // }

    // @ApiOperation(value = "Update Review")
    // @PutMapping("/reviews")
    // public ResponseEntity<ReviewDTO> updateReview(@RequestHeader(name = "token", required = true) String token,
    // @ApiParam(name = "review", required = true, value = "review") @Valid @RequestBody ReviewUpdateDTO review) {
    //
    //
    // ReviewDTO reviewDTO = reviewService.update(review);
    //
    // return new ResponseEntity<>(reviewDTO, HttpStatus.OK);
    // }
}
