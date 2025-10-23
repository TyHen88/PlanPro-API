package com.planprostructure.planpro.domain.myNote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyNotesRepository extends JpaRepository<MyNotes, Long> {

     @Query("SELECT m FROM MyNotes m WHERE m.userId = ?1 AND m.isDeleted = false")
     List<MyNotes> findAllByUserIdAndStatus(Long userId);

     @Query("SELECT m FROM MyNotes m WHERE m.id = ?1 AND m.isDeleted = false")
     MyNotes findByIdAndStatus(Long id);

     @Query(value = """
                   SELECT
                       note_id,
                       user_id,
                       title,
                       content,
                       created_at,
                       updated_at,
                       color,
                       text_color,
                       is_deleted,
                       is_calendar_event,
                       is_notify
                   FROM tb_my_notes
                   WHERE user_id = :userId
                   AND is_deleted = false
                   AND (:searchTerm IS NULL OR :searchTerm = '' OR :searchTerm = 'all' OR
                        (title ILIKE '%' || :searchTerm || '%' OR content ILIKE '%' || :searchTerm || '%'))
                   AND (:dateFilter IS NULL OR :dateFilter = '' OR
                        CAST(created_at AS TEXT) LIKE '%' || :dateFilter || '%' OR CAST(updated_at AS TEXT) LIKE '%' || :dateFilter || '%')
                   AND (:yearFilter IS NULL OR :yearFilter = '' OR
                        CAST(created_at AS TEXT) LIKE '%' || :yearFilter || '%' OR CAST(updated_at AS TEXT) LIKE '%' || :yearFilter || '%')
                   ORDER BY created_at DESC
               """, nativeQuery = true)
     List<MyNotes> searchNotesOptimized(
               @Param("userId") Long userId,
               @Param("searchTerm") String searchTerm,
               @Param("dateFilter") String dateFilter,
               @Param("yearFilter") String yearFilter);
}
