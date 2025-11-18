package com.planprostructure.planpro.config;

import com.planprostructure.planpro.components.telegram.MyTelegramBot;
import com.planprostructure.planpro.components.telegram.TelegramCommandHandler;
import com.planprostructure.planpro.service.telegramBot.TelegramService;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@ConditionalOnProperty(name = "telegram-setting.enable", havingValue = "true", matchIfMissing = false)
public class TelegramConfig {
    private static final Logger logger = LoggerFactory.getLogger(TelegramConfig.class);

    @Value("${telegram-setting.access-token}")
    private String botToken;

    @Value("${telegram-setting.username}")
    private String botUsername;

    private MyTelegramBot telegramBot;

    @Bean
    public MyTelegramBot myTelegramBot(TelegramCommandHandler commandHandler,
                                       TelegramService telegramService) {
        if (botToken == null || botToken.isEmpty()) {
            throw new IllegalStateException("Telegram bot token is not configured");
        }
        if (botUsername == null || botUsername.isEmpty()) {
            throw new IllegalStateException("Telegram bot username is not configured");
        }

        logger.info("Creating Telegram bot with username: {}", botUsername);
        this.telegramBot = new MyTelegramBot(telegramService, commandHandler, botToken, botUsername);
        return telegramBot;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(MyTelegramBot bot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
        logger.info("Telegram bot registered successfully");
        return botsApi;
    }

    @PreDestroy
    public void cleanup() {
        if (telegramBot != null) {
            try {
                logger.info("Shutting down Telegram bot...");
                telegramBot.onClosing();
            } catch (Exception e) {
                logger.error("Error during Telegram bot shutdown", e);
            }
        }
    }
}
