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
public class GithubAppConfig {

    @Value("${aws.deploy.region:us-west-2}")
    private String                   targetRegion;

    @Value("${database.username}")
    private String                   databaseUsername;

    @Value("${database.password}")
    private String                   databasePassword;

    @Value("${database.url}")
    private String                   databaseUrl;

    @Value("${spring.datasource.name}")
    private String                   databaseName;

    @Value("${aws.access.key}")
    private String                   awsAccessKey;

    @Value("${aws.secret.access.key}")
    private String                   awsSecretAccessKey;

    @Autowired
    private AwsSecretsManagerService awsSecretsManagerService;

    private Regions getTargetRegion() {
        return Regions.fromName(targetRegion);
    }

    @Bean(name = "amazonAWSCredentialsProvider")
    public AWSCredentialsProvider amazonAWSCredentialsProvider() {
        log.info("accessKey={}, secretKey={}", awsAccessKey, awsSecretAccessKey);
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretAccessKey));

    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider()).withRegion(getTargetRegion()).build();
    }

    @Bean
    public AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder.standard()
                .withCredentials(amazonAWSCredentialsProvider())
                .withEndpointConfiguration(new EndpointConfiguration("sqs." + getTargetRegion().getName() + ".amazonaws.com", getTargetRegion().getName()))
                .build();
    }

    @Bean
    public AmazonSimpleEmailService amazonSES() {
        return AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider()).withRegion(Regions.US_WEST_2).build();
    }

    /* ================== datasource =============== */
    @DependsOn("amazonAWSCredentialsProvider")
    @Bean
    public HikariDataSource dataSource() {
        log.info("Configuring dataSource...");

        log.info("dbUrl={}", databaseUrl);
        log.info("dbUsername={}", databaseUsername);
        log.info("dbPassword={}", databasePassword);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(databaseUsername);
        config.setPassword(databasePassword);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");

        HikariDataSource hds = new HikariDataSource(config);
        hds.setMaximumPoolSize(30);
        hds.setMinimumIdle(5);
        hds.setMaxLifetime(1800000);
        hds.setConnectionTimeout(30000);
        hds.setIdleTimeout(600000);
        // 45 seconds
        hds.setLeakDetectionThreshold(45000);

        log.info("DataSource configured!");

        return hds;
    }

    /**
     * Override default flyway initializer to do nothing
     */
    @Bean
    FlywayMigrationInitializer flywayInitializer() {
        return new FlywayMigrationInitializer(setUpFlyway(), (f) -> {// do nothing
            log.info("do no migration yet. wait til hibernate initializes tables...");
        });
    }

    /**
     * Create a second flyway initializer to run after jpa has created the schema
     */
    @Bean
    @DependsOn("dataSource")
    FlywayMigrationInitializer delayedFlywayInitializer() {
        Flyway flyway = setUpFlyway();
        return new FlywayMigrationInitializer(flyway, null);
    }

    private Flyway setUpFlyway() {

        FluentConfiguration configuration = Flyway.configure().dataSource(databaseUrl, databaseUsername, databasePassword);
        configuration.schemas(databaseName);
        configuration.baselineOnMigrate(true);
        return configuration.load();
    }

    @Bean(name = "twilioSecrets")
    public TwilioSecrets twilioSecrets() {
        return awsSecretsManagerService.getTwilioSecrets();
    }

    @Bean(name = "stripeApiSecretKey")
    public String stripeApiSecretKey(@Value("${stripe.secret.key}") String stripeApiSecretKey) {
        return stripeApiSecretKey;
    }

    @Bean(name = "stripeProductId")
    public String stripeProductId(@Value("${stripe.product}") String stripeProductId) {
        return stripeProductId;
    }

    @Bean(name = "stripeWebhookSubscriptionSigningSecret")
    public String stripeWebhookSubscriptionSigningSecret(@Value("${stripe.webhook.subscription.signing.secret}") String stripeWebhookSubscriptionSigningSecret) {
        return stripeWebhookSubscriptionSigningSecret;
    }

    @Bean(name = "queue")
    public String queue(@Value("${queue}") String queue) {
        return queue;
    }
}
