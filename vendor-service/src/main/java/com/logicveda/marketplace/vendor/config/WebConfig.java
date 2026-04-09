package com.logicveda.marketplace.vendor.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for CORS and other web settings
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/api/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.split(",");

        registry.addMapping("/api/**")
            .allowedOrigins(origins)
            .allowedMethods(allowedMethods.split(","))
            .allowedHeaders(allowedHeaders)
            .maxAge(maxAge)
            .allowCredentials(allowCredentials)
            .exposedHeaders("X-Total-Count", "X-Page-Number", "X-Page-Size");

        // Allow Swagger UI
        registry.addMapping("/v3/api-docs/**")
            .allowedOrigins(origins)
            .allowedMethods("GET")
            .maxAge(3600);

        registry.addMapping("/swagger-ui/**")
            .allowedOrigins(origins)
            .allowedMethods("GET")
            .maxAge(3600);
    }
}
