package com.relatosdepapel.ms_books_catalogue.controller;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

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

import com.relatosdepapel.ms_books_catalogue.dto.BookPatchDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookRequestDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.ErrorResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.StockUpdateDTO;
import com.relatosdepapel.ms_books_catalogue.dto.AvailabilityResponseDTO;
import com.relatosdepapel.ms_books_catalogue.service.BookService;

import lombok.RequiredArgsConstructor;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/books") // Mapea las peticiones a esta URL
@RequiredArgsConstructor // Constructor con dependencias inyectadas
public class BookController {
    private final BookService bookService; // Inyección de dependencia del BookService

    // GET ENDPOINTS

    /**
     * GET /api/books
     * Obtiene todos los libros del catálogo
     *
     * @return 200 OK con lista de libros
     */
    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        List<BookResponseDTO> books = bookService.getAll(); // obtiene todos los libros
        return ResponseEntity.ok(books); // 200 OK
    }

    /**
     * GET /api/books/{id}
     * Obtiene un libro por su ID
     *
     * @param id ID del libro
     * @return 200 OK con el libro encontrado
     *         404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        BookResponseDTO book = bookService.getById(id); // obtiene el libro por ID
        if (book == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(book); // 200 OK
    }

    // POST ENDPOINT

    /**
     * POST /api/books
     * Crea un nuevo libro en el catálogo
     *
     * @param dto Datos del libro a crear
     * @return 201 Created con el libro creado
     *         400 Bad Request si hay errores de validación
     */
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookRequestDTO dto) {
        // Validación 1: título no vacío
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El título no puede estar vacío"));
        }

        // Validación 2: autor no vacío
        if (dto.getAuthor() == null || dto.getAuthor().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El autor no puede estar vacío"));
        }

        // Validación 3: ISBN no vacío
        if (dto.getIsbn() == null || dto.getIsbn().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El ISBN no puede estar vacío"));
        }

        // Validación 4: precio positivo
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El precio debe ser mayor a 0"));
        }

        // Validación 5: stock no negativo
        if (dto.getStock() == null || dto.getStock() < 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El stock no puede ser negativo"));
        }
        try {
            BookResponseDTO createdBook = bookService.create(dto); // crea el libro
            return ResponseEntity.status(201).body(createdBook); // 201 Created
        } catch (IllegalArgumentException e) {
            // ISBN duplicado u otra validación de negocio
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "El ISBN ya existe"));
        }
    }

    // PUT ENDPOINT

    /**
     * PUT /api/books/{id}
     * Actualiza un libro completo (todos los campos)
     *
     * @param id  ID del libro a actualizar
     * @param dto Datos completos del libro
     * @return 200 OK con el libro actualizado
     *         400 Bad Request si hay errores de validación
     *         404 Not Found si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookRequestDTO dto) {
        // Validación 1: título no vacío
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El título no puede estar vacío"));
        }

        // Validación 2: autor no vacío
        if (dto.getAuthor() == null || dto.getAuthor().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El autor no puede estar vacío"));
        }

        // Validación 3: ISBN no vacío
        if (dto.getIsbn() == null || dto.getIsbn().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El ISBN no puede estar vacío"));
        }

        // Validación 4: precio positivo
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El precio debe ser mayor a 0"));
        }

        // Validación 5: stock no negativo
        if (dto.getStock() == null || dto.getStock() < 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "El stock no puede ser negativo"));
        }

        BookResponseDTO updatedBook = bookService.update(id, dto); // actualiza el libro
        if (updatedBook == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(updatedBook); // 200 OK
    }

    // PATCH ENDPOINT

    /**
     * PATCH /api/books/{id}
     * Actualiza un libro parcialmente (solo campos enviados)
     *
     * @param id  ID del libro a actualizar
     * @param dto Datos parciales del libro
     * @return 200 OK con el libro actualizado
     *         404 Not Found si no existe
     */
    @PatchMapping("/{id}")
    public ResponseEntity<BookResponseDTO> patchBook(@PathVariable Long id, @RequestBody BookPatchDTO dto) {
        BookResponseDTO updatedBook = bookService.patch(id, dto); // actualiza el libro
        if (updatedBook == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(updatedBook); // 200 OK
    }

    // DELETE ENDPOINT

    /**
     * DELETE /api/books/{id}
     * Elimina un libro del catálogo
     *
     * @param id ID del libro a eliminar
     * @return 204 No Content si se eliminó correctamente
     *         404 Not Found si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // SEARCH ENDPOINT

    /**
     * GET /api/books/search
     * Búsqueda dinámica de libros con múltiples filtros
     *
     * @param title               Título (búsqueda parcial)
     * @param author              Autor (búsqueda parcial)
     * @param category            Categoría exacta
     * @param isbn                ISBN exacto
     * @param ratingMin           Valoración mínima
     * @param ratingMax           Valoración máxima
     * @param visible             Visibilidad
     * @param minPrice            Precio mínimo
     * @param maxPrice            Precio máximo
     * @param minStock            Stock mínimo
     * @param publicationDateFrom Fecha de publicación desde
     * @param publicationDateTo   Fecha de publicación hasta
     * @return 200 OK con lista de libros que cumplen los criterios
     */
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
        List<BookResponseDTO> results = bookService.search(title, author, category, isbn, ratingMin, ratingMax, visible,
                minPrice, maxPrice, publicationDateFrom, publicationDateTo, minStock); // busca libros con los filtros
        return ResponseEntity.ok(results); // 200 OK
    }

    // AVAILABILITY ENDPOINT

    /**
     * GET /api/books/{id}/availability
     * Verifica la disponibilidad de un libro
     * Usado por ms-books-payments antes de crear una compra
     *
     * @param id ID del libro
     * @return 200 OK con información de disponibilidad
     *         404 Not Found si no existe
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponseDTO> checkAvailability(@PathVariable Long id) {
        AvailabilityResponseDTO availability = bookService.checkAvailability(id); // verifica la disponibilidad
        if (availability == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(availability); // 200 OK
    }

    // STOCK ENDPOINT

    /**
     * PATCH /api/books/{id}/stock
     * Actualiza el stock de un libro (incremento o decremento)
     * Usado por ms-books-payments para manejar inventario
     *
     * @param id  ID del libro
     * @param dto Objeto con quantity (positivo = incremento, negativo = decremento)
     * @return 200 OK con el libro actualizado
     *         400 Bad Request si hay errores
     *         404 Not Found si no existe
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody StockUpdateDTO dto) {
        // Validación 1: quantity no null
        if (dto.getQuantity() == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "La cantidad no puede ser nula"));
        }
        // Verificar que el libro existe
        BookResponseDTO book = bookService.getById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        // Validación 2: stock suficiente si es decremento
        if (dto.getQuantity() < 0 && book.getStock() + dto.getQuantity() < 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(400, "Stock insuficiente"));
        }
        BookResponseDTO updatedBook = bookService.updateStock(id, dto); // actualiza el stock
        if (updatedBook == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(updatedBook); // 200 OK
    }
}
