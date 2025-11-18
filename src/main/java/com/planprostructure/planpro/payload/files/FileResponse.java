package com.planprostructure.planpro.payload.files;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FileResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private String createdAt;
    private String updatedAt;
    private String size;
    private Integer width;
    private Integer height;
    private Long telegramId;
    private Long folderId;
    private Long userId;


    public FileResponse(Long id, String fileName, String fileType, String fileUrl, String createdAt, String updatedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
