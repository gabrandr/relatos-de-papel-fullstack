package com.relatosdepapel.ms_books_payments.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import com.relatosdepapel.ms_books_payments.utils.Consts;

/**
 * Entidad que representa un pago/compra de libro en el sistema.
 * Almacena información desnormalizada del libro para mantener un historial
 * completo aunque el libro sea modificado o eliminado del catálogo.
 */
@Entity // Indica que esta clase es una entidad JPA
@Table(name = "payments") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals, hashCode automáticamente
@NoArgsConstructor // Constructor sin argumentos (requerido por JPA)
@AllArgsConstructor // Constructor con todos los argumentos (requerido por Lombok)
@Builder // Patron Builder
public class Payment {
    /**
     * Identificador único del pago
     */
    @Id // Indica que este campo es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementado por la BD
    private Long id;
    /**
     * ID del usuario que realizó la compra
     */
    @Column(name = Consts.USER_ID, nullable = false) // Campo obligatorio
    private Long userId;
    /**
     * ID del libro comprado (referencia a MS Books Catalogue)
     */
    @Column(name = Consts.BOOK_ID, nullable = false)
    private Long bookId;
    /**
     * Título del libro (desnormalizado para historial)
     */
    @Column(name = Consts.BOOK_TITLE, nullable = false)
    private String bookTitle;
    /**
     * ISBN del libro (desnormalizado para historial)
     */
    @Column(name = Consts.BOOK_ISBN, nullable = false)
    private String bookIsbn;
    /**
     * Cantidad de libros comprados
     */
    @Column(name = Consts.QUANTITY, nullable = false)
    private Integer quantity;
    /**
     * Precio unitario del libro al momento de la compra
     */
    @Column(name = Consts.UNIT_PRICE, nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    /**
     * Precio total de la compra (quantity × unitPrice)
     */
    @Column(name = Consts.TOTAL_PRICE, nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    /**
     * Fecha y hora en que se realizó la compra
     */
    @Column(name = Consts.PURCHASE_DATE, nullable = false)
    private LocalDateTime purchaseDate;
    /**
     * Estado del pago: COMPLETED (completado) o CANCELLED (cancelado)
     */
    @Column(name = Consts.STATUS, nullable = false)
    private String status;

}
