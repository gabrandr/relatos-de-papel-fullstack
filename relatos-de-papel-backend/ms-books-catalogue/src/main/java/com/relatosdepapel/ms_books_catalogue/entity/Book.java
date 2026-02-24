package com.relatosdepapel.ms_books_catalogue.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.relatosdepapel.ms_books_catalogue.utils.Consts;
import com.relatosdepapel.ms_books_catalogue.dto.BookRequestDTO;

/**
 * Entidad que representa un libro
 */
@Entity // Indica que es una entidad
@Table(name = "books") // Indica el nombre de la tabla
@Data // Genera los getters y setters
@NoArgsConstructor // Genera el constructor vacio
@AllArgsConstructor // Genera el constructor con todos los atributos
@Builder // Patron Builder
public class Book {
    /**
     * Identificador único del libro
     */
    @Id // Marca clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera ID autoincrementable
    private Long id;

    /**
     * Título del libro
     */
    @Column(name = Consts.TITLE, nullable = false)
    private String title;

    /**
     * Autor del libro
     */
    @Column(name = Consts.AUTHOR, nullable = false)
    private String author;

    /**
     * Fecha de publicación del libro
     */
    @Column(name = Consts.PUBLICATION_DATE)
    private LocalDate publicationDate;

    /**
     * Categoría o género del libro
     */
    @Column(name = Consts.CATEGORY)
    private String category;

    /**
     * ISBN del libro (identificador único internacional)
     */
    @Column(name = Consts.ISBN, unique = true, nullable = false)
    private String isbn;

    /**
     * Valoración del libro (escala de 1 a 5)
     */
    @Column(name = Consts.RATING)
    private Integer rating;

    /**
     * Indica si el libro es visible en el catálogo (true) o está oculto (false)
     */
    @Column(name = Consts.VISIBLE, nullable = false)
    private Boolean visible;

    /**
     * Cantidad disponible en inventario
     */
    @Column(name = Consts.STOCK, nullable = false)
    private Integer stock;

    /**
     * Precio del libro (precision: 10 dígitos totales, scale: 2 decimales)
     */
    @Column(name = Consts.PRICE, nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Metodo para actualizar Book desde un BookRequestDTO - no se actualiza el ID e
    // ISBN
    public void updateFromDTO(BookRequestDTO dto) {
        this.title = dto.getTitle();
        this.author = dto.getAuthor();
        this.publicationDate = dto.getPublicationDate();
        this.category = dto.getCategory();
        this.rating = dto.getRating();
        this.visible = dto.getVisible();
        this.stock = dto.getStock();
        this.price = dto.getPrice();
    }
}
