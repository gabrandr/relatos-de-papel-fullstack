package com.relatosdepapel.ms_books_catalogue.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.relatosdepapel.ms_books_catalogue.dto.AvailabilityResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookFacetsResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookPatchDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookRequestDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.StockUpdateDTO;

import lombok.RequiredArgsConstructor;

/**
 * Implementación de negocio del catálogo basada en OpenSearch.
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final OpenSearchBookStore bookStore;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BookResponseDTO> getAll() {
        return bookStore.findAllVisible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookResponseDTO getById(Long id) {
        return bookStore.findById(id);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException cuando el ISBN ya existe.
     */
    @Override
    public BookResponseDTO create(BookRequestDTO dto) {
        if (bookStore.existsByIsbn(dto.getIsbn())) {
            throw new IllegalArgumentException("El ISBN ya existe: " + dto.getIsbn());
        }
        return bookStore.create(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookResponseDTO update(Long id, BookRequestDTO dto) {
        return bookStore.update(id, dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookResponseDTO patch(Long id, BookPatchDTO dto) {
        BookResponseDTO current = bookStore.findById(id);
        if (current == null) {
            return null;
        }

        if (dto.getTitle() != null) {
            current.setTitle(dto.getTitle());
        }
        if (dto.getAuthor() != null) {
            current.setAuthor(dto.getAuthor());
        }
        if (dto.getPublicationDate() != null) {
            current.setPublicationDate(dto.getPublicationDate());
        }
        if (dto.getCategory() != null) {
            current.setCategory(dto.getCategory());
        }
        if (dto.getRating() != null) {
            current.setRating(dto.getRating());
        }
        if (dto.getVisible() != null) {
            current.setVisible(dto.getVisible());
        }
        if (dto.getStock() != null) {
            current.setStock(dto.getStock());
        }
        if (dto.getPrice() != null) {
            current.setPrice(dto.getPrice());
        }

        return bookStore.save(current);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Long id) {
        return bookStore.delete(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BookResponseDTO> search(String title, String author, String category, String isbn, Integer ratingMin,
            Integer ratingMax, Boolean visible, BigDecimal minPrice, BigDecimal maxPrice, LocalDate publicationDateFrom,
            LocalDate publicationDateTo, Integer minStock) {
        return bookStore.search(title, author, category, isbn, ratingMin, ratingMax, visible, minPrice, maxPrice,
                publicationDateFrom, publicationDateTo, minStock);
    }

    /**
     * {@inheritDoc}
     * Limita el tamaño de respuesta entre 1 y 20 para proteger la consulta de suggest.
     */
    @Override
    public List<String> suggest(String text, Integer size) {
        int pageSize = (size == null || size <= 0) ? 8 : Math.min(size, 20);
        return bookStore.suggest(text, pageSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookFacetsResponseDTO facets(String text, Boolean visible, String category, String author) {
        return bookStore.facets(text, visible, category, author);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AvailabilityResponseDTO checkAvailability(Long id) {
        BookResponseDTO book = bookStore.findById(id);
        if (book == null) {
            return null;
        }

        boolean available = Boolean.TRUE.equals(book.getVisible()) && book.getStock() != null && book.getStock() > 0;
        return new AvailabilityResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                available,
                book.getVisible(),
                book.getStock(),
                book.getPrice());
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException cuando el ajuste deja stock negativo.
     */
    @Override
    public BookResponseDTO updateStock(Long id, StockUpdateDTO dto) {
        BookResponseDTO book = bookStore.findById(id);
        if (book == null) {
            return null;
        }

        int currentStock = book.getStock() == null ? 0 : book.getStock();
        int newStock = currentStock + dto.getQuantity();
        if (newStock < 0) {
            throw new IllegalArgumentException("El stock resultante no puede ser negativo");
        }

        book.setStock(newStock);
        return bookStore.save(book);
    }
}
