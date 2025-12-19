package com.planprostructure.planpro.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.planprostructure.planpro.payload.chatRoom.SendMessageRequest;
import com.planprostructure.planpro.service.chatRoom.ChatMessageService;

@Controller
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;

    public ChatWebSocketController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    /**
     * Client sends to: /app/chat/rooms/{roomId}/send
     * Clients subscribe to: /topic/room/{roomId}
     */
    @MessageMapping("/chat/rooms/{roomId}/send")
    public void sendToRoom(@DestinationVariable Long roomId,
            @Payload SendMessageRequest request,
            Principal principal) {

        Long senderId = resolveSenderId(request, principal);
        chatMessageService.sendMessage(roomId, senderId, request.getContent(), request.getMessageType());
    }

    private Long resolveSenderId(SendMessageRequest request, Principal principal) {
        if (principal != null) {
            try {
                return Long.parseLong(principal.getName());
            } catch (NumberFormatException ignored) {
                // fall back to request.senderId
            }
        }
        if (request.getSenderId() == null) {
            throw new IllegalArgumentException("senderId is required when no authenticated Principal is present");
        }
        return request.getSenderId();
    }
}
