package com.planprostructure.planpro.domain.myNote;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_my_notes")
public class MyNotes {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "note_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // Content of the note

    @Column(name = "created_at", nullable = false)
    private String createdAt; // Date when the note was created

    @Column(name = "updated_at")
    private String updatedAt; // Date when the note was last updated

    @Column(name = "color")
    private String color;

    @Column(name = "text_color")
    private String textColor; // Color of the text in the note

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "is_calendar_event", nullable = false)
    private boolean isCalendarEvent = false;

    @Column(name = "is_notify")
    private boolean isNotify = false; // Whether the note is a calendar event or not

    @Builder
    public MyNotes(Long userId, String title, String content, String createdAt, String updatedAt, String color, String textColor, boolean isDeleted, boolean isCalendarEvent, boolean isNotify) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.color = color;
        this.textColor = textColor;
        this.isDeleted = isDeleted;
        this.isCalendarEvent = isCalendarEvent;
        this.isNotify = isNotify;
    }


}
