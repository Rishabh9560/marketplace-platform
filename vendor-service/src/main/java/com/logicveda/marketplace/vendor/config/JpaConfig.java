package com.logicveda.marketplace.vendor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA configuration for database operations
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.logicveda.marketplace.vendor.repository"
)
@EnableJpaAuditing
public class JpaConfig {
    // JPA configuration is handled by Spring Boot auto-configuration
    // This class enables JPA repositories and auditing
}
