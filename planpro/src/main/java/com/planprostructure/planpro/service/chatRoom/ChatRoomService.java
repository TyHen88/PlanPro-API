package com.planprostructure.planpro.service.chatRoom;

import org.springframework.stereotype.Service;

import com.planprostructure.planpro.domain.chatRoom.ChatRoom;
import com.planprostructure.planpro.domain.chatRoom.ChatRoomRepository;
import com.planprostructure.planpro.domain.chatRoom.RoomParticipant;
import com.planprostructure.planpro.domain.chatRoom.RoomParticipantRepository;
import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.domain.users.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Service
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final RoomParticipantRepository participantRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
            UserRepository userRepository,
            RoomParticipantRepository participantRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
    }

    // Create or get direct chat between two users
    public ChatRoom getOrCreateDirectChat(Long user1Id, Long user2Id) {
        // Try to find existing direct chat
        System.out.println("Looking for existing chat between users: " + user1Id + " and " + user2Id);
        Optional<ChatRoom> existingChat = chatRoomRepository
                .findDirectChatBetweenUsers(user1Id, user2Id);

        if (existingChat.isPresent()) {
            System.out.println("Found existing chat: " + existingChat.get().getId());
            return existingChat.get();
        }

        System.out.println("No existing chat found, creating new one");

        // Create new direct chat
        Users user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User not found: " + user1Id));
        Users user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User not found: " + user2Id));

        String roomName = (user1.getFirstName() != null ? user1.getFirstName() : user1.getUsername()) +
                " and " + (user2.getFirstName() != null ? user2.getFirstName() : user2.getUsername());
        ChatRoom directChat = new ChatRoom();
        directChat.setName(roomName);
        directChat.setType(ChatRoom.RoomType.DIRECT);
        directChat.setCreatedBy(user1);

        // Save room first
        ChatRoom savedRoom = chatRoomRepository.save(directChat);

        // Add participants
        addParticipant(savedRoom, user1);
        addParticipant(savedRoom, user2);

        return savedRoom;
    }

    // Create group chat
    public ChatRoom createGroupChat(String roomName, Long creatorId, List<Long> participantIds) {
        Users creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found: " + creatorId));

        ChatRoom groupChat = new ChatRoom();
        groupChat.setName(roomName);
        groupChat.setType(ChatRoom.RoomType.GROUP);
        groupChat.setCreatedBy(creator);
        ChatRoom savedRoom = chatRoomRepository.save(groupChat);

        // Add creator as participant
        addParticipant(savedRoom, creator);

        // Add other participants
        participantIds.stream()
                .filter(id -> !id.equals(creatorId))
                .forEach(userId -> {
                    Users user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    addParticipant(savedRoom, user);
                });

        return savedRoom;
    }

    // Add participant to room
    public void addParticipantToRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Check if already participant
        if (participantRepository.findByRoomIdAndUserId(roomId, userId).isPresent()) {
            throw new RuntimeException("User already in room");
        }

        addParticipant(room, user);
    }

    private void addParticipant(ChatRoom room, Users user) {
        RoomParticipant participant = new RoomParticipant();
        participant.setRoom(room);
        participant.setUser(user);
        participantRepository.save(participant);
    }

    // Get user's rooms
    public List<ChatRoom> getUserRooms(Long userId) {
        System.out.println("getUserRooms: " + userId);
        return chatRoomRepository.findRoomsByUserId(userId);
    }

    // Validate user can access room
    public boolean canUserAccessRoom(Long roomId, Long userId) {
        return chatRoomRepository.isUserParticipant(roomId, userId);
    }
}
