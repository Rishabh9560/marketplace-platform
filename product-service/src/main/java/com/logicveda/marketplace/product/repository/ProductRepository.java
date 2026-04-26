package com.logicveda.marketplace.product.repository;

import com.logicveda.marketplace.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Find product by slug.
     */
    Optional<Product> findBySlug(String slug);

    /**
     * Find all products by vendor.
     */
    Page<Product> findByVendorIdAndStatus(UUID vendorId, Product.ProductStatus status, Pageable pageable);

    /**
     * Find all approved products by vendor.
     */
    Page<Product> findByVendorIdAndStatusIs(UUID vendorId, Product.ProductStatus status, Pageable pageable);

    /**
     * Find all featured products.
     */
    Page<Product> findByStatusAndIsFeaturedTrue(Product.ProductStatus status, Pageable pageable);

    /**
     * Find all products pending approval.
     */
    Page<Product> findByStatusOrderByCreatedAtDesc(Product.ProductStatus status, Pageable pageable);

    /**
     * Check if product slug exists.
     */
    boolean existsBySlug(String slug);

    /**
     * Find products by category.
     */
    Page<Product> findByCategoryIdAndStatus(UUID categoryId, Product.ProductStatus status, Pageable pageable);

    /**     * Find products by vendor ID with pagination.
     */
    Page<Product> findByVendorId(UUID vendorId, Pageable pageable);

    /**
     * Find products by vendor ID without pagination.
     */
    List<Product> findByVendorId(UUID vendorId);

    /**
     * Search products by name (case-insensitive) with pagination.
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**     * Search products by name or description.
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND (" +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchByQuery(String query, Pageable pageable);
}
