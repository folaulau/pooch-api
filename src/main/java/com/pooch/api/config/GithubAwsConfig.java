package com.pooch.api.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.pooch.api.aws.secretsmanager.AwsSecretsManagerService;
import com.pooch.api.aws.secretsmanager.TwilioSecrets;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile({"github"})
@Configuration
// @PropertySource("classpath:config/application-github.properties")
public class GithubAwsConfig {

    @Value("${aws.deploy.region:us-west-2}")
    private String targetRegion;

    /**
     * from github secrets
     */
    @Value("${aws.access.key}")
    private String awsAccessKey;

    /**
     * from github secrets
     */
    @Value("${aws.secret.key}")
    private String awsSecretkey;

    /**
     * AWS
     */

    private Regions getTargetRegion() {
        return Regions.fromName(targetRegion);
    }

    @Bean
    public AWSCredentialsProvider amazonAWSCredentialsProvider() {
        AWSStaticCredentialsProvider credProvider = new AWSStaticCredentialsProvider(new AWSCredentials() {

            @Override
            public String getAWSSecretKey() {
                // TODO Auto-generated method stub
                return awsSecretkey;
            }

            @Override
            public String getAWSAccessKeyId() {
                // TODO Auto-generated method stub
                return awsAccessKey;
            }
        });

        return credProvider;

        // return DefaultAWSCredentialsProviderChain.getInstance();
    }

    @Bean
    public AWSSecretsManager awsSecretsManager(AWSCredentialsProvider aWSCredentialsProvider) {
        String endpoint = "secretsmanager." + getTargetRegion().getName() + ".amazonaws.com";
        AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, getTargetRegion().getName());
        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
        clientBuilder.setEndpointConfiguration(config);
        clientBuilder.setCredentials(aWSCredentialsProvider);
        return clientBuilder.build();
    }
}
