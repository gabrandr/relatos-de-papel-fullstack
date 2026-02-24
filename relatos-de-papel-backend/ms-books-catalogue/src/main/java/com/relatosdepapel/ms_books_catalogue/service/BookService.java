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

public interface BookService {
    List<BookResponseDTO> getAll();

    BookResponseDTO getById(Long id);

    BookResponseDTO create(BookRequestDTO dto);

    BookResponseDTO update(Long id, BookRequestDTO dto);

    BookResponseDTO patch(Long id, BookPatchDTO dto);

    boolean delete(Long id);

    List<BookResponseDTO> search(String title, String author, String category, String isbn, Integer ratingMin,
            Integer ratingMax, Boolean visible, BigDecimal minPrice, BigDecimal maxPrice, LocalDate publicationDateFrom,
            LocalDate publicationDateTo, Integer minStock);

    List<String> suggest(String text, Integer size);

    BookFacetsResponseDTO facets(String text, Boolean visible);

    AvailabilityResponseDTO checkAvailability(Long id);

    BookResponseDTO updateStock(Long id, StockUpdateDTO dto);
}
