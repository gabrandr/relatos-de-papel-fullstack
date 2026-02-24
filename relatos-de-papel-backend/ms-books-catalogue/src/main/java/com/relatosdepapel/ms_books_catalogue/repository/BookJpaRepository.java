package com.relatosdepapel.ms_books_catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.relatosdepapel.ms_books_catalogue.entity.Book;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Book (Capa 1).
 * Extiende JpaRepository para operaciones CRUD básicas y
 * JpaSpecificationExecutor para búsquedas dinámicas y complejas.
 */
public interface BookJpaRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // Metodos personalizados - Query Methods
    /**
     * Busca libros por título (búsqueda parcial, case-insensitive).
     * Ejemplo: findByTitleContainingIgnoreCase("quijote")
     * SQL generado: WHERE LOWER(title) LIKE LOWER('%quijote%')
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Busca libros por autor (búsqueda parcial, case-insensitive).
     * Ejemplo: findByAuthorContainingIgnoreCase("cervantes")
     * SQL generado: WHERE LOWER(author) LIKE LOWER('%cervantes%')
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);

    /**
     * Busca libros por categoría (búsqueda exacta).
     * Ejemplo: findByCategory("Clásicos")
     * SQL generado: WHERE category = 'Clásicos'
     */
    List<Book> findByCategory(String category);

    /**
     * Busca un libro por ISBN (búsqueda exacta).
     * Ejemplo: findByIsbn("9788467033601")
     * SQL generado: WHERE isbn = '9788467033601'
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Busca todos los libros visibles.
     * Ejemplo: findByVisibleTrue()
     * SQL generado: WHERE visible = true
     */
    List<Book> findByVisibleTrue();

    /**
     * Verifica si existe un libro con el ISBN dado.
     * Ejemplo: existsByIsbn("9788467033601")
     * SQL generado: SELECT COUNT(*) > 0 FROM books WHERE isbn = '9788467033601'
     */
    boolean existsByIsbn(String isbn);
}
