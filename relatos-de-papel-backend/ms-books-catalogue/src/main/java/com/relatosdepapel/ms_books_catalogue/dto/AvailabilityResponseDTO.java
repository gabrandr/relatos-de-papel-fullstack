package com.relatosdepapel.ms_books_catalogue.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;

/**
 * DTO para verificar disponibilidad de un libro
 * Usado por ms-books-payments antes de procesar un pago
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponseDTO {
    /** ID del libro */
    private Long id;

    /** Título del libro */
    private String title;

    /** ISBN del libro */
    private String isbn;

    /** Disponibilidad real (true si visible=true Y stock>0) */
    private Boolean available;

    /** Visibilidad en el catálogo */
    private Boolean visible;

    /** Stock actual disponible */
    private Integer stock;

    /** Precio del libro */
    private BigDecimal price;
}
