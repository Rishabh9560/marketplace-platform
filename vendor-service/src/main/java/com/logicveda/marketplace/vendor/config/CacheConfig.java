package com.logicveda.marketplace.vendor.config;

import com.logicveda.marketplace.vendor.constants.VendorConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for caching vendor and listing data
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "vendors",
            "vendors-by-id",
            "vendors-by-email",
            "listings",
            "listings-by-vendor",
            "listings-by-product",
            "payouts",
            "payouts-by-vendor"
        );
    }
}
