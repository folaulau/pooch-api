package com.pooch.api.library.aws.secretsmanager;

import java.nio.ByteBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"github", "dev", "qa", "prod"})
@Component
public class AwsSecretsManagerServiceImp implements AwsSecretsManagerService {

    @Value("${database.secret.name}")
    private String            databaseSecretName;

    @Value("${stripe.secret.name}")
    private String            stripeSecretName;

    @Value("${twilio.secret.name}")
    private String            twilioSecretName;

    @Value("${firebase.secret.name}")
    private String            firebaseSecretName;

    @Autowired
    private AWSSecretsManager awsSecretsManager;

    @Override
    public DatabaseSecrets getDbSecret() {

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
        getSecretValueRequest.setSecretId(databaseSecretName);

        GetSecretValueResult getSecretValueResponse = null;
        try {
            getSecretValueResponse = awsSecretsManager.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            log.error("Failed to get values for sercret {}, msg:{}", databaseSecretName, e.getMessage(), e);
        }

        if (getSecretValueResponse == null) {
            return null;
        }

        ByteBuffer binarySecretData;
        String secret;
        // Decrypted secret using the associated KMS CMK
        // Depending on whether the secret was a string or binary, one of these fields
        // will be populated
        if (getSecretValueResponse.getSecretString() != null) {
            log.info("secret string");
            secret = getSecretValueResponse.getSecretString();
            return DatabaseSecrets.fromJson(secret);
        } else {
            log.info("secret binary secret data");
            binarySecretData = getSecretValueResponse.getSecretBinary();
            return DatabaseSecrets.fromJson(binarySecretData.toString());
        }
    }

    @Override
    public StripeSecrets getStripeSecrets() {
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
        getSecretValueRequest.setSecretId(stripeSecretName);

        GetSecretValueResult getSecretValueResponse = null;
        try {
            getSecretValueResponse = awsSecretsManager.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            log.error("Failed to get values for sercret {}, msg:{}", stripeSecretName, e.getMessage(), e);
        }

        if (getSecretValueResponse == null) {
            return null;
        }

        ByteBuffer binarySecretData;
        String secret;
        // Decrypted secret using the associated KMS CMK
        // Depending on whether the secret was a string or binary, one of these fields
        // will be populated
        if (getSecretValueResponse.getSecretString() != null) {
            log.info("secret string");
            secret = getSecretValueResponse.getSecretString();
            return StripeSecrets.fromJson(secret);
        } else {
            log.info("secret binary secret data");
            binarySecretData = getSecretValueResponse.getSecretBinary();
            return StripeSecrets.fromJson(binarySecretData.toString());
        }
    }

    @Override
    public TwilioSecrets getTwilioSecrets() {
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
        getSecretValueRequest.setSecretId(twilioSecretName);

        GetSecretValueResult getSecretValueResponse = null;
        try {
            getSecretValueResponse = awsSecretsManager.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            log.error("Failed to get values for sercret {}, msg:{}", twilioSecretName, e.getMessage(), e);
        }

        if (getSecretValueResponse == null) {
            return null;
        }

        ByteBuffer binarySecretData;
        String secret;
        // Decrypted secret using the associated KMS CMK
        // Depending on whether the secret was a string or binary, one of these fields
        // will be populated
        if (getSecretValueResponse.getSecretString() != null) {
            log.info("secret string");
            secret = getSecretValueResponse.getSecretString();
            return TwilioSecrets.fromJson(secret);
        } else {
            log.info("secret binary secret data");
            binarySecretData = getSecretValueResponse.getSecretBinary();
            return TwilioSecrets.fromJson(binarySecretData.toString());
        }
    }

    @Override
    public FirebaseSecrets getFirebaseSecrets() {
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
        getSecretValueRequest.setSecretId(firebaseSecretName);

        GetSecretValueResult getSecretValueResponse = null;
        try {
            getSecretValueResponse = awsSecretsManager.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            log.error("Failed to get values for sercret {}, msg:{}", firebaseSecretName, e.getMessage(), e);
        }

        if (getSecretValueResponse == null) {
            return null;
        }

        ByteBuffer binarySecretData;
        String secret;
        // Decrypted secret using the associated KMS CMK
        // Depending on whether the secret was a string or binary, one of these fields
        // will be populated
        if (getSecretValueResponse.getSecretString() != null) {
            log.info("secret string");
            secret = getSecretValueResponse.getSecretString();
            return FirebaseSecrets.fromJson(secret);
        } else {
            log.info("secret binary secret data");
            binarySecretData = getSecretValueResponse.getSecretBinary();
            return FirebaseSecrets.fromJson(binarySecretData.toString());
        }
    }
}