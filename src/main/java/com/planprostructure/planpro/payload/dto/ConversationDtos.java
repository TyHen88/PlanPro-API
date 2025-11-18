package com.planprostructure.planpro.payload.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class ConversationDtos {
  public record CreateRequest(@NotBlank String type, @Size(min=1) List<String> members, String title) {}
} 