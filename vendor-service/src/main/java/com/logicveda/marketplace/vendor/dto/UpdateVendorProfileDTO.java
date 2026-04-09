package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Update Vendor Profile Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Update Vendor Profile Request")
public class UpdateVendorProfileDTO {

    @Size(min = 3, max = 255, message = "Business name must be between 3 and 255 characters")
    @Schema(description = "Business name")
    private String businessName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Business description")
    private String businessDescription;

    @Email(message = "Business email should be valid")
    @Schema(description = "Business email")
    private String businessEmail;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Business phone should be valid")
    @Schema(description = "Business phone number")
    private String businessPhone;

    @Schema(description = "Business website")
    private String website;

    @Schema(description = "Business street address")
    private String businessAddress;

    @Schema(description = "Business city")
    private String businessCity;

    @Schema(description = "Business state")
    private String businessState;

    @Schema(description = "Business postal code")
    private String businessPostalCode;

    @Schema(description = "Business country")
    private String businessCountry;

    @DecimalMin(value = "0.00", message = "Commission rate must be >= 0")
    @DecimalMax(value = "100.00", message = "Commission rate must be <= 100")
    @Schema(description = "Commission rate percentage")
    private BigDecimal commissionRate;

    @Schema(description = "Suspension reason (for suspending vendor)")
    private String suspensionReason;
}
