package com.relatosdepapel.ms_books_catalogue.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.relatosdepapel.ms_books_catalogue.dto.AvailabilityResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookFacetsResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookPatchDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookRequestDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.StockUpdateDTO;

/**
 * Contrato de negocio del catálogo de libros.
 * Centraliza operaciones CRUD, búsqueda, sugerencias, facets y stock.
 */
public interface BookService {
    /**
     * Obtiene el catálogo visible para el frontend.
     *
     * @return libros visibles.
     */
    List<BookResponseDTO> getAll();

    /**
     * Obtiene un libro por id.
     *
     * @param id identificador de libro.
     * @return libro encontrado o `null` si no existe.
     */
    BookResponseDTO getById(Long id);

    /**
     * Crea un libro nuevo validando unicidad de ISBN.
     *
     * @param dto payload de creación.
     * @return libro persistido.
     */
    BookResponseDTO create(BookRequestDTO dto);

    /**
     * Reemplaza los campos editables de un libro existente.
     *
     * @param id identificador de libro.
     * @param dto payload de actualización completa.
     * @return libro actualizado o `null` si no existe.
     */
    BookResponseDTO update(Long id, BookRequestDTO dto);

    /**
     * Actualiza parcialmente un libro existente.
     *
     * @param id identificador de libro.
     * @param dto payload parcial.
     * @return libro actualizado o `null` si no existe.
     */
    BookResponseDTO patch(Long id, BookPatchDTO dto);

    /**
     * Elimina un libro por id.
     *
     * @param id identificador de libro.
     * @return `true` si se elimina, `false` si no existe.
     */
    boolean delete(Long id);

    /**
     * Ejecuta búsqueda compuesta por texto y filtros de negocio.
     *
     * @param title texto por título.
     * @param author texto por autor.
     * @param category categoría exacta.
     * @param isbn isbn exacto.
     * @param ratingMin rating mínimo.
     * @param ratingMax rating máximo.
     * @param visible visibilidad esperada.
     * @param minPrice precio mínimo.
     * @param maxPrice precio máximo.
     * @param publicationDateFrom fecha publicación inicial.
     * @param publicationDateTo fecha publicación final.
     * @param minStock stock mínimo.
     * @return libros que cumplen criterios.
     */
    List<BookResponseDTO> search(String title, String author, String category, String isbn, Integer ratingMin,
            Integer ratingMax, Boolean visible, BigDecimal minPrice, BigDecimal maxPrice, LocalDate publicationDateFrom,
            LocalDate publicationDateTo, Integer minStock);

    /**
     * Retorna sugerencias de autocompletado para el buscador.
     *
     * @param text texto parcial.
     * @param size cantidad máxima solicitada.
     * @return títulos sugeridos.
     */
    List<String> suggest(String text, Integer size);

    /**
     * Calcula facets de categorías y autores sobre una consulta.
     *
     * @param text texto opcional para acotar agregaciones.
     * @param visible filtro opcional por visibilidad.
     * @param category filtro opcional de categoría.
     * @param author filtro opcional de autor.
     * @return estructura agregada para filtros de UI.
     */
    BookFacetsResponseDTO facets(String text, Boolean visible, String category, String author);

    /**
     * Consulta disponibilidad de un libro para flujo de pagos.
     *
     * @param id identificador de libro.
     * @return estado de disponibilidad o `null` si no existe.
     */
    AvailabilityResponseDTO checkAvailability(Long id);

    /**
     * Ajusta el stock en base a una cantidad relativa (positiva o negativa).
     *
     * @param id identificador de libro.
     * @param dto cantidad de ajuste.
     * @return libro con stock actualizado o `null` si no existe.
     */
    BookResponseDTO updateStock(Long id, StockUpdateDTO dto);
}
