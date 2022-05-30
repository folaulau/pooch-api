package com.pooch.api.entity.s3file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.s3.AwsS3Service;
import com.pooch.api.library.aws.s3.AwsUploadResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3FileServiceImp implements S3FileService {

  @Autowired
  private S3FileDAO s3FileDAO;

  @Autowired
  private AwsS3Service awsS3Service;

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Override
  public S3FileDTO refreshTTL(String uuid) {
    S3File s3File = s3FileDAO.getByUuid(uuid).orElseThrow(() -> new ApiException("File not found"));
    AwsUploadResponse uploadResponse = awsS3Service.refreshTTL(s3File.getS3key());
    s3File.setUrl(uploadResponse.getObjectUrl());
    s3File = s3FileDAO.save(s3File);

    return entityDTOMapper.mapS3FileToS3FileDTO(s3File);
  }

  @Override
  public Boolean delete(String uuid) {

    S3File s3File = s3FileDAO.getByUuid(uuid).orElseThrow(() -> new ApiException("File not found"));

    return s3FileDAO.delete(s3File);
  }

  @Override
  public S3FileDTO setGroomerMainProfileImage(String uuid, String groomerUuid) {

    S3File s3File = s3FileDAO.getByUuid(uuid).orElseThrow(() -> new ApiException("File not found"));

    Groomer groomer = s3File.getGroomer();

    if (groomer == null || groomer.getUuid() == null
        || !groomer.getUuid().equalsIgnoreCase(groomerUuid)) {
      throw new ApiException(ApiError.DEFAULT_MSG, "s3File does not belong to this groomer");
    }

    s3File = s3FileDAO.setMainProfileImage(groomer, s3File);

    return entityDTOMapper.mapS3FileToS3FileDTO(s3File);
  }

  @Override
  public S3FileDTO setParentMainProfileImage(String uuid, String parentUuid) {

    S3File s3File = s3FileDAO.getByUuid(uuid).orElseThrow(() -> new ApiException("File not found"));

    Parent parent = s3File.getParent();

    if (parent == null || parent.getUuid() == null
        || !parent.getUuid().equalsIgnoreCase(parentUuid)) {
      throw new ApiException(ApiError.DEFAULT_MSG, "s3File does not belong to this groomer");
    }

    s3File = s3FileDAO.setMainProfileImage(parent, s3File);

    return entityDTOMapper.mapS3FileToS3FileDTO(s3File);
  }

}
