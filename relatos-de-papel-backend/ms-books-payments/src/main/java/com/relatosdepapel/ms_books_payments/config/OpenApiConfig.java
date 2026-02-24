package com.relatosdepapel.ms_books_payments.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI paymentsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Books Payments API")
                        .description("API para gestion de pagos y operaciones de compra.")
                        .version("v1"));
    }
}
