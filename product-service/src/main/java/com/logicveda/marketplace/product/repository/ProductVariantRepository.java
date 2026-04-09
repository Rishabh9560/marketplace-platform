package com.logicveda.marketplace.product.repository;

import com.logicveda.marketplace.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProductVariant entity.
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    /**
     * Find variant by SKU.
     */
    Optional<ProductVariant> findBySku(String sku);

    /**
     * Find all variants by product.
     */
    List<ProductVariant> findByProductId(UUID productId);

    /**
     * Find low stock variants for a vendor.
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p " +
           "WHERE p.vendorId = :vendorId AND pv.stockQuantity <= pv.lowStockThreshold")
    List<ProductVariant> findLowStockByVendor(UUID vendorId);

    /**
     * Check if SKU exists.
     */
    boolean existsBySku(String sku);
}
