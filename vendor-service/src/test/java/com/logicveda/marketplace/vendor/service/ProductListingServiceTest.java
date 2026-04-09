package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.config.TestDataConfig;
import com.logicveda.marketplace.vendor.dto.*;
import com.logicveda.marketplace.vendor.entity.ProductListing;
import com.logicveda.marketplace.vendor.exception.VendorException;
import com.logicveda.marketplace.vendor.repository.ProductListingRepository;
import com.logicveda.marketplace.vendor.repository.VendorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductListingService
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ProductListingService Integration Tests")
public class ProductListingServiceTest {

    @Autowired
    private ProductListingService listingService;

    @Autowired
    private VendorProfileService vendorService;

    @Autowired
    private ProductListingRepository listingRepository;

    @Autowired
    private VendorProfileRepository vendorRepository;

    private UUID testVendorId;
    private ProductListingDTO testListingDTO;

    @BeforeEach
    public void setUp() {
        listingRepository.deleteAll();
        vendorRepository.deleteAll();

        // Create test vendor
        VendorProfileDTO vendorDTO = TestDataConfig.createTestVendorProfileDTO();
        vendorDTO.setBusinessEmail("vendor" + UUID.randomUUID() + "@test.com");
        VendorProfileDTO vendor = vendorService.registerVendor(vendorDTO);
        testVendorId = vendor.getId();
        vendorService.verifyKYC(testVendorId);

        // Create test listing
        testListingDTO = TestDataConfig.createTestProductListingDTO(testVendorId);
    }

    @Test
    @DisplayName("Should create product listing successfully")
    public void testCreateListing() {
        // Act
        ProductListingDTO createdListing = listingService.createListing(testListingDTO);

        // Assert
        assertNotNull(createdListing);
        assertNotNull(createdListing.getId());
        assertEquals(testListingDTO.getProductName(), createdListing.getProductName());
        assertEquals("DRAFT", createdListing.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when vendor not KYC verified")
    public void testCreateListingWithoutKYC() {
        // Arrange
        VendorProfileDTO newVendorDTO = TestDataConfig.createTestVendorProfileDTO();
        newVendorDTO.setBusinessEmail("unverified" + UUID.randomUUID() + "@test.com");
        VendorProfileDTO newVendor = vendorService.registerVendor(newVendorDTO);

        ProductListingDTO listingDTO = TestDataConfig.createTestProductListingDTO(newVendor.getId());

        // Act & Assert
        assertThrows(Exception.class, () -> 
            listingService.createListing(listingDTO)
        );
    }

    @Test
    @DisplayName("Should retrieve listing by ID")
    public void testGetListingById() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);

        // Act
        ProductListingDTO retrieved = listingService.getListingById(created.getId());

        // Assert
        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(testListingDTO.getProductName(), retrieved.getProductName());
    }

