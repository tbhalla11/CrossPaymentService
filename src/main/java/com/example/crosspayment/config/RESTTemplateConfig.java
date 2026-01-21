package com.example.crosspayment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;


@Configuration
public class RESTTemplateConfig {

    /**
     * timeout value
     * Taken from applications.properties
     *
     */
    @Value("${fx.service.timeout:5000}")
    private long timeout;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.setConnectTimeout(Duration.ofMillis(timeout)).
                setReadTimeout(Duration.ofMillis(timeout)).build();
    }
}
