package com.planprostructure.planpro.payload.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {
  public record RegisterRequest(@NotBlank String username, @Email String email, @NotBlank String password, String displayName) {}
  public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
  public record TokenResponse(String accessToken, String refreshToken) {}
  public record RefreshRequest(@NotBlank String refreshToken) {}
} 