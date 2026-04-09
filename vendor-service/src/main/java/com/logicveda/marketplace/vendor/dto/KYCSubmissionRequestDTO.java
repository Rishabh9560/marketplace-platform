package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * KYC Submission Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "KYC Submission Request")
public class KYCSubmissionRequestDTO {

    @NotBlank(message = "Business license number is required")
    @Schema(description = "Business license number", example = "BL-2024-001")
    private String businessLicenseNumber;

    @NotBlank(message = "Tax ID is required")
    @Schema(description = "Tax ID", example = "TX-1234567890")
    private String taxId;

    @NotBlank(message = "Bank account number is required")
    @Schema(description = "Bank account number", example = "XXXX-XXXX-XXXX-1234")
    private String bankAccountNumber;

    @NotBlank(message = "Bank routing number is required")
    @Schema(description = "Bank routing number", example = "021000021")
    private String bankRoutingNumber;

    @NotBlank(message = "Bank name is required")
    @Schema(description = "Bank name", example = "Chase Bank")
    private String bankName;

    @NotEmpty(message = "At least one KYC document is required")
    @Schema(description = "KYC documents to upload")
    private List<KYCDocumentDTO> documents;

    @Schema(description = "Additional notes for KYC team")
    private String notes;
}
