package com.pooch.api.entity.demo;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import com.pooch.api.dto.*;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.entity.groomer.careservice.type.GroomerServiceCategory;
import com.pooch.api.entity.groomer.careservice.type.GroomerServiceTypeService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.xapikey.XApiKeyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "Demos", description = "Demo Operations")
@Slf4j
@RestController
@RequestMapping("/demos")
public class DemoRestController {

    @Autowired
    private DemoService            demoService;

    @Autowired
    private XApiKeyService            xApiKeyService;

    @Operation(summary = "Schedule", description = "schedule a demo")
    @PostMapping(value = "/schedule")
    public ResponseEntity<DemoDTO> schedule(@RequestHeader(name = "x-api-key", required = true) String xApiKey, @Valid @RequestBody DemoCreateDTO demoCreateDTO) {
        log.info("demoCreateDTO={}", ObjectUtils.toJson(demoCreateDTO));

        xApiKeyService.validate(xApiKey);

        DemoDTO demoDTO = demoService.schedule(demoCreateDTO);

        return new ResponseEntity<>(demoDTO, OK);
    }

    
}
