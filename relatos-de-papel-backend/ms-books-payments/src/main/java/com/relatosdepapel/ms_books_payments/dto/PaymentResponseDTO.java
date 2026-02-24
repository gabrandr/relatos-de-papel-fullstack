package com.relatosdepapel.ms_books_payments.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuestas de pagos
 * Contiene toda la información del pago incluyendo datos desnormalizados del
 * libro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    /** ID único del pago */
    private Long id;

    /** ID del usuario que realizó la compra */
    private Long userId;

    /** ID del libro comprado */
    private Long bookId;

    /** Título del libro (desnormalizado) */
    private String bookTitle;

    /** ISBN del libro (desnormalizado) */
    private String bookIsbn;

    /** Cantidad comprada */
    private Integer quantity;

    /** Precio unitario al momento de la compra */
    private BigDecimal unitPrice;

    /** Precio total (quantity × unitPrice) */
    private BigDecimal totalPrice;

    /** Fecha y hora de la compra */
    private LocalDateTime purchaseDate;

    /** Estado del pago: COMPLETED o CANCELLED */
    private String status;
}