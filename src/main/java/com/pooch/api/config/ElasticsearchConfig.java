package com.pooch.api.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = { "local" })
@Configuration
@EnableElasticsearchAuditing()
@EnableElasticsearchRepositories(basePackages = "com.pooch.api.elastic.repo")
public class ElasticsearchConfig {

	@Value("${elasticsearch.host}")
	private String clusterNode;

	@Value("${elasticsearch.httptype}")
	private String clusterHttpType;

	@Value("${elasticsearch.username}")
	private String username;

	@Value("${elasticsearch.password}")
	private String password;

	@Value("${elasticsearch.port}")
	private int clusterHttpPort;

	@Bean
	public RestHighLevelClient restHighLevelClient() {

		RestHighLevelClient restHighLevelClient = null;
		try {

			// @formatter:off
            
            final int numberOfThreads = 10;
            final int connectionTimeoutTime = 60;
 
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

            RestClientBuilder restClientBuilder = RestClient
            .builder(new HttpHost(clusterNode, clusterHttpPort, clusterHttpType));
            
            restClientBuilder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    
                    httpClientBuilder = httpClientBuilder.setDefaultIOReactorConfig(
                            IOReactorConfig.custom()
                                .setIoThreadCount(numberOfThreads)
                                .setConnectTimeout(connectionTimeoutTime)
                                .build());
                    
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
            
            restClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

                @Override
                public Builder customizeRequestConfig(Builder requestConfigBuilder) {
                    // TODO Auto-generated method stub
                    return requestConfigBuilder.setConnectTimeout(1000000).setSocketTimeout(6000000).setConnectionRequestTimeout(300000);
                }

            });
            
            // @formatter:on

			restHighLevelClient = new RestHighLevelClient(restClientBuilder);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return restHighLevelClient;
	}

//	@Bean
//	public ElasticsearchClient elasticsearchClient() {
//		RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
//
//		// Create the transport with a Jackson mapper
//		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//
//		// And create the API client
//		return new ElasticsearchClient(transport);
//	}

	@Bean
	public ElasticsearchConverter elasticsearchConverter() {
		return new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext());
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		return new ElasticsearchRestTemplate(restHighLevelClient(), elasticsearchConverter());
	}
}
