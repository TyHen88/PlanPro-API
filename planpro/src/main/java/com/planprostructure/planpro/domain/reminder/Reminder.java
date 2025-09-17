package com.planprostructure.planpro.domain.reminder;

import java.sql.Types;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import com.planprostructure.planpro.enums.RecurrenceType;
import com.planprostructure.planpro.enums.Status;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Table(name = "tb_reminder")
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long tripId;
    private Long noteId;
    private Long telegramUserId;
    private String title;
    private String description;
    private String dueDate;
    private String dueTime;
    private String category;
    private String priority;
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    @ColumnDefault("1")
    private Status status;
    private boolean isRecurring;
    private String recurrenceType;
    private boolean isStarred;
    private String createdAt;
    private String lastModified;
    private String tags;
    private String reminderStatus;

    @Builder
    public Reminder(Long id, Long userId, Long tripId, Long noteId, Long telegramUserId, String title, String description, String dueDate, String dueTime, String category, String priority, Status status, boolean isRecurring, String recurrenceType, boolean isStarred, String createdAt, String lastModified, String tags, String reminderStatus) {
        this.id = id;
        this.userId = userId;
        this.tripId = tripId;
        this.noteId = noteId;
        this.telegramUserId = telegramUserId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.isRecurring = isRecurring;
        this.recurrenceType = recurrenceType;
        this.isStarred = isStarred;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.tags = tags;
        this.reminderStatus = reminderStatus;
    }
}
