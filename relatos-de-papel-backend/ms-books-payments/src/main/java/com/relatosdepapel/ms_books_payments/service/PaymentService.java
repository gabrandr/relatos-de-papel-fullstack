package com.relatosdepapel.ms_books_payments.service;

import com.relatosdepapel.ms_books_payments.dto.PaymentRequestDTO;
import com.relatosdepapel.ms_books_payments.dto.PaymentResponseDTO;
import com.relatosdepapel.ms_books_payments.dto.PaymentStatusDTO;
import com.relatosdepapel.ms_books_payments.dto.UserPaymentsResponseDTO;
import java.util.List;

/**
 * Interface del servicio de pagos.
 * Define los métodos que PaymentServiceImpl debe implementar.
 */
public interface PaymentService {
    /**
     * Obtiene todos los pagos.
     * 
     * @return Lista de todos los pagos
     */
    List<PaymentResponseDTO> getAll();

    /**
     * Busca un pago por ID.
     * 
     * @param id ID del pago
     * @return PaymentResponseDTO si existe, null si no
     */
    PaymentResponseDTO getById(Long id);

    /**
     * Crea un nuevo pago.
     * Valida disponibilidad del libro y decrementa stock.
     * 
     * @param dto Datos del pago a crear
     * @return PaymentResponseDTO del pago creado
     */
    PaymentResponseDTO create(PaymentRequestDTO dto);

    /**
     * Busca pagos con filtros dinámicos.
     * 
     * @param userId ID del usuario (opcional)
     * @param bookId ID del libro (opcional)
     * @param status Estado del pago (opcional)
     * @return Lista de pagos que cumplen los filtros
     */
    List<PaymentResponseDTO> search(Long userId, Long bookId, String status);

    /**
     * Obtiene todos los pagos de un usuario con totales.
     * 
     * @param userId ID del usuario
     * @return UserPaymentsResponseDTO con pagos y estadísticas
     */
    UserPaymentsResponseDTO getByUserId(Long userId);

    /**
     * Actualiza el estado de un pago.
     * 
     * @param id  ID del pago
     * @param dto Nuevo estado
     * @return PaymentResponseDTO actualizado, null si no existe
     */
    PaymentResponseDTO updateStatus(Long id, PaymentStatusDTO dto);

    /**
     * Cancela un pago y restaura el stock.
     * 
     * @param id ID del pago a cancelar
     * @return true si se canceló, false si no existe
     */
    boolean cancelPayment(Long id);
}