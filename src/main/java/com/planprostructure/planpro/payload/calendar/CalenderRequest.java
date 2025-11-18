package com.planprostructure.planpro.payload.calendar;

import com.planprostructure.planpro.enums.CalendarEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CalenderRequest {
    private String eventTitle;
    private String description;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String location;
    private String status;
    private CalendarEnum calendarType; // e.g., "WORK", "PERSONAL", "TRIP"
    private String attendees; // Comma-separated list of attendee names or emails

    @Builder
    public CalenderRequest(String eventTitle, String description, String startDate, String endDate, String startTime, String endTime, String location, String status, CalendarEnum calendarType, String attendees) {
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
    }

}
