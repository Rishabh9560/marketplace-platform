package com.logicveda.marketplace.vendor.repository;

import com.logicveda.marketplace.vendor.entity.VendorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VendorProfile entity
 */
@Repository
public interface VendorProfileRepository extends JpaRepository<VendorProfile, UUID> {

    /**
     * Find vendor by user ID
     */
    Optional<VendorProfile> findByUserId(UUID userId);

    /**
     * Check if user is registered as vendor
     */
    boolean existsByUserId(UUID userId);

    /**
     * Find active vendors (not suspended)
     */
    Page<VendorProfile> findAllByIsActiveTrue(Pageable pageable);

    /**
     * Find vendors with KYC verification
     */
    Page<VendorProfile> findAllByKycStatusAndIsActiveTrueAndIsSuspendedFalse(
        VendorProfile.KYCStatus kycStatus,
        Pageable pageable
    );

    /**
     * Find vendors by KYC status
     */
    List<VendorProfile> findAllByKycStatus(VendorProfile.KYCStatus kycStatus);

    /**
     * Find suspended vendors
     */
    List<VendorProfile> findAllByIsSuspendedTrue();

    /**
     * Find vendors by business email
     */
    Optional<VendorProfile> findByBusinessEmail(String businessEmail);

    /**
     * Find vendors by business name (like search)
     */
    Page<VendorProfile> findByBusinessNameContainingIgnoreCase(String businessName, Pageable pageable);

    /**
     * Find vendors by tax ID
     */
    Optional<VendorProfile> findByTaxId(String taxId);

    /**
     * Find high-rated vendors
     */
    @Query("SELECT v FROM VendorProfile v WHERE v.averageRating >= :minRating " +
           "AND v.isActive = true AND v.isSuspended = false " +
           "AND v.kycStatus = 'VERIFIED' ORDER BY v.averageRating DESC")
    Page<VendorProfile> findHighRatedVendors(@Param("minRating") BigDecimal minRating, Pageable pageable);

    /**
     * Find vendors by city
     */
    Page<VendorProfile> findByBusinessCityIgnoreCaseAndIsActiveTrueAndIsSuspendedFalse(
        String city, 
        Pageable pageable
    );

    /**
     * Get vendor count by KYC status
     */
    long countByKycStatus(VendorProfile.KYCStatus kycStatus);

    /**
     * Get count of active vendors
     */
    long countByIsActiveTrueAndIsSuspendedFalse();

    /**
     * Find pending KYC vendors
     */
    @Query("SELECT v FROM VendorProfile v WHERE v.kycStatus = 'PENDING' OR v.kycStatus = 'SUBMITTED' " +
           "ORDER BY v.createdAt ASC")
    List<VendorProfile> findPendingKycVendors();

    /**
     * Find vendors with available balance
     */
    @Query("SELECT v FROM VendorProfile v WHERE v.availableBalance > 0 " +
           "AND v.isActive = true AND v.isSuspended = false")
    List<VendorProfile> findVendorsWithAvailableBalance();

    /**
     * Find top earning vendors
     */
    @Query("SELECT v FROM VendorProfile v WHERE v.isActive = true " +
           "ORDER BY v.totalEarnings DESC")
    Page<VendorProfile> findTopEarningVendors(Pageable pageable);

    /**
     * Find vendors by business country
     */
    Page<VendorProfile> findByBusinessCountryIgnoreCaseAndIsActiveTrueAndIsSuspendedFalse(
        String country,
        Pageable pageable
    );

    /**
     * Get total earnings across all vendors
     */
    @Query("SELECT SUM(v.totalEarnings) FROM VendorProfile v")
    Long getTotalVendorEarnings();

    /**
     * Find verified vendors by average rating
     */
    @Query("SELECT v FROM VendorProfile v WHERE v.kycStatus = 'VERIFIED' " +
           "AND v.isActive = true AND v.isSuspended = false " +
           "ORDER BY v.averageRating DESC, v.totalReviews DESC")
    Page<VendorProfile> findVerifiedVendorsByRating(Pageable pageable);

    /**
     * Find vendors by KYC status with pagination
     */
    Page<VendorProfile> findByKycStatus(VendorProfile.KYCStatus kycStatus, Pageable pageable);

    /**
     * Find vendors by multiple KYC statuses with pagination
     */
    Page<VendorProfile> findByKycStatusIn(List<VendorProfile.KYCStatus> kycStatuses, Pageable pageable);
}
