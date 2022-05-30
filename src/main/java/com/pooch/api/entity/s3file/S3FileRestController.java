package com.pooch.api.entity.s3file;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.exception.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Files", description = "File Operations")
@RestController
@RequestMapping("/files")
public class S3FileRestController {

  @Autowired
  private S3FileService s3FileService;

  @Operation(summary = "Refresh Private File Url", description = "refresh private file url")
  @GetMapping(value = "/{uuid}/fresh")
  public ResponseEntity<S3FileDTO> refreshPrivateFileUrl(
      @RequestHeader(name = "token", required = true) String token, @PathVariable String uuid) {
    log.info("refreshPrivateFileUrl, uuid={}", uuid);

    if (true) {
      throw new ApiException("This endpoint is not ready");
    }

    S3FileDTO s3FileDTOs = s3FileService.refreshTTL(uuid);

    return new ResponseEntity<>(s3FileDTOs, OK);
  }

  @Operation(summary = "Delete File", description = "delete file")
  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<Boolean> delete(
      @RequestHeader(name = "token", required = true) String token, @PathVariable String uuid) {
    log.info("delete, uuid={}", uuid);

    Boolean result = s3FileService.delete(uuid);

    return new ResponseEntity<>(result, OK);
  }

  @Operation(summary = "Set Groomer Main Profile Image",
      description = "set groomer main profile image. All other profile images will have mainProfileImage as false")
  @PutMapping(value = "/{uuid}/groomer-profile-image")
  public ResponseEntity<S3FileDTO> setGroomerMainProfileImage(
      @RequestHeader(name = "token", required = true) String token, @PathVariable String uuid,
      @RequestParam String groomerUuid) {
    log.info("setGroomerMainProfileImage, uuid={}, groomerUuid={}", uuid, groomerUuid);

    S3FileDTO s3FileDTO = s3FileService.setGroomerMainProfileImage(uuid, groomerUuid);

    return new ResponseEntity<>(s3FileDTO, OK);
  }

  @Operation(summary = "Set Parent Main Profile Image",
      description = "set parent main profile image. All other profile images will have mainProfileImage as false")
  @PutMapping(value = "/{uuid}/parent-profile-image")
  public ResponseEntity<S3FileDTO> setParentMainProfileImage(
      @RequestHeader(name = "token", required = true) String token, @PathVariable String uuid,
      @RequestParam String parentUuid) {
    log.info("setParentMainProfileImage, uuid={}, parentUuid={}", uuid, parentUuid);

    S3FileDTO s3FileDTO = s3FileService.setParentMainProfileImage(uuid, parentUuid);

    return new ResponseEntity<>(s3FileDTO, OK);
  }
}
