package com.planprostructure.planpro.service.calendar;

import com.planprostructure.planpro.domain.calendar.Calendar;
import com.planprostructure.planpro.domain.calendar.CalendarRepository;
import com.planprostructure.planpro.enums.CalendarEnum;
import com.planprostructure.planpro.enums.CategoryEnums;
import com.planprostructure.planpro.enums.Status;
import com.planprostructure.planpro.helper.AuthHelper;
import com.planprostructure.planpro.payload.calendar.CalendarResponse;
import com.planprostructure.planpro.payload.calendar.CalenderRequest;
import com.planprostructure.planpro.utils.DateTimeUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;

    @Override
    @Transactional
    public void createCalendarEvent(CalenderRequest request) throws Throwable {
        Long userId = AuthHelper.getUserId();
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }

        if (java.time.LocalDate.parse(request.getStartDate()).isAfter(java.time.LocalDate.parse(request.getEndDate()))) {
            throw new IllegalArgumentException("End date must be greater than start date");
        }


        Calendar calendar = Calendar.builder()
                .userId(userId)
                .title(request.getEventTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(DateTimeUtils.formatTimeToHHmmss(request.getStartTime()))
                .endTime(DateTimeUtils.formatTimeToHHmmss(request.getEndTime()))
                .location(request.getLocation())
                .status(Status.NORMAL)
                .calendarType(request.getCalendarType())
                .attendees(request.getAttendees())
                .build();
        calendarRepository.save(calendar);

    }

    @Override
    @Transactional(readOnly = true)
    public Object getCalendarEvent() throws Throwable {
        var calendar = calendarRepository.findByCalendarId(AuthHelper.getUserId());
        if (calendar == null) {
            return Collections.emptyList();
        }
        return calendar.stream().map(cal -> CalendarResponse.builder()
                .id(cal.getId())
                .eventTitle(cal.getTitle())
                .description(cal.getDescription())
                .startDate(cal.getStartDate())
                .endDate(cal.getEndDate())
                .startTime(cal.getStartTime())
                .endTime(cal.getEndTime())
                .location(cal.getLocation())
                .status(cal.getStatus())
                .calendarType(cal.getCalendarType())
                .attendees(cal.getAttendees())
                .noteId(cal.getNoteId())
                .build()).toList();

    }

    @Override
    @Transactional(readOnly = true)
    public Object getCalendarEventById(Long calendarId) throws Throwable {
    var calendar = calendarRepository.findByIdAndStatus(calendarId, Status.NORMAL.getValue())
            .orElseThrow(() -> new IllegalArgumentException("Calendar event not found"));
        return CalendarResponse.builder()
                .id(calendar.getId())
                .eventTitle(calendar.getTitle())
                .description(calendar.getDescription())
                .startDate(calendar.getStartDate())
                .endDate(calendar.getEndDate())
                .startTime(calendar.getStartTime())
                .endTime(calendar.getEndTime())
                .location(calendar.getLocation())
                .status(calendar.getStatus())
                .calendarType(calendar.getCalendarType())
                .attendees(calendar.getAttendees())
                .noteId(calendar.getNoteId())
                .build();
    }

    @Override
    @Transactional
    public void updateCalendarEvent(Long calendarId, CalenderRequest request) throws Throwable {
        var calendar = calendarRepository.findByIdAndStatus(calendarId, Status.NORMAL.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Calendar event not found"));
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }

        if (java.time.LocalDate.parse(request.getStartDate()).isAfter(java.time.LocalDate.parse(request.getEndDate()))) {
            throw new IllegalArgumentException("End date must be greater than start date");
        }

        // Parse and format start and end times
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HHmmss");
        LocalTime startTime = LocalTime.parse(request.getStartTime(), inputFormatter);
        LocalTime endTime = LocalTime.parse(request.getEndTime(), inputFormatter);

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("End time must be greater than start time");
        }

        calendar.setTitle(request.getEventTitle());
        calendar.setDescription(request.getDescription());
        calendar.setStartDate(request.getStartDate());
        calendar.setEndDate(request.getEndDate());
        calendar.setStartTime(DateTimeUtils.formatTimeToHHmmss(request.getStartTime()));
        calendar.setEndTime(DateTimeUtils.formatTimeToHHmmss(request.getEndTime()));
        calendar.setLocation(request.getLocation());
        calendar.setCalendarType(request.getCalendarType());
        calendar.setAttendees(request.getAttendees());
        calendar.setStatus(Status.NORMAL);
        calendarRepository.save(calendar);
    }

    @Override
    @Transactional
    public void deleteCalendarEvent(Long calendarId) throws Throwable {
        var calendar = calendarRepository.findByIdAndStatus(calendarId, Status.NORMAL.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Calendar event not found"));
        calendar.setStatus(Status.DISABLE);
        calendarRepository.save(calendar);
    }
}
