package com.planprostructure.planpro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import java.net.URI;

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

    @Bean
    @ConditionalOnProperty(name = "DATABASE_URL")
    public DataSource dataSource(@Value("${DATABASE_URL}") String databaseUrl) {
        try {
            // Parse Railway's DATABASE_URL format: postgresql://user:password@host:port/database
            URI dbUri = new URI(databaseUrl);
            
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String host = dbUri.getHost();
            int port = dbUri.getPort();
            String database = dbUri.getPath().substring(1); // Remove leading slash
            
            // Replace internal hostname with external hostname for Railway
            if (host.contains("railway.internal")) {
                host = host.replace("railway.internal", "railway.app");
            }
            
            // Build JDBC URL with proper settings for Railway
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?tcpKeepAlive=true&socketTimeout=30&connectTimeout=30", 
                host, port, database);
            
            System.out.println("Connecting to database: " + jdbcUrl);
            
            return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
                
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DATABASE_URL: " + databaseUrl, e);
        }
    }

    @Bean
    @ConditionalOnProperty(name = "PGHOST")
    public DataSource dataSourceFromEnv(
            @Value("${PGHOST}") String host,
            @Value("${PGPORT:5432}") int port,
            @Value("${PGDATABASE}") String database,
            @Value("${PGUSER}") String username,
            @Value("${PGPASSWORD}") String password) {
        
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?tcpKeepAlive=true&socketTimeout=30&connectTimeout=30", 
            host, port, database);
        
        System.out.println("Connecting to database using PGHOST: " + jdbcUrl);
        
        return DataSourceBuilder.create()
            .url(jdbcUrl)
            .username(username)
            .password(password)
            .driverClassName("org.postgresql.Driver")
            .build();
    }
}
