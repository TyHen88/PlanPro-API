package com.planprostructure.planpro.service.telegramBot;

import com.planprostructure.planpro.components.common.api.StatusCode;
import com.planprostructure.planpro.domain.telegramBot.TelegramBotUser;
import com.planprostructure.planpro.domain.telegramBot.TelegramBotUserRepository;
import com.planprostructure.planpro.domain.users.UserRepository;
import com.planprostructure.planpro.enums.BotState;
import com.planprostructure.planpro.exception.BusinessException;
import com.planprostructure.planpro.helper.AuthHelper;
import com.planprostructure.planpro.payload.telegram.TelegramMainResponse;
import com.planprostructure.planpro.payload.telegram.TelegramUserInfoResponse;
import com.planprostructure.planpro.utils.Regex;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    private final TelegramBotUserRepository telegramBotUserRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(TelegramServiceImpl.class);

    @Override
    @Transactional
    public String registerUser(User user, long chatId) {
        return telegramBotUserRepository.findByChatId(chatId)
                .map(existingUser -> handleExistingUser(existingUser, user))
                .orElseGet(() -> createNewUser(user, chatId));
    }


    @Override
    @Transactional
    public void updateUserPhone(long chatId, String phoneNumber) {
        telegramBotUserRepository.findByChatId(chatId)
                .ifPresent(user -> {
                    user.setPhoneNumber(phoneNumber);
                    telegramBotUserRepository.save(user);
                });
    }

    @Override
    public void connectTelegramUserToPlanProUser(Long chatId) {
        Long userId = AuthHelper.getUserId();
        var telegramUserOpt = telegramBotUserRepository.findByChatId(chatId);
        if (telegramUserOpt.isPresent()) {
            TelegramBotUser telegramUser = telegramUserOpt.get();
            telegramUser.setUserId(userId);
           var saveTelegram =  telegramBotUserRepository.save(telegramUser);
           Long savedTeleId = saveTelegram.getChatId();
           if (savedTeleId != null){
               userRepository.findById(userId).ifPresent(user -> {
                     user.setTelegramId(savedTeleId);
                     userRepository.save(user);
               });
           }
            logger.info("Connected Telegram user {} to PlanPro user {}", chatId, userId);
        } else {
            logger.warn("No Telegram user found with chat ID: {}", chatId);
        }
    }

    @Override
    @Transactional
    public void disconnectTelegram(Long telegramUserId) throws Throwable {
        var telegramUserOpt = telegramBotUserRepository.findByChatId(telegramUserId);
        if (telegramUserOpt.isPresent()) {
            TelegramBotUser telegramUser = telegramUserOpt.get();
            telegramUser.setConnected(false);
            telegramUser.setActive(false);
            telegramBotUserRepository.save(telegramUser);
            logger.info("Disconnected Telegram user {} from PlanPro user {}", telegramUserId, telegramUser.getUserId());
        } else {
            logger.warn("No Telegram user found with ID: {}", telegramUserId);
            throw new BusinessException(StatusCode.NOT_FOUND, "Telegram user not found");
        }
    }

    @Override
    @Transactional
    public void reconnectTelegram(Long chatId) throws Throwable {
        var telegramUserOpt = telegramBotUserRepository.findByChatId(chatId);
        if (telegramUserOpt.isPresent()) {
            TelegramBotUser telegramUser = telegramUserOpt.get();

            // If the user is already connected, set all other connected records for this userId to false
            if (!telegramUser.isConnected()) {
                Long userId = telegramUser.getUserId();
                if (userId != null) {
                    // Find all TelegramBotUser records for this userId with connected = true
                    var connectedUsers = telegramBotUserRepository.findByUserId(userId)
                        .stream()
                        .filter(u -> u.isConnected() && !u.getChatId().equals(chatId))
                        .collect(Collectors.toList());
                    for (TelegramBotUser u : connectedUsers) {
                        u.setConnected(false);
                        telegramBotUserRepository.save(u);
                    }
                }
                telegramUser.setConnected(true);
                telegramBotUserRepository.save(telegramUser);
                logger.info("Reconnected Telegram user {} to PlanPro user {}", chatId, telegramUser.getUserId());
            } else {
                // If already connected, still ensure all others are disconnected
                Long userId = telegramUser.getUserId();
                if (userId != null) {
                    var connectedUsers = telegramBotUserRepository.findByUserId(userId)
                        .stream()
                        .filter(u -> u.isConnected() && !u.getChatId().equals(chatId))
                        .collect(Collectors.toList());
                    for (TelegramBotUser u : connectedUsers) {
                        u.setConnected(false);
                        telegramBotUserRepository.save(u);
                    }
                }
                logger.info("Telegram user {} is already connected. Ensured all other connections are set to false.", chatId);
            }
        } else {
            logger.warn("No Telegram user found with chat ID: {}", chatId);
            throw new BusinessException(StatusCode.NOT_FOUND, "Telegram user not found");
        }
    }

    @Override
    @Transactional
    public void telegramSetting(Long chatId, boolean isActive) throws Throwable {
        var telegramUserOpt = telegramBotUserRepository.findByChatId(chatId);
        if (telegramUserOpt.isPresent()) {
            TelegramBotUser telegramUser = telegramUserOpt.get();
            telegramUser.setActive(isActive);
            telegramBotUserRepository.save(telegramUser);
        } else {
            logger.warn("No Telegram user found with chat ID: {}", chatId);
            throw new BusinessException(StatusCode.NOT_FOUND, "Telegram user not found");
        }
    }

    @Override
    public Object verifyTelegramUser(Long chatId) {
        var telegramUserOpt = telegramBotUserRepository.findByChatId(chatId).
                orElseThrow(() -> new BusinessException(StatusCode.NOT_FOUND));
        return TelegramUserInfoResponse.builder().
                id(telegramUserOpt.getChatId()).
                firstName(telegramUserOpt.getFirstName()).
                lastName(telegramUserOpt.getLastName()).
                username(telegramUserOpt.getUsername()).
                build();
    }

    @Override
    public Object getTelegramUserByChatId() throws Throwable {
        var telegramUserOpt = telegramBotUserRepository.findByUserIdAndChatId(AuthHelper.getUserId());
        if (telegramUserOpt.isPresent()) {
            return telegramUserOpt.get();
        } else {
            logger.warn("No Telegram user found with user ID: {}", AuthHelper.getUserId());
            return null;
        }
    }

    @Override
    public Object getHistoryOfTelegramUser() throws Throwable {
        Long userId = AuthHelper.getUserId();
        var telegramUserOpt = telegramBotUserRepository.findByUserId(userId);
        if (telegramUserOpt.size() > 0) {
            return telegramUserOpt.stream().map(
                telegramUser -> TelegramUserInfoResponse.builder().
                id(telegramUser.getChatId()).
                firstName(telegramUser.getFirstName()).
                lastName(telegramUser.getLastName()).
                username(telegramUser.getUsername()).
                phoneNumber(telegramUser.getPhoneNumber()).
                isActive(telegramUser.isActive()).
                isConnected(telegramUser.isConnected()).
                currentState(telegramUser.getCurrentState()).
                createdAt(telegramUser.getCreatedAt().toString()).
                build()
            ).collect(Collectors.toList());
        } else {
            logger.warn("No Telegram user found with user ID: {}", userId);
            return null;
        }
    }

    private String createNewUser(User telegramUser, long chatId) {
        TelegramBotUser newUser = new TelegramBotUser();
        newUser.setChatId(chatId);
        newUser.setUsername(telegramUser.getUserName());
        newUser.setFirstName(telegramUser.getFirstName());
        newUser.setLastName(telegramUser.getLastName());
        newUser.setActive(true);
        newUser.setConnected(true);
        newUser.setCurrentState(BotState.STARTED.name());

        try {
            telegramBotUserRepository.save(newUser);
            return messageRegistrationSuccess(telegramUser, chatId);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Duplicate user registration attempted: {}", newUser.getChatId());
            return "👋 Welcome back! You’ve already registered. (cached)";
        }

    }

    private String messageRegistrationSuccess(User telegramUser, long chatId) {
        return  "👋 Welcome, " + telegramUser.getFirstName() + "!\n\n" +
                "🔑 Your Chat ID: " + chatId + "\n" +
                "📝 Please use this Chat ID to link your Telegram account with the web app.\n\n" +
                "👉 Let's get started!";
    }

    private String handleExistingUser(TelegramBotUser existingUser, User telegramUser) {
        boolean updated = false;

        if (!Objects.equals(existingUser.getFirstName(), telegramUser.getFirstName())) {
            existingUser.setFirstName(telegramUser.getFirstName());
            updated = true;
        }
        if (!Objects.equals(existingUser.getLastName(), telegramUser.getLastName())) {
            existingUser.setLastName(telegramUser.getLastName());
            updated = true;
        }
        if (!Objects.equals(existingUser.getUsername(), telegramUser.getUserName())) {
            existingUser.setUsername(telegramUser.getUserName()); // may still be null; it's fine
            updated = true;
        }

//        existingUser.setLastActiveAt(java.time.Instant.now());
        updated = true;

        if (updated) {
            telegramBotUserRepository.save(existingUser);
        }

        return "Welcome back, " + telegramUser.getFirstName() +"!";
    }

}
