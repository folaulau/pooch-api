package com.pooch.api;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.utils.HttpUtils;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 * This controller responds to health check but does not show on swagger.
 * 
 * @author folaukaveinga
 */
@Hidden
@Tag(name = "Ping", description = "Ping Operation")
@RestController
public class PingController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Operation(summary = "Sign Up", description = "sign up")
    @GetMapping(path = {"/ping", "/csrf"})
    public ResponseEntity<String> ping(HttpServletRequest request) {

        log.info("ping, ip: {}", HttpUtils.getRequestIP(request));

        return new ResponseEntity<>("up", HttpStatus.OK);
    }

    /**
     * In QA and Production, this path redirects to the swagger page.
     */
    @Operation(summary = "Welcome", description = "welcome")
    @GetMapping(path = {"/"})
    public ResponseEntity<String> welcome(HttpServletRequest request) {

        log.info("welcome, ip: {}", HttpUtils.getRequestIP(request));

        return new ResponseEntity<>("Welcome to Pooch API", HttpStatus.OK);
    }

}