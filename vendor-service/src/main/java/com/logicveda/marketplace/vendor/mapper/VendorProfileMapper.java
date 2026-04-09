package com.logicveda.marketplace.vendor.mapper;

import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.entity.VendorProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for VendorProfile entity and DTO conversions
 */
@Component
public class VendorProfileMapper {

    /**
     * Convert VendorProfile entity to DTO
     */
    public VendorProfileDTO toDTO(VendorProfile entity) {
        if (entity == null) {
            return null;
        }
        return VendorProfileDTO.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .businessName(entity.getBusinessName())
            .businessDescription(entity.getBusinessDescription())
            .businessEmail(entity.getBusinessEmail())
            .businessPhone(entity.getBusinessPhone())
            .website(entity.getWebsite())
            .businessAddress(entity.getBusinessAddress())
            .businessCity(entity.getBusinessCity())
            .businessState(entity.getBusinessState())
            .businessPostalCode(entity.getBusinessPostalCode())
            .businessCountry(entity.getBusinessCountry())
            .kycStatus(entity.getKycStatus().toString())
            .businessLicenseNumber(entity.getBusinessLicenseNumber())
            .taxId(entity.getTaxId())
            .bankAccountNumber(entity.getBankAccountNumber())
            .bankRoutingNumber(entity.getBankRoutingNumber())
            .bankName(entity.getBankName())
            .kycDocuments(entity.getKycDocuments())
            .kycVerifiedAt(entity.getKycVerifiedAt())
            .commissionRate(entity.getCommissionRate())
            .totalEarnings(entity.getTotalEarnings())
            .totalPayouts(entity.getTotalPayouts())
            .availableBalance(entity.getAvailableBalance())
            .isActive(entity.getIsActive())
            .isSuspended(entity.getIsSuspended())
            .suspensionReason(entity.getSuspensionReason())
            .averageRating(entity.getAverageRating())
            .totalReviews(entity.getTotalReviews())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    /**
     * Convert VendorProfile DTO to entity
     */
    public VendorProfile toEntity(VendorProfileDTO dto) {
        if (dto == null) {
            return null;
        }
        return VendorProfile.builder()
            .id(dto.getId())
            .userId(dto.getUserId())
            .businessName(dto.getBusinessName())
            .businessDescription(dto.getBusinessDescription())
            .businessEmail(dto.getBusinessEmail())
            .businessPhone(dto.getBusinessPhone())
            .website(dto.getWebsite())
            .businessAddress(dto.getBusinessAddress())
            .businessCity(dto.getBusinessCity())
            .businessState(dto.getBusinessState())
            .businessPostalCode(dto.getBusinessPostalCode())
            .businessCountry(dto.getBusinessCountry())
            .kycStatus(dto.getKycStatus() != null ? 
                VendorProfile.KYCStatus.valueOf(dto.getKycStatus()) : 
                VendorProfile.KYCStatus.PENDING)
            .businessLicenseNumber(dto.getBusinessLicenseNumber())
            .taxId(dto.getTaxId())
            .bankAccountNumber(dto.getBankAccountNumber())
            .bankRoutingNumber(dto.getBankRoutingNumber())
            .bankName(dto.getBankName())
            .kycDocuments(dto.getKycDocuments())
            .kycVerifiedAt(dto.getKycVerifiedAt())
            .commissionRate(dto.getCommissionRate())
            .totalEarnings(dto.getTotalEarnings())
            .totalPayouts(dto.getTotalPayouts())
            .availableBalance(dto.getAvailableBalance())
            .isActive(dto.getIsActive())
            .isSuspended(dto.getIsSuspended())
            .suspensionReason(dto.getSuspensionReason())
            .averageRating(dto.getAverageRating())
            .totalReviews(dto.getTotalReviews())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }

    /**
     * Convert list of entities to DTOs
     */
    public List<VendorProfileDTO> toDTOList(List<VendorProfile> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convert list of DTOs to entities
     */
    public List<VendorProfile> toEntityList(List<VendorProfileDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    /**
     * Update entity from DTO (for partial updates)
     */
    public VendorProfile updateEntityFromDTO(VendorProfileDTO dto, VendorProfile entity) {
        if (dto == null) {
            return entity;
        }
        if (dto.getBusinessName() != null) {
            entity.setBusinessName(dto.getBusinessName());
        }
        if (dto.getBusinessDescription() != null) {
            entity.setBusinessDescription(dto.getBusinessDescription());
        }
        if (dto.getBusinessEmail() != null) {
            entity.setBusinessEmail(dto.getBusinessEmail());
        }
        if (dto.getBusinessPhone() != null) {
            entity.setBusinessPhone(dto.getBusinessPhone());
        }
        if (dto.getWebsite() != null) {
            entity.setWebsite(dto.getWebsite());
        }
        if (dto.getBusinessAddress() != null) {
            entity.setBusinessAddress(dto.getBusinessAddress());
        }
        if (dto.getBusinessCity() != null) {
            entity.setBusinessCity(dto.getBusinessCity());
        }
        if (dto.getBusinessState() != null) {
            entity.setBusinessState(dto.getBusinessState());
        }
        if (dto.getBusinessPostalCode() != null) {
            entity.setBusinessPostalCode(dto.getBusinessPostalCode());
        }
        if (dto.getBusinessCountry() != null) {
            entity.setBusinessCountry(dto.getBusinessCountry());
        }
        if (dto.getCommissionRate() != null) {
            entity.setCommissionRate(dto.getCommissionRate());
        }
        return entity;
    }
}
