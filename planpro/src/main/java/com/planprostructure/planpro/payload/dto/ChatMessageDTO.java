package com.planprostructure.planpro.payload.dto;

import java.time.LocalDateTime;

import com.planprostructure.planpro.domain.chatRoom.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private Long roomId;
    private ChatMessage.MessageType messageType;
    private LocalDateTime sentAt;
}
