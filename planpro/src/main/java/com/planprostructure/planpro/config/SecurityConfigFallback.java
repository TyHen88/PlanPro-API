package com.planprostructure.planpro.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.planprostructure.planpro.service.auth.UserAuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "rsa", name = "private-key", havingValue = "false", matchIfMissing = true)
public class SecurityConfigFallback {
    private final UnauthorizedHandler unauthorizedHandler;
    private final AccessDeniedHandler accessDeniedHandler;
    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
    private final PasswordEncoder passwordEncoder;

    @Primary
    @Bean("userAuthProvider")
    public AuthenticationManager userAuthProvider(UserAuthServiceImpl userDetailsService) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setHideUserNotFoundExceptions(false);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/auth/**",
                                "/api/wb/v1/auth/**",
                                "/api/wb/v1/password/**",
                                "/api/v1/auth/**",
                                "/api/v1/image/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html/**",
                                "/swagger-ui.html**",
                                "/swagger.json",
                                "/swagger-ui/**",
                                "/swagger-ui/index.html",
                                "/api/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/wb/v1/users/**",
                                "/api/wb/v1/trips/**",
                                "/api/wb/v1/files/upload-image",
                                "/api/wb/v1/calendar/**",
                                "/api/wb/v1/my-notes/**",
                                "/api/wb/v1/telegram/**",
                                "/api/wb/v1/chat/**",
                                "/api/v1/chat/**",
                                "/api/v1/conversations/**",
                                "/api/v1/message/**",
                                "/api/v1/contacts/**"
                        ).authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(customJwtAuthenticationConverter)
                        )
                )
                .build();
    }

    @Bean
    @Primary
    JwtDecoder jwtDecoder() {
        // Use a simple secret key for JWT decoding
        String secret = "your-secret-key-here-make-it-long-enough-for-hs256-algorithm";
        SecretKey key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    @Primary
    JwtEncoder jwtEncoder() {
        // Use a simple secret key for JWT encoding
        String secret = "your-secret-key-here-make-it-long-enough-for-hs256-algorithm";
        SecretKey key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        JWK jwk = new com.nimbusds.jose.jwk.OctetSequenceKey.Builder(key).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }
}
