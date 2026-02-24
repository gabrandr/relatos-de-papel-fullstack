package com.relatosdepapel.ms_books_catalogue.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para respuestas de consultas (GET)
 * Incluye el ID generado por la BD
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    /** ID único del libro */
    private Long id;

    /** Título del libro */
    private String title;

    /** Autor del libro */
    private String author;

    /** Fecha de publicación */
    private LocalDate publicationDate;

    /** Categoría o género */
    private String category;

    /** ISBN (identificador único internacional) */
    private String isbn;

    /** Valoración (escala de 1 a 5) */
    private Integer rating;

    /** Visibilidad en el catálogo (true = visible, false = oculto) */
    private Boolean visible;

    /** Cantidad disponible en inventario */
    private Integer stock;

    /** Precio del libro */
    private BigDecimal price;
}
