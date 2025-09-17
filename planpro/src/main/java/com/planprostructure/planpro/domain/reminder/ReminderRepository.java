package com.planprostructure.planpro.domain.reminder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.planprostructure.planpro.payload.reminder.IGetReminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    @Query(value="""
           SELECT  
                rm.id AS reminder_id, 
                rm.title, 
                rm.description, 
                rm.due_date, 
                rm.due_time, 
                rm.recurrence_type as recurrence_type, 
                rm.category as category, 
                rm.created_at as created_at, 
                rm.last_modified as last_modified, 
                rm.reminder_status as reminder_status , 
                rm.is_starred as is_starred, 
                rm.priority as priority, 
                rm.tags as tags,
                rm.user_id AS user_id,
                rm.trip_id,
                rm.note_id,
                rm.telegram_user_id,
                rm.is_recurring as is_recurring
                FROM 
                tb_reminder rm
                WHERE 
                rm.user_id = ?1 AND rm.status = '1'
                ORDER BY 
                rm.created_at DESC, rm.last_modified DESC
            
            """, nativeQuery = true)
    Page<IGetReminder> findAllReminder(Long userId, Pageable pageable);
}
