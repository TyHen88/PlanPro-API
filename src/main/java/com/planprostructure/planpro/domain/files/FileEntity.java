package com.planprostructure.planpro.domain.files;

import com.planprostructure.planpro.domain.CreatableEntity;
import com.planprostructure.planpro.domain.folder.Folder;
import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.enums.FileType;
import com.planprostructure.planpro.enums.Status;
import com.planprostructure.planpro.enums.StatusUser;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "tb_file")
@Entity
public class FileEntity extends CreatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(nullable = false, name = "file_name")
    private String fileName;

    @Column(nullable = false, name = "stored_name")
    private String storedName;

    @Column(nullable = false, name = "file_url")
    private String fileUrl;

    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = FileType.Converter.class)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    private String mimeType;
    private Long fileSize;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "folder_id", nullable = false)
    private Long folderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    // Photo-specific fields
    private Integer width;
    private Integer height;
    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(name = "status")
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    @ColumnDefault("1")
    private Status status;

    @Builder
    public FileEntity(String fileName, String storedName, String fileUrl, FileType fileType, String mimeType, Long fileSize, Long telegramId, Long folderId, Long userId, LocalDateTime uploadedAt, Integer width, Integer height, String thumbnailPath, Status status) {
        this.fileName = fileName;
        this.storedName = storedName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.telegramId = telegramId;
        this.folderId = folderId;
        this.userId = userId;
        this.uploadedAt = uploadedAt;
        this.width = width;
        this.height = height;
        this.thumbnailPath = thumbnailPath;
        this.status = status;
    }
}
