package com.logicveda.marketplace.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user signup.
 */
@Schema(description = "User signup request")
public record SignupRequest(
    @Schema(description = "User email address", example = "user@example.com")
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    String email,

    @Schema(description = "User password", example = "SecurePassword123!")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    String password,

    @Schema(description = "User full name", example = "John Doe")
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    String fullName,

    @Schema(description = "User phone number", example = "+919876543210")
    String phone
) {}
