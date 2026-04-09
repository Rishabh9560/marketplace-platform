package com.logicveda.marketplace.vendor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Auditor configuration for tracking who created/modified entities
 */
@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    return Optional.of(authentication.getName());
                }
            } catch (Exception e) {
                // Exception handling
            }
            return Optional.of("SYSTEM");
        };
    }
}
