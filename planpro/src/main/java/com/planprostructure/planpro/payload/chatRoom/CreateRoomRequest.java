package com.planprostructure.planpro.payload.chatRoom;

import java.util.List;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String name;
    private List<Long> participantIds;

    public CreateRoomRequest() {
    }
}
