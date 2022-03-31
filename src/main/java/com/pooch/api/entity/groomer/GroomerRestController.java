package com.pooch.api.entity.groomer;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import com.pooch.api.dto.*;
import com.pooch.api.elastic.repo.GroomerES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.utils.ObjectUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "Groomers", description = "Groomer Operations")
@Slf4j
@RestController
@RequestMapping("/groomers")
public class GroomerRestController {

  @Autowired private GroomerService groomerService;

  @Operation(summary = "Authenticate", description = "sign up or sign in")
  @PostMapping(value = "/authenticate")
  public ResponseEntity<AuthenticationResponseDTO> authenticate(
      @RequestHeader(name = "x-api-key", required = true) String xApiKey,
      @RequestBody AuthenticatorDTO authenticatorDTO) {
    log.info("authenticate={}", ObjectUtils.toJson(authenticatorDTO));

    AuthenticationResponseDTO authenticationResponseDTO =
        groomerService.authenticate(authenticatorDTO);

    return new ResponseEntity<>(authenticationResponseDTO, OK);
  }

  @Operation(summary = "Update Profile", description = "update profile")
  @PutMapping(value = "/profile")
  public ResponseEntity<GroomerDTO> update(
      @RequestHeader(name = "token", required = true) String token,
      @RequestBody GroomerUpdateDTO groomerUpdateDTO) {
    log.info("groomerUpdateDTO={}", ObjectUtils.toJson(groomerUpdateDTO));

    GroomerDTO groomerDTO = groomerService.updateProfile(groomerUpdateDTO);

    return new ResponseEntity<>(groomerDTO, OK);
  }

  @Operation(summary = "Upload Profile Images", description = "upload profile images")
  @PostMapping(value = "/{uuid}/profile/images")
  public ResponseEntity<List<S3FileDTO>> uploadProfileImages(
      @RequestHeader(name = "token", required = true) String token,
      @PathVariable String uuid,
      @RequestParam(name = "images") List<MultipartFile> images) {
    log.info("uploadProfileImages, uuid={}", uuid);

    List<S3FileDTO> s3FileDTOs = groomerService.uploadProfileImages(uuid, images);

    return new ResponseEntity<>(s3FileDTOs, OK);
  }

  @Operation(summary = "Upload Contract Documents", description = "upload contract documents")
  @PostMapping(value = "/{uuid}/contract/documents")
  public ResponseEntity<List<S3FileDTO>> uploadContractDocuments(
      @RequestHeader(name = "token", required = true) String token,
      @PathVariable String uuid,
      @RequestParam(name = "images") List<MultipartFile> images) {
    log.info("uploadProfileImages, uuid={}", uuid);

    List<S3FileDTO> s3FileDTOs = groomerService.uploadContractDocuments(uuid, images);

    return new ResponseEntity<>(s3FileDTOs, OK);
  }

  @Operation(summary = "Search Groomers", description = "search groomers")
  @GetMapping(value = "/search")
  public ResponseEntity<CustomPage<GroomerES>> search(
          @RequestHeader(name = "token", required = true) String token,
          @RequestParam(required = false, defaultValue = "0") Long pageNumber,
          @RequestParam(required = false, defaultValue = "25") Long pageSize,
          @RequestParam Long lat,
          @RequestParam Long lon,
          @RequestParam(required = false) String searchPhrase,
          HttpServletRequest request) {
    log.info("search, searchPhrase={}", searchPhrase);

    CustomPage<GroomerES> results = groomerService.search(pageNumber,pageSize, lat, lon, searchPhrase);

    return new ResponseEntity<>(results, OK);
  }
}
