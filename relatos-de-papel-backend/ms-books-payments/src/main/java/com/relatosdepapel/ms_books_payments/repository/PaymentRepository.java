package com.relatosdepapel.ms_books_payments.repository;

import com.relatosdepapel.ms_books_payments.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Wrapper del repositorio JPA (Capa 2).
 * Encapsula PaymentJpaRepository y proporciona métodos con nombres más claros.
 */
@Repository
@RequiredArgsConstructor
public class PaymentRepository {
    private final PaymentJpaRepository jpaRepository;

    /**
     * Obtiene todos los pagos.
     * 
     * @return Lista de todos los pagos
     */
    public List<Payment> getAll() {
        return jpaRepository.findAll();
    }

    /**
     * Busca un pago por ID.
     * 
     * @param id ID del pago
     * @return Payment si existe, null si no existe
     */
    public Payment getById(Long id) {
        return jpaRepository.findById(id).orElse(null);
    }

    /**
     * Guarda un pago (crear o actualizar).
     * 
     * @param payment Entidad Payment a guardar
     * @return Payment guardado con ID asignado
     */
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }

    /**
     * Elimina un pago por ID.
     * 
     * @param id ID del pago a eliminar
     */
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    /**
     * Busca pagos por usuario usando Query Method.
     * 
     * @param userId ID del usuario
     * @return Lista de pagos del usuario
     */
    public List<Payment> getByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    /**
     * Realiza una búsqueda dinámica usando Specifications.
     * Este método se usará en el Service para búsquedas con filtros combinados.
     * 
     * @param spec Especificación de búsqueda (condiciones dinámicas)
     * @return Lista de pagos que cumplen la especificación
     */
    public List<Payment> search(Specification<Payment> spec) {
        return jpaRepository.findAll(spec);
    }
}