package com.pooch.api.library.aws.s3;

import java.io.InputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Service
@Slf4j
public class AwsS3ServiceImp implements AwsS3Service {

    @Value("${aws.s3.bucket}")
    private String   S3_BUCKET;

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public AwsUploadResponse uploadPublicObj(String objectKey, ObjectMetadata metadata, InputStream inputStream) {

        PutObjectResult result = null;

        objectKey = "public/" + objectKey;

        try {
            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(S3_BUCKET, objectKey, inputStream, metadata);

            result = amazonS3.putObject(request);

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {
            return null;
        }

        URL objectUrl = amazonS3.getUrl(S3_BUCKET, objectKey);

        return new AwsUploadResponse(objectKey, objectUrl.toString());
    }

    @Override
    public AwsUploadResponse uploadPrivateObj(String objectKey, ObjectMetadata metadata, InputStream inputStream) {

        PutObjectResult result = null;

        objectKey = "private/" + objectKey;

        try {
            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(S3_BUCKET, objectKey, inputStream, metadata);

            result = amazonS3.putObject(request);

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {
            return null;
        }

        URL objectUrl = amazonS3.getUrl(S3_BUCKET, objectKey);

        return new AwsUploadResponse(objectKey, objectUrl.toString());
    }
}
