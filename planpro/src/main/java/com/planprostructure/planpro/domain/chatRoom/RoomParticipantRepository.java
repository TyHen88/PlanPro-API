package com.planprostructure.planpro.domain.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {
    Optional<RoomParticipant> findByRoomIdAndUserId(Long roomId, Long userId);

    List<RoomParticipant> findByRoomId(Long roomId);

    @Modifying
    @Query("UPDATE RoomParticipant p SET p.lastReadAt = :timestamp " +
            "WHERE p.room.id = :roomId AND p.user.id = :userId")
    void updateLastReadTime(@Param("roomId") Long roomId,
            @Param("userId") Long userId,
            @Param("timestamp") LocalDateTime timestamp);
}
