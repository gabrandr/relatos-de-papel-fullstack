package com.relatosdepapel.ms_books_catalogue.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI catalogueOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Books Catalogue API")
                        .description("API para gestion y busqueda del catalogo de libros.")
                        .version("v1"));
    }
}
