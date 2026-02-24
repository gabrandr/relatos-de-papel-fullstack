package com.relatosdepapel.ms_books_payments.repository;

import com.relatosdepapel.ms_books_payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * Repositorio JPA para la entidad Payment (Capa 1).
 * Extiende JpaRepository para operaciones CRUD básicas y
 * JpaSpecificationExecutor para búsquedas dinámicas con Specifications.
 */
public interface PaymentJpaRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    /**
     * Busca todos los pagos de un usuario.
     * Ejemplo: findByUserId(1L)
     * SQL generado: SELECT * FROM payments WHERE user_id = 1
     * 
     * @param userId ID del usuario
     * @return Lista de pagos del usuario
     */
    List<Payment> findByUserId(Long userId);

    /**
     * Busca pagos por ID de libro.
     * Ejemplo: findByBookId(5L)
     * SQL generado: SELECT * FROM payments WHERE book_id = 5
     * 
     * @param bookId ID del libro
     * @return Lista de pagos asociados al libro
     */
    List<Payment> findByBookId(Long bookId);

    /**
     * Busca pagos por estado.
     * Ejemplo: findByStatus(\"PENDING\")
     * SQL generado: SELECT * FROM payments WHERE status = 'PENDING'
     * 
     * @param status Estado del pago (PENDING, APPROVED, CANCELLED)
     * @return Lista de pagos con el estado especificado
     */
    List<Payment> findByStatus(String status);

    /**
     * Busca pagos por usuario y estado.
     * Ejemplo: findByUserIdAndStatus(1L, \"APPROVED\")
     * SQL generado: SELECT * FROM payments WHERE user_id = 1 AND status =
     * 'APPROVED'
     * 
     * @param userId ID del usuario
     * @param status Estado del pago
     * @return Lista de pagos que cumplen ambas condiciones
     */
    List<Payment> findByUserIdAndStatus(Long userId, String status);
}