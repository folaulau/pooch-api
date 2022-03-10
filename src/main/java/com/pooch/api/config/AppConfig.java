package com.pooch.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.utils.ObjectUtils;

@Profile({"local", "github", "dev", "qa", "prod"})
@Configuration
@EnableRetry
public class AppConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = ObjectUtils.getObjectMapper();
        return objectMapper;
    }
}
