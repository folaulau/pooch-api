package com.pooch.api.entity.s3file;

import com.pooch.api.dto.S3FileDTO;

public interface S3FileService {

  S3FileDTO refreshTTL(String uuid);

  Boolean delete(String uuid);

  S3FileDTO setGroomerMainProfileImage(String uuid, String groomerUuid);

  S3FileDTO setParentMainProfileImage(String uuid, String parentUuid);

}
