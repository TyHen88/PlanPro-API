package com.planprostructure.planpro.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIRestTemplateConfig {

    @Value("${spring.ai.gemini.api-key}")
    private String geminiApiKey;

    @Bean
    @Qualifier("geminiRestTemplate")
    public RestTemplate geminiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}