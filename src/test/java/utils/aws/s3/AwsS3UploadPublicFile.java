package utils.aws.s3;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.pooch.api.config.LocalAwsConfig;
import com.pooch.api.utils.ObjectUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class AwsS3UploadPublicFile {

    private static AmazonS3              amazonS3;

    private final List<Future<PartETag>> futuresPartETags = new ArrayList<>();

    public static void main(String[] args) {
        LocalAwsConfig awsConfig = new LocalAwsConfig();
        amazonS3 = awsConfig.amazonS3();

        List<Bucket> s3Buckets = amazonS3.listBuckets();

        s3Buckets.stream().forEach(bucket -> {
            log.info("name: {}", bucket.getName());
        });
        //uploadToPublicFolder();

        uploadToPrivateFolder();
    }

    public static void uploadToPublicFolder() {
        try {
            String bucketName = "pooch-api-local";
            String stringObjKeyName = "usa.txt";
            String fileObjKeyName = "public/tonga.txt";
            String fileName = "notes.txt";

            // Upload a text string as a new object.
            amazonS3.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            //
            // List<Tag> tags = new ArrayList<Tag>();
            // tags.add(new Tag("Tag 1", "This is tag 1"));
            // tags.add(new Tag("Tag 2", "This is tag 2"));
            // request.setTagging(new ObjectTagging(tags));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", "someTitle");
            request.setMetadata(metadata);
            PutObjectResult result = amazonS3.putObject(request);

            URL objectUrl = amazonS3.getUrl(bucketName, fileObjKeyName);

            log.info("metadata={}", ObjectUtils.toJson(result.getMetadata()));

            log.info("objectUrl={}", objectUrl.toString());

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    public static void uploadToPrivateFolder() {
        try {
            String bucketName = "pooch-api-local";
            String stringObjKeyName = "usa.txt";
            String fileObjKeyName = "private/tonga.txt";
            String fileName = "notes.txt";

            // Upload a text string as a new object.
            amazonS3.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            //
            // List<Tag> tags = new ArrayList<Tag>();
            // tags.add(new Tag("Tag 1", "This is tag 1"));
            // tags.add(new Tag("Tag 2", "This is tag 2"));
            // request.setTagging(new ObjectTagging(tags));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", "someTitle");
            request.setMetadata(metadata);
            PutObjectResult result = amazonS3.putObject(request);

            URL objectUrl = amazonS3.getUrl(bucketName, fileObjKeyName);

            log.info("metadata={}", ObjectUtils.toJson(result.getMetadata()));

            log.info("objectUrl={}", objectUrl.toString());

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    public void uploadStreamToS3(String bucketName, String fileKey) {

        String uploadId = UUID.randomUUID().toString();
        try {
            List<PartETag> partETags = new ArrayList<>();
            for (Future<PartETag> partETagFuture : futuresPartETags) {
                partETags.add(partETagFuture.get());
            }
            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(bucketName, fileKey, uploadId, partETags);

            amazonS3.completeMultipartUpload(completeRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
