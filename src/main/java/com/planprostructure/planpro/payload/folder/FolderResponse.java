package com.planprostructure.planpro.payload.folder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FolderResponse {
    private Long id;
    private Long userId;
    private Long telegramId;
    private String name;
    private String createdAt;
    private String updatedAt;


    public FolderResponse(Long id, String name, Long userId, Long telegramId, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
