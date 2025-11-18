package com.planprostructure.planpro.domain.myNote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MyNotesRepository extends JpaRepository<MyNotes, Long> {

    @Query("SELECT m FROM MyNotes m WHERE m.userId = ?1 AND m.isDeleted = false")
    List<MyNotes> findAllByUserIdAndStatus(Long userId);

    @Query("SELECT m FROM MyNotes m WHERE m.id = ?1 AND m.isDeleted = false")
    MyNotes findByIdAndStatus(Long id);
}
