package com.planprostructure.planpro.payload.telegram;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TelegramMainResponse {
    private String message;
    private String error;
    private boolean success;
    private TelegramUserInfoResponse telegramUserInfoResponse;

    @Builder
    public TelegramMainResponse(String message, String error, boolean success, TelegramUserInfoResponse telegramUserInfoResponse) {
        this.message = message;
        this.error = error;
        this.success = success;
        this.telegramUserInfoResponse = telegramUserInfoResponse;
    }




}
