package com.pooch.api.library.aws.s3;

import java.io.InputStream;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

public interface AwsS3Service {

    AwsUploadResponse uploadPublicObj(String objectKey, ObjectMetadata metadata, InputStream speechStream);

    AwsUploadResponse uploadPrivateObj(String objectKey, ObjectMetadata metadata, InputStream speechStream);
}
