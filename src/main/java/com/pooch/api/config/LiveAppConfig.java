package com.pooch.api.config;

import com.pooch.api.library.aws.secretsmanager.*;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
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

import com.pooch.api.utils.ObjectUtils;
import com.sendgrid.SendGrid;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Profile(value = {"dev", "qa", "prod"})
@Configuration
public class LiveAppConfig {

    @Value("${aws.deploy.region:us-west-2}")
    private String                   targetRegion;

    @Value("${spring.datasource.name}")
    private String                   databaseName;

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

    @Bean(name = "stripeSecrets")
    public StripeSecrets stripeSecrets() {
        return awsSecretsManagerService.getStripeSecrets();
    }

    @Bean(name = "twilioSecrets")
    public TwilioSecrets twilioSecrets() {
        return awsSecretsManagerService.getTwilioSecrets();
    }

    @Bean(name = "firebaseSecrets")
    public FirebaseSecrets firebaseSecrets() {
        return awsSecretsManagerService.getFirebaseSecrets();
    }

    @Bean(name = "queue")
    public String queue(@Value("${queue}") String queue) {
        return queue;
    }

    @Bean(name = "xApiKey")
    public XApiKey xApiKeySecrets() {
        return awsSecretsManagerService.getXApiKeys();
    }

    @Bean
    public SendGrid sendGrid() {
        SendGridSecrets sendGridSecrets = awsSecretsManagerService.getSendGridSecrets();
        SendGrid sendGrid = new SendGrid(sendGridSecrets.getApiKey());
        return sendGrid;
    }

    // @Bean
    // public JavaMailSender javaMailSender() {
    // JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    // mailSender.setHost("smtp.sendgrid.net");
    // mailSender.setPort(587);
    //
    // SMTPSecrets sMTPSecrets = awsSecretsManagerService.getSMTPSecrets();
    //
    // mailSender.setUsername(sMTPSecrets.getUsername());
    // mailSender.setPassword(sMTPSecrets.getPassword());
    //
    // Properties props = mailSender.getJavaMailProperties();
    // props.put("mail.transport.protocol", "smtp");
    // props.put("mail.smtp.auth", "true");
    // props.put("mail.smtp.starttls.enable", "true");
    // props.put("mail.debug", "true");
    //
    // return mailSender;
    // }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        log.info("configuring elasticsearch");
        RestHighLevelClient restHighLevelClient = null;
        try {

            ElasticsearchSecrets esSecrets = awsSecretsManagerService.getElasticsearchSecrets();

            log.info("ElasticsearchSecrets={}", ObjectUtils.toJson(esSecrets));

            final int numberOfThreads = 50;
            final int connectionTimeoutTime = 60;

            // final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            // credentialsProvider.setCredentials(
            // AuthScope.ANY, new UsernamePasswordCredentials(esSecrets.getUsername(),
            // esSecrets.getPassword()));

            RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(esSecrets.getHost(), esSecrets.getPort(), esSecrets.getHttpType()));

      // @formatter:off
            restClientBuilder.setHttpClientConfigCallback(
              new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
    
                  httpClientBuilder =
                      httpClientBuilder.setDefaultIOReactorConfig(
                          IOReactorConfig.custom()
                              .setIoThreadCount(numberOfThreads)
                              .setConnectTimeout(connectionTimeoutTime)
                              .build());
    
                  return httpClientBuilder;//.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
    
            log.info("HttpClientConfigCallback set!");
            restClientBuilder.setRequestConfigCallback(
              new RestClientBuilder.RequestConfigCallback() {
    
                @Override
                public RequestConfig.Builder customizeRequestConfig(
                    RequestConfig.Builder requestConfigBuilder) {
                  // TODO Auto-generated method stub
                  return requestConfigBuilder
                      .setConnectTimeout(1000000)
                      .setSocketTimeout(6000000)
                      .setConnectionRequestTimeout(300000);
                }
            });
            log.info("RequestConfigCallback set!");
            // @formatter:on

            restHighLevelClient = new RestHighLevelClient(restClientBuilder);
            log.info("RestHighLevelClient set!");
        } catch (Exception e) {
            log.warn("Exception RestHighLevelClient, msg={}", e.getMessage());
        }

        boolean ping = false;
        try {
            // ping = restHighLevelClient.ping(RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.warn("IOException. ping error, msg={}", e.getMessage());
        }

        log.info("elasticsearch configured! ping={}", ping);
        return restHighLevelClient;
    }

    @Bean
    public ElasticsearchConverter elasticsearchConverter() {
        return new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext());
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(restHighLevelClient(), elasticsearchConverter());
    }
}
