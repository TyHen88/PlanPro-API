package com.planprostructure.planpro.domain.chatRoom;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(Long roomId);

    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE cm.room.id = :roomId " +
            "ORDER BY cm.sentAt DESC")
    List<ChatMessage> findLatestMessages(@Param("roomId") Long roomId,
            Pageable pageable);

    // Find unread messages count for a user in a room
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.room.id = :roomId " +
            "AND cm.sentAt > COALESCE((SELECT p.lastReadAt FROM RoomParticipant p " +
            "WHERE p.room.id = :roomId AND p.user.id = :userId), cm.sentAt)")
    Long countUnreadMessages(@Param("roomId") Long roomId,
            @Param("userId") Long userId);
}
