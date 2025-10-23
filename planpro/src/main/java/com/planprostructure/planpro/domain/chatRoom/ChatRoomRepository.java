package com.planprostructure.planpro.domain.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
        // Find direct chat between two users
        @Query(value = "SELECT cr.* FROM chat_rooms cr " +
                        "WHERE cr.type = 'DIRECT' " +
                        "AND cr.id IN (SELECT room_id FROM room_participants " +
                        "WHERE user_id IN (:userId1, :userId2) " +
                        "GROUP BY room_id " +
                        "HAVING COUNT(DISTINCT user_id) = 2)", nativeQuery = true)
        Optional<ChatRoom> findDirectChatBetweenUsers(@Param("userId1") Long userId1,
                        @Param("userId2") Long userId2);

        // Find all rooms for a user
        @Query(value = "SELECT DISTINCT cr.* FROM chat_rooms cr " +
                        "JOIN room_participants rp ON cr.id = rp.room_id " +
                        "WHERE rp.user_id = :userId", nativeQuery = true)
        List<ChatRoom> findRoomsByUserId(@Param("userId") Long userId);

        // Check if user is participant of room
        @Query(value = "SELECT COUNT(*) > 0 FROM room_participants " +
                        "WHERE room_id = :roomId AND user_id = :userId", nativeQuery = true)
        boolean isUserParticipant(@Param("roomId") Long roomId,
                        @Param("userId") Long userId);
}
