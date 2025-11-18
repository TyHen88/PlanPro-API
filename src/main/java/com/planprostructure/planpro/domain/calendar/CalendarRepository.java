package com.planprostructure.planpro.domain.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    @Query("SELECT c FROM Calendar c WHERE c.userId = ?1 AND c.status = '1'")
    List<Calendar> findByCalendarId(Long userId);

    @Query("SELECT c FROM Calendar c WHERE c.id = ?1 AND c.status = '1'")
    Optional<Calendar> findByIdAndStatus(Long calendarId, String status);

    @Query("SELECT c FROM Calendar c WHERE c.noteId = ?1 AND c.status = '1'")
    Calendar findByNoteId(Long noteId);

}
