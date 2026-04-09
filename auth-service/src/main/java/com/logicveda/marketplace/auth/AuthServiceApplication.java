package com.logicveda.marketplace.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot application entry point for Auth Service.
 * Handles JWT authentication, user registration, and token management.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaAuditing
@EnableScheduling
@ComponentScan(basePackages = {
    "com.logicveda.marketplace.auth",
    "com.logicveda.marketplace.common"
})
@OpenAPIDefinition(
    info = @Info(
        title = "Auth Service API",
        version = "1.0.0",
        description = "JWT Authentication and User Management Service",
        contact = @Contact(
            name = "LogicVeda Team",
            url = "https://logicveda.com"
        )
    )
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT access token",
    in = SecuritySchemeIn.HEADER
)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
