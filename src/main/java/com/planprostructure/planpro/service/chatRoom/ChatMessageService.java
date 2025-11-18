package com.planprostructure.planpro.service.chatRoom;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planprostructure.planpro.domain.chatRoom.ChatMessage;
import com.planprostructure.planpro.domain.chatRoom.ChatMessageRepository;
import com.planprostructure.planpro.domain.chatRoom.ChatRoom;
import com.planprostructure.planpro.domain.chatRoom.ChatRoomRepository;
import com.planprostructure.planpro.domain.chatRoom.RoomParticipant;
import com.planprostructure.planpro.domain.chatRoom.RoomParticipantRepository;
import com.planprostructure.planpro.domain.users.UserRepository;
import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.payload.dto.ChatMessageDTO;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final RoomParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageService(ChatMessageRepository messageRepository,
            ChatRoomRepository chatRoomRepository,
            UserRepository userRepository,
            RoomParticipantRepository participantRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Send message to room
    public ChatMessage sendMessage(Long roomId, Long senderId, String content,
            ChatMessage.MessageType messageType) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        Users sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found: " + senderId));

        // Validate user is participant
        if (!chatRoomRepository.isUserParticipant(roomId, senderId)) {
            throw new RuntimeException("User not in room");
        }

        ChatMessage message = ChatMessage.builder()
                .content(content)
                .sender(sender)
                .room(room)
                .messageType(messageType)
                .build();
        ChatMessage savedMessage = messageRepository.save(message);

        // Broadcast to room participants
        broadcastMessage(savedMessage);

        return savedMessage;
    }

    // Get room messages
    public List<ChatMessage> getRoomMessages(Long roomId, Long userId) {
        // Validate access
        if (!chatRoomRepository.isUserParticipant(roomId, userId)) {
            throw new RuntimeException("Access denied");
        }

        return messageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    // Mark messages as read
    public void markMessagesAsRead(Long roomId, Long userId) {
        participantRepository.updateLastReadTime(roomId, userId, LocalDateTime.now());
    }

    // Get unread count
    public Long getUnreadCount(Long roomId, Long userId) {
        return messageRepository.countUnreadMessages(roomId, userId);
    }

    private void broadcastMessage(ChatMessage message) {
        // Convert to DTO for sending over WebSocket
        ChatMessageDTO messageDTO = convertToDTO(message);

        // Send to room-specific topic
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoom().getId(), messageDTO);

        // Also send to user-specific topics for real-time updates
        List<RoomParticipant> participants = participantRepository.findByRoomId(message.getRoom().getId());
        for (RoomParticipant participant : participants) {
            messagingTemplate.convertAndSendToUser(
                    participant.getUser().getId().toString(),
                    "/queue/messages",
                    messageDTO);
        }
    }

    private ChatMessageDTO convertToDTO(ChatMessage message) {
        return new ChatMessageDTO(
                message.getId(),
                message.getContent(),
                message.getSender().getId(),
                message.getSender().getFirstName() + " " + message.getSender().getLastName(),
                message.getRoom().getId(),
                message.getMessageType(),
                message.getSentAt());
    }
}
