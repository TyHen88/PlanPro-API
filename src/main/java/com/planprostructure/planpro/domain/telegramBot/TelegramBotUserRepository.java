package com.planprostructure.planpro.domain.telegramBot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TelegramBotUserRepository extends JpaRepository<TelegramBotUser, Long> {
    @Query("SELECT t FROM TelegramBotUser t WHERE t.chatId = ?1")
    Optional<TelegramBotUser> findByChatId(Long chatId);
    @Query("SELECT t FROM TelegramBotUser t WHERE t.userId = ?1 AND t.connected = true")
    Optional<TelegramBotUser> findByUserIdAndChatId(Long userId);
    @Query("SELECT t FROM TelegramBotUser t WHERE t.userId = ?1")
    List<TelegramBotUser> findByUserId(Long userId);
}
