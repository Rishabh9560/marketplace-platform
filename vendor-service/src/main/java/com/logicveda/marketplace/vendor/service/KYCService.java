package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.dto.KYCSubmissionRequestDTO;
import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.entity.VendorProfile;
import com.logicveda.marketplace.vendor.exception.VendorException;
import com.logicveda.marketplace.vendor.mapper.VendorProfileMapper;
import com.logicveda.marketplace.vendor.repository.VendorProfileRepository;
import com.logicveda.marketplace.vendor.util.DateUtils;
import com.logicveda.marketplace.vendor.util.JsonUtils;
import com.logicveda.marketplace.vendor.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for KYC (Know Your Customer) management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KYCService {

    private final VendorProfileRepository vendorRepository;
    private final VendorProfileMapper vendorMapper;
    private final JsonUtils jsonUtils;

    /**
     * Submit KYC application
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public VendorProfileDTO submitKYC(UUID vendorId, KYCSubmissionRequestDTO kycSubmission) {
        log.info("Submitting KYC for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findByUserId(vendorId)
            .orElseThrow(() -> VendorException.userNotVendor(vendorId.toString()));

        // Validate KYC not already submitted
        if (vendor.getKycStatus() == VendorProfile.KYCStatus.SUBMITTED ||
            vendor.getKycStatus() == VendorProfile.KYCStatus.VERIFIED) {
            throw VendorException.kycAlreadySubmitted(vendorId.toString());
        }

        if (vendor.getKycStatus() == VendorProfile.KYCStatus.REJECTED) {
            throw VendorException.kycInvalidStatus(vendorId.toString(), "REJECTED");
        }

        // Validate input
        if (!ValidationUtils.isValidEmail(kycSubmission.getTaxId())) {
            ValidationUtils.validateBusinessName(kycSubmission.getBusinessLicenseNumber());
        }

        // Update vendor with KYC info
        vendor.setBusinessLicenseNumber(kycSubmission.getBusinessLicenseNumber());
        vendor.setTaxId(kycSubmission.getTaxId());
        vendor.setBankAccountNumber(kycSubmission.getBankAccountNumber());
        vendor.setBankRoutingNumber(kycSubmission.getBankRoutingNumber());
        vendor.setBankName(kycSubmission.getBankName());

        // Store KYC documents as JSON
        vendor.setKycDocuments(jsonUtils.toJson(kycSubmission.getDocuments()));
        vendor.setKycStatus(VendorProfile.KYCStatus.SUBMITTED);
        vendor.setUpdatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.info("KYC submitted for vendor: {}", vendorId);

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Verify KYC for vendor
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public VendorProfileDTO verifyKYC(UUID vendorId) {
        log.info("Verifying KYC for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        if (vendor.getKycStatus() != VendorProfile.KYCStatus.SUBMITTED) {
            throw VendorException.kycInvalidStatus(vendorId.toString(), vendor.getKycStatus().toString());
        }

        vendor.setKycStatus(VendorProfile.KYCStatus.VERIFIED);
        vendor.setKycVerifiedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.info("KYC verified for vendor: {}", vendorId);

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Reject KYC for vendor
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public VendorProfileDTO rejectKYC(UUID vendorId, String reason) {
        log.warn("Rejecting KYC for vendor: {} - Reason: {}", vendorId, reason);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        if (vendor.getKycStatus() != VendorProfile.KYCStatus.SUBMITTED) {
            throw VendorException.kycInvalidStatus(vendorId.toString(), vendor.getKycStatus().toString());
        }

        vendor.setKycStatus(VendorProfile.KYCStatus.REJECTED);
        vendor.setUpdatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.warn("KYC rejected for vendor: {}", vendorId);

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Request KYC re-submission
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public VendorProfileDTO requestKYCResubmission(UUID vendorId, String reason) {
        log.info("Requesting KYC re-submission for vendor: {} - Reason: {}", vendorId, reason);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        if (vendor.getKycStatus() != VendorProfile.KYCStatus.REJECTED) {
            throw VendorException.kycInvalidStatus(vendorId.toString(), vendor.getKycStatus().toString());
        }

        vendor.setKycStatus(VendorProfile.KYCStatus.PENDING);
        vendor.setUpdatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.info("KYC re-submission requested for vendor: {}", vendorId);

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Check if vendor KYC is verified
     */
    @Transactional(readOnly = true)
    public boolean isKYCVerified(UUID vendorId) {
        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        return vendor.getKycStatus() == VendorProfile.KYCStatus.VERIFIED;
    }

    /**
     * Get pending KYC submissions
     */
    @Transactional(readOnly = true)
    public List<VendorProfileDTO> getPendingKYCSubmissions() {
        log.debug("Fetching pending KYC submissions");
        List<VendorProfile> vendors = vendorRepository.findPendingKycVendors();
        return vendors.stream()
            .map(vendorMapper::toDTO)
            .toList();
    }

    /**
     * Get KYC status for vendor
     */
    @Transactional(readOnly = true)
    public String getKYCStatus(UUID vendorId) {
        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        return vendor.getKycStatus().toString();
    }

    /**
     * Get verified vendor count
     */
    @Transactional(readOnly = true)
    public long getVerifiedVendorCount() {
        return vendorRepository.countByKycStatus(VendorProfile.KYCStatus.VERIFIED);
    }

    /**
     * Get pending KYC count
     */
    @Transactional(readOnly = true)
    public long getPendingKYCCount() {
        return vendorRepository.countByKycStatus(VendorProfile.KYCStatus.PENDING) +
               vendorRepository.countByKycStatus(VendorProfile.KYCStatus.SUBMITTED);
    }

    /**
     * Get rejected KYC count
     */
    @Transactional(readOnly = true)
    public long getRejectedKYCCount() {
        return vendorRepository.countByKycStatus(VendorProfile.KYCStatus.REJECTED);
    }

    /**
     * Validate KYC documents
     */
    public boolean validateKYCDocuments(UUID vendorId) {
        log.debug("Validating KYC documents for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        // Check if required fields are present
        if (ValidationUtils.isNullOrEmpty(vendor.getBusinessLicenseNumber())) {
            log.warn("Business license number missing for vendor: {}", vendorId);
            return false;
        }

        if (ValidationUtils.isNullOrEmpty(vendor.getTaxId())) {
            log.warn("Tax ID missing for vendor: {}", vendorId);
            return false;
        }

        if (ValidationUtils.isNullOrEmpty(vendor.getBankAccountNumber())) {
            log.warn("Bank account number missing for vendor: {}", vendorId);
            return false;
        }

        if (ValidationUtils.isNullOrEmpty(vendor.getKycDocuments())) {
            log.warn("KYC documents missing for vendor: {}", vendorId);
            return false;
        }

        return true;
    }

    /**
     * Renew KYC verification (re-verify after expiry)
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public VendorProfileDTO renewKYCVerification(UUID vendorId) {
        log.info("Renewing KYC verification for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        vendor.setKycVerifiedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.info("KYC verification renewed for vendor: {}", vendorId);

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Check if KYC requirements are met
     */
    @Transactional(readOnly = true)
    public boolean areKYCRequirementsMet(UUID vendorId) {
        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        // All required fields must be present
        return validateKYCDocuments(vendorId) &&
               vendor.getKycStatus() == VendorProfile.KYCStatus.VERIFIED &&
               !vendor.getIsSuspended() &&
               vendor.getIsActive();
    }
}
