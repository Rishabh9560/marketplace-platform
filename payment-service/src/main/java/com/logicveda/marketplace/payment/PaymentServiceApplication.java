package com.logicveda.marketplace.payment;

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
 * Payment Service Application
 * Handles payment processing via Stripe and transaction management
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.logicveda.marketplace"})
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Payment Service API")
                .description("Payment Processing and Transaction Management - Multi-Vendor E-Commerce Platform\n\n" +
                    "Handles payment processing, transaction tracking, and refund management via Stripe.\n\n" +
                    "**Key Features:**\n" +
                    "- Stripe payment processing (PCI-DSS compliant)\n" +
                    "- Transaction tracking and history\n" +
                    "- Full and partial refund support\n" +
                    "- Payment method validation\n" +
                    "- WebHook support for Stripe events\n" +
                    "- Multi-currency support\n\n" +
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
