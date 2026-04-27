package com.example.aitrainer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // RestTemplate is Spring's HTTP client — like fetch() in JavaScript
    // We declare it as a @Bean so Spring can inject it anywhere we need it
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
