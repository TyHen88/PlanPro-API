package com.planprostructure.planpro.controller;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.payload.MultiSortBuilder;
import com.planprostructure.planpro.payload.reminder.ReminderRequest;
import com.planprostructure.planpro.service.reminder.ReminderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wb/v1/reminders")
@RequiredArgsConstructor
@Tag(name = "Reminder", description = "Reminder API")
public class ReminderController extends ProPlanRestController {

    private final ReminderService reminderService;

    @GetMapping
    public Object getListReminder(
            @RequestParam(name = "page_number", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sort_columns", required = false, defaultValue = "id:desc") String sortColumns
            ) throws Throwable {
        List<Sort.Order> sortBuilder = new MultiSortBuilder().with(sortColumns).build();
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortBuilder));
        return ok(reminderService.getListReminder(pageRequest));
    }

    @PostMapping("/create")
    public Object createReminder(@Valid @RequestBody ReminderRequest reminderRequest) throws Throwable {
        reminderService.createReminder(reminderRequest);
        return ok();
    }

    @PutMapping("/update/{id}")
    public Object updateReminder(@PathVariable Long id, @RequestBody ReminderRequest reminderRequest) throws Throwable {
        reminderService.updateReminder(id, reminderRequest);
        return ok();
    }

    @DeleteMapping("/delete/{id}")
    public Object deleteReminder(@PathVariable Long id) throws Throwable {
        reminderService.deleteReminder(id);
        return ok();
    }
    
    @PutMapping("/mark-as-done/{id}")
    public Object markAsDone(@PathVariable Long id, @RequestBody boolean isDone) throws Throwable {
        reminderService.markAsDone(id, isDone);
        return ok();
    }
    
    @PutMapping("/mark-as-starred/{id}")
    public Object markAsStarred(@PathVariable Long id, @RequestBody boolean isStarred) throws Throwable {
        reminderService.markAsIsStarred(id, isStarred);
        return ok();
    }
}
