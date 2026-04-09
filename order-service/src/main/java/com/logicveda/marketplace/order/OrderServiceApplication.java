package com.logicveda.marketplace.order;

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
 * Order Service Application
 * Handles order management, fulfillment, and state transitions
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.logicveda.marketplace"})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Order Service API")
                .description("Order Management and Fulfillment - Multi-Vendor E-Commerce Platform\n\n" +
                    "Manages customer orders, order items, fulfillment workflow, and order state transitions.\n\n" +
                    "**Key Features:**\n" +
                    "- Order creation from cart checkout\n" +
                    "- Order state machine (PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED)\n" +
                    "- Multi-vendor order fulfillment\n" +
                    "- Payment confirmation workflow\n" +
                    "- Order cancellation and refunds\n" +
                    "- Tracking and shipping updates\n" +
                    "- Customer order history\n\n" +
                    "**Authorization:** All endpoints require JWT token")
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
