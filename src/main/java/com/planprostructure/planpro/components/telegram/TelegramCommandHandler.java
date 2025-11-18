package com.planprostructure.planpro.components.telegram;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class TelegramCommandHandler {
    private final Map<String, Function<Message, String>> commandMap = new HashMap<>();

    public TelegramCommandHandler() {
        commandMap.put("/start", this::handleStart);
        commandMap.put("/help", this::handleHelp);
        commandMap.put("/test", this::handleTest);
        // Add more commands here
    }

    public String handleCommand(Message message) {
        String command = message.getText().split(" ")[0];
        Function<Message, String> commandFunction = commandMap.get(command);

        if (commandFunction != null) {
            return commandFunction.apply(message);
        }
        return handleDefault(message);
    }

    private String handleStart(Message message) {
        return "Hello " + message.getFrom().getFirstName() + "! Welcome to our bot.";
    }
    private String handleTest(Message message) {
        return "Test command received!";
    }

    private String handleHelp(Message message) {
        return "Available commands:\n" +
                "/start - Start the bot\n" +
                "/help - Show this help message";
    }

    private String handleDefault(Message message) {
        return "I don't understand that command. Type /help for available commands.";
    }
}
