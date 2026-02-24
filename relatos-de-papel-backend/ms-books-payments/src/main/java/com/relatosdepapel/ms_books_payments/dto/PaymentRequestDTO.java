package com.relatosdepapel.ms_books_payments.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para crear un nuevo pago
 * Solo requiere userId, bookId y quantity (los demás datos se obtienen del
 * catálogo)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    /** ID del usuario que realiza la compra */
    private Long userId;

    /** ID del libro a comprar */
    private Long bookId;

    /** Cantidad de libros a comprar */
    private Integer quantity;
}