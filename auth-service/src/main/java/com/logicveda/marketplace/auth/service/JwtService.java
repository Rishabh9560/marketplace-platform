package com.logicveda.marketplace.auth.service;

import com.logicveda.marketplace.auth.entity.RefreshToken;
import com.logicveda.marketplace.auth.repository.RefreshTokenRepository;
import com.logicveda.marketplace.common.security.JwtUserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Service for JWT token operations: generation, validation, and rotation.
 * Implements refresh token rotation for enhanced security.
 */
@Slf4j
@Service
public class JwtService {

    private final String jwtSecret;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecretKey key;

    public JwtService(
            @Value("${jwt.secret:mySecretKeyForJWTEncodingAndDecodingThatIsAt32CharactersOrMore}") String jwtSecret,
            @Value("${jwt.access-token-expiration:900000}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration:1209600000}") long refreshTokenExpiration,
            RefreshTokenRepository refreshTokenRepository) {
        this.jwtSecret = jwtSecret;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.refreshTokenRepository = refreshTokenRepository;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT access token (short-lived, 15 minutes).
     */
    public String generateAccessToken(UserDetails userDetails) {
        JwtUserPrincipal principal = (JwtUserPrincipal) userDetails;
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("userId", principal.getUserId().toString())
                .claim("email", principal.getEmail())
                .claim("authorities", principal.getAuthorities()
                        .stream()
                        .map(auth -> auth.getAuthority())
                        .toArray(String[]::new))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate JWT refresh token (long-lived, 14 days).
     * Token is hashed and stored in database for revocation support.
     */
    @Transactional
    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Hash token for storage (use SHA256)
        String tokenHash = hashToken(token);

        // Store hashed token in database
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .expiresAt(expiryDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    /**
     * Validate and extract claims from access token.
     */
    public Claims validateAndGetClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Validate refresh token and check revocation status.
     */
    @Transactional(readOnly = true)
    public boolean isValidRefreshToken(String token) {
        try {
            // Validate JWT structure and signature
            validateAndGetClaims(token);

            // Check if token is revoked in database
            String tokenHash = hashToken(token);
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByTokenHash(tokenHash);

            if (refreshToken.isEmpty()) {
                log.warn("Refresh token not found in database");
                return false;
            }

            boolean isValid = refreshToken.get().isValid();
            if (!isValid) {
                log.warn("Refresh token is revoked or expired");
            }

            return isValid;
        } catch (JwtException e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract user ID from token claims.
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = validateAndGetClaims(token);
        return UUID.fromString(claims.get("userId", String.class));
    }

    /**
     * Extract email from token claims.
     */
    public String getEmailFromToken(String token) {
        Claims claims = validateAndGetClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * Extract username from token claims.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = validateAndGetClaims(token);
        return claims.getSubject();
    }

    /**
     * Revoke refresh token (mark as revoked in database).
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        try {
            String tokenHash = hashToken(token);
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByTokenHash(tokenHash);
            if (refreshToken.isPresent()) {
                refreshToken.get().setRevoked(true);
                refreshTokenRepository.save(refreshToken.get());
                log.info("Refresh token revoked");
            }
        } catch (Exception e) {
            log.error("Error revoking refresh token: {}", e.getMessage());
        }
    }

    /**
     * Revoke all refresh tokens for a user (logout from all devices).
     */
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        int revokedCount = refreshTokenRepository.revokeAllUserTokens(userId);
        log.info("Revoked {} refresh tokens for user: {}", revokedCount, userId);
    }

    /**
     * Clean up expired refresh tokens from database.
     */
    @Transactional
    public void cleanupExpiredTokens() {
        int deletedCount = refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.debug("Deleted {} expired refresh tokens", deletedCount);
    }

    /**
     * Hash token using SHA256 for secure storage.
     */
    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
