package com.logicveda.marketplace.product.repository;

import com.logicveda.marketplace.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for ProductImage entity.
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    /**
     * Find all images for a product, ordered by sort order.
     */
    List<ProductImage> findByProductIdOrderBySortOrder(UUID productId);

    /**
     * Find primary image for a product.
     */
    ProductImage findByProductIdAndIsPrimaryTrue(UUID productId);

    /**
     * Find all images for a variant.
     */
    List<ProductImage> findByVariantIdOrderBySortOrder(UUID variantId);
}
