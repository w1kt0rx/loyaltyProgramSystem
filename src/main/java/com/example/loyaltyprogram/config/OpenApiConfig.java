package com.example.loyaltyprogram.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI loyaltyOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Loyalty Program API")
                .version("1.0"));
    }
}