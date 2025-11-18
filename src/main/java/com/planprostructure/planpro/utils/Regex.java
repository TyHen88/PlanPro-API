package com.planprostructure.planpro.utils;

import com.planprostructure.planpro.components.common.api.StatusCode;
import com.planprostructure.planpro.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class Regex {
   // Validates that chatId consists of exactly 9 numeric digits
   public static final String CHAT_ID_REGEX = "\\d{9}";

    public static void validateChatId(Long chatId) {
        if (chatId == null || !String.valueOf(chatId).matches(CHAT_ID_REGEX)) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "Invalid chatId format. It must be exactly 9 digits.");
        }
    }
}
