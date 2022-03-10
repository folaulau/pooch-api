package com.pooch.api.aws.secretsmanager;

public interface AwsSecretsManagerService {

    public DatabaseSecrets getDbSecret();

    public StripeSecrets getStripeSecrets();
}
