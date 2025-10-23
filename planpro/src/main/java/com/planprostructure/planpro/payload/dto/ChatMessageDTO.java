package com.planprostructure.planpro.payload.dto;

import java.time.LocalDateTime;

import com.planprostructure.planpro.domain.chatRoom.ChatMessage;

public class ChatMessageDTO {
    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private Long roomId;
    private ChatMessage.MessageType messageType;
    private LocalDateTime sentAt;

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(Long id, String content, Long senderId, String senderName,
            Long roomId, ChatMessage.MessageType messageType, LocalDateTime sentAt) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.senderName = senderName;
        this.roomId = roomId;
        this.messageType = messageType;
        this.sentAt = sentAt;
    }

}
