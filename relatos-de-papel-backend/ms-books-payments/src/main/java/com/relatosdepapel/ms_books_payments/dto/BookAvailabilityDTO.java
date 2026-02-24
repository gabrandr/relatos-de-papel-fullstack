package com.relatosdepapel.ms_books_payments.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO que recibe la respuesta del endpoint de disponibilidad de MS Books
 * Catalogue
 * GET http://MS-BOOKS-CATALOGUE/api/books/{id}/availability
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAvailabilityDTO {
    /** ID del libro */
    private Long id;

    /** Título del libro */
    private String title;

    /** ISBN del libro */
    private String isbn;

    /** Disponibilidad real (true si visible=true Y stock>0) */
    private Boolean available;

    /** Stock actual disponible */
    private Integer stock;

    /** Precio actual del libro */
    private BigDecimal price;
}