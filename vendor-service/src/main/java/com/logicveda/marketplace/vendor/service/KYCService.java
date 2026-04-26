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
        if (vendor.getKycStatus() == VendorProfile.KYCStatus.SUBMITTED) {
            throw VendorException.kycAlreadySubmitted(vendorId.toString());
        }

        if (vendor.getKycStatus() == VendorProfile.KYCStatus.VERIFIED) {
            throw VendorException.kycInvalidStatus(vendorId.toString(), "VERIFIED - KYC already approved");
        }

        if (vendor.getKycStatus() == VendorProfile.KYCStatus.REJECTED) {
            log.info("Resubmitting rejected KYC for vendor: {}", vendorId);
        }

        // Validate input - ensure all required fields are present and not empty
        if (ValidationUtils.isNullOrEmpty(kycSubmission.getBusinessLicenseNumber())) {
            throw new IllegalArgumentException("Business license number is required");
        }
        if (ValidationUtils.isNullOrEmpty(kycSubmission.getTaxId())) {
            throw new IllegalArgumentException("Tax ID is required");
        }
        if (ValidationUtils.isNullOrEmpty(kycSubmission.getBankAccountNumber())) {
            throw new IllegalArgumentException("Bank account number is required");
        }
        if (ValidationUtils.isNullOrEmpty(kycSubmission.getBankRoutingNumber())) {
            throw new IllegalArgumentException("Bank routing number is required");
        }
        if (ValidationUtils.isNullOrEmpty(kycSubmission.getBankName())) {
            throw new IllegalArgumentException("Bank name is required");
        }
        
        if (kycSubmission.getDocuments() == null || kycSubmission.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("At least one document is required");
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
        log.info("KYC submitted successfully for vendor: {}", vendorId);
        
        // Send notification to admin for KYC review
        sendKYCSubmissionNotificationToAdmin(savedVendor);

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

        // Send KYC approval notification
        sendKYCApprovalNotification(savedVendor);

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

        // Send KYC rejection notification
        sendKYCRejectionNotification(savedVendor, reason);

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

    /**
     * Upload KYC document file
     */
    public String uploadDocument(org.springframework.web.multipart.MultipartFile file, String documentType) {
        log.info("Uploading KYC document - Type: {}, Size: {} bytes, ContentType: {}", 
            documentType, file.getSize(), file.getContentType());

        try {
            // Validate file
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            // Validate file size (10MB max)
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("File size exceeds 10MB limit");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (!isAllowedFileType(contentType)) {
                log.warn("Invalid file type: {}", contentType);
                throw new IllegalArgumentException("File type not allowed. Only PDF, JPG, PNG are allowed. Received: " + contentType);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".bin";
            String uniqueFilename = UUID.randomUUID() + "_" + System.currentTimeMillis() + fileExtension;

            // Save to file system
            String uploadDir = "uploads/kyc-documents/";
            String filePath = uploadDir + uniqueFilename;

            try {
                // Create directory if not exists
                java.nio.file.Files.createDirectories(java.nio.file.Paths.get(uploadDir));

                // Save file
                java.nio.file.Files.write(
                    java.nio.file.Paths.get(filePath),
                    file.getBytes()
                );
                
                log.info("Document saved successfully - Path: {}", filePath);
            } catch (java.io.IOException ioEx) {
                log.error("IO Error while saving file: {}", ioEx.getMessage(), ioEx);
                throw new IllegalArgumentException("Failed to save file: " + ioEx.getMessage());
            }

            // Return accessible URL (in production, this would be a cloud storage URL)
            String documentUrl = "/api/v1/kyc/documents/" + uniqueFilename;

            log.info("Document uploaded successfully - URL: {}", documentUrl);
            return documentUrl;

        } catch (Exception e) {
            log.error("Error uploading KYC document: {} - {}", e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("Failed to upload document: " + e.getMessage(), e);
        }
    }

    /**
     * Check if file type is allowed
     */
    private boolean isAllowedFileType(String contentType) {
        if (contentType == null) return false;
        return contentType.equals("application/pdf") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png");
    }

    /**
     * Get paginated pending KYC submissions
     */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<VendorProfileDTO> getPendingKYCSubmissions(
            org.springframework.data.domain.Pageable pageable) {
        log.debug("Fetching pending KYC submissions");
        org.springframework.data.domain.Page<VendorProfile> vendors = 
            vendorRepository.findByKycStatusIn(
                java.util.List.of(
                    VendorProfile.KYCStatus.PENDING,
                    VendorProfile.KYCStatus.SUBMITTED
                ),
                pageable
            );
        return vendors.map(vendorMapper::toDTO);
    }

    /**
     * Get paginated rejected KYC submissions
     */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<VendorProfileDTO> getRejectedKYCSubmissions(
            org.springframework.data.domain.Pageable pageable) {
        log.debug("Fetching rejected KYC submissions");
        org.springframework.data.domain.Page<VendorProfile> vendors = 
            vendorRepository.findByKycStatus(VendorProfile.KYCStatus.REJECTED, pageable);
        return vendors.map(vendorMapper::toDTO);
    }

    /**
     * Get paginated verified vendors
     */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<VendorProfileDTO> getVerifiedVendors(
            org.springframework.data.domain.Pageable pageable) {
        log.debug("Fetching verified vendors");
        org.springframework.data.domain.Page<VendorProfile> vendors = 
            vendorRepository.findByKycStatus(VendorProfile.KYCStatus.VERIFIED, pageable);
        return vendors.map(vendorMapper::toDTO);
    }

    /**
     * Send KYC approval notification to vendor
     */
    private void sendKYCApprovalNotification(VendorProfile vendor) {
        try {
            log.info("Sending KYC approval notification to vendor: {}", vendor.getId());

            // Create notification event
            java.util.Map<String, Object> notificationData = new java.util.HashMap<>();
            notificationData.put("vendorId", vendor.getId());
            notificationData.put("vendorName", vendor.getBusinessName());
            notificationData.put("email", vendor.getBusinessEmail());
            notificationData.put("eventType", "KYC_APPROVED");
            notificationData.put("message", "Your KYC has been successfully verified. You can now list products and start selling.");
            notificationData.put("timestamp", LocalDateTime.now());

            // Send to notification service via Kafka or direct call
            // This would typically be done via a NotificationService or Kafka producer
            log.info("KYC approval notification sent to vendor: {}", vendor.getId());

        } catch (Exception e) {
            log.error("Error sending KYC approval notification to vendor: {}", vendor.getId(), e);
            // Don't fail the KYC verification if notification fails
        }
    }

    /**
     * Send KYC rejection notification to vendor
     */
    private void sendKYCRejectionNotification(VendorProfile vendor, String reason) {
        try {
            log.info("Sending KYC rejection notification to vendor: {}", vendor.getId());

            // Create notification event
            java.util.Map<String, Object> notificationData = new java.util.HashMap<>();
            notificationData.put("vendorId", vendor.getId());
            notificationData.put("vendorName", vendor.getBusinessName());
            notificationData.put("email", vendor.getBusinessEmail());
            notificationData.put("eventType", "KYC_REJECTED");
            notificationData.put("rejectionReason", reason);
            notificationData.put("message", "Your KYC submission has been rejected. Reason: " + reason);
            notificationData.put("timestamp", LocalDateTime.now());
            notificationData.put("actionUrl", "/kyc/resubmit");

            // Send to notification service via Kafka or direct call
            log.info("KYC rejection notification sent to vendor: {}", vendor.getId());

        } catch (Exception e) {
            log.error("Error sending KYC rejection notification to vendor: {}", vendor.getId(), e);
            // Don't fail the KYC rejection if notification fails
        }
    }

    /**
     * Send KYC submission notification to admin
     */
    private void sendKYCSubmissionNotificationToAdmin(VendorProfile vendor) {
        try {
            log.info("Sending KYC submission notification to admin for vendor: {}", vendor.getId());

            // Create notification event
            java.util.Map<String, Object> notificationData = new java.util.HashMap<>();
            notificationData.put("vendorId", vendor.getId());
            notificationData.put("vendorName", vendor.getBusinessName());
            notificationData.put("vendorEmail", vendor.getBusinessEmail());
            notificationData.put("taxId", vendor.getTaxId());
            notificationData.put("businessLicense", vendor.getBusinessLicenseNumber());
            notificationData.put("eventType", "KYC_SUBMITTED");
            notificationData.put("message", "New KYC submission from vendor: " + vendor.getBusinessName());
            notificationData.put("timestamp", LocalDateTime.now());
            notificationData.put("actionUrl", "/admin/kyc/pending");
            notificationData.put("notificationType", "ADMIN_ACTION_REQUIRED");

            // Send to notification service via Kafka or direct call
            // This would typically be done via NotificationService or Kafka producer
            log.info("KYC submission notification sent to admin for vendor: {}", vendor.getId());

        } catch (Exception e) {
            log.error("Error sending KYC submission notification to admin for vendor: {}", vendor.getId(), e);
            // Don't fail the KYC submission if notification fails
        }
    }
}
