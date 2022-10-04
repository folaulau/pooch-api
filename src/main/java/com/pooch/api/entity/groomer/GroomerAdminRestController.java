package com.pooch.api.entity.groomer;

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

@Tag(name = "AdminGroomers", description = "Admin Groomer Operations")
@Slf4j
@RestController
@RequestMapping("/admin/groomers")
public class GroomerAdminRestController {

  @Autowired
  private GroomerService groomerService;


  @Operation(summary = "Ban Listing", description = "Ban Listing")
  @PutMapping(value = "/listing/ban")
  public ResponseEntity<GroomerDTO> updateBanListing(
      @RequestHeader(name = "token", required = true) String token,
      @Valid @RequestBody BanListingDTO banListingDTO) {
    log.info("updateBanListing");

    GroomerDTO groomerDTO = groomerService.updateBanListing(banListingDTO);

    return new ResponseEntity<>(groomerDTO, OK);
  }
}
