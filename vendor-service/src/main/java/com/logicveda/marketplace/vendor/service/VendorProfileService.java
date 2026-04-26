package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.dto.UpdateVendorProfileDTO;
import com.logicveda.marketplace.vendor.entity.VendorProfile;
import com.logicveda.marketplace.vendor.exception.VendorException;
import com.logicveda.marketplace.vendor.mapper.VendorProfileMapper;
import com.logicveda.marketplace.vendor.repository.VendorProfileRepository;
import com.logicveda.marketplace.vendor.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service layer for vendor profile management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VendorProfileService {

    private final VendorProfileRepository vendorRepository;
    private final VendorProfileMapper vendorMapper;

    /**
     * Register new vendor
     */
    public VendorProfileDTO registerVendor(VendorProfileDTO vendorDTO) {
        log.info("Registering new vendor: {}", vendorDTO.getBusinessName());

        // Validate input
        ValidationUtils.validateBusinessName(vendorDTO.getBusinessName());
        if (ValidationUtils.isNotNullOrEmpty(vendorDTO.getBusinessEmail())) {
            if (!ValidationUtils.isValidEmail(vendorDTO.getBusinessEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }
            // Check for duplicate email
            if (vendorRepository.findByBusinessEmail(vendorDTO.getBusinessEmail()).isPresent()) {
                throw VendorException.duplicateVendorEmail(vendorDTO.getBusinessEmail());
            }
        }

        // Check for duplicate tax ID
        if (ValidationUtils.isNotNullOrEmpty(vendorDTO.getTaxId())) {
            if (vendorRepository.findByTaxId(vendorDTO.getTaxId()).isPresent()) {
                throw VendorException.duplicateTaxId(vendorDTO.getTaxId());
            }
        }

        // Create new vendor
        VendorProfile vendor = vendorMapper.toEntity(vendorDTO);
        vendor.setId(UUID.randomUUID());
        vendor.setIsActive(true);
        vendor.setIsSuspended(false);
        vendor.setKycStatus(VendorProfile.KYCStatus.PENDING);
        vendor.setCreatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.info("Vendor registered successfully: {}", savedVendor.getId());

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Get vendor by ID
     */
    @Cacheable(value = "vendors-by-id", key = "#vendorId")
    @Transactional(readOnly = true)
    public VendorProfileDTO getVendorById(UUID vendorId) {
        log.debug("Fetching vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        return vendorMapper.toDTO(vendor);
    }

    /**
     * Get vendor by user ID
     */
    @Cacheable(value = "vendors-by-email", key = "#userId")
    @Transactional(readOnly = true)
    public VendorProfileDTO getVendorByUserId(UUID userId) {
        log.debug("Fetching vendor by user ID: {}", userId);

        VendorProfile vendor = vendorRepository.findByUserId(userId)
            .orElseThrow(() -> VendorException.userNotVendor(userId.toString()));

        return vendorMapper.toDTO(vendor);
    }

    /**
     * Update vendor profile
     */
    @CacheEvict(value = {"vendors-by-id", "vendors-by-email"}, allEntries = true)
    public VendorProfileDTO updateVendor(UUID vendorId, UpdateVendorProfileDTO updateDTO) {
        log.info("Updating vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        // Validate commission rate if updating
        if (updateDTO.getCommissionRate() != null) {
            ValidationUtils.validateCommissionRate(updateDTO.getCommissionRate());
            vendor.setCommissionRate(updateDTO.getCommissionRate());
        }

        vendor.setUpdatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.info("Vendor updated successfully: {}", vendorId);

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Verify vendor KYC
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public VendorProfileDTO verifyKYC(UUID vendorId) {
        log.info("Verifying KYC for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        if (vendor.getKycStatus() == VendorProfile.KYCStatus.REJECTED) {
            throw VendorException.kycInvalidStatus(vendorId.toString(), "REJECTED");
        }

        vendor.setKycStatus(VendorProfile.KYCStatus.VERIFIED);
        vendor.setKycVerifiedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.info("KYC verified for vendor: {}", vendorId);

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Suspend vendor
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public VendorProfileDTO suspendVendor(UUID vendorId, String reason) {
        log.warn("Suspending vendor: {} - Reason: {}", vendorId, reason);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        vendor.setIsSuspended(true);
        vendor.setSuspensionReason(reason);
        vendor.setUpdatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.warn("Vendor suspended: {}", vendorId);

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Unsuspend vendor
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public VendorProfileDTO unsuspendVendor(UUID vendorId) {
        log.info("Unsuspending vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        vendor.setIsSuspended(false);
        vendor.setSuspensionReason(null);
        vendor.setUpdatedAt(LocalDateTime.now());

        VendorProfile savedVendor = vendorRepository.save(vendor);
        log.info("Vendor unsuspended: {}", vendorId);

        return vendorMapper.toDTO(savedVendor);
    }

    /**
     * Get all active vendors (paginated)
     */
    @Transactional(readOnly = true)
    public Page<VendorProfileDTO> getActiveVendors(Pageable pageable) {
        log.debug("Fetching active vendors");
        return vendorRepository.findAllByIsActiveTrue(pageable)
            .map(vendorMapper::toDTO);
    }

    /**
     * Get verified vendors (paginated)
     */
    @Transactional(readOnly = true)
    public Page<VendorProfileDTO> getVerifiedVendors(Pageable pageable) {
        log.debug("Fetching verified vendors");
        return vendorRepository.findVerifiedVendorsByRating(pageable)
            .map(vendorMapper::toDTO);
    }

    /**
     * Get high-rated vendors
     */
    @Transactional(readOnly = true)
    public Page<VendorProfileDTO> getHighRatedVendors(BigDecimal minRating, Pageable pageable) {
        log.debug("Fetching high-rated vendors with minimum rating: {}", minRating);
        return vendorRepository.findHighRatedVendors(minRating, pageable)
            .map(vendorMapper::toDTO);
    }

    /**
     * Search vendors by business name
     */
    @Transactional(readOnly = true)
    public Page<VendorProfileDTO> searchVendorsByName(String businessName, Pageable pageable) {
        log.debug("Searching vendors by name: {}", businessName);
        return vendorRepository.findByBusinessNameContainingIgnoreCase(businessName, pageable)
            .map(vendorMapper::toDTO);
    }

    /**
     * Get vendors by city
     */
    @Transactional(readOnly = true)
    public Page<VendorProfileDTO> getVendorsByCity(String city, Pageable pageable) {
        log.debug("Fetching vendors in city: {}", city);
        return vendorRepository.findByBusinessCityIgnoreCaseAndIsActiveTrueAndIsSuspendedFalse(city, pageable)
            .map(vendorMapper::toDTO);
    }

    /**
     * Get top earning vendors
     */
    @Transactional(readOnly = true)
    public Page<VendorProfileDTO> getTopEarningVendors(Pageable pageable) {
        log.debug("Fetching top earning vendors");
        return vendorRepository.findTopEarningVendors(pageable)
            .map(vendorMapper::toDTO);
    }

    /**
     * Update vendor balance
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public void updateVendorBalance(UUID vendorId, BigDecimal amount, boolean isAddition) {
        log.debug("Updating vendor balance for: {} - Amount: {} - Addition: {}", vendorId, amount, isAddition);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        if (isAddition) {
            vendor.setAvailableBalance(vendor.getAvailableBalance().add(amount));
            vendor.setTotalEarnings(vendor.getTotalEarnings().add(amount));
        } else {
            if (vendor.getAvailableBalance().compareTo(amount) < 0) {
                throw VendorException.insufficientBalance(vendorId.toString());
            }
            vendor.setAvailableBalance(vendor.getAvailableBalance().subtract(amount));
        }

        vendor.setUpdatedAt(LocalDateTime.now());
        vendorRepository.save(vendor);
    }

    /**
     * Update vendor ratings
     */
    @CacheEvict(value = "vendors-by-id", key = "#vendorId")
    public void updateVendorRating(UUID vendorId, BigDecimal rating, int reviewCount) {
        log.debug("Updating vendor rating for: {} - Rating: {} - Reviews: {}", vendorId, rating, reviewCount);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        vendor.setAverageRating(rating);
        vendor.setTotalReviews(reviewCount);
        vendor.setUpdatedAt(LocalDateTime.now());

        vendorRepository.save(vendor);
    }

    /**
     * Check if vendor can sell products
     */
    @Transactional(readOnly = true)
    public boolean canVendorSellProducts(UUID vendorId) {
        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        return vendor.canSellProducts();
    }

    /**
     * Check if vendor exists
     */
    @Transactional(readOnly = true)
    public boolean vendorExists(UUID vendorId) {
        return vendorRepository.existsById(vendorId);
    }

    /**
     * Check if user is vendor
     */
    @Transactional(readOnly = true)
    public boolean isUserVendor(UUID userId) {
        return vendorRepository.existsByUserId(userId);
    }

    /**
     * Get total vendor count
     */
    @Transactional(readOnly = true)
    public long getTotalVendorCount() {
        return vendorRepository.countByIsActiveTrueAndIsSuspendedFalse();
    }
}
