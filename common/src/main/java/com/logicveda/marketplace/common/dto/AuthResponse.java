package com.logicveda.marketplace.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Response DTO for authentication operations.
 */
@Schema(description = "Authentication response with tokens and user details")
public record AuthResponse(
    @Schema(description = "Access token (JWT)")
    @JsonProperty("access_token")
    String accessToken,

    @Schema(description = "Refresh token (JWT)")
    @JsonProperty("refresh_token")
    String refreshToken,

    @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID userId,

    @Schema(description = "User email", example = "user@example.com")
    String email,

    @Schema(description = "User full name", example = "John Doe")
    String fullName,

    @Schema(description = "User role", example = "CUSTOMER")
    String role,

    @Schema(description = "Token expiration time in seconds", example = "900")
    @JsonProperty("expires_in")
    Long expiresIn
) {}
