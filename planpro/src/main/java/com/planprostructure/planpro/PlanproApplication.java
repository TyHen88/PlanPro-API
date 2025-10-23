package com.planprostructure.planpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { OAuth2ClientAutoConfiguration.class })
@ConfigurationPropertiesScan("com.planprostructure.planpro.properties")
@EnableRetry
@EnableScheduling
public class PlanproApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlanproApplication.class, args);
    }
}
