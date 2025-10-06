package com.planprostructure.planpro.payload.chatRoom;

import com.planprostructure.planpro.domain.chatRoom.ChatMessage;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String content;
    private ChatMessage.MessageType messageType = ChatMessage.MessageType.TEXT;
    private Long senderId; // Add this field

    public SendMessageRequest() {
    }

}
