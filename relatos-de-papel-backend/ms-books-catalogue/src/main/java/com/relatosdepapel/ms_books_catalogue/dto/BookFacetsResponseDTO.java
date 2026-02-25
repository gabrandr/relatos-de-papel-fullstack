package com.relatosdepapel.ms_books_catalogue.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta agregada de facets para catálogo.
 * Expone el total de hits y los buckets principales para categorías y autores.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookFacetsResponseDTO {
    /**
     * Total de libros que cumplen los filtros de la consulta de facets.
     */
    private long total;
    /**
     * Conteo por categoría (`categoria -> documentos`).
     */
    private Map<String, Long> categories;
    /**
     * Conteo por autor (`autor -> documentos`).
     */
    private Map<String, Long> authors;
}
