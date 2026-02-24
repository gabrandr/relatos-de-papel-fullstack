package com.relatosdepapel.ms_books_catalogue.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.relatosdepapel.ms_books_catalogue.dto.AvailabilityResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookPatchDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookRequestDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.StockUpdateDTO;
import com.relatosdepapel.ms_books_catalogue.entity.Book;
import com.relatosdepapel.ms_books_catalogue.repository.BookRepository;
import org.springframework.data.jpa.domain.Specification;
import com.relatosdepapel.ms_books_catalogue.specification.BookSpecification;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de libros.
 * Contiene la lógica de negocio para la gestión del catálogo.
 */
@Service // Indica que es un servicio
@RequiredArgsConstructor // Inyección de dependencias sin constructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository; // Dependencia del repositorio

    // METODOS CRUD

    /**
     * Obtiene todos los libros del catálogo.
     * 
     * @return Lista de todos los libros como BookResponseDTO
     */
    @Override
    public List<BookResponseDTO> getAll() {
        // devolver solo libros visibles
        return bookRepository.findVisibleBooks().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca un libro por ID.
     * 
     * @param id ID del libro a buscar
     * @return BookResponseDTO si existe, null si no existe
     */
    @Override
    public BookResponseDTO getById(Long id) {
        Book book = bookRepository.getById(id); // buscar el libro por ID
        if (book == null) {
            return null; // si no existe, devolver null
        }
        return toResponseDTO(book); // convertir la entidad a DTO y devolverlo
    }

    /**
     * Crea un nuevo libro en el catálogo.
     * 
     * @param dto Datos del libro a crear
     * @return BookResponseDTO del libro creado (con ID generado)
     * @throws IllegalArgumentException si el ISBN ya existe
     */
    @Override
    public BookResponseDTO create(BookRequestDTO dto) {
        // validar que el ISBN no exista
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new IllegalArgumentException("El ISBN ya existe: " + dto.getIsbn());
        }
        // convertir el DTO a entidad
        Book book = toEntity(dto);
        // guardar el libro
        Book savedBook = bookRepository.save(book);
        // convertir la entidad a DTO y devolverlo
        return toResponseDTO(savedBook);
    }

    /**
     * Actualiza un libro existente (PUT - actualización completa).
     * Actualiza todos los campos excepto ID e ISBN.
     * 
     * @param id  ID del libro a actualizar
     * @param dto Nuevos datos del libro
     * @return BookResponseDTO actualizado o null si no existe
     */
    @Override
    public BookResponseDTO update(Long id, BookRequestDTO dto) {
        Book book = bookRepository.getById(id); // buscar el libro por ID
        if (book == null) {
            return null; // si no existe, devolver null
        }
        book.updateFromDTO(dto); // actualizar el libro con los datos del DTO
        Book updatedBook = bookRepository.save(book); // guardar el libro actualizado
        return toResponseDTO(updatedBook); // convertir la entidad a DTO y devolverlo
    }

    /**
     * Actualiza parcialmente un libro existente (PATCH - actualización selectiva).
     * Solo actualiza los campos proporcionados en el DTO.
     * 
     * @param id  ID del libro a actualizar
     * @param dto Campos a actualizar (solo los proporcionados)
     * @return BookResponseDTO actualizado o null si no existe
     */
    @Override
    public BookResponseDTO patch(Long id, BookPatchDTO dto) {
        // busca libro por ID
        Book book = bookRepository.getById(id);
        // si no existe retornar null
        if (book == null) {
            return null;
        }
        // actualizar campos opcionales
        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getAuthor() != null) {
            book.setAuthor(dto.getAuthor());
        }
        if (dto.getPublicationDate() != null) {
            book.setPublicationDate(dto.getPublicationDate());
        }
        if (dto.getCategory() != null) {
            book.setCategory(dto.getCategory());
        }
        if (dto.getRating() != null) {
            book.setRating(dto.getRating());
        }
        if (dto.getVisible() != null) {
            book.setVisible(dto.getVisible());
        }
        if (dto.getStock() != null) {
            book.setStock(dto.getStock());
        }
        if (dto.getPrice() != null) {
            book.setPrice(dto.getPrice());
        }
        // guardar el libro actualizado
        Book patchedBook = bookRepository.save(book);
        // convertir la entidad a DTO y devolverlo
        return toResponseDTO(patchedBook);
    }

    /**
     * Elimina un libro del catálogo.
     * 
     * @param id ID del libro a eliminar
     * @return true si se eliminó, false si no existía
     */
    @Override
    public boolean delete(Long id) {
        // busca libro por ID
        Book book = bookRepository.getById(id);
        // si no existe retornar false
        if (book == null) {
            return false;
        }
        // eliminar el libro
        bookRepository.delete(book);
        // retornar true
        return true;
    }

    // METODOS DE BUSQUEDA DINAMICA

    /**
     * Búsqueda dinámica de libros con múltiples filtros opcionales.
     * Los filtros se combinan con AND (todos deben cumplirse).
     * 
     * @param title               Filtro por título (parcial, case-insensitive)
     * @param author              Filtro por autor (parcial, case-insensitive)
     * @param category            Filtro por categoría (exacto)
     * @param isbn                Filtro por ISBN (exacto)
     * @param ratingMin           Rating mínimo
     * @param ratingMax           Rating máximo
     * @param visible             Filtro por visibilidad
     * @param minPrice            Precio mínimo
     * @param maxPrice            Precio máximo
     * @param publicationDateFrom Fecha de publicación desde
     * @param publicationDateTo   Fecha de publicación hasta
     * @param minStock            Stock mínimo
     * @return Lista de libros que cumplen todos los filtros aplicados
     */
    @Override
    public List<BookResponseDTO> search(String title, String author, String category, String isbn, Integer ratingMin,
            Integer ratingMax, Boolean visible, BigDecimal minPrice, BigDecimal maxPrice, LocalDate publicationDateFrom,
            LocalDate publicationDateTo, Integer minStock) {
        // crear specification base vacio
        Specification<Book> spec = (root, query, criteriaBuilder) -> null;
        // agregar filtros segun parametros proporcionados

        // filtro por titulo - busqueda parcial
        if (title != null) {
            spec = spec.and(BookSpecification.titleContains(title));
        }
        // filtro por autor - busqueda parcial
        if (author != null) {
            spec = spec.and(BookSpecification.authorContains(author));
        }
        // filtro por categoria - busqueda exacta
        if (category != null) {
            spec = spec.and(BookSpecification.hasCategory(category));
        }
        // filtro por ISBN - busqueda exacta
        if (isbn != null) {
            spec = spec.and(BookSpecification.hasIsbn(isbn));
        }
        // filtro por rating minimo
        if (ratingMin != null) {
            spec = spec.and(BookSpecification.ratingGreaterThanOrEqual(ratingMin));
        }
        // filtro por rating maximo
        if (ratingMax != null) {
            spec = spec.and(BookSpecification.ratingLessThanOrEqual(ratingMax));
        }
        // filtro por visibilidad
        if (visible != null) {
            spec = spec.and(BookSpecification.isVisible(visible));
        }
        // filtro por precio minimo
        if (minPrice != null) {
            spec = spec.and(BookSpecification.priceGreaterThanOrEqual(minPrice));
        }
        // filtro por precio maximo
        if (maxPrice != null) {
            spec = spec.and(BookSpecification.priceLessThanOrEqual(maxPrice));
        }
        // filtro por fecha de publicacion desde
        if (publicationDateFrom != null) {
            spec = spec.and(BookSpecification.publicationDateAfterOrEqual(publicationDateFrom));
        }
        // filtro por fecha de publicacion hasta
        if (publicationDateTo != null) {
            spec = spec.and(BookSpecification.publicationDateBeforeOrEqual(publicationDateTo));
        }
        // filtro por stock minimo
        if (minStock != null) {
            spec = spec.and(BookSpecification.stockGreaterThanOrEqual(minStock));
        }
        // ejecutar busqueda
        List<Book> books = bookRepository.search(spec);
        // convertir a DTO y retornar
        return books.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // METODOS ESPECIALES

    /**
     * Verifica la disponibilidad de un libro para compra.
     * Un libro está disponible si es visible Y tiene stock.
     * 
     * @param id ID del libro a verificar
     * @return AvailabilityResponseDTO con info de disponibilidad, o null si no
     *         existe
     */
    @Override
    public AvailabilityResponseDTO checkAvailability(Long id) {
        // busca libro por ID
        Book book = bookRepository.getById(id);
        // si no existe retornar null
        if (book == null) {
            return null;
        }
        // verificar disponibilidad
        boolean available = book.getVisible() && book.getStock() > 0;
        // retornar AvailabilityResponseDTO
        return new AvailabilityResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                available, // available calculado en base a visible y stock
                book.getVisible(),
                book.getStock(),
                book.getPrice());
    }

    /**
     * Actualiza el stock de un libro.
     * 
     * @param id  ID del libro
     * @param dto Cantidad a sumar (positivo) o restar (negativo)
     * @return BookResponseDTO actualizado o null si no existe
     * @throws IllegalArgumentException si el stock resultante sería negativo
     */
    @Override
    public BookResponseDTO updateStock(Long id, StockUpdateDTO dto) {
        // busca libro por ID
        Book book = bookRepository.getById(id);
        // si no existe retornar null
        if (book == null) {
            return null;
        }
        // calcular nuevo stock
        int newStock = book.getStock() + dto.getQuantity();
        // verificar que el stock resultante no sea negativo
        if (newStock < 0) {
            throw new IllegalArgumentException("El stock resultante no puede ser negativo. Stock actual: "
                    + book.getStock() + ", Cantidad a restar: " + dto.getQuantity());
        }
        // actualizar el stock
        book.setStock(newStock);
        // guardar el libro actualizado
        Book updatedBook = bookRepository.save(book);
        // convertir la entidad a DTO y devolverlo
        return toResponseDTO(updatedBook);
    }

    // METODOS HELPERS

    /**
     * Convierte una entidad Book a BookResponseDTO.
     * 
     * @param book Entidad Book de la base de datos
     * @return BookResponseDTO para enviar al cliente
     */
    private BookResponseDTO toResponseDTO(Book book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationDate(),
                book.getCategory(),
                book.getIsbn(),
                book.getRating(),
                book.getVisible(),
                book.getStock(),
                book.getPrice());
    }

    /**
     * Convierte un BookRequestDTO a entidad Book.
     * Nota: El ID se genera automáticamente por JPA, no se incluye.
     * 
     * @param dto DTO recibido del cliente
     * @return Entidad Book lista para guardar en BD
     */
    private Book toEntity(BookRequestDTO dto) {
        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .publicationDate(dto.getPublicationDate())
                .category(dto.getCategory())
                .isbn(dto.getIsbn())
                .rating(dto.getRating())
                .visible(dto.getVisible())
                .stock(dto.getStock())
                .price(dto.getPrice())
                .build();
    }

}
