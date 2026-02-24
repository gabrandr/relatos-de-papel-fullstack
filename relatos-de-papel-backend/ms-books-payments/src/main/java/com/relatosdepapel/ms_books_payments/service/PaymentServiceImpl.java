package com.relatosdepapel.ms_books_payments.service;

import com.relatosdepapel.ms_books_payments.client.BookCatalogueClient;
import com.relatosdepapel.ms_books_payments.dto.*;
import com.relatosdepapel.ms_books_payments.entity.Payment;
import com.relatosdepapel.ms_books_payments.exception.BookNotFoundException;
import com.relatosdepapel.ms_books_payments.repository.PaymentRepository;
import com.relatosdepapel.ms_books_payments.specification.PaymentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de pagos.
 * Contiene la lógica de negocio para gestionar pagos y su integración con MS
 * Catalogue.
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository; // Inyección de dependencias Repository
    private final BookCatalogueClient catalogueClient; // Inyección de dependencias Client peticiones a MS Catalogue

    // METODOS CRUD

    /**
     * Obtiene todos los pagos del sistema.
     * 
     * @return Lista de todos los pagos como PaymentResponseDTO
     */
    @Override
    public List<PaymentResponseDTO> getAll() {
        return paymentRepository.getAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca un pago por ID.
     * 
     * @param id ID del pago a buscar
     * @return PaymentResponseDTO si existe, null si no existe
     */
    @Override
    public PaymentResponseDTO getById(Long id) {
        Payment payment = paymentRepository.getById(id);
        if (payment == null) {
            return null;
        }
        return toResponseDTO(payment);
    }

    /**
     * Obtiene todos los pagos de un usuario con estadísticas.
     * Incluye total de pagos y monto total gastado.
     * 
     * @param userId ID del usuario
     * @return UserPaymentsResponseDTO con pagos y totales
     */
    @Override
    public UserPaymentsResponseDTO getByUserId(Long userId) {
        List<Payment> payments = paymentRepository.getByUserId(userId);
        return toUserPaymentsDTO(userId, payments);
    }

    /**
     * Crea un nuevo pago.
     * 
     * @param dto Datos del pago a crear (userId, bookId, quantity)
     * @return PaymentResponseDTO del pago creado
     * @throws IllegalArgumentException si los datos son inválidos o stock
     *                                  insuficiente
     * @throws RuntimeException         si el libro no existe o servicio no
     *                                  disponible
     */
    @Override
    public PaymentResponseDTO create(PaymentRequestDTO dto) {
        // Validación 1: userId debe ser mayor a 0
        if (dto.getUserId() == null || dto.getUserId() <= 0) {
            throw new IllegalArgumentException("userId debe ser mayor a 0");
        }

        // Validación 2: bookId debe ser mayor a 0
        if (dto.getBookId() == null || dto.getBookId() <= 0) {
            throw new IllegalArgumentException("bookId debe ser mayor a 0");
        }

        // Validación 3: quantity debe ser mayor a 0
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("quantity debe ser mayor a 0");
        }

        // 1. Llamar a MS Catalogue para verificar disponibilidad
        BookAvailabilityDTO book;
        try {
            book = catalogueClient.checkAvailability(dto.getBookId());
        } catch (HttpClientErrorException.NotFound e) {
            // Libro no existe en catálogo
            throw new BookNotFoundException("El libro con ID " + dto.getBookId() + " no existe");
        } catch (HttpClientErrorException e) {
            // Error 4xx distinto de 404
            throw new IllegalArgumentException("No fue posible validar el libro solicitado");
        } catch (Exception e) {
            // Error de conexión u otros fallos
            throw new RuntimeException("El servicio de catálogo no está disponible");
        }
        // 2. Validar que el libro esté marcado como visible/disponible
        if (Boolean.FALSE.equals(book.getAvailable())) {
            throw new IllegalArgumentException("El libro '" + book.getTitle() + "' no está disponible para la venta");
        }
        // 3. Validar Stock suficiente
        if (book.getStock() < dto.getQuantity()) {
            throw new IllegalArgumentException("Stock insuficiente para el libro '" + book.getTitle() +
                    "'. Solicitado: " + dto.getQuantity() + ", Disponible: " + book.getStock());
        }
        // Usar datos REALES del libro
        String bookTitle = book.getTitle();
        String bookIsbn = book.getIsbn();
        BigDecimal unitPrice = book.getPrice();

        // Usar el helper toEntity() para crear el Payment
        Payment payment = toEntity(dto, bookTitle, bookIsbn, unitPrice);

        // Guardar en la base de datos
        Payment savedPayment = paymentRepository.save(payment);

        // Decremento del stock en MS Catalogue
        try {
            // Llamar a MS Catalogue para restar el stock
            catalogueClient.decrementStock(dto.getBookId(), dto.getQuantity());
        } catch (Exception e) {
            // FALLÓ LA ACTUALIZACIÓN DE STOCK
            // ROLLBACK MANUAL: Borrar el pago que acabamos de crear para no dejar datos
            // inconsistentes
            paymentRepository.delete(savedPayment.getId());

            throw new RuntimeException(
                    "Error al actualizar el stock. Se ha cancelado el pago. Error: " + e.getMessage());
        }
        // Si todo salió bien, retornamos el pago creado
        return toResponseDTO(savedPayment);
    }

    /**
     * Actualiza el estado de un pago.
     * 
     * @param id  ID del pago
     * @param dto Nuevo estado
     * @return PaymentResponseDTO actualizado, o null si no se encuentra
     */
    @Override
    public PaymentResponseDTO updateStatus(Long id, PaymentStatusDTO dto) {
        // Buscar el pago
        Payment payment = paymentRepository.getById(id);
        if (payment == null) {
            return null; // Retornamos null si no existe
        }
        // Actualizar estado
        payment.setStatus(dto.getStatus());
        // Guardar cambios
        Payment updatedPayment = paymentRepository.save(payment);
        // Retornar DTO
        return toResponseDTO(updatedPayment);
    }

    /**
     * Cancela un pago y restaura el stock si es necesario.
     * 
     * @param id ID del pago a cancelar
     * @return true si se canceló correctamente, false si no existe o ya estaba
     *         cancelado
     * @throws RuntimeException si falla la restauración de stock
     */
    @Override
    public boolean cancelPayment(Long id) {
        // Buscar el pago
        Payment payment = paymentRepository.getById(id);
        if (payment == null) {
            return false; // Retornamos false si no existe
        }
        // Si ya está cancelado, devolvemos success (idempotencia) o false según lógica
        // de negocio.
        // En BookController delete devuelve false si no existe.
        // Aquí si ya está cancelado, podríamos decir que ya está hecho, pero para
        // seguir lógica estricta:
        if ("CANCELLED".equals(payment.getStatus())) {
            return false; // Ya estaba cancelado
        }
        // Restaurar stock en MS Catalogue (solo si ya se había descontado lo lógico)
        // Asumimos que PENDING y APPROVED ya descontaron stock al crearse
        try {
            catalogueClient.restoreStock(payment.getBookId(), payment.getQuantity());
        } catch (Exception e) {
            // Si falla la restauración, no podemos cancelar el pago (inconsistencia)
            throw new RuntimeException(
                    "Error al restaurar el stock. No se puede cancelar el pago. Error: " + e.getMessage());
        }
        // Actualizar estado a CANCELLED
        payment.setStatus("CANCELLED");
        paymentRepository.save(payment);
        return true;
    }

    /**
     * Busca pagos con filtros dinámicos usando Specifications.
     * 
     * @param userId ID del usuario (opcional)
     * @param bookId ID del libro (opcional)
     * @param status Estado del pago (opcional)
     * @return Lista de pagos que cumplen con los filtros
     */
    @Override
    public List<PaymentResponseDTO> search(Long userId, Long bookId, String status) {
        // Construir la Specification usando el método estático que creamos
        Specification<Payment> spec = PaymentSpecification.filterBy(userId, bookId, status);
        // Ejecutar la búsqueda en la BD usando el Repository
        List<Payment> payments = paymentRepository.search(spec);
        // Convertir a DTOs
        return payments.stream()
                .map(this::toResponseDTO)
                .toList();
    }
    // MÉTODOS HELPER

    /**
     * Convierte una entidad Payment a PaymentResponseDTO.
     *
     * @param payment Entidad Payment de la base de datos
     * @return PaymentResponseDTO para enviar al cliente
     */
    private PaymentResponseDTO toResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getUserId(),
                payment.getBookId(),
                payment.getBookTitle(),
                payment.getBookIsbn(),
                payment.getQuantity(),
                payment.getUnitPrice(),
                payment.getTotalPrice(),
                payment.getPurchaseDate(),
                payment.getStatus());
    }

    /**
     * Convierte un PaymentRequestDTO a entidad Payment.
     * 
     * @param dto       DTO recibido del cliente
     * @param bookTitle Título del libro (obtenido de MS Catalogue)
     * @param bookIsbn  ISBN del libro (obtenido de MS Catalogue)
     * @param unitPrice Precio unitario (obtenido de MS Catalogue)
     * @return Entidad Payment lista para guardar en BD
     */
    private Payment toEntity(PaymentRequestDTO dto, String bookTitle, String bookIsbn, BigDecimal unitPrice) {
        // Multiplica precio por cantidad
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));

        return Payment.builder()
                .userId(dto.getUserId())
                .bookId(dto.getBookId())
                .bookTitle(bookTitle)
                .bookIsbn(bookIsbn)
                .quantity(dto.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .purchaseDate(LocalDateTime.now())
                .status("COMPLETED")
                .build();
    }

    /**
     * Convierte una lista de pagos a UserPaymentsResponseDTO.
     * Calcula totales automáticamente.
     *
     * @param userId   ID del usuario
     * @param payments Lista de pagos del usuario
     * @return UserPaymentsResponseDTO con pagos y estadísticas
     */
    private UserPaymentsResponseDTO toUserPaymentsDTO(Long userId, List<Payment> payments) {
        // Convierte cada payment a DTO
        List<PaymentResponseDTO> paymentDTOs = payments.stream()
                .map(this::toResponseDTO)
                .toList();

        // Cuenta el número de pagos
        int totalPayments = payments.size();

        // Suma el total gastado
        BigDecimal totalAmountSpent = payments.stream()
                .map(Payment::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new UserPaymentsResponseDTO(
                userId, // ID del usuario
                paymentDTOs, // Lista de pagos del usuario
                totalPayments, // Número de pagos
                totalAmountSpent); // Total gastado
    }
}
