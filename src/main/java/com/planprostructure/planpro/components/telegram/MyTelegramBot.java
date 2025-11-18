package com.planprostructure.planpro.components.telegram;

import com.planprostructure.planpro.service.telegramBot.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyTelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);

    private final TelegramService telegramService;
    private final TelegramCommandHandler commandHandler;
    private final String botToken;
    private final String botUsername;

    public MyTelegramBot(TelegramService telegramService,
                         TelegramCommandHandler commandHandler,
                         String botToken,
                         String botUsername) {
        super(botToken); // Initialize parent with token
        this.telegramService = telegramService;
        this.commandHandler = commandHandler;
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        Message message = update.getMessage();
        try {
            if (message.hasText()) {
                handleTextMessage(message);
            } else if (message.hasContact()) {
                handleContactMessage(message);
            }
        } catch (Exception e) {
            logger.error("Error processing Telegram update for chatId: {}", message.getChatId(), e);
            sendErrorResponse(message.getChatId());
        }
    }

//    private void handleTextMessage(Message message) throws TelegramApiException {
//        long chatId = message.getChatId();
//        User user = message.getFrom();
//        //check if user is already registered
//
//
//        if (user != null) {
//            telegramService.registerUser(user, chatId);
//        }
//
//        String response = commandHandler.handleCommand(message);
//        sendResponse(chatId, response);
//    }
private void handleTextMessage(Message message) throws TelegramApiException {
    long chatId = message.getChatId();
    User user = message.getFrom();

    // Now registerUser returns a greeting message
    String welcomeMessage = telegramService.registerUser(user, chatId);

    // Get command response
    String commandResponse = commandHandler.handleCommand(message);

    // Combine both
    String response = welcomeMessage + "\n\n" + commandResponse;

    sendResponse(chatId, response);
}


    private void handleContactMessage(Message message) throws TelegramApiException {
        long chatId = message.getChatId();
        if (message.getContact() != null) {
            String phoneNumber = message.getContact().getPhoneNumber();
            telegramService.updateUserPhone(chatId, phoneNumber);
            sendResponse(chatId, "Thank you for sharing your phone number!");
        } else {
            sendResponse(chatId, "No contact information found.");
        }
    }

    private void sendResponse(long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message to chatId: {}", chatId, e);
            throw e;
        }
    }

    private void sendErrorResponse(long chatId) {
        try {
            SendMessage errorMessage = new SendMessage();
            errorMessage.setChatId(String.valueOf(chatId));
            errorMessage.setText("Sorry, an error occurred. Please try again later.");
            execute(errorMessage);
        } catch (TelegramApiException e) {
            logger.error("Failed to send error message to chatId: {}", chatId, e);
        }
    }


}
