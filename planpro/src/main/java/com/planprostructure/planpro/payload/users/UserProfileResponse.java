package com.planprostructure.planpro.payload.users;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String role; // e.g., "USER", "ADMIN", etc.
    private String status; // e.g., "ACTIVE", "INACTIVE", etc.
    private String gender;
    private String profileImageUrl;
    private String dob; // Date of Birth
    private String authProvider;
    private Boolean isPass;

    @Builder
    public UserProfileResponse(Long id, String firstName, String lastName, String username, String email,
            String phoneNumber, String role, String status, String gender, String profileImageUrl, String dob,
            String authProvider, Boolean isPass) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = status;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.authProvider = authProvider;
        this.isPass = isPass;
    }
}
