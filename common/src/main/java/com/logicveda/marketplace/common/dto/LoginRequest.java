package com.logicveda.marketplace.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user login.
 */
@Schema(description = "User login request")
public record LoginRequest(
    @Schema(description = "User email address", example = "user@example.com")
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    String email,

    @Schema(description = "User password", example = "SecurePassword123!")
    @NotBlank(message = "Password is required")
    String password
) {}
