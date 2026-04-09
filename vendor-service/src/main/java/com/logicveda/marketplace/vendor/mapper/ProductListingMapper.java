package com.logicveda.marketplace.vendor.mapper;

import com.logicveda.marketplace.vendor.dto.ProductListingDTO;
import com.logicveda.marketplace.vendor.entity.ProductListing;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for ProductListing entity and DTO conversions
 */
@Component
public class ProductListingMapper {

    /**
     * Convert ProductListing entity to DTO
     */
    public ProductListingDTO toDTO(ProductListing entity) {
        if (entity == null) {
            return null;
        }
        return ProductListingDTO.builder()
            .id(entity.getId())
            .vendorId(entity.getVendorId())
            .productId(entity.getProductId())
            .productName(entity.getProductName())
            .productDescription(entity.getProductDescription())
            .sku(entity.getSku())
            .vendorPrice(entity.getVendorPrice())
            .marketplaceList(entity.getMarketplaceList())
            .discountPercentage(entity.getDiscountPercentage())
            .quantityAvailable(entity.getQuantityAvailable())
            .quantityReserved(entity.getQuantityReserved())
            .reorderLevel(entity.getReorderLevel())
            .reorderQuantity(entity.getReorderQuantity())
            .status(entity.getStatus().toString())
            .isVisible(entity.getIsVisible())
            .isHighlighted(entity.getIsHighlighted())
            .shippingCost(entity.getShippingCost())
            .freeShipping(entity.getFreeShipping())
            .shippingDays(entity.getShippingDays())
            .vendorRating(entity.getVendorRating())
            .totalSales(entity.getTotalSales())
            .totalReviews(entity.getTotalReviews())
            .images(entity.getImages())
            .features(entity.getFeatures())
            .viewCount(entity.getViewCount())
            .favoriteCount(entity.getFavoriteCount())
            .lastViewedAt(entity.getLastViewedAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .listedAt(entity.getListedAt())
            .delistedAt(entity.getDelistedAt())
            .build();
    }

    /**
     * Convert ProductListing DTO to entity
     */
    public ProductListing toEntity(ProductListingDTO dto) {
        if (dto == null) {
            return null;
        }
        return ProductListing.builder()
            .id(dto.getId())
            .vendorId(dto.getVendorId())
            .productId(dto.getProductId())
            .productName(dto.getProductName())
            .productDescription(dto.getProductDescription())
            .sku(dto.getSku())
            .vendorPrice(dto.getVendorPrice())
            .marketplaceList(dto.getMarketplaceList())
            .discountPercentage(dto.getDiscountPercentage())
            .quantityAvailable(dto.getQuantityAvailable())
            .quantityReserved(dto.getQuantityReserved())
            .reorderLevel(dto.getReorderLevel())
            .reorderQuantity(dto.getReorderQuantity())
            .status(dto.getStatus() != null ? 
                ProductListing.ListingStatus.valueOf(dto.getStatus()) : 
                ProductListing.ListingStatus.DRAFT)
            .isVisible(dto.getIsVisible())
            .isHighlighted(dto.getIsHighlighted())
            .shippingCost(dto.getShippingCost())
            .freeShipping(dto.getFreeShipping())
            .shippingDays(dto.getShippingDays())
            .vendorRating(dto.getVendorRating())
            .totalSales(dto.getTotalSales())
            .totalReviews(dto.getTotalReviews())
            .images(dto.getImages())
            .features(dto.getFeatures())
            .viewCount(dto.getViewCount())
            .favoriteCount(dto.getFavoriteCount())
            .lastViewedAt(dto.getLastViewedAt())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .listedAt(dto.getListedAt())
            .delistedAt(dto.getDelistedAt())
            .build();
    }

    /**
     * Convert list of entities to DTOs
     */
    public List<ProductListingDTO> toDTOList(List<ProductListing> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convert list of DTOs to entities
     */
    public List<ProductListing> toEntityList(List<ProductListingDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    /**
     * Update entity from DTO (for partial updates)
     */
    public ProductListing updateEntityFromDTO(ProductListingDTO dto, ProductListing entity) {
        if (dto == null) {
            return entity;
        }
        if (dto.getProductName() != null) {
            entity.setProductName(dto.getProductName());
        }
        if (dto.getProductDescription() != null) {
            entity.setProductDescription(dto.getProductDescription());
        }
        if (dto.getVendorPrice() != null) {
            entity.setVendorPrice(dto.getVendorPrice());
        }
        if (dto.getDiscountPercentage() != null) {
            entity.setDiscountPercentage(dto.getDiscountPercentage());
        }
        if (dto.getShippingCost() != null) {
            entity.setShippingCost(dto.getShippingCost());
        }
        if (dto.getFreeShipping() != null) {
            entity.setFreeShipping(dto.getFreeShipping());
        }
        if (dto.getShippingDays() != null) {
            entity.setShippingDays(dto.getShippingDays());
        }
        if (dto.getIsVisible() != null) {
            entity.setIsVisible(dto.getIsVisible());
        }
        if (dto.getIsHighlighted() != null) {
            entity.setIsHighlighted(dto.getIsHighlighted());
        }
        if (dto.getImages() != null) {
            entity.setImages(dto.getImages());
        }
        if (dto.getFeatures() != null) {
            entity.setFeatures(dto.getFeatures());
        }
        return entity;
    }
}
