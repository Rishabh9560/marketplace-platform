package com.logicveda.marketplace.vendor.controller;

import com.logicveda.marketplace.vendor.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for health checks and service status
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Service health and status endpoints")
public class HealthController {

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if service is running")
    public ResponseEntity<ApiResponse<Object>> health() {
        log.debug("Health check request");

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final String status = "UP";
                public final String service = "vendor-service";
                public final String version = "1.0.0";
            },
            "Service is running"
        ));
    }

    /**
     * Liveness probe for Kubernetes
     */
    @GetMapping("/live")
    @Operation(summary = "Liveness check", description = "Kubernetes liveness probe")
    public ResponseEntity<ApiResponse<Object>> live() {
        log.trace("Liveness check request");

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final String status = "ALIVE";
            },
            ""
        ));
    }

    /**
     * Readiness probe for Kubernetes
     */
    @GetMapping("/ready")
    @Operation(summary = "Readiness check", description = "Kubernetes readiness probe")
    public ResponseEntity<ApiResponse<Object>> ready() {
        log.trace("Readiness check request");

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final String status = "READY";
            },
            ""
        ));
    }

    /**
     * Get service information
     */
    @GetMapping("/info")
    @Operation(summary = "Service info", description = "Get service information and version")
    public ResponseEntity<ApiResponse<Object>> getServiceInfo() {
        log.debug("Service info request");

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final String serviceName = "Vendor Service";
                public final String version = "1.0.0";
                public final String description = "Vendor and Product Management Service";
                public final String buildTime = "2024-01-15T10:00:00Z";
                public final String environment = "production";
            },
            "Service information retrieved"
        ));
    }

    /**
     * Get service metrics
     */
    @GetMapping("/metrics/summary")
    @Operation(summary = "Service metrics", description = "Get service performance metrics")
    public ResponseEntity<ApiResponse<Object>> getServiceMetrics() {
        log.debug("Service metrics request");

        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final long usedMemoryMB = usedMemory;
                public final long maxMemoryMB = maxMemory;
                public final int availableProcessors = runtime.availableProcessors();
                public final long uptime = System.currentTimeMillis();
            },
            "Service metrics retrieved"
        ));
    }

    /**
     * Get API documentation
     */
    @GetMapping("/docs")
    @Operation(summary = "API documentation", description = "Get API documentation links")
    public ResponseEntity<ApiResponse<Object>> getApiDocs() {
        log.debug("API docs request");

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final String swagger = "/swagger-ui.html";
                public final String openApi = "/v3/api-docs";
                public final String contact = "support@vendor-service.com";
            },
            "API documentation links"
        ));
    }

    /**
     * Service status with timestamp
     */
    @GetMapping("/status")
    @Operation(summary = "Service status", description = "Get detailed service status")
    public ResponseEntity<ApiResponse<Object>> getStatus() {
        log.debug("Status request");

        final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final String status = "RUNNING";
                public final String currentTimestamp = timestamp;
                public final Map<String, String> components = createComponentStatusMap();
            },
            "Service status retrieved"
        ));
    }

    /**
     * Helper method to create component status map
     */
    private Map<String, String> createComponentStatusMap() {
        Map<String, String> components = new HashMap<>();
        components.put("database", "UP");
        components.put("cache", "UP");
        components.put("security", "UP");
        components.put("discoverClient", "UP");
        return components;
    }
}
