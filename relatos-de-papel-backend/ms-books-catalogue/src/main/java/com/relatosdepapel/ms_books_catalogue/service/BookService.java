package com.relatosdepapel.ms_books_catalogue.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.relatosdepapel.ms_books_catalogue.dto.AvailabilityResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookPatchDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookRequestDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.StockUpdateDTO;

/**
 * Interface del servicio de libros.
 * Define los métodos de negocio para la gestión del catálogo.
 */
public interface BookService {
    // METODOS CRUD

    /**
     * Obtiene todos los libros (sin filtros).
     */
    List<BookResponseDTO> getAll();

    /**
     * Busca un libro por ID.
     * 
     * @return BookResponseDTO si existe, null si no existe
     */
    BookResponseDTO getById(Long id);

    /**
     * Crea un nuevo libro.
     * 
     * @throws IllegalArgumentException si el ISBN ya existe
     */
    BookResponseDTO create(BookRequestDTO dto);

    /**
     * Actualiza un libro completo (PUT).
     * 
     * @return BookResponseDTO actualizado o null si no existe
     */
    BookResponseDTO update(Long id, BookRequestDTO dto);

    /**
     * Actualiza parcialmente un libro (PATCH).
     * 
     * @return BookResponseDTO actualizado o null si no existe
     */
    BookResponseDTO patch(Long id, BookPatchDTO dto);

    /**
     * Elimina un libro por ID.
     * 
     * @return true si se eliminó, false si no existía
     */
    boolean delete(Long id);

    // METODO DE BUSQUEDA DINAMICA

    /**
     * Búsqueda avanzada con múltiples filtros opcionales.
     * Todos los parámetros son opcionales (pueden ser null).
     * Se combinan con AND si se proporcionan múltiples filtros.
     * 
     * @param title               Búsqueda parcial en título (case-insensitive)
     * @param author              Búsqueda parcial en autor (case-insensitive)
     * @param category            Búsqueda exacta por categoría
     * @param isbn                Búsqueda exacta por ISBN
     * @param ratingMin           Rating mínimo (>=)
     * @param ratingMax           Rating máximo (<=)
     * @param visible             Filtro de visibilidad (true/false/null)
     * @param minPrice            Precio mínimo (>=)
     * @param maxPrice            Precio máximo (<=)
     * @param publicationDateFrom Fecha de publicación desde (>=)
     * @param publicationDateTo   Fecha de publicación hasta (<=)
     * @param minStock            Stock mínimo (>=)
     * @return Lista de libros que cumplen los filtros (vacía si no hay resultados)
     */
    List<BookResponseDTO> search(String title, String author, String category, String isbn, Integer ratingMin,
            Integer ratingMax, Boolean visible, BigDecimal minPrice, BigDecimal maxPrice, LocalDate publicationDateFrom,
            LocalDate publicationDateTo, Integer minStock);

    // METODOS ESPECIALES

    /**
     * Verifica la disponibilidad de un libro.
     * Retorna información sobre si el libro existe, está visible y tiene stock.
     * 
     * @return AvailabilityResponseDTO con la información
     */
    AvailabilityResponseDTO checkAvailability(Long id);

    /**
     * Actualiza el stock de un libro.
     * Puede incrementar (quantity > 0) o decrementar (quantity < 0).
     * 
     * @param dto Contiene la cantidad a sumar/restar
     * @return BookResponseDTO actualizado o null si no existe
     * @throws IllegalArgumentException si el stock resultante sería negativo
     */
    BookResponseDTO updateStock(Long id, StockUpdateDTO dto);
}
