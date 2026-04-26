package com.logicveda.marketplace.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing email verification and password reset tokens using Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTokenService {

    private final RedissonClient redissonClient;

    private static final String VERIFICATION_TOKEN_PREFIX = "email_verification:";
    private static final String PASSWORD_RESET_TOKEN_PREFIX = "password_reset:";
    private static final long VERIFICATION_TOKEN_TTL_HOURS = 24;
    private static final long PASSWORD_RESET_TOKEN_TTL_MINUTES = 60;

    /**
     * Generate and store email verification token.
     */
    public String generateVerificationToken(String email) {
        String token = generateSecureToken();
        String key = VERIFICATION_TOKEN_PREFIX + token;
        
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(email, VERIFICATION_TOKEN_TTL_HOURS, TimeUnit.HOURS);
        
        log.info("Generated verification token for email: {}", email);
        return token;
    }

    /**
     * Verify and retrieve email from verification token.
     */
    public String verifyEmailToken(String token) {
        String key = VERIFICATION_TOKEN_PREFIX + token;
        RBucket<String> bucket = redissonClient.getBucket(key);
        
        if (!bucket.isExists()) {
            log.warn("Verification token not found or expired: {}", token);
            return null;
        }
        
        String email = bucket.get();
        bucket.delete();
        
        log.info("Email verified for token, email: {}", email);
        return email;
    }

    /**
     * Generate and store password reset token.
     */
    public String generatePasswordResetToken(String email) {
        String token = generateSecureToken();
        String key = PASSWORD_RESET_TOKEN_PREFIX + token;
        
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(email, PASSWORD_RESET_TOKEN_TTL_MINUTES, TimeUnit.MINUTES);
        
        log.info("Generated password reset token for email: {}", email);
        return token;
    }

    /**
     * Verify and retrieve email from password reset token.
     */
    public String verifyPasswordResetToken(String token) {
        String key = PASSWORD_RESET_TOKEN_PREFIX + token;
        RBucket<String> bucket = redissonClient.getBucket(key);
        
        if (!bucket.isExists()) {
            log.warn("Password reset token not found or expired: {}", token);
            return null;
        }
        
        String email = bucket.get();
        // Do NOT delete token here - let it expire naturally or be consumed in resetPassword()
        
        log.info("Password reset token verified for email: {}", email);
        return email;
    }

    /**
     * Consume password reset token (delete it).
     */
    public void consumePasswordResetToken(String token) {
        String key = PASSWORD_RESET_TOKEN_PREFIX + token;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.delete();
        log.info("Password reset token consumed and deleted");
    }

    /**
     * Generate secure random token.
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
