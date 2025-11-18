package com.planprostructure.planpro.payload.reminder;

import org.springframework.beans.factory.annotation.Value;

public interface IGetReminder {
    @Value("#{target.reminder_id}")
    Long getReminderId();
    @Value("#{target.title}")
    String getTitle();
    @Value("#{target.description}")
    String getDescription();
    @Value("#{target.due_date}")
    String getDueDate();
    @Value("#{target.due_time}")
    String getDueTime();
    @Value("#{target.category}")
    String getCategory();
    @Value("#{target.priority}")
    String getPriority();
    @Value("#{target.tags}")
    String getTags();
    @Value("#{target.created_at}")
    String getCreatedAt();
    @Value("#{target.last_modified}")
    String getLastModified();
    @Value("#{target.reminder_status}")
    String getStatus();
    @Value("#{target.is_starred}")
    Boolean getStarred();
    @Value("#{target.recurrence_type}")
    String getRecurrenceType();
    @Value("#{target.user_id}")
    Long getUserId();
    @Value("#{target.trip_id}")
    Long getTripId();
    @Value("#{target.note_id}")
    Long getNoteId();
    @Value("#{target.telegram_user_id}")
    Long getTelegramUserId();
    @Value("#{target.is_recurring}")
    Boolean getIsRecurring();
    @Value("#{target.reminder_status}")
    String getReminderStatus();
}
