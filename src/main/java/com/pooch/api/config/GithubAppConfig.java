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
import com.pooch.api.library.aws.secretsmanager.AwsSecretsManagerService;
import com.pooch.api.library.aws.secretsmanager.FirebaseSecrets;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.aws.secretsmanager.TwilioSecrets;
import com.pooch.api.library.aws.secretsmanager.XApiKey;
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
@Profile({"github"})
@Configuration
public class GithubAppConfig {

  @Value("${aws.deploy.region:us-west-2}")
  private String targetRegion;

  @Value("${database.username}")
  private String databaseUsername;

  @Value("${database.password}")
  private String databasePassword;

  @Value("${database.url}")
  private String databaseUrl;

  @Value("${spring.datasource.name}")
  private String databaseName;

  @Value("${aws.access.key}")
  private String awsAccessKey;

  @Value("${aws.secret.access.key}")
  private String awsSecretAccessKey;

  @Value("${firebase.web.api.key}")
  private String firebaseWebApiKey;

  @Value("${elasticsearch.host}")
  private String clusterNode;

  @Value("${elasticsearch.httptype}")
  private String clusterHttpType;

  @Value("${elasticsearch.username}")
  private String username;

  @Value("${elasticsearch.password}")
  private String password;

  @Value("${elasticsearch.port:9200}")
  private int clusterHttpPort;

  @Autowired
  private AwsSecretsManagerService awsSecretsManagerService;

  private Regions getTargetRegion() {
    return Regions.fromName(targetRegion);
  }

  @Bean(name = "amazonAWSCredentialsProvider")
  public AWSCredentialsProvider amazonAWSCredentialsProvider() {
    log.info("accessKey={}, secretKey={}", awsAccessKey, awsSecretAccessKey);
    return new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(awsAccessKey, awsSecretAccessKey));

  }

  @Bean
  public AmazonS3 amazonS3() {
    return AmazonS3ClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider())
        .withRegion(getTargetRegion()).build();
  }

  @Bean
  public AmazonSQS amazonSQS() {
    return AmazonSQSClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider())
        .withEndpointConfiguration(new EndpointConfiguration(
            "sqs." + getTargetRegion().getName() + ".amazonaws.com", getTargetRegion().getName()))
        .build();
  }

  @Bean
  public AmazonSimpleEmailService amazonSES() {
    return AmazonSimpleEmailServiceClientBuilder.standard()
        .withCredentials(amazonAWSCredentialsProvider()).withRegion(Regions.US_WEST_2).build();
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

  @Bean(name = "twilioSecrets")
  public TwilioSecrets twilioSecrets() {
    return awsSecretsManagerService.getTwilioSecrets();
  }

  @Bean(name = "xApiKey")
  public XApiKey xApiKeySecrets() {
    return awsSecretsManagerService.getXApiKeys();
  }

  @Bean(name = "stripeSecrets")
  public StripeSecrets stripeSecrets() {
    return awsSecretsManagerService.getStripeSecrets();
  }

  @Bean(name = "queue")
  public String queue(@Value("${queue}") String queue) {
    return queue;
  }

  @Bean(name = "firebaseSecrets")
  public FirebaseSecrets firebaseSecrets() {
    FirebaseSecrets firebaseSecrets = new FirebaseSecrets();
    firebaseSecrets.setAuthWebApiKey(firebaseWebApiKey);
    return firebaseSecrets;
  }

  @Bean
  public RestHighLevelClient restHighLevelClient() {

    RestHighLevelClient restHighLevelClient = null;
    try {

      // @formatter:off

            final int numberOfThreads = 10;
            final int connectionTimeoutTime = 60;

//            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

            RestClientBuilder restClientBuilder = RestClient
                    .builder(new HttpHost(clusterNode, clusterHttpPort, clusterHttpType));

            restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {

                    httpClientBuilder = httpClientBuilder.setDefaultIOReactorConfig(
                            IOReactorConfig.custom()
                                    .setIoThreadCount(numberOfThreads)
                                    .setConnectTimeout(connectionTimeoutTime)
                                    .build());

                    return httpClientBuilder;//.setDefaultCredentialsProvider(credentialsProvider);
                }
            });

            restClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                    // TODO Auto-generated method stub
                    return requestConfigBuilder.setConnectTimeout(1000000).setSocketTimeout(6000000).setConnectionRequestTimeout(300000);
                }

            });

            // @formatter:on

      restHighLevelClient = new RestHighLevelClient(restClientBuilder);

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
