package com.pooch.api.library.aws.secretsmanager;

public interface AwsSecretsManagerService {

  public DatabaseSecrets getDbSecret();

  public StripeSecrets getStripeSecrets();

  public TwilioSecrets getTwilioSecrets();

  public FirebaseSecrets getFirebaseSecrets();

  public ElasticsearchSecrets getElasticsearchSecrets();

  public XApiKey getXApiKeys();

  public SMTPSecrets getSMTPSecrets();
  
  public MailChimpSecrets getMailChimpSecrets();
  
  public SendGridSecrets getSendGridSecrets();

}
