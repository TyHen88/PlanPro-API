package com.planprostructure.planpro.payload.calendar;

import com.planprostructure.planpro.enums.CalendarEnum;
import com.planprostructure.planpro.enums.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CalendarResponse {
    private Long id;
    private String eventTitle;
    private String description;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String location;
    private Status status;
    private CalendarEnum calendarType;
    private String attendees; // Comma-separated list of attendee names or emails
    private Long noteId;

   private boolean isNote;

    public boolean hasNoteId() {
        return noteId != null;
    }

    @Builder
    public CalendarResponse(Long id, String eventTitle, String description, String startDate, String endDate, String startTime, String endTime, String location, Status status, CalendarEnum calendarType, String attendees, Long noteId) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.status = status;
        this.calendarType = calendarType;
        this.attendees = attendees;
        this.noteId = noteId;
        this.isNote = noteId != null;
    }

}
