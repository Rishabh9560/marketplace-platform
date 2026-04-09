package com.logicveda.marketplace.vendor.mapper;

import com.logicveda.marketplace.vendor.dto.VendorPayoutRecordDTO;
import com.logicveda.marketplace.vendor.entity.VendorPayoutRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for VendorPayoutRecord entity and DTO conversions
 */
@Component
public class VendorPayoutRecordMapper {

    /**
     * Convert VendorPayoutRecord entity to DTO
     */
    public VendorPayoutRecordDTO toDTO(VendorPayoutRecord entity) {
        if (entity == null) {
            return null;
        }
        return VendorPayoutRecordDTO.builder()
            .id(entity.getId())
            .vendorId(entity.getVendorId())
            .payoutPeriod(entity.getPayoutPeriod())
            .totalSalesAmount(entity.getTotalSalesAmount())
            .commissionDeducted(entity.getCommissionDeducted())
            .refundsDeducted(entity.getRefundsDeducted())
            .adjustments(entity.getAdjustments())
            .netPayoutAmount(entity.getNetPayoutAmount())
            .status(entity.getStatus().toString())
            .bankAccountNumber(entity.getBankAccountNumber())
            .bankName(entity.getBankName())
            .transactionId(entity.getTransactionId())
            .scheduledPayoutDate(entity.getScheduledPayoutDate())
            .actualPayoutDate(entity.getActualPayoutDate())
            .failureReason(entity.getFailureReason())
            .retryCount(entity.getRetryCount())
            .orderSummary(entity.getOrderSummary())
            .reportUrl(entity.getReportUrl())
            .notes(entity.getNotes())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    /**
     * Convert VendorPayoutRecord DTO to entity
     */
    public VendorPayoutRecord toEntity(VendorPayoutRecordDTO dto) {
        if (dto == null) {
            return null;
        }
        return VendorPayoutRecord.builder()
            .id(dto.getId())
            .vendorId(dto.getVendorId())
            .payoutPeriod(dto.getPayoutPeriod())
            .totalSalesAmount(dto.getTotalSalesAmount())
            .commissionDeducted(dto.getCommissionDeducted())
            .refundsDeducted(dto.getRefundsDeducted())
            .adjustments(dto.getAdjustments())
            .netPayoutAmount(dto.getNetPayoutAmount())
            .status(dto.getStatus() != null ? 
                VendorPayoutRecord.PayoutStatus.valueOf(dto.getStatus()) : 
                VendorPayoutRecord.PayoutStatus.PENDING)
            .bankAccountNumber(dto.getBankAccountNumber())
            .bankName(dto.getBankName())
            .transactionId(dto.getTransactionId())
            .scheduledPayoutDate(dto.getScheduledPayoutDate())
            .actualPayoutDate(dto.getActualPayoutDate())
            .failureReason(dto.getFailureReason())
            .retryCount(dto.getRetryCount())
            .orderSummary(dto.getOrderSummary())
            .reportUrl(dto.getReportUrl())
            .notes(dto.getNotes())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }

    /**
     * Convert list of entities to DTOs
     */
    public List<VendorPayoutRecordDTO> toDTOList(List<VendorPayoutRecord> entities) {
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
    public List<VendorPayoutRecord> toEntityList(List<VendorPayoutRecordDTO> dtos) {
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
    public VendorPayoutRecord updateEntityFromDTO(VendorPayoutRecordDTO dto, VendorPayoutRecord entity) {
        if (dto == null) {
            return entity;
        }
        if (dto.getStatus() != null) {
            entity.setStatus(VendorPayoutRecord.PayoutStatus.valueOf(dto.getStatus()));
        }
        if (dto.getScheduledPayoutDate() != null) {
            entity.setScheduledPayoutDate(dto.getScheduledPayoutDate());
        }
        if (dto.getActualPayoutDate() != null) {
            entity.setActualPayoutDate(dto.getActualPayoutDate());
        }
        if (dto.getTransactionId() != null) {
            entity.setTransactionId(dto.getTransactionId());
        }
        if (dto.getFailureReason() != null) {
            entity.setFailureReason(dto.getFailureReason());
        }
        if (dto.getRetryCount() != null) {
            entity.setRetryCount(dto.getRetryCount());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }
        return entity;
    }
}
