package com.relatosdepapel.ms_books_payments.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para actualizar el stock de un libro en MS Books Catalogue
 * PATCH http://MS-BOOKS-CATALOGUE/api/books/{id}/stock
 * 
 * Quantity positivo = incrementar stock
 * Quantity negativo = decrementar stock
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateDTO {
    /**
     * Cantidad a sumar/restar del stock
     * Positivo: incrementar (ej: 5 → stock + 5)
     * Negativo: decrementar (ej: -2 → stock - 2)
     */
    private Integer quantity;
}