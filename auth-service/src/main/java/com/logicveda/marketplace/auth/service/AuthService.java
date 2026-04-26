package com.logicveda.marketplace.auth.service;

import com.logicveda.marketplace.auth.repository.UserRepository;
import com.logicveda.marketplace.common.dto.AuthResponse;
import com.logicveda.marketplace.common.dto.LoginRequest;
import com.logicveda.marketplace.common.dto.SignupRequest;
import com.logicveda.marketplace.common.entity.User;
import com.logicveda.marketplace.common.exception.BusinessException;
import com.logicveda.marketplace.common.exception.ResourceNotFoundException;
import com.logicveda.marketplace.common.security.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for user authentication and account management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final EmailTokenService emailTokenService;

    /**
     * Register a new user account.
     */
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(
                    "Email already registered: " + request.email(),
                    "EMAIL_ALREADY_EXISTS"
            );
        }

        // Create new user
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setRole(User.UserRole.CUSTOMER);
        user.setEmailVerified(false);
        user.setIsActive(true);

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getId());

        // Generate verification token and send email
        String verificationToken = emailTokenService.generateVerificationToken(request.email());
        try {
            emailService.sendVerificationEmail(request.email(), verificationToken);
            emailService.sendWelcomeEmail(request.email(), request.fullName());
        } catch (Exception e) {
            log.warn("Failed to send verification email for {}: {}", request.email(), e.getMessage());
        }

        // Generate tokens
        JwtUserPrincipal principal = JwtUserPrincipal.create(
                user.getId(),
                user.getEmail(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().toString(),
                user.getIsActive()
        );

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().toString(),
                900L // 15 minutes in seconds
        );
    }

    /**
     * Authenticate user and return tokens.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(
                        "Invalid email or password",
                        "INVALID_CREDENTIALS"
                ));

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(
                    "Invalid email or password",
                    "INVALID_CREDENTIALS"
            );
        }

        // Check if account is active
        if (!user.getIsActive()) {
            throw new BusinessException(
                    "Account is inactive",
                    "ACCOUNT_INACTIVE"
            );
        }

        log.info("User logged in: {}", user.getId());

        // Generate tokens
        JwtUserPrincipal principal = JwtUserPrincipal.create(
                user.getId(),
                user.getEmail(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().toString(),
                user.getIsActive()
        );

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().toString(),
                900L // 15 minutes in seconds
        );
    }

    /**
     * Refresh access token using refresh token.
     */
    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        // Validate refresh token
        if (!jwtService.isValidRefreshToken(refreshToken)) {
            throw new BusinessException(
                    "Invalid or expired refresh token",
                    "INVALID_REFRESH_TOKEN"
            );
        }

        // Extract user ID from token
        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        // Revoke old refresh token
        jwtService.revokeRefreshToken(refreshToken);

        // Generate new tokens (token rotation)
        JwtUserPrincipal principal = JwtUserPrincipal.create(
                user.getId(),
                user.getEmail(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().toString(),
                user.getIsActive()
        );

        String newAccessToken = jwtService.generateAccessToken(principal);
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());

        log.info("Access token refreshed for user: {}", userId);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().toString(),
                900L
        );
    }

    /**
     * Logout user (revoke all refresh tokens).
     */
    @Transactional
    public void logout(UUID userId) {
        jwtService.revokeAllUserTokens(userId);
        log.info("User logged out: {}", userId);
    }

    /**
     * Get user by ID.
     */
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    /**
     * Get current user details.
     */
    @Transactional(readOnly = true)
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    /**
     * Verify user email address with token.
     */
    @Transactional
    public void verifyEmail(String token) {
        String email = emailTokenService.verifyEmailToken(token);
        if (email == null) {
            throw new BusinessException(
                    "Invalid or expired verification token",
                    "INVALID_VERIFICATION_TOKEN"
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        user.setEmailVerified(true);
        userRepository.save(user);
        
        log.info("Email verified for user: {}", user.getId());
    }

    /**
     * Initiate password reset by sending reset email.
     */
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        // Generate password reset token
        String resetToken = emailTokenService.generatePasswordResetToken(email);
        
        try {
            emailService.sendPasswordResetEmail(email, resetToken);
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
            throw new BusinessException("Failed to send password reset email", "EMAIL_SEND_FAILED");
        }

        log.info("Password reset email sent to: {}", email);
    }

    /**
     * Reset user password with reset token.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        String email = emailTokenService.verifyPasswordResetToken(token);
        if (email == null) {
            throw new BusinessException(
                    "Invalid or expired password reset token",
                    "INVALID_RESET_TOKEN"
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Consume the token
        emailTokenService.consumePasswordResetToken(token);
        
        // Revoke all existing refresh tokens
        jwtService.revokeAllUserTokens(user.getId());

        log.info("Password reset successfully for user: {}", user.getId());
    }
}
