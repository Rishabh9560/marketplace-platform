package com.logicveda.marketplace.product;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Product Service Application
 * Handles all product catalog operations, inventory, and approval workflow
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.logicveda.marketplace"})
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Bean
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Product Service API")
                .description("Product Catalog Management - Multi-Vendor E-Commerce Platform\n\n" +
                    "Manages product listings, variants, inventory, and approval workflow.\n\n" +
                    "**Key Features:**\n" +
                    "- Multi-vendor product catalog\n" +
                    "- Product variants with JSONB attributes (color, size, storage)\n" +
                    "- Inventory management with low-stock alerts\n" +
                    "- Product approval workflow (DRAFT → PENDING_APPROVAL → APPROVED → PUBLISHED)\n" +
                    "- Category hierarchy\n" +
                    "- Search and filtering\n\n" +
                    "**Authorization:** All endpoints except GET (public listing) require JWT token")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Marketplace Support")
                    .email("support@marketplace.local")
                    .url("https://marketplace.local"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .addSecurityItem(new SecurityRequirement().addList("JWT"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("JWT", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token for authentication")));
    }
}
