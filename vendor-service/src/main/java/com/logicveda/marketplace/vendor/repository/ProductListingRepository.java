package com.logicveda.marketplace.vendor.repository;

import com.logicveda.marketplace.vendor.entity.ProductListing;
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
 * Repository for ProductListing entity
 */
@Repository
public interface ProductListingRepository extends JpaRepository<ProductListing, UUID> {

    /**
     * Find all products by vendor
     */
    Page<ProductListing> findByVendorId(UUID vendorId, Pageable pageable);

    /**
     * Find active products by vendor
     */
    Page<ProductListing> findByVendorIdAndStatusAndIsVisibleTrue(
        UUID vendorId,
        ProductListing.ListingStatus status,
        Pageable pageable
    );

    /**
     * Find product listing by vendor and product ID
     */
    Optional<ProductListing> findByVendorIdAndProductId(UUID vendorId, UUID productId);

    /**
     * Find product listings in marketplace
     */
    Page<ProductListing> findByStatusAndIsVisibleTrue(
        ProductListing.ListingStatus status,
        Pageable pageable
    );

    /**
     * Find product by SKU
     */
    Optional<ProductListing> findByVendorIdAndSku(UUID vendorId, String sku);

    /**
     * Find all listings for a product (across all vendors)
     */
    Page<ProductListing> findByProductIdAndStatusAndIsVisibleTrue(
        UUID productId,
        ProductListing.ListingStatus status,
        Pageable pageable
    );

    /**
     * Count active listings by vendor
     */
    long countByVendorIdAndStatus(UUID vendorId, ProductListing.ListingStatus status);

    /**
     * Find listings by vendor with low stock
     */
    @Query("SELECT pl FROM ProductListing pl WHERE pl.vendorId = :vendorId " +
           "AND pl.quantityAvailable <= pl.reorderLevel")
    List<ProductListing> findLowStockListings(@Param("vendorId") UUID vendorId);

    /**
     * Find highlighted products
     */
    Page<ProductListing> findByIsHighlightedTrueAndStatusAndIsVisibleTrue(
        ProductListing.ListingStatus status,
        Pageable pageable
    );

    /**
     * Find best-selling listings
     */
    @Query("SELECT pl FROM ProductListing pl WHERE pl.vendorId = :vendorId " +
           "ORDER BY pl.totalSales DESC")
    Page<ProductListing> findBestSellingByVendor(@Param("vendorId") UUID vendorId, Pageable pageable);

    /**
     * Find top-rated listings by vendor
     */
    @Query("SELECT pl FROM ProductListing pl WHERE pl.vendorId = :vendorId " +
           "AND pl.status = 'ACTIVE' AND pl.isVisible = true " +
           "ORDER BY pl.vendorRating DESC, pl.totalReviews DESC")
    Page<ProductListing> findTopRatedByVendor(@Param("vendorId") UUID vendorId, Pageable pageable);

    /**
     * Search listings by product name
     */
    Page<ProductListing> findByProductNameContainingIgnoreCaseAndStatusAndIsVisibleTrue(
        String productName,
        ProductListing.ListingStatus status,
        Pageable pageable
    );

    /**
     * Find listings with discounts
     */
    @Query("SELECT pl FROM ProductListing pl WHERE pl.discountPercentage > 0 " +
           "AND pl.status = 'ACTIVE' AND pl.isVisible = true")
    Page<ProductListing> findDiscountedListings(Pageable pageable);

    /**
     * Find listings within price range
     */
    @Query("SELECT pl FROM ProductListing pl WHERE pl.vendorPrice >= :minPrice " +
           "AND pl.vendorPrice <= :maxPrice AND pl.status = 'ACTIVE' AND pl.isVisible = true")
    Page<ProductListing> findByPriceRange(
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );

    /**
     * Get total inventory by vendor
     */
    @Query("SELECT SUM(pl.quantityAvailable) FROM ProductListing pl WHERE pl.vendorId = :vendorId " +
           "AND pl.status = 'ACTIVE'")
    Integer getTotalInventoryByVendor(@Param("vendorId") UUID vendorId);

    /**
     * Find products with stock available
     */
    @Query("SELECT pl FROM ProductListing pl WHERE pl.vendorId = :vendorId " +
           "AND pl.quantityAvailable > 0 AND pl.status = 'ACTIVE' AND pl.isVisible = true")
    Page<ProductListing> findInStockListings(@Param("vendorId") UUID vendorId, Pageable pageable);

    /**
     * Count out of stock listings by vendor
     */
    @Query("SELECT COUNT(pl) FROM ProductListing pl WHERE pl.vendorId = :vendorId " +
           "AND pl.quantityAvailable = 0 AND pl.status = 'ACTIVE'")
    long countOutOfStockListings(@Param("vendorId") UUID vendorId);

    /**
     * Find recently viewed/popular products
     */
    @Query("SELECT pl FROM ProductListing pl WHERE pl.status = 'ACTIVE' " +
           "AND pl.isVisible = true ORDER BY pl.viewCount DESC, pl.lastViewedAt DESC")
    Page<ProductListing> findPopularListings(Pageable pageable);

    /**
     * Find listings by product ID and vendor rating
     */
    @Query("SELECT pl FROM ProductListing pl WHERE pl.productId = :productId " +
           "AND pl.status = 'ACTIVE' AND pl.isVisible = true " +
           "ORDER BY pl.vendorRating DESC")
    Page<ProductListing> findBestListingsByProduct(@Param("productId") UUID productId, Pageable pageable);

    /**
     * Get total sales by vendor
     */
    @Query("SELECT SUM(pl.totalSales) FROM ProductListing pl WHERE pl.vendorId = :vendorId")
    Integer getTotalSalesByVendor(@Param("vendorId") UUID vendorId);

    /**
     * Find free shipping listings
     */
    Page<ProductListing> findByFreeShippingTrueAndStatusAndIsVisibleTrue(
        ProductListing.ListingStatus status,
        Pageable pageable
    );
}
