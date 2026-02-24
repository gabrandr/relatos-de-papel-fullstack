package com.relatosdepapel.ms_books_catalogue.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.relatosdepapel.ms_books_catalogue.dto.AvailabilityResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookFacetsResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookPatchDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookRequestDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.ErrorResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.StockUpdateDTO;
import com.relatosdepapel.ms_books_catalogue.service.BookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        BookResponseDTO book = bookService.getById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookRequestDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El título no puede estar vacío"));
        }
        if (dto.getAuthor() == null || dto.getAuthor().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El autor no puede estar vacío"));
        }
        if (dto.getIsbn() == null || dto.getIsbn().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El ISBN no puede estar vacío"));
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El precio debe ser mayor a 0"));
        }
        if (dto.getStock() == null || dto.getStock() < 0) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El stock no puede ser negativo"));
        }

        try {
            BookResponseDTO createdBook = bookService.create(dto);
            return ResponseEntity.status(201).body(createdBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(409, "El ISBN ya existe"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookRequestDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El título no puede estar vacío"));
        }
        if (dto.getAuthor() == null || dto.getAuthor().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El autor no puede estar vacío"));
        }
        if (dto.getIsbn() == null || dto.getIsbn().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El ISBN no puede estar vacío"));
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El precio debe ser mayor a 0"));
        }
        if (dto.getStock() == null || dto.getStock() < 0) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "El stock no puede ser negativo"));
        }

        BookResponseDTO updatedBook = bookService.update(id, dto);
        if (updatedBook == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBook);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookResponseDTO> patchBook(@PathVariable Long id, @RequestBody BookPatchDTO dto) {
        BookResponseDTO updatedBook = bookService.patch(id, dto);
        if (updatedBook == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Integer ratingMin,
            @RequestParam(required = false) Integer ratingMax,
            @RequestParam(required = false) Boolean visible,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDate publicationDateFrom,
            @RequestParam(required = false) LocalDate publicationDateTo,
            @RequestParam(required = false) Integer minStock) {

        List<BookResponseDTO> results = bookService.search(
                title,
                author,
                category,
                isbn,
                ratingMin,
                ratingMax,
                visible,
                minPrice,
                maxPrice,
                publicationDateFrom,
                publicationDateTo,
                minStock);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/suggest")
    public ResponseEntity<List<String>> suggest(@RequestParam String text,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(bookService.suggest(text, size));
    }

    @GetMapping("/search/facets")
    public ResponseEntity<BookFacetsResponseDTO> facets(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Boolean visible) {
        return ResponseEntity.ok(bookService.facets(text, visible));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponseDTO> checkAvailability(@PathVariable Long id) {
        AvailabilityResponseDTO availability = bookService.checkAvailability(id);
        if (availability == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(availability);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody StockUpdateDTO dto) {
        if (dto.getQuantity() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "La cantidad no puede ser nula"));
        }

        BookResponseDTO book = bookService.getById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        int currentStock = book.getStock() == null ? 0 : book.getStock();
        if (dto.getQuantity() < 0 && currentStock + dto.getQuantity() < 0) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "Stock insuficiente"));
        }

        BookResponseDTO updatedBook = bookService.updateStock(id, dto);
        if (updatedBook == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBook);
    }
}
