package com.logicveda.marketplace.auth.repository;

import com.logicveda.marketplace.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RefreshToken entity.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Find refresh token by token hash.
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Find all valid tokens for a user.
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = ?1 AND rt.revoked = false AND rt.expiresAt > ?2")
    java.util.List<RefreshToken> findValidTokensByUserId(UUID userId, LocalDateTime now);

    /**
     * Revoke all refresh tokens for a user.
     */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.userId = ?1")
    int revokeAllUserTokens(UUID userId);

    /**
     * Delete expired tokens.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < ?1")
    int deleteExpiredTokens(LocalDateTime now);
}
