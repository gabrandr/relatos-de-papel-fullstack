package com.relatosdepapel.ms_books_payments.specification;

import com.relatosdepapel.ms_books_payments.entity.Payment;
import com.relatosdepapel.ms_books_payments.utils.Consts;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

/**
 * Specifications para búsquedas dinámicas de Payment.
 * Cada método retorna una Specification que representa una condición SQL.
 */
public class PaymentSpecification {
    /**
     * Filtra pagos por ID de usuario.
     * SQL generado: WHERE user_id = userId
     * 
     * @param userId ID del usuario
     * @return Specification para filtrar por usuario
     */
    public static Specification<Payment> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return null; // Sin filtro
            }
            return criteriaBuilder.equal(root.get(Consts.USER_ID), userId);
        };
    }

    /**
     * Filtra pagos por ID de libro.
     * SQL generado: WHERE book_id = bookId
     * 
     * @param bookId ID del libro
     * @return Specification para filtrar por libro
     */
    public static Specification<Payment> hasBookId(Long bookId) {
        return (root, query, criteriaBuilder) -> {
            if (bookId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(Consts.BOOK_ID), bookId);
        };
    }

    /**
     * Filtra pagos por estado.
     * SQL generado: WHERE status = status
     * 
     * @param status Estado del pago (PENDING, APPROVED, CANCELLED)
     * @return Specification para filtrar por estado
     */
    public static Specification<Payment> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get(Consts.STATUS), status);
        };
    }

    /**
     * Filtra pagos con fecha de pago mayor o igual a la especificada.
     * SQL generado: WHERE purchase_date >= date
     * 
     * @param date Fecha mínima
     * @return Specification para filtrar por fecha desde
     */
    public static Specification<Payment> paymentDateAfterOrEqual(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(Consts.PURCHASE_DATE), date);
        };
    }

    /**
     * Filtra pagos con fecha de pago menor o igual a la especificada.
     * SQL generado: WHERE purchase_date <= date
     * 
     * @param date Fecha máxima
     * @return Specification para filtrar por fecha hasta
     */
    public static Specification<Payment> paymentDateBeforeOrEqual(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get(Consts.PURCHASE_DATE), date);
        };
    }

    /**
     * Combina los filtros opcionales usando AND.
     * 
     * @param userId ID del usuario
     * @param bookId ID del libro
     * @param status Estado del pago
     * @return Specification combinada
     */
    public static Specification<Payment> filterBy(Long userId, Long bookId, String status) {
        return Specification.where(hasUserId(userId))
                .and(hasBookId(bookId))
                .and(hasStatus(status));
    }
}