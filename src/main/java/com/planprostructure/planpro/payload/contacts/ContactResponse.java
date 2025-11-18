package com.planprostructure.planpro.payload.contacts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String photoUrl;
    private Instant createdAt;
    private Instant updatedAt;
} 