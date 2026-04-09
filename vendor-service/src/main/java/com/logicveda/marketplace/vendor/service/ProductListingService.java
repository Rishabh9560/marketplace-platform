package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.dto.ProductListingDTO;
import com.logicveda.marketplace.vendor.dto.PriceUpdateDTO;
import com.logicveda.marketplace.vendor.dto.InventoryUpdateDTO;
import com.logicveda.marketplace.vendor.entity.ProductListing;
import com.logicveda.marketplace.vendor.exception.VendorException;
import com.logicveda.marketplace.vendor.mapper.ProductListingMapper;
import com.logicveda.marketplace.vendor.repository.ProductListingRepository;
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
import java.util.List;
import java.util.UUID;

/**
 * Service layer for product listing management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductListingService {

    private final ProductListingRepository listingRepository;
    private final ProductListingMapper listingMapper;
    private final VendorProfileService vendorService;

    /**
     * Create new product listing
     */
    public ProductListingDTO createListing(ProductListingDTO listingDTO) {
        log.info("Creating product listing for vendor: {} - Product: {}", 
            listingDTO.getVendorId(), listingDTO.getProductName());

        // Validate vendor exists and can sell
        if (!vendorService.canVendorSellProducts(listingDTO.getVendorId())) {
            throw VendorException.kycNotVerified(listingDTO.getVendorId().toString());
        }

        // Validate input
        ValidationUtils.validateProductName(listingDTO.getProductName());
        ValidationUtils.validateSKU(listingDTO.getSku());
        ValidationUtils.validateCurrencyAmount(listingDTO.getVendorPrice());
        ValidationUtils.validateCurrencyAmount(listingDTO.getMarketplaceList());
        ValidationUtils.validatePriceComparison(listingDTO.getVendorPrice(), listingDTO.getMarketplaceList());
        ValidationUtils.validateInventoryQuantity(listingDTO.getQuantityAvailable());
        ValidationUtils.validateDiscountPercentage(listingDTO.getDiscountPercentage());

        // Create listing
        ProductListing listing = listingMapper.toEntity(listingDTO);
        listing.setId(UUID.randomUUID());
        listing.setStatus(ProductListing.ListingStatus.DRAFT);
        listing.setIsVisible(false);
        listing.setCreatedAt(LocalDateTime.now());

        ProductListing savedListing = listingRepository.save(listing);
        log.info("Product listing created: {}", savedListing.getId());

        return listingMapper.toDTO(savedListing);
    }

    /**
     * Get listing by ID
     */
    @Cacheable(value = "listings-by-id", key = "#listingId")
    @Transactional(readOnly = true)
    public ProductListingDTO getListingById(UUID listingId) {
        log.debug("Fetching listing: {}", listingId);

        ProductListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> VendorException.productListingNotFound(listingId.toString()));

        return listingMapper.toDTO(listing);
    }

    /**
     * Update listing
     */
    @CacheEvict(value = "listings-by-id", key = "#listingId")
    public ProductListingDTO updateListing(UUID listingId, ProductListingDTO updateDTO) {
        log.info("Updating listing: {}", listingId);

        ProductListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> VendorException.productListingNotFound(listingId.toString()));

        // Validate updates
        if (updateDTO.getProductName() != null) {
            ValidationUtils.validateProductName(updateDTO.getProductName());
        }
        if (updateDTO.getVendorPrice() != null) {
            ValidationUtils.validateCurrencyAmount(updateDTO.getVendorPrice());
        }
        if (updateDTO.getDiscountPercentage() != null) {
            ValidationUtils.validateDiscountPercentage(updateDTO.getDiscountPercentage());
        }

        // Update fields
        ProductListing updatedListing = listingMapper.updateEntityFromDTO(updateDTO, listing);
        updatedListing.setUpdatedAt(LocalDateTime.now());

        ProductListing savedListing = listingRepository.save(updatedListing);
        log.info("Listing updated: {}", listingId);

        return listingMapper.toDTO(savedListing);
    }

    /**
     * Publish listing
     */
    @CacheEvict(value = "listings-by-id", key = "#listingId")
    public ProductListingDTO publishListing(UUID listingId) {
        log.info("Publishing listing: {}", listingId);

        ProductListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> VendorException.productListingNotFound(listingId.toString()));

        if (listing.getStatus() != ProductListing.ListingStatus.DRAFT) {
            throw new IllegalArgumentException("Can only publish draft listings");
        }

        listing.setStatus(ProductListing.ListingStatus.ACTIVE);
        listing.setIsVisible(true);
        listing.setListedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());

        ProductListing savedListing = listingRepository.save(listing);
        log.info("Listing published: {}", listingId);

        return listingMapper.toDTO(savedListing);
    }

    /**
     * Delist listing
     */
    @CacheEvict(value = "listings-by-id", key = "#listingId")
    public ProductListingDTO delistListing(UUID listingId) {
        log.info("Delisting listing: {}", listingId);

        ProductListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> VendorException.productListingNotFound(listingId.toString()));

        listing.setStatus(ProductListing.ListingStatus.DELISTED);
        listing.setIsVisible(false);
        listing.setDelistedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());

        ProductListing savedListing = listingRepository.save(listing);
        log.info("Listing delisted: {}", listingId);

        return listingMapper.toDTO(savedListing);
    }

    /**
     * Update inventory
     */
    @CacheEvict(value = "listings-by-id", key = "#listingId")
    public ProductListingDTO updateInventory(UUID listingId, InventoryUpdateDTO inventoryDTO) {
        log.info("Updating inventory for listing: {} - Quantity: {}", listingId, inventoryDTO.getQuantityAvailable());

        ProductListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> VendorException.productListingNotFound(listingId.toString()));

        ValidationUtils.validateInventoryQuantity(inventoryDTO.getQuantityAvailable());

        listing.setQuantityAvailable(inventoryDTO.getQuantityAvailable());
        if (inventoryDTO.getReorderLevel() != null) {
            listing.setReorderLevel(inventoryDTO.getReorderLevel());
        }
        if (inventoryDTO.getReorderQuantity() != null) {
            listing.setReorderQuantity(inventoryDTO.getReorderQuantity());
        }
        listing.setUpdatedAt(LocalDateTime.now());

        ProductListing savedListing = listingRepository.save(listing);
        log.info("Inventory updated for listing: {}", listingId);

        return listingMapper.toDTO(savedListing);
    }

    /**
     * Update price
     */
    @CacheEvict(value = "listings-by-id", key = "#listingId")
    public ProductListingDTO updatePrice(UUID listingId, PriceUpdateDTO priceDTO) {
        log.info("Updating price for listing: {} - Price: {}", listingId, priceDTO.getVendorPrice());

        ProductListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> VendorException.productListingNotFound(listingId.toString()));

        ValidationUtils.validateCurrencyAmount(priceDTO.getVendorPrice());
        ValidationUtils.validateDiscountPercentage(priceDTO.getDiscountPercentage());

        listing.setVendorPrice(priceDTO.getVendorPrice());
        if (priceDTO.getDiscountPercentage() != null) {
            listing.setDiscountPercentage(priceDTO.getDiscountPercentage());
        }
        if (priceDTO.getShippingCost() != null) {
            listing.setShippingCost(priceDTO.getShippingCost());
        }
        if (priceDTO.getFreeShipping() != null) {
            listing.setFreeShipping(priceDTO.getFreeShipping());
        }
        listing.setUpdatedAt(LocalDateTime.now());

        ProductListing savedListing = listingRepository.save(listing);
        log.info("Price updated for listing: {}", listingId);

        return listingMapper.toDTO(savedListing);
    }

    /**
     * Get vendor's listings
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> getVendorListings(UUID vendorId, Pageable pageable) {
        log.debug("Fetching listings for vendor: {}", vendorId);
        return listingRepository.findByVendorId(vendorId, pageable)
            .map(listingMapper::toDTO);
    }

    /**
     * Get vendor's active listings
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> getVendorActiveListings(UUID vendorId, Pageable pageable) {
        log.debug("Fetching active listings for vendor: {}", vendorId);
        return listingRepository.findByVendorIdAndStatusAndIsVisibleTrue(
            vendorId,
            ProductListing.ListingStatus.ACTIVE,
            pageable
        ).map(listingMapper::toDTO);
    }

    /**
     * Get active marketplace listings
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> getActiveMarketplaceListings(Pageable pageable) {
        log.debug("Fetching active marketplace listings");
        return listingRepository.findByStatusAndIsVisibleTrue(
            ProductListing.ListingStatus.ACTIVE,
            pageable
        ).map(listingMapper::toDTO);
    }

    /**
     * Search listings by product name
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> searchListings(String productName, Pageable pageable) {
        log.debug("Searching listings: {}", productName);
        return listingRepository.findByProductNameContainingIgnoreCaseAndStatusAndIsVisibleTrue(
            productName,
            ProductListing.ListingStatus.ACTIVE,
            pageable
        ).map(listingMapper::toDTO);
    }

    /**
     * Get discounted listings
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> getDiscountedListings(Pageable pageable) {
        log.debug("Fetching discounted listings");
        return listingRepository.findDiscountedListings(pageable)
            .map(listingMapper::toDTO);
    }

    /**
     * Get listings in price range
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> getListingsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching listings in price range: {} - {}", minPrice, maxPrice);
        return listingRepository.findByPriceRange(minPrice, maxPrice, pageable)
            .map(listingMapper::toDTO);
    }

    /**
     * Get available listings for product
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> getAvailableListingsForProduct(UUID productId, Pageable pageable) {
        log.debug("Fetching available listings for product: {}", productId);
        return listingRepository.findBestListingsByProduct(productId, pageable)
            .map(listingMapper::toDTO);
    }

    /**
     * Get low stock listings for vendor
     */
    @Transactional(readOnly = true)
    public List<ProductListingDTO> getLowStockListings(UUID vendorId) {
        log.debug("Fetching low stock listings for vendor: {}", vendorId);
        List<ProductListing> listings = listingRepository.findLowStockListings(vendorId);
        return listings.stream()
            .map(listingMapper::toDTO)
            .toList();
    }

    /**
     * Get vendor's best selling listings
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> getVendorBestSelling(UUID vendorId, Pageable pageable) {
        log.debug("Fetching best selling listings for vendor: {}", vendorId);
        return listingRepository.findBestSellingByVendor(vendorId, pageable)
            .map(listingMapper::toDTO);
    }

    /**
     * Get vendor's top rated listings
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> getVendorTopRated(UUID vendorId, Pageable pageable) {
        log.debug("Fetching top rated listings for vendor: {}", vendorId);
        return listingRepository.findTopRatedByVendor(vendorId, pageable)
            .map(listingMapper::toDTO);
    }

    /**
     * Get popular listings
     */
    @Transactional(readOnly = true)
    public Page<ProductListingDTO> getPopularListings(Pageable pageable) {
        log.debug("Fetching popular listings");
        return listingRepository.findPopularListings(pageable)
            .map(listingMapper::toDTO);
    }

    /**
     * Reserve inventory
     */
    @CacheEvict(value = "listings-by-id", key = "#listingId")
    public void reserveInventory(UUID listingId, int quantity) {
        log.debug("Reserving inventory for listing: {} - Quantity: {}", listingId, quantity);

        ProductListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> VendorException.productListingNotFound(listingId.toString()));

        if (listing.getQuantityAvailable() < quantity) {
            throw new IllegalArgumentException("Insufficient inventory");
        }

        listing.setQuantityReserved(listing.getQuantityReserved() + quantity);
        listing.setUpdatedAt(LocalDateTime.now());
        listingRepository.save(listing);
    }

    /**
     * Release inventory reservation
     */
    @CacheEvict(value = "listings-by-id", key = "#listingId")
    public void releaseInventory(UUID listingId, int quantity) {
        log.debug("Releasing inventory for listing: {} - Quantity: {}", listingId, quantity);

        ProductListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> VendorException.productListingNotFound(listingId.toString()));

        int newReserved = Math.max(0, listing.getQuantityReserved() - quantity);
        listing.setQuantityReserved(newReserved);
        listing.setUpdatedAt(LocalDateTime.now());
        listingRepository.save(listing);
    }

    /**
     * Increment view count
     */
    @CacheEvict(value = "listings-by-id", key = "#listingId")
    public void incrementViewCount(UUID listingId) {
        ProductListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> VendorException.productListingNotFound(listingId.toString()));

        listing.setViewCount(listing.getViewCount() + 1);
        listing.setLastViewedAt(LocalDateTime.now());
        listingRepository.save(listing);
    }

    /**
     * Get total inventory for vendor
     */
    @Transactional(readOnly = true)
    public Integer getTotalInventoryForVendor(UUID vendorId) {
        Integer total = listingRepository.getTotalInventoryByVendor(vendorId);
        return total != null ? total : 0;
    }

    /**
     * Get total sales for vendor
     */
    @Transactional(readOnly = true)
    public Integer getTotalSalesForVendor(UUID vendorId) {
        Integer total = listingRepository.getTotalSalesByVendor(vendorId);
        return total != null ? total : 0;
    }

    /**
     * Count out of stock listings
     */
    @Transactional(readOnly = true)
    public long countOutOfStockListings(UUID vendorId) {
        return listingRepository.countOutOfStockListings(vendorId);
    }
}
