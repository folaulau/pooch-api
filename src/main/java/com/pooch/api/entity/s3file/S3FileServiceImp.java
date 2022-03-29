package com.pooch.api.entity.s3file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.library.aws.s3.AwsS3Service;
import com.pooch.api.library.aws.s3.AwsUploadResponse;
import com.twilio.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3FileServiceImp implements S3FileService {

    @Autowired
    private S3FileDAO       s3FileDAO;

    @Autowired
    private AwsS3Service    awsS3Service;

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

}
