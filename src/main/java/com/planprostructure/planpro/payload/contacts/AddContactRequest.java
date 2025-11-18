package com.planprostructure.planpro.payload.contacts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddContactRequest {
    private String name;
    private String email;
    private String phone;
    private String photoUrl;
} 