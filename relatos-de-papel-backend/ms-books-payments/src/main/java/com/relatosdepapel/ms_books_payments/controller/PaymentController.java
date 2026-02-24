package com.relatosdepapel.ms_books_payments.controller;

import com.relatosdepapel.ms_books_payments.dto.ErrorResponseDTO;
import com.relatosdepapel.ms_books_payments.dto.PaymentRequestDTO;
import com.relatosdepapel.ms_books_payments.dto.PaymentResponseDTO;
import com.relatosdepapel.ms_books_payments.dto.PaymentStatusDTO;
import com.relatosdepapel.ms_books_payments.exception.BookNotFoundException;
import com.relatosdepapel.ms_books_payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * GET /api/payments
     * Obtiene todos los pagos del sistema
     *
     * @return 200 OK con lista de todos los pagos
     */
    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAll() {
        return ResponseEntity.ok(paymentService.getAll()); // 200 OK
    }

    /**
     * GET /api/payments/search
     * Busca pagos aplicando filtros opcionales
     *
     * @param userId ID del usuario (opcional)
     * @param bookId ID del libro (opcional)
     * @param status Estado del pago (opcional)
     * @return 200 OK con lista de pagos filtrada
     */
    @GetMapping("/search")
    public ResponseEntity<List<PaymentResponseDTO>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String status) {

        List<PaymentResponseDTO> payments = paymentService.search(userId, bookId, status);
        return ResponseEntity.ok(payments); // 200 OK
    }

    /**
     * GET /api/payments/{id}
     * Obtiene un pago por su ID
     *
     * @param id ID del pago
     * @return 200 OK con el pago encontrado
     *         404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getById(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.getById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(payment); // 200 OK
    }

    /**
     * POST /api/payments
     * Crea un nuevo pago
     *
     * @param dto Datos del pago a crear
     * @return 201 Created con el pago creado
     *         400 Bad Request si hay error de validación
     *         404 Not Found si el libro no existe
     *         500 Internal Server Error si hay error inesperado
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PaymentRequestDTO dto) {
        // Validación 1: userId válido
        if (dto.getUserId() == null || dto.getUserId() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "userId debe ser mayor a 0"));
        }

        // Validación 2: bookId válido
        if (dto.getBookId() == null || dto.getBookId() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "bookId debe ser mayor a 0"));
        }

        // Validación 3: quantity válido
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "quantity debe ser mayor a 0"));
        }

        try {
            PaymentResponseDTO createdPayment = paymentService.create(dto);
            return ResponseEntity.status(201).body(createdPayment);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Libro no encontrado"));
        } catch (IllegalArgumentException e) {
            // Captura validaciones de negocio del servicio (ej: Stock insuficiente)
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "No fue posible crear el pago"));
        } catch (RuntimeException e) {
            // Captura errores inesperados o de conexión con otros MS
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO(500, "Error interno al procesar la compra"));
        }
    }

    /**
     * PATCH /api/payments/{id}
     * Actualiza el estado de un pago
     *
     * @param id  ID del pago
     * @param dto Nuevo estado
     * @return 200 OK con el pago actualizado
     *         404 Not Found si no existe
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody PaymentStatusDTO dto) {

        // Validación básica
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El estado no puede estar vacío"));
        }

        // El servicio retorna null si no encuentra el pago
        PaymentResponseDTO updatedPayment = paymentService.updateStatus(id, dto);

        if (updatedPayment == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(updatedPayment); // 200 OK
    }

    /**
     * DELETE /api/payments/{id}
     * Cancela un pago
     *
     * @param id ID del pago a cancelar
     * @return 204 No Content si se canceló correctamente
     *         404 Not Found si el pago no existe
     *         409 Conflict si ya estaba cancelado
     *         500 Internal Server Error si falla
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        // Primero verificamos existencia (opcional, pero buena práctica REST)
        if (paymentService.getById(id) == null) {
            return ResponseEntity.notFound().build(); // 404
        }

        try {
            // El servicio devuelve false si el pago ya estaba cancelado o no existe (aunque
            // ya validamos existencia arriba)
            boolean cancelled = paymentService.cancelPayment(id);
            if (!cancelled) {
                // Si retorna false, es porque ya estaba cancelado
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponseDTO(409, "El pago ya se encuentra cancelado"));
            }
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            // Error al restaurar stock (inconsistencia grave)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO(500, "No se pudo cancelar el pago"));
        }
    }
}
