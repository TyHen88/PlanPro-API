package com.planprostructure.planpro.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.domain.chatRoom.ChatMessage;
import com.planprostructure.planpro.payload.chatRoom.SendMessageRequest;
import com.planprostructure.planpro.service.chatRoom.ChatMessageService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/chat/messages/rooms")
@Tag(name = "Chat Message", description = "Chat Message API")
public class ChatMessageController extends ProPlanRestController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // WebSocket endpoint for sending messages
    @MessageMapping("/chat.send")
    @SendTo("/topic/room/{roomId}")
    public void sendMessage(@Payload SendMessageRequest request,
            @Header("roomId") Long roomId,
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        chatMessageService.sendMessage(roomId, userId, request.getContent(),
                request.getMessageType());
    }

    @PostMapping("/{roomId}/send")
    @SendTo("/topic/public")
    public ResponseEntity<ChatMessage> sendMessageViaRest(
            @PathVariable Long roomId,
            @RequestBody SendMessageRequest request,
            @RequestParam Long senderId) {

        try {
            ChatMessage message = chatMessageService.sendMessage(
                    roomId,
                    senderId,
                    request.getContent(),
                    request.getMessageType());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // REST endpoint for getting message history
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getRoomMessages(@PathVariable Long roomId,
            @RequestParam Long userId) {
        List<ChatMessage> messages = chatMessageService.getRoomMessages(roomId, userId);
        return ResponseEntity.ok(messages);
    }

    // Mark messages as read
    @PostMapping("/{roomId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long roomId,
            @RequestParam Long userId) {
        chatMessageService.markMessagesAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    // Get unread count
    @GetMapping("/{roomId}/unread")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long roomId,
            @RequestParam Long userId) {
        Long count = chatMessageService.getUnreadCount(roomId, userId);
        return ResponseEntity.ok(count);
    }
}