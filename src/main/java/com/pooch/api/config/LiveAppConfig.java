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
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

import java.io.IOException;

@Slf4j
@Profile(value = {"dev", "qa", "prod"})
@Configuration
public class LiveAppConfig {

  @Value("${aws.deploy.region:us-west-2}")
  private String targetRegion;

  @Value("${spring.datasource.name}")
  private String databaseName;

  @Autowired private AwsSecretsManagerService awsSecretsManagerService;

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

  /** Override default flyway initializer to do nothing */
  @Bean
  FlywayMigrationInitializer flywayInitializer() {
    return new FlywayMigrationInitializer(
        setUpFlyway(),
        (f) -> { // do nothing
          log.info("do no migration yet. wait til hibernate initializes tables...");
        });
  }

  /** Create a second flyway initializer to run after jpa has created the schema */
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

    FluentConfiguration configuration =
        Flyway.configure()
            .dataSource(url, databaseSecrets.getUsername(), databaseSecrets.getPassword());
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

  @Bean(name = "firebaseSecrets")
  public FirebaseSecrets firebaseSecrets() {
    return awsSecretsManagerService.getFirebaseSecrets();
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

  @Bean
  public RestHighLevelClient restHighLevelClient() {
    log.info("configuring elasticsearch");
    RestHighLevelClient restHighLevelClient = null;
    try {

      ElasticsearchSecrets esSecrets = awsSecretsManagerService.getElasticsearchSecrets();

      log.info("ElasticsearchSecrets={}", ObjectUtils.toJson(esSecrets));
      // @formatter:off

      final int numberOfThreads = 50;
      final int connectionTimeoutTime = 60;

//      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//      credentialsProvider.setCredentials(
//          AuthScope.ANY, new UsernamePasswordCredentials(esSecrets.getUsername(), esSecrets.getPassword()));

      RestClientBuilder restClientBuilder =
          RestClient.builder(new HttpHost(esSecrets.getHost(), esSecrets.getPort(), esSecrets.getHttpType()));

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

      // @formatter:on

      restHighLevelClient = new RestHighLevelClient(restClientBuilder);

    } catch (Exception e) {
      log.error(e.getMessage());
    }
    boolean ping = false;
    try {
      ping = restHighLevelClient.ping(RequestOptions.DEFAULT);
    } catch (IOException e) {
      e.printStackTrace();
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
