package com.planprostructure.planpro.service.calendar;

import com.planprostructure.planpro.payload.calendar.CalendarResponse;
import com.planprostructure.planpro.payload.calendar.CalenderRequest;

public interface CalendarService {
    void createCalendarEvent(CalenderRequest request) throws Throwable;

    Object getCalendarEvent() throws Throwable;

    Object getCalendarEventById(Long calendarId) throws Throwable;

    void updateCalendarEvent(Long calendarId, CalenderRequest request) throws Throwable;

    void deleteCalendarEvent(Long calendarId) throws Throwable;
}
