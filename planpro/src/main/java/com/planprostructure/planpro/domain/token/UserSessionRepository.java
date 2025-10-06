package com.planprostructure.planpro.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByToken(String token);

    List<UserSession> findByUserId(Long userId);

    List<UserSession> findByExpiresAt(LocalDateTime expiresAt);

    List<UserSession> findByCreatedAt(LocalDateTime createdAt);

    List<UserSession> findByUpdatedAt(LocalDateTime updatedAt);

    List<UserSession> findByIsActive(boolean isActive);

    // Find active sessions for a user
    @Query("SELECT us FROM UserSession us WHERE us.userId = :userId AND us.isActive = true AND us.expiresAt > :now")
    List<UserSession> findActiveSessionsByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Find valid token (active and not expired)
    @Query("SELECT us FROM UserSession us WHERE us.token = :token AND us.isActive = true AND us.expiresAt > :now")
    Optional<UserSession> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    // Deactivate all sessions for a user
    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false, us.updatedAt = :now WHERE us.userId = :userId")
    void deactivateAllSessionsForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Deactivate specific token
    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false, us.updatedAt = :now WHERE us.token = :token")
    void deactivateToken(@Param("token") String token, @Param("now") LocalDateTime now);

    // Clean up expired sessions
    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false, us.updatedAt = :now WHERE us.expiresAt <= :now")
    void deactivateExpiredSessions(@Param("now") LocalDateTime now);

    @Query("SELECT us FROM UserSession us JOIN Users u ON us.userId = u.id WHERE u.email = :email AND us.isActive = true AND us.expiresAt > :now")
    Optional<UserSession> findActiveSessionByEmail(@Param("email") String email, @Param("now") LocalDateTime now);
}