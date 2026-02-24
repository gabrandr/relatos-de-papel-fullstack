package com.relatosdepapel.ms_books_payments.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para respuesta de pagos agrupados por usuario
 * Incluye lista de pagos y totales calculados
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentsResponseDTO {
    /** ID del usuario */
    private Long userId;

    /** Lista de todos los pagos del usuario */
    private List<PaymentResponseDTO> payments;

    /** Cantidad total de pagos realizados */
    private Integer totalPayments;

    /** Monto total gastado (suma de todos los totalPrice) */
    private BigDecimal totalAmountSpent;
}