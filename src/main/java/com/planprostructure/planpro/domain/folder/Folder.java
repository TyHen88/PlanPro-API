package com.planprostructure.planpro.domain.folder;

import com.planprostructure.planpro.domain.files.FileEntity;
import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_folder")
public class Folder {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "file_id")
    private Long fileId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status")
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    @ColumnDefault("1")
    private Status status;

    @Builder
    public Folder(Long id, String name, Long userId, Long telegramId, Long fileId, LocalDateTime createdAt, Status status) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.telegramId = telegramId;
        this.fileId = fileId;
        this.createdAt = createdAt;
        this.status = status;

    }

}
