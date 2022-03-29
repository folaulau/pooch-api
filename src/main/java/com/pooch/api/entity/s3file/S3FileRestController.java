package com.pooch.api.entity.s3file;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<S3FileDTO> refreshPrivateFileUrl(@RequestHeader(name = "token", required = true) String token, @PathVariable String uuid) {
        log.info("refreshPrivateFileUrl, uuid={}", uuid);

        if (true) {
            throw new ApiException("This endpoint is not ready");
        }

        S3FileDTO s3FileDTOs = s3FileService.refreshTTL(uuid);

        return new ResponseEntity<>(s3FileDTOs, OK);
    }

    @Operation(summary = "Delete File", description = "delete file")
    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<Boolean> delete(@RequestHeader(name = "token", required = true) String token, @PathVariable String uuid) {
        log.info("delete, uuid={}", uuid);

        Boolean result = s3FileService.delete(uuid);

        return new ResponseEntity<>(result, OK);
    }
}
