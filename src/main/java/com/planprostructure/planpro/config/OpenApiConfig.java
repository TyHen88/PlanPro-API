// package com.planprostructure.planpro.config;

// import io.swagger.v3.oas.annotations.OpenAPIDefinition;
// import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
// import io.swagger.v3.oas.annotations.info.Info;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import io.swagger.v3.oas.annotations.security.SecurityScheme;
// import io.swagger.v3.oas.annotations.servers.Server;
// import org.springdoc.core.models.GroupedOpenApi;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// @OpenAPIDefinition(
//         servers = {
//                 @Server(url = "/", description = "Default Server URL")
//         },
//         info = @Info(
//                 title = "PlanPro API",
//                 version = "1.0",
//                 description = "PlanPro API Documentation"
//         ),
//         security = @SecurityRequirement(name = "bearerAuth")
// )
// @SecurityScheme(
//         name = "bearerAuth",
//         type = SecuritySchemeType.HTTP,
//         scheme = "bearer",
//         bearerFormat = "JWT"
// )
// public class OpenApiConfig {

//     @Bean
//     public GroupedOpenApi publicApi() {
//         return GroupedOpenApi.builder()
//                 .group("public")
//                 .pathsToMatch("/api/wb/v1/auth/**", "/api/wb/v1/password/**")
//                 .build();
//     }

//     @Bean
//     public GroupedOpenApi allApi() {
//         return GroupedOpenApi.builder()
//                 .group("all")
//                 .pathsToMatch("/**")
//                 .build();
//     }
// }