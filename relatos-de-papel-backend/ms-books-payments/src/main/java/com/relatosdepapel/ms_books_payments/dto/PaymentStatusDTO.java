package com.relatosdepapel.ms_books_payments.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para actualizar el estado de un pago
 * Usado en PATCH /api/payments/{id}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusDTO {
    /** Nuevo estado del pago: COMPLETED o CANCELLED */
    private String status;
}