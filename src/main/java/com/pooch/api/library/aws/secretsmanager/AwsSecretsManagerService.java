package com.pooch.api.library.aws.secretsmanager;

public interface AwsSecretsManagerService {

    public DatabaseSecrets getDbSecret();

    public StripeSecrets getStripeSecrets();

    public TwilioSecrets getTwilioSecrets();
    
    public FirebaseSecrets getFirebaseSecrets();
}
