package com.planprostructure.planpro.payload.myNotes;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyNoteRequest {
    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String color;
    private String textColor;
    private boolean isCalendarEvent;
    private boolean isNotify;

    @Builder
    public MyNoteRequest(Long id, String title, String content, String createdAt, String updatedAt, String color, String textColor, boolean isCalendarEvent, boolean isNotify) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.color = color;
        this.textColor = textColor;
        this.isCalendarEvent = isCalendarEvent;
        this.isNotify = isNotify;
    }
}
