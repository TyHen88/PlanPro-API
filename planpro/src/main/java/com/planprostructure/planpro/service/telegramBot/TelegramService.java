package com.planprostructure.planpro.service.telegramBot;

import org.telegram.telegrambots.meta.api.objects.User;

public interface TelegramService {
    String registerUser(User user, long chatId);
    void updateUserPhone(long chatId, String phoneNumber);

    void connectTelegramUserToPlanProUser(Long chatI) throws Throwable;

    Object verifyTelegramUser(Long chatId) throws Throwable;

    Object getTelegramUserByChatId() throws Throwable;

    void disconnectTelegram(Long telegramUserId) throws Throwable;

    void telegramSetting(Long chatId, boolean isActive) throws Throwable;

    Object getHistoryOfTelegramUser() throws Throwable;

    void reconnectTelegram(Long chatId) throws Throwable;
}
