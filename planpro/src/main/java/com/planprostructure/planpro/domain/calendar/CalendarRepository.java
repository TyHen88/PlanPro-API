package com.planprostructure.planpro.domain.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    @Query("SELECT c FROM Calendar c WHERE c.userId = ?1 AND c.status = '1'")
    List<Calendar> findByCalendarId(Long userId);

    @Query("SELECT c FROM Calendar c WHERE c.id = ?1 AND c.status = '1'")
    Optional<Calendar> findByIdAndStatus(Long calendarId, String status);

    @Query("SELECT c FROM Calendar c WHERE c.noteId = ?1 AND c.status = '1'")
    Calendar findByNoteId(Long noteId);

    @Query(value = """
                SELECT
                    cal_id,
                    user_id,
                    trip_id,
                    note_id,
                    event_title,
                    start_date,
                    end_date,
                    start_time,
                    end_time,
                    description,
                    location,
                    cal_type,
                    status,
                    attendees,
                    is_notify,
                    created_at,
                    change_at
                FROM tb_calendar
                WHERE user_id = :userId
                AND status = '1'
                AND (:searchTerm IS NULL OR :searchTerm = '' OR :searchTerm = 'all' OR
                     (event_title ILIKE '%' || :searchTerm || '%' OR description ILIKE '%' || :searchTerm || '%'))
                AND (:dateFilter IS NULL OR :dateFilter = '' OR
                     CAST(start_date AS TEXT) LIKE '%' || :dateFilter || '%' OR CAST(end_date AS TEXT) LIKE '%' || :dateFilter || '%')
                AND (:yearFilter IS NULL OR :yearFilter = '' OR
                     CAST(start_date AS TEXT) LIKE '%' || :yearFilter || '%' OR CAST(end_date AS TEXT) LIKE '%' || :yearFilter || '%' OR
                     CAST(created_at AS TEXT) LIKE '%' || :yearFilter || '%')
                ORDER BY start_date DESC, created_at DESC
            """, nativeQuery = true)
    List<Calendar> searchEventsOptimized(
            @Param("userId") Long userId,
            @Param("searchTerm") String searchTerm,
            @Param("dateFilter") String dateFilter,
            @Param("yearFilter") String yearFilter);

}
