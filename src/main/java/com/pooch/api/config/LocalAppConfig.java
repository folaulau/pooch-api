package com.pooch.api.config;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.pooch.api.library.aws.secretsmanager.FirebaseSecrets;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.aws.secretsmanager.TwilioSecrets;
import com.pooch.api.library.aws.secretsmanager.XApiKey;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.io.IOException;
import javax.sql.DataSource;

@Slf4j
@Profile(value = {"local"})
@Configuration
@EnableElasticsearchAuditing()
@EnableElasticsearchRepositories(basePackages = "com.pooch.api.elastic.repo")
@PropertySource("classpath:config/local-secrets.properties")
public class LocalAppConfig {

    /** Postgres */
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

    /**
     * Elasticsearch
     *
     * @param stripeApiSecretKey
     * @return
     */
    @Value("${elasticsearch.host}")
    private String clusterNode;

    @Value("${elasticsearch.httptype}")
    private String clusterHttpType;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Value("${elasticsearch.port}")
    private int    clusterHttpPort;

    @Bean(name = "stripeSecrets")
    public StripeSecrets stripeSecrets(@Value("${stripe.publishable.key}") String publishableKey, @Value("${stripe.secret.key}") String secretKey, @Value("${stripe.product}") String productId,
            @Value("${stripe.webhook.signing.key}") String webhookSigningKey) {
        return new StripeSecrets(publishableKey, secretKey, productId, webhookSigningKey);
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

    @Bean(name = "xApiKey")
    public XApiKey xApiKeySecrets(@Value("${web.x.api.key}") String webXApiKey, @Value("${mobile.x.api.key}") String mobileXApiKey, @Value("${utility.x.api.key}") String utilityXApiKey) {
        return new XApiKey(webXApiKey, mobileXApiKey, utilityXApiKey);
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

    @Bean(name = "firebaseSecrets")
    public FirebaseSecrets firebaseSecrets() {
        FirebaseSecrets firebaseSecrets = new FirebaseSecrets();
        firebaseSecrets.setAuthWebApiKey(firebaseWebApiKey);
        return firebaseSecrets;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        log.info("configuing elasticsearch");
        RestHighLevelClient restHighLevelClient = null;
        try {
            log.info("username={}, password={}", username, password);

//             clusterNode = "search-dev-es-pooch-api-x4vu7wl4j4bccdz6wn6abne5uu.us-west-2.es.amazonaws.com";
//             clusterHttpType = "https";
//             clusterHttpPort = 443;

            log.info("clusterNode={}, httpType={}", clusterNode, this.clusterHttpType);

            final int numberOfThreads = 10;
            final int connectionTimeoutTime = 60;

      // @formatter:off
//      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//      credentialsProvider.setCredentials(
//          AuthScope.ANY, new UsernamePasswordCredentials(username, password));

      
      RestClientBuilder restClientBuilder =
          RestClient.builder(new HttpHost(clusterNode, clusterHttpPort, clusterHttpType));

      restClientBuilder = restClientBuilder.setHttpClientConfigCallback(
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
      restClientBuilder = restClientBuilder.setRequestConfigCallback(
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
            ping = restHighLevelClient.ping(RequestOptions.DEFAULT);
        } catch (IOException e) {
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
