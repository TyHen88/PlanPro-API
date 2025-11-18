package com.planprostructure.planpro.controller;

import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.payload.calendar.CalenderRequest;
import com.planprostructure.planpro.service.calendar.CalendarService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wb/v1/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Calendar API")
public class CalendarController extends ProPlanRestController {
    private final CalendarService calendarService;

    @PostMapping
    public Object createCalendarEvent(@Valid @RequestBody CalenderRequest request) throws Throwable {
        calendarService.createCalendarEvent(request);
        return ok();
    }

    @GetMapping
    public Object getAllCalendarEvents() throws Throwable {
        return ok(calendarService.getCalendarEvent());
    }

    @GetMapping("/{calendarId}")
    public Object getCalendarEventById(@PathVariable Long calendarId) throws Throwable {
        return ok(calendarService.getCalendarEventById(calendarId));
    }

    @PutMapping("/{calendarId}")
    public Object updateCalendarEvent(@PathVariable Long calendarId, @Valid @RequestBody CalenderRequest request) throws Throwable {
        calendarService.updateCalendarEvent(calendarId, request);
        return ok();
    }
    @DeleteMapping("/{calendarId}")
    public Object deleteCalendarEvent(@PathVariable Long calendarId) throws Throwable {
        calendarService.deleteCalendarEvent(calendarId);
        return ok();
    }
}
