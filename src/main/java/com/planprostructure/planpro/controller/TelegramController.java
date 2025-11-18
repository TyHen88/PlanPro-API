package com.planprostructure.planpro.controller;

import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.service.telegramBot.TelegramService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wb/v1/telegram")
@RequiredArgsConstructor
@Tag(name = "Telegram Bot", description = "Telegram Bot API")
public class TelegramController extends ProPlanRestController {
    private final TelegramService telegramService;

    @PutMapping("/connect-telegram/{chatId}")
    public Object connectTelegramUserToPlanProUser(@PathVariable Long chatId) throws Throwable {
        telegramService.connectTelegramUserToPlanProUser(chatId);
        return ok();
    }

    @GetMapping("/verify-telegram/{chatId}")
    public Object verifyTelegramUser(@PathVariable Long chatId) throws Throwable {
        return ok(telegramService.verifyTelegramUser(chatId));
    }

    @GetMapping("/get-telegram-user-info")
    public Object getTelegramUserInfo() throws Throwable {
        return ok(telegramService.getTelegramUserByChatId());
    }

    @PutMapping("/disconnect-telegram/{chatId}")
    public Object disconnectTelegram(@PathVariable Long chatId) throws Throwable {
        telegramService.disconnectTelegram(chatId);
        return ok();
    }

    @PutMapping("/telegram-setting/{chatId}/{isActive}")
    public Object telegramSetting(@PathVariable Long chatId, @PathVariable boolean isActive) throws Throwable {
        telegramService.telegramSetting(chatId, isActive);
        return ok();
    }

    @GetMapping("/get-history-of-telegram-user")
    public Object getHistoryOfTelegramUser() throws Throwable {
        return ok(telegramService.getHistoryOfTelegramUser());
    }

    @PutMapping("/reconnect-telegram/{chatId}")
    public Object reconnectTelegram(@PathVariable Long chatId) throws Throwable {
        telegramService.reconnectTelegram(chatId);
        return ok();
    }

}
