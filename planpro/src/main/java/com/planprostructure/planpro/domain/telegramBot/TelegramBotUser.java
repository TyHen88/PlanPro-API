package com.planprostructure.planpro.domain.telegramBot;

import com.planprostructure.planpro.domain.UpdatableEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_telegram_bot_user")
public class TelegramBotUser extends UpdatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long chatId;

    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String currentState;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean connected = true;

    @Builder
    public TelegramBotUser(Long chatId, Long userId, String username, String firstName, String lastName, String phoneNumber, String currentState) {
        this.chatId = chatId;
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.currentState = currentState;
    }
}
