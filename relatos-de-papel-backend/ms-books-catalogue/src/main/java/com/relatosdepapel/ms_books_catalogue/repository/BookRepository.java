package com.relatosdepapel.ms_books_catalogue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import com.relatosdepapel.ms_books_catalogue.entity.Book;

import lombok.RequiredArgsConstructor;

/**
 * Repositorio Wrapper para Book (Capa 2).
 * Encapsula BookJpaRepository y proporciona métodos de acceso a datos.
 * Permite agregar lógica adicional sin acoplar el Service a JPA.
 */
@Repository // Indica que esta clase es un repositorio
@RequiredArgsConstructor // Genera constructor con parámetros para inyección de dependencias
public class BookRepository {
    private final BookJpaRepository jpaRepository; // Inyección de dependencia del repositorio JPA

    // METODOS CRUD BASICOS

    /**
     * Obtiene todos los libros.
     */
    public List<Book> getAll() {
        return jpaRepository.findAll();
    }

    /**
     * Busca un libro por ID.
     * 
     * @return Book si existe, null si no existe
     */
    public Book getById(Long id) {
        return jpaRepository.findById(id).orElse(null);
    }

    /**
     * Guarda o actualiza un libro.
     */
    public Book save(Book book) {
        return jpaRepository.save(book);
    }

    /**
     * Elimina un libro.
     */
    public void delete(Book book) {
        jpaRepository.delete(book);
    }

    // METODOS DE BUSQUEDA

    /**
     * Verifica si existe un libro con el ISBN dado.
     */
    public boolean existsByIsbn(String isbn) {
        return jpaRepository.existsByIsbn(isbn);
    }

    /**
     * Busca libros por título (búsqueda parcial).
     */
    public List<Book> findByTitle(String title) {
        return jpaRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Busca libros por autor (búsqueda parcial).
     */
    public List<Book> findByAuthor(String author) {
        return jpaRepository.findByAuthorContainingIgnoreCase(author);
    }

    /**
     * Busca libros por categoría (búsqueda exacta).
     */
    public List<Book> findByCategory(String category) {
        return jpaRepository.findByCategory(category);
    }

    /**
     * Busca un libro por ISBN.
     */
    public Optional<Book> findByIsbn(String isbn) {
        return jpaRepository.findByIsbn(isbn);
    }

    /**
     * Busca todos los libros visibles.
     */
    public List<Book> findVisibleBooks() {
        return jpaRepository.findByVisibleTrue();
    }

    // METODO DE BUSQUEDA DINAMICA

    /**
     * Realiza una búsqueda dinámica usando Specifications.
     * Este método se usará en el Service para búsquedas combinadas.
     * 
     * @param spec Especificación de búsqueda (condiciones dinámicas)
     * @return Lista de libros que cumplen la especificación
     */
    public List<Book> search(Specification<Book> spec) {
        return jpaRepository.findAll(spec);
    }
}