    @Test
    @DisplayName("Should update listing successfully")
    public void testUpdateListing() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);

        ProductListingDTO updateDTO = TestDataConfig.createTestProductListingDTO(testVendorId);
        updateDTO.setProductName("Updated Product Name");

        // Act
        ProductListingDTO updated = listingService.updateListing(created.getId(), updateDTO);

        // Assert
        assertNotNull(updated);
        assertEquals("Updated Product Name", updated.getProductName());
    }

    @Test
    @DisplayName("Should publish listing from DRAFT to ACTIVE")
    public void testPublishListing() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);

        // Act
        ProductListingDTO published = listingService.publishListing(created.getId());

        // Assert
        assertNotNull(published);
        assertEquals("ACTIVE", published.getStatus());
        assertNotNull(published.getListedAt());
    }

    @Test
    @DisplayName("Should delist listing from ACTIVE to DELISTED")
    public void testDelistListing() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        listingService.publishListing(created.getId());

        // Act
        ProductListingDTO delisted = listingService.delistListing(created.getId());

        // Assert
        assertNotNull(delisted);
        assertEquals("DELISTED", delisted.getStatus());
        assertNotNull(delisted.getDelistedAt());
    }

    @Test
    @DisplayName("Should update inventory successfully")
    public void testUpdateInventory() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        InventoryUpdateDTO inventoryDTO = TestDataConfig.createTestInventoryUpdateDTO();
        inventoryDTO.setQuantityAvailable(200);

        // Act
        ProductListingDTO updated = listingService.updateInventory(created.getId(), inventoryDTO);

        // Assert
        assertNotNull(updated);
        assertEquals(200, updated.getQuantityAvailable());
    }

    @Test
    @DisplayName("Should update price successfully")
    public void testUpdatePrice() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        PriceUpdateDTO priceDTO = TestDataConfig.createTestPriceUpdateDTO();
        priceDTO.setVendorPrice(new BigDecimal("149.99"));

        // Act
        ProductListingDTO updated = listingService.updatePrice(created.getId(), priceDTO);

        // Assert
        assertNotNull(updated);
        assertEquals(new BigDecimal("149.99"), updated.getVendorPrice());
    }

    @Test
    @DisplayName("Should reserve inventory successfully")
    public void testReserveInventory() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);

        // Act
        listingService.reserveInventory(created.getId(), 20);
        ProductListingDTO retrieved = listingService.getListingById(created.getId());

        // Assert
        assertNotNull(retrieved);
        assertEquals(20, retrieved.getQuantityReserved());
        assertEquals(80, retrieved.getQuantityAvailable());
    }

    @Test
    @DisplayName("Should release inventory successfully")
    public void testReleaseInventory() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        listingService.reserveInventory(created.getId(), 20);

        // Act
        listingService.releaseInventory(created.getId(), 15);
        ProductListingDTO retrieved = listingService.getListingById(created.getId());

        // Assert
        assertNotNull(retrieved);
        assertEquals(5, retrieved.getQuantityReserved());
        assertEquals(95, retrieved.getQuantityAvailable());
    }

    @Test
    @DisplayName("Should get vendor listings")
    public void testGetVendorListings() {
        // Arrange
        ProductListingDTO listing1 = listingService.createListing(testListingDTO);

        testListingDTO.setProductName("Second Product");
        testListingDTO.setSku("SKU" + System.nanoTime());
        ProductListingDTO listing2 = listingService.createListing(testListingDTO);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<ProductListingDTO> listings = listingService.getVendorListings(testVendorId, pageable);

        // Assert
        assertNotNull(listings);
        assertTrue(listings.getContent().size() >= 2);
    }

    @Test
    @DisplayName("Should get vendor active listings")
    public void testGetVendorActiveListings() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        listingService.publishListing(created.getId());

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<ProductListingDTO> activeListings = listingService.getVendorActiveListings(testVendorId, pageable);

        // Assert
        assertNotNull(activeListings);
        assertTrue(activeListings.getContent().stream()
            .anyMatch(l -> l.getId().equals(created.getId())));
    }

    @Test
    @DisplayName("Should get low stock listings")
    public void testGetLowStockListings() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        InventoryUpdateDTO inventoryDTO = TestDataConfig.createTestInventoryUpdateDTO();
        inventoryDTO.setQuantityAvailable(5); // Below reorder level of 10
        listingService.updateInventory(created.getId(), inventoryDTO);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<ProductListingDTO> lowStock = listingService.getLowStockListings(testVendorId, pageable);

        // Assert
        assertNotNull(lowStock);
        assertTrue(lowStock.getContent().stream()
            .anyMatch(l -> l.getId().equals(created.getId())));
    }

    @Test
    @DisplayName("Should get active marketplace listings")
    public void testGetActiveMarketplaceListings() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        listingService.publishListing(created.getId());

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<ProductListingDTO> marketplaceListings = listingService.getActiveMarketplaceListings(pageable);

        // Assert
        assertNotNull(marketplaceListings);
        assertTrue(marketplaceListings.getContent().size() > 0);
    }

    @Test
    @DisplayName("Should get discounted listings")
    public void testGetDiscountedListings() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        PriceUpdateDTO priceDTO = TestDataConfig.createTestPriceUpdateDTO();
        priceDTO.setDiscountPercentage(new BigDecimal("15.00"));
        listingService.updatePrice(created.getId(), priceDTO);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<ProductListingDTO> discounted = listingService.getDiscountedListings(pageable);

        // Assert
        assertNotNull(discounted);
        assertTrue(discounted.getContent().stream()
            .anyMatch(l -> l.getId().equals(created.getId())));
    }

    @Test
    @DisplayName("Should get listings by price range")
    public void testGetListingsByPriceRange() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<ProductListingDTO> rangeListings = listingService.getListingsByPriceRange(
            new BigDecimal("50.00"),
            new BigDecimal("150.00"),
            pageable
        );

        // Assert
        assertNotNull(rangeListings);
        assertTrue(rangeListings.getContent().stream()
            .anyMatch(l -> l.getId().equals(created.getId())));
    }

    @Test
    @DisplayName("Should increment view count")
    public void testIncrementViewCount() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        int initialViews = created.getViewCount();

        // Act
        listingService.incrementViewCount(created.getId());
        ProductListingDTO retrieved = listingService.getListingById(created.getId());

        // Assert
        assertNotNull(retrieved);
        assertEquals(initialViews + 1, retrieved.getViewCount());
    }

    @Test
    @DisplayName("Should get best-selling listings")
    public void testGetBestSellingListings() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        listingService.publishListing(created.getId());

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<ProductListingDTO> bestSelling = listingService.getBestSellingListings(pageable);

        // Assert
        assertNotNull(bestSelling);
    }

    @Test
    @DisplayName("Should get popular listings")
    public void testGetPopularListings() {
        // Arrange
        ProductListingDTO created = listingService.createListing(testListingDTO);
        listingService.publishListing(created.getId());
        listingService.incrementViewCount(created.getId());

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<ProductListingDTO> popular = listingService.getPopularListings(pageable);

        // Assert
        assertNotNull(popular);
    }

    @Test
    @DisplayName("Should search listings by name")
    public void testSearchListings() {
        // Arrange
        testListingDTO.setProductName("Unique Search Product");
        ProductListingDTO created = listingService.createListing(testListingDTO);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<ProductListingDTO> results = listingService.searchListings("Unique", pageable);

        // Assert
        assertNotNull(results);
        assertTrue(results.getContent().stream()
            .anyMatch(l -> l.getId().equals(created.getId())));
    }

    @Test
    @DisplayName("Should get total inventory for vendor")
    public void testGetTotalInventoryForVendor() {
        // Arrange
        ProductListingDTO listing1 = listingService.createListing(testListingDTO);

        testListingDTO.setProductName("Second Product");
        testListingDTO.setSku("SKU" + System.nanoTime());
        ProductListingDTO listing2 = listingService.createListing(testListingDTO);

        // Act
        BigDecimal totalInventory = listingService.getTotalInventoryForVendor(testVendorId);

        // Assert
        assertNotNull(totalInventory);
        assertTrue(totalInventory.compareTo(BigDecimal.ZERO) > 0);
    }
}
