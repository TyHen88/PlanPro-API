package com.planprostructure.planpro.payload.telegram;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TelegramUserInfoResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isActive;
    private boolean isConnected;
    private String currentState;
    private String createdAt;
    private String updatedAt;

    @Builder
    public TelegramUserInfoResponse(Long id, String username, String firstName, String lastName, String phoneNumber, boolean isActive, boolean isConnected, String currentState, String createdAt, String updatedAt) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
        this.isConnected = isConnected;
        this.currentState = currentState;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
