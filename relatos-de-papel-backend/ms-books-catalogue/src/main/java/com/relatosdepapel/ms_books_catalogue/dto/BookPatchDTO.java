package com.relatosdepapel.ms_books_catalogue.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para actualización parcial (PATCH)
 * Todos los campos son opcionales
 * No incluye ID (viene en la URL) ni ISBN (no se debe modificar)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookPatchDTO {
    /** Título del libro (opcional) */
    private String title;

    /** Autor del libro (opcional) */
    private String author;

    /** Fecha de publicación (opcional) */
    private LocalDate publicationDate;

    /** Categoría o género (opcional) */
    private String category;

    /** Valoración (escala de 1 a 5, opcional) */
    private Integer rating;

    /** Visibilidad en el catálogo (opcional) */
    private Boolean visible;

    /** Cantidad disponible en inventario (opcional) */
    private Integer stock;

    /** Precio del libro (opcional) */
    private BigDecimal price;
}
