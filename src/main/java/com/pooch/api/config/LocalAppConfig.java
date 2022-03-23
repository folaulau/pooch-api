package com.pooch.api.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.pooch.api.aws.secretsmanager.FirebaseSecrets;
import com.pooch.api.aws.secretsmanager.TwilioSecrets;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"local"})
@Configuration
@PropertySource("classpath:config/local-secrets.properties")
public class LocalAppConfig {

    @Value("${database.username}")
    private String databaseUsername;

    @Value("${database.password}")
    private String databasePassword;

    @Value("${database.name}")
    private String databaseName;

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${aws.deploy.region:us-west-2}")
    private String targetRegion;

    @Value("${firebase.web.api.key}")
    private String firebaseWebApiKey;

    @Bean(name = "stripeApiSecretKey")
    public String stripeApiSecretKey(@Value("${stripe.secret.key}") String stripeApiSecretKey) {
        return stripeApiSecretKey;
    }

    @Bean(name = "stripeProductId")
    public String stripeProductId(@Value("${stripe.product}") String stripeProductId) {
        return stripeProductId;
    }

    @Bean(name = "queue")
    public String queue(@Value("${queue}") String queue) {
        return queue;
    }

    @Bean(name = "twilioSecrets")
    public TwilioSecrets getTwilioSecrets(@Value("${twilio.account.sid}") String twilioAccountId, @Value("${twilio.auth.token}") String twilioAuthToken,
            @Value("${twilio.sms.sender}") String twilioSMSSender) {
        TwilioSecrets twilioSecrets = new TwilioSecrets();
        twilioSecrets.setAccountSid(twilioAccountId);
        twilioSecrets.setAuthToken(twilioAuthToken);
        twilioSecrets.setSmsSender(twilioSMSSender);
        return twilioSecrets;
    }

    /* ================== datasource =============== */
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

    @Bean(name = "firebaseSecrets")
    public FirebaseSecrets firebaseSecrets() {
        FirebaseSecrets firebaseSecrets = new FirebaseSecrets();
        firebaseSecrets.setAuthWebApiKey(firebaseWebApiKey);
        return firebaseSecrets;
    }
}
