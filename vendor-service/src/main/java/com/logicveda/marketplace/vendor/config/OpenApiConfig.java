package com.logicveda.marketplace.vendor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for API documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vendorServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Vendor Service API")
                .description("Marketplace Vendor Service - Manages vendor profiles, products, and payouts")
                .version("1.0.0")
                .contact(new Contact()
                    .name("LogicVeda Team")
                    .email("support@logicveda.com")
                    .url("https://logicveda.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
