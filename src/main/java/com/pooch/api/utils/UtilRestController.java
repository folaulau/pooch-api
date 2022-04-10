package com.pooch.api.utils;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.elastic.DataLoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Utils", description = "Utility Operations")
@Slf4j
@RestController
@RequestMapping("/utils")
public class UtilRestController {

    @Autowired
    private DataLoadService dataLoadService;

    @Operation(summary = "Load Groomers To ES", description = "Reload groomers into Elasticsearch")
    @PostMapping(value = "/load-groomers-to-es")
    public ResponseEntity<ApiDefaultResponseDTO> loadGroomersToES(@RequestHeader(name = "token", required = true) String token) {
        log.info("loadGroomersToES");

        ApiDefaultResponseDTO response = dataLoadService.loadGroomers();

        log.info("loadGroomersToES done!");

        return new ResponseEntity<>(response, OK);
    }
}
