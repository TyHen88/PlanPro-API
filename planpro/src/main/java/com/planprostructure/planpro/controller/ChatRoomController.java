package com.planprostructure.planpro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.domain.chatRoom.ChatRoom;
import com.planprostructure.planpro.payload.chatRoom.CreateRoomRequest;
import com.planprostructure.planpro.service.chatRoom.ChatRoomService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/chat/rooms")
@Tag(name = "Chat Room", description = "Chat Room API")
public class ChatRoomController extends ProPlanRestController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping("/direct")
    public Object createOrGetDirectChat(@RequestParam Long user1Id,
            @RequestParam Long user2Id) {
        ChatRoom room = chatRoomService.getOrCreateDirectChat(user1Id, user2Id);
        return ok(room);
    }

    @PostMapping("/group")
    public Object createGroupChat(@RequestBody CreateRoomRequest request,
            @RequestParam Long creatorId) {
        ChatRoom room = chatRoomService.createGroupChat(request.getName(), creatorId,
                request.getParticipantIds());
        return ok(room);
    }

    @GetMapping("/user/{userId}")
    public Object getUserRooms(@PathVariable Long userId) {
        List<ChatRoom> rooms = chatRoomService.getUserRooms(userId);
        return ok(rooms);
    }

    @PostMapping("/{roomId}/participants")
    public Object addParticipant(@PathVariable Long roomId,
            @RequestParam Long userId) {
        chatRoomService.addParticipantToRoom(roomId, userId);
        return ok();
    }
}
