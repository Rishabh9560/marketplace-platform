package com.logicveda.marketplace.auth.controller;

import com.logicveda.marketplace.auth.service.AuthService;
import com.logicveda.marketplace.common.dto.AuthResponse;
import com.logicveda.marketplace.common.dto.ErrorResponse;
import com.logicveda.marketplace.common.dto.LoginRequest;
import com.logicveda.marketplace.common.dto.SignupRequest;
import com.logicveda.marketplace.common.entity.User;
import com.logicveda.marketplace.common.security.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user authentication endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and account management endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user account.
     */
    @PostMapping("/signup")
    @Operation(summary = "Register new user", description = "Create a new customer account")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("User signup request for email: {}", request.email());
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate user and return access + refresh tokens.
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate with email and password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login request for email: {}", request.email());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token using refresh token.
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get a new access token using refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponse> refresh(@RequestBody java.util.Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }
        AuthResponse response = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout user (revoke all refresh tokens).
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Revoke all refresh tokens for the user")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> logout(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal) {
            JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
            authService.logout(principal.getUserId());
            log.info("User logged out: {}", principal.getUserId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Get current authenticated user details.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieve authenticated user information")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal) {
            JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
            User user = authService.getUserById(principal.getUserId());
            return ResponseEntity.ok(new UserDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhone(),
                    user.getRole().toString(),
                    user.getEmailVerified()
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Verify email address (placeholder for email verification flow).
     */
    @GetMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify user email address with token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid verification token")
    })
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        // TODO: Implement email verification with Redis token storage
        return ResponseEntity.ok("Email verification not yet implemented");
    }

    /**
     * Request password reset (placeholder).
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Request password reset link")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password reset link sent"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<String> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        // TODO: Implement password reset flow with Redis token + email notification
        return ResponseEntity.ok("Password reset email sent");
    }

    /**
     * Reset password with token (placeholder).
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using reset token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password reset successful"),
        @ApiResponse(responseCode = "400", description = "Invalid reset token")
    })
    public ResponseEntity<String> resetPassword(@RequestBody java.util.Map<String, String> request) {
        // TODO: Implement password reset with token validation and new password setting
        return ResponseEntity.ok("Password reset successful");
    }

    /**
     * DTO for user information response.
     */
    @Schema(name = "UserDTO", description = "User information")
    public record UserDTO(
            @Schema(description = "User ID")
            java.util.UUID id,

            @Schema(description = "User email")
            String email,

            @Schema(description = "User full name")
            String fullName,

            @Schema(description = "User phone")
            String phone,

            @Schema(description = "User role")
            String role,

            @Schema(description = "Email verified flag")
            Boolean emailVerified
    ) {}
}
