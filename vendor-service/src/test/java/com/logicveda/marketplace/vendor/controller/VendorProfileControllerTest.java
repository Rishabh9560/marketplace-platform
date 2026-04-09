package com.logicveda.marketplace.vendor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicveda.marketplace.vendor.config.TestDataConfig;
import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.repository.VendorProfileRepository;
import com.logicveda.marketplace.vendor.service.VendorProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * REST Controller integration tests for vendor endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("VendorProfileController Integration Tests")
public class VendorProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VendorProfileService vendorService;

    @Autowired
    private VendorProfileRepository vendorRepository;

    private VendorProfileDTO testVendorDTO;
    private UUID testVendorId;

    @BeforeEach
    public void setUp() {
        vendorRepository.deleteAll();
        testVendorDTO = TestDataConfig.createTestVendorProfileDTO();
        testVendorDTO.setBusinessEmail("vendor" + UUID.randomUUID() + "@test.com");
    }

    @Test
    @DisplayName("Should register vendor via POST /api/v1/vendors/register")
    public void testRegisterVendorEndpoint() throws Exception {
        // Arrange
        String requestBody = objectMapper.writeValueAsString(testVendorDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/vendors/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.businessName").value(testVendorDTO.getBusinessName()))
            .andExpect(jsonPath("$.data.kycStatus").value("PENDING"));
    }

    @Test
    @DisplayName("Should get vendor by ID via GET /api/v1/vendors/{vendorId}")
    public void testGetVendorByIdEndpoint() throws Exception {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();

        // Act & Assert
        mockMvc.perform(get("/api/v1/vendors/{vendorId}", testVendorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(testVendorId.toString()))
            .andExpect(jsonPath("$.data.businessName").value(testVendorDTO.getBusinessName()));
    }

    @Test
    @DisplayName("Should return 404 when vendor not found")
    public void testGetVendorNotFoundEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/vendors/{vendorId}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get active vendors via GET /api/v1/vendors")
    public void testGetActiveVendorsEndpoint() throws Exception {
        // Arrange
        vendorService.registerVendor(testVendorDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vendors")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Should get verified vendors via GET /api/v1/vendors/verified")
    public void testGetVerifiedVendorsEndpoint() throws Exception {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        vendorService.verifyKYC(registered.getId());

        // Act & Assert
        mockMvc.perform(get("/api/v1/vendors/verified")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should search vendors via GET /api/v1/vendors/search")
    public void testSearchVendorsEndpoint() throws Exception {
        // Arrange
        testVendorDTO.setBusinessName("Unique Search Vendor");
        vendorService.registerVendor(testVendorDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vendors/search")
                .param("name", "Unique")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should get vendor statistics via GET /api/v1/vendors/{vendorId}/statistics")
    public void testGetVendorStatisticsEndpoint() throws Exception {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();

        // Act & Assert
        mockMvc.perform(get("/api/v1/vendors/{vendorId}/statistics", testVendorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isMap());
    }

    @Test
    @DisplayName("Should check vendor health via GET /api/v1/vendors/{vendorId}/health")
    public void testCheckVendorHealthEndpoint() throws Exception {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();
        vendorService.verifyKYC(testVendorId);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vendors/{vendorId}/health", testVendorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.canSellProducts").value(true));
    }

    @Test
    @DisplayName("Should get health status via GET /api/v1/health")
    public void testHealthCheckEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    @DisplayName("Should get service info via GET /api/v1/info")
    public void testGetServiceInfoEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.serviceName").value("Vendor Service"));
    }

    @Test
    @DisplayName("Should get service status via GET /api/v1/status")
    public void testGetStatusEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("RUNNING"));
    }

    @Test
    @DisplayName("Should return proper error format for invalid request")
    public void testInvalidRequestFormat() throws Exception {
        // Arrange
        String invalidBody = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/v1/vendors/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate required fields")
    public void testValidationOfRequiredFields() throws Exception {
        // Arrange
        VendorProfileDTO invalidDTO = new VendorProfileDTO();
        // Missing required fields

        String requestBody = objectMapper.writeValueAsString(invalidDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/vendors/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get liveness probe via GET /api/v1/live")
    public void testLivenessProbeEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/live"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status").value("ALIVE"));
    }

    @Test
    @DisplayName("Should get readiness probe via GET /api/v1/ready")
    public void testReadinessProbeEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/ready"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status").value("READY"));
    }

    @Test
    @DisplayName("Should get total vendor count via GET /api/v1/vendors/stats/total")
    public void testGetTotalVendorCountEndpoint() throws Exception {
        // Arrange
        vendorService.registerVendor(testVendorDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vendors/stats/total"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalVendors").isNumber());
    }

    @Test
    @DisplayName("Should get service metrics via GET /api/v1/metrics/summary")
    public void testGetServiceMetricsEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/metrics/summary"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.usedMemoryMB").isNumber())
            .andExpect(jsonPath("$.data.maxMemoryMB").isNumber());
    }

    @Test
    @DisplayName("Should get API docs info via GET /api/v1/docs")
    public void testGetApiDocsEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.swagger").isString());
    }
}
