package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * KYC Document DTO for KYC submission and verification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "KYC Document Information")
public class KYCDocumentDTO {

    @NotBlank(message = "Document type is required")
    @Schema(description = "Document type", example = "BUSINESS_LICENSE")
    private String documentType;

    @NotBlank(message = "Document URL is required")
    @Schema(description = "Document file URL", example = "https://storage.example.com/docs/license.pdf")
    private String documentUrl;

    @Schema(description = "Document upload timestamp")
    private String uploadedAt;

    @Schema(description = "Verification status", example = "PENDING")
    private String verificationStatus;

    @Schema(description = "Verification notes")
    private String verificationNotes;
}
