package com.logicveda.marketplace.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicveda.marketplace.common.exception.BusinessException;
import com.logicveda.marketplace.common.exception.ResourceNotFoundException;
import com.logicveda.marketplace.product.dto.CategoryResponse;
import com.logicveda.marketplace.product.dto.CreateProductRequest;
import com.logicveda.marketplace.product.dto.CreateVariantRequest;
import com.logicveda.marketplace.product.dto.ImageResponse;
import com.logicveda.marketplace.product.dto.ProductResponse;
import com.logicveda.marketplace.product.dto.UpdateProductRequest;
import com.logicveda.marketplace.product.dto.UpdateVariantRequest;
import com.logicveda.marketplace.product.dto.VariantResponse;
import com.logicveda.marketplace.product.entity.*;
import com.logicveda.marketplace.product.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Product Service
 * Handles all product operations: CRUD, search, approval workflow, inventory management
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // ============= PRODUCT CREATION & MANAGEMENT =============

    /**
     * Create a new product (vendor creates in DRAFT status)
     */
    public ProductResponse createProduct(CreateProductRequest request, UUID vendorId) {
        log.info("Creating product for vendor: {}", vendorId);

        // Validate category exists
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categoryId()));

        // Check for duplicate slug
        String slug = generateSlug(request.name());
        productRepository.findBySlug(slug)
            .ifPresent(p -> {
                throw new BusinessException("Product with similar name already exists");
            });

        // Create product in DRAFT status
        Product product = new Product();
        product.setVendorId(vendorId);
        product.setCategoryId(category.getId());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setShortDescription(request.shortDescription());
        product.setBrand(request.brand());
        product.setSlug(slug);
        product.setTags(request.tags() != null ? request.tags().toArray(new String[0]) : new String[0]);
        product.setStatus(Product.ProductStatus.DRAFT);
        product.setIsFeatured(false);
        product.setAverageRating(BigDecimal.ZERO);
        product.setReviewCount(0);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        product = productRepository.save(product);
        
        // Create variants
        if (request.variants() != null && !request.variants().isEmpty()) {
            for (CreateVariantRequest variantRequest : request.variants()) {
                createVariantInternal(product.getId(), variantRequest, vendorId);
            }
        }
        
        log.info("Product created successfully: {} with ID: {}", product.getName(), product.getId());

        // Reload to get variants
        product = productRepository.findById(product.getId()).orElseThrow();
        return toProductResponse(product, category);
    }

    /**
     * Update product details (only by vendor)
     */
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request, UUID vendorId) {
        Product product = getProductByIdAndVendor(productId, vendorId);
        Category category = categoryRepository.findById(product.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Update fields
        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.shortDescription() != null) product.setShortDescription(request.shortDescription());
        if (request.brand() != null) product.setBrand(request.brand());
        if (request.categoryId() != null) {
            Category newCategory = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategoryId(newCategory.getId());
        }
        if (request.tags() != null) product.setTags(request.tags().toArray(new String[0]));
        if (request.name() != null) product.setSlug(generateSlug(request.name()));
        
        product.setUpdatedAt(LocalDateTime.now());
        product = productRepository.save(product);
        
        log.info("Product updated: {}", productId);
        return toProductResponse(product, category);
    }

    /**
     * Get product by ID and vendor (ownership check)
     */
    private Product getProductByIdAndVendor(UUID productId, UUID vendorId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getVendorId().equals(vendorId)) {
            throw new BusinessException("Unauthorized: You don't own this product");
        }

        return product;
    }

    /**
     * Get product by ID
     */
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        Category category = categoryRepository.findById(product.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return toProductResponse(product, category);
    }

    /**
     * Get vendor's products with pagination
     */
    public Page<ProductResponse> getVendorProducts(UUID vendorId, Pageable pageable) {
        Page<Product> products = productRepository.findByVendorId(vendorId, pageable);
        return products.map(product -> {
            Category category = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            return toProductResponse(product, category);
        });
    }

    /**
     * Get published products by category
     */
    public Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable) {
        Page<Product> products = productRepository.findByCategoryIdAndStatus(
            categoryId, Product.ProductStatus.APPROVED, pageable);
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        return products.map(product -> toProductResponse(product, category));
    }

    /**
     * Search products by keyword
     */
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return products.map(product -> {
            Category category = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            return toProductResponse(product, category);
        });
    }

    // ============= PRODUCT VARIANTS MANAGEMENT =============

    /**
     * Add variant to product
     */
    private void createVariantInternal(UUID productId, CreateVariantRequest request, UUID vendorId) {
        Product product = getProductByIdAndVendor(productId, vendorId);

        // Check SKU uniqueness
        productVariantRepository.findBySku(request.sku())
            .ifPresent(v -> {
                throw new BusinessException("SKU already exists: " + request.sku());
            });

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(request.sku());
        variant.setName(request.name());
        // Convert Map attributes to JsonNode if present
        if (request.attributes() != null) {
            variant.setAttributes(OBJECT_MAPPER.valueToTree(request.attributes()));
        }
        variant.setPrice(request.price());
        variant.setCompareAtPrice(request.compareAtPrice());
        variant.setCostPrice(request.costPrice());
        variant.setStockQuantity(request.stockQuantity());
        variant.setLowStockThreshold(request.lowStockThreshold());

        variant = productVariantRepository.save(variant);
        
        // Add images for this variant
        if (request.imageUrls() != null && !request.imageUrls().isEmpty()) {
            for (int i = 0; i < request.imageUrls().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setVariant(variant);
                image.setUrl(request.imageUrls().get(i));
                image.setSortOrder(i);
                image.setIsPrimary(i == 0);
                productImageRepository.save(image);
            }
        }

        log.info("Variant added to product: {} with SKU: {}", productId, request.sku());
    }

    /**
     * Update variant
     */
    public VariantResponse updateVariant(UUID variantId, UpdateVariantRequest request, UUID vendorId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        // Ownership check - get the product and check vendor
        Product product = productRepository.findById(variant.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getVendorId().equals(vendorId)) {
            throw new BusinessException("Unauthorized: You don't own this variant");
        }

        // Update inventory and price
        if (request.price() != null) variant.setPrice(request.price());
        if (request.compareAtPrice() != null) variant.setCompareAtPrice(request.compareAtPrice());
        if (request.stockQuantity() != null) variant.setStockQuantity(request.stockQuantity());
        if (request.name() != null) variant.setName(request.name());

        variant = productVariantRepository.save(variant);
        
        log.info("Variant updated: {}", variantId);
        return toVariantResponse(variant);
    }

    /**
     * Adjust variant inventory
     */
    public VariantResponse adjustInventory(UUID variantId, int quantity, UUID vendorId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        Product product = productRepository.findById(variant.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getVendorId().equals(vendorId)) {
            throw new BusinessException("Unauthorized: You don't own this variant");
        }

        int newQuantity = Math.max(0, variant.getStockQuantity() + quantity);
        variant.setStockQuantity(newQuantity);

        variant = productVariantRepository.save(variant);
        log.info("Inventory adjusted for variant: {} by: {}", variantId, quantity);

        return toVariantResponse(variant);
    }

    /**
     * Get variant by ID
     */
    public VariantResponse getVariantById(UUID variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        return toVariantResponse(variant);
    }

    /**
     * Get all variants for a product
     */
    public List<VariantResponse> getProductVariants(UUID productId) {
        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        return variants.stream()
            .map(this::toVariantResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get low stock variants for vendor
     */
    public List<VariantResponse> getLowStockVariants(UUID vendorId) {
        // Get all vendor's products
        List<Product> vendorProducts = productRepository.findByVendorId(vendorId);
        List<UUID> productIds = vendorProducts.stream().map(Product::getId).collect(Collectors.toList());
        
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Get low stock variants for these products
        List<ProductVariant> variants = productVariantRepository.findByProductIdInAndIsLowStockTrue(productIds);
        return variants.stream()
            .map(this::toVariantResponse)
            .collect(Collectors.toList());
    }

    // ============= PRODUCT IMAGES MANAGEMENT =============

    /**
     * Get product images
     */
    public List<ImageResponse> getProductImages(UUID productId) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrder(productId);
        return images.stream()
            .map(this::toImageResponse)
            .collect(Collectors.toList());
    }

    /**
     * Set primary image for product
     */
    public void setPrimaryImage(UUID imageId, UUID vendorId) {
        ProductImage image = productImageRepository.findById(imageId)
            .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        Product product = productRepository.findById(image.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getVendorId().equals(vendorId)) {
            throw new BusinessException("Unauthorized: You don't own this image");
        }

        // Remove primary flag from other images
        UUID productId = image.getProductId();
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrder(productId);
        images.forEach(img -> img.setIsPrimary(false));
        productImageRepository.saveAll(images);

        // Set this as primary
        image.setIsPrimary(true);
        productImageRepository.save(image);
    }

    /**
     * Delete image
     */
    public void deleteImage(UUID imageId, UUID vendorId) {
        ProductImage image = productImageRepository.findById(imageId)
            .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        Product product = productRepository.findById(image.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getVendorId().equals(vendorId)) {
            throw new BusinessException("Unauthorized: You don't own this image");
        }

        productImageRepository.delete(image);
        log.info("Image deleted: {}", imageId);
    }

    // ============= CATEGORY MANAGEMENT =============

    /**
     * Get all categories
     */
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
            .map(this::toCategoryResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    public CategoryResponse getCategoryById(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return toCategoryResponse(category);
    }

    // ============= UTILITY METHODS =============

    private String generateSlug(String name) {
        return name.toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
    }

    private ProductResponse toProductResponse(Product product, Category category) {
        List<VariantResponse> variants = productVariantRepository.findByProductId(product.getId()).stream()
            .map(this::toVariantResponse)
            .collect(Collectors.toList());
        
        List<ImageResponse> images = productImageRepository.findByProductIdOrderBySortOrder(product.getId()).stream()
            .map(this::toImageResponse)
            .collect(Collectors.toList());
        
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getSlug(),
            product.getDescription(),
            product.getBrand(),
            product.getVendorId(),
            category.getId(),
            category.getName(),
            product.getStatus().name(),
            product.getIsFeatured(),
            product.getAverageRating(),
            product.getReviewCount(),
            product.getTags(),
            variants,
            images,
            product.getCreatedAt().format(DATE_FORMATTER),
            product.getUpdatedAt().format(DATE_FORMATTER)
        );
    }

    private VariantResponse toVariantResponse(ProductVariant variant) {
        List<ProductImage> images = productImageRepository.findByVariantIdOrderBySortOrder(variant.getId());
        String[] imageUrls = images.stream()
            .map(ProductImage::getUrl)
            .toArray(String[]::new);
        
        return new VariantResponse(
            variant.getId(),
            variant.getSku(),
            variant.getName(),
            variant.getAttributes(),
            variant.getPrice(),
            variant.getCompareAtPrice(),
            variant.getStockQuantity(),
            variant.getStockQuantity() > 0,
            variant.getStockQuantity() <= variant.getLowStockThreshold(),
            imageUrls
        );
    }

    private ImageResponse toImageResponse(ProductImage image) {
        return new ImageResponse(
            image.getId(),
            image.getUrl(),
            image.getAltText(),
            image.getIsPrimary(),
            image.getSortOrder()
        );
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getSlug(),
            category.getDescription(),
            category.getImageUrl(),
            category.getIsActive(),
            category.getSortOrder()
        );
    }
}

