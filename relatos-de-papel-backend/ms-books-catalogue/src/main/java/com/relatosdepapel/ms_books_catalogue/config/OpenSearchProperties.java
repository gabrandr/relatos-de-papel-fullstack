package com.relatosdepapel.ms_books_catalogue.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Propiedades de configuración para la conexión de `ms-books-catalogue` con OpenSearch.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "opensearch")
public class OpenSearchProperties {
    /**
     * URL base del cluster (puede incluir user-info).
     */
    private String url;
    /**
     * Usuario opcional para autenticación basic.
     */
    private String username;
    /**
     * Password opcional para autenticación basic.
     */
    private String password;
    /**
     * Nombre del índice de catálogo.
     */
    private String index = "relatos";
}
