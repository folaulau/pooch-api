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

import com.pooch.api.aws.secretsmanager.AwsSecretsManagerService;
import com.pooch.api.aws.secretsmanager.DatabaseSecrets;
import com.pooch.api.aws.secretsmanager.StripeSecrets;
import com.pooch.api.aws.secretsmanager.TwilioSecrets;
import com.pooch.api.utils.ObjectUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"dev", "qa", "prod"})
@Configuration
public class LiveAppConfig {

    @Value("${aws.deploy.region:us-west-2}")
    private String                targetRegion;

    @Value("${spring.datasource.name}")
    private String                databaseName;

    @Autowired
    private AwsSecretsManagerService awsSecretsManagerService;

    /* ================== datasource =============== */
    @Bean
    public HikariDataSource dataSource() {
        log.info("Configuring dataSource...");

        DatabaseSecrets databaseSecrets = awsSecretsManagerService.getDbSecret();

        log.info("databaseSecrets={}", ObjectUtils.toJson(databaseSecrets));
        // jdbc:postgresql://localhost:5432/learnmymath_api_db
        int port = 5432;
        String host = databaseSecrets.getHost();
        String username = databaseSecrets.getUsername();
        String password = databaseSecrets.getPassword();
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(databaseSecrets.getUsername());
        config.setPassword(databaseSecrets.getPassword());
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

        DatabaseSecrets databaseSecrets = awsSecretsManagerService.getDbSecret();

        // jdbc:postgresql://localhost:5432/learnmymath_api_db
        int port = 5432;
        String host = databaseSecrets.getHost();
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;

        FluentConfiguration configuration = Flyway.configure().dataSource(url, databaseSecrets.getUsername(), databaseSecrets.getPassword());
        configuration.schemas(databaseName);
        configuration.baselineOnMigrate(true);
        return configuration.load();
    }

    @Bean(name = "stripeSecret")
    public StripeSecrets stripeSecret() {
        return awsSecretsManagerService.getStripeSecrets();
    }
    
    @Bean(name = "twilioSecrets")
    public TwilioSecrets twilioSecrets() {
        return awsSecretsManagerService.getTwilioSecrets();
    }

    @DependsOn("stripeSecret")
    @Bean(name = "stripeApiSecretKey")
    public String stripeApiSecretKey(StripeSecrets stripeSecrets) {
        return stripeSecrets.getSecretKey();
    }

    @DependsOn("stripeSecret")
    @Bean(name = "stripeApiPublishableKey")
    public String stripeApiPublishableKey(StripeSecrets stripeSecrets) {
        return stripeSecrets.getPublishableKey();
    }

    @DependsOn("stripeSecret")
    @Bean(name = "stripeProductId")
    public String stripeProductId(StripeSecrets stripeSecrets) {
        return stripeSecrets.getProductId();
    }
    
    @DependsOn("stripeSecret")
    @Bean(name = "stripeWebhookSubscriptionSigningSecret")
    public String stripeWebhookSubscriptionSigningSecret(StripeSecrets stripeSecrets) {
        return stripeSecrets.getWebhookSubscriptionSigningSecret();
    }

    @Bean(name = "queue")
    public String queue(@Value("${queue}") String queue) {
        return queue;
    }
}
