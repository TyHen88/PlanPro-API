package com.planprostructure.planpro.config;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public WebSocketAuthChannelInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;

        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");

        this.jwtAuthenticationConverter = new JwtAuthenticationConverter();
        this.jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
    }

    @Override
    @Nullable
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveBearerToken(accessor);
            if (token != null) {
                Jwt jwt = jwtDecoder.decode(token);
                Authentication authentication = toAuthentication(jwt);
                accessor.setUser(authentication);
            }
        }

        return message;
    }

    private Authentication toAuthentication(Jwt jwt) {
        JwtAuthenticationToken base = (JwtAuthenticationToken) jwtAuthenticationConverter.convert(jwt);
        List<GrantedAuthority> authorities = base != null ? base.getAuthorities().stream().toList() : List.of();

        // For chat we want a stable identifier; prefer numeric user id claim if present.
        String principalName = jwt.getClaimAsString("id");
        if (principalName == null || principalName.isBlank()) {
            principalName = jwt.getClaimAsString("username");
        }
        if (principalName == null || principalName.isBlank()) {
            principalName = jwt.getSubject();
        }

        return new JwtAuthenticationToken(jwt, authorities, principalName);
    }

    @Nullable
    private String resolveBearerToken(StompHeaderAccessor accessor) {
        // STOMP native headers: "Authorization: Bearer <token>"
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            authHeaders = accessor.getNativeHeader("authorization");
        }
        if (authHeaders == null || authHeaders.isEmpty()) {
            return null;
        }

        String value = authHeaders.get(0);
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return value.substring(7).trim();
        }
        return null;
    }
}
