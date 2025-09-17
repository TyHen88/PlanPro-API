package com.planprostructure.planpro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("railway")
public class RailwayConfig {

    @Value("${PORT:8080}")
    private String port;

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            try {
                int portNumber = Integer.parseInt(port);
                factory.setPort(portNumber);
            } catch (NumberFormatException e) {
                // Fallback to default port if PORT is not a valid number
                factory.setPort(8080);
            }
        };
    }
}
