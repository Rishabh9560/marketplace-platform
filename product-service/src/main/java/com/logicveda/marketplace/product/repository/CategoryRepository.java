package com.logicveda.marketplace.product.repository;

import com.logicveda.marketplace.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Find category by slug.
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Find all root categories (no parent).
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.sortOrder")
    List<Category> findRootCategories();

    /**
     * Find active categories.
     */
    List<Category> findByIsActiveTrueOrderBySortOrder();

    /**
     * Check if slug exists.
     */
    boolean existsBySlug(String slug);
}
