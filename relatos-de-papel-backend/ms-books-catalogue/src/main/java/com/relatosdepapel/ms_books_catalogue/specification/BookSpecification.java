package com.relatosdepapel.ms_books_catalogue.specification;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.relatosdepapel.ms_books_catalogue.entity.Book;
import com.relatosdepapel.ms_books_catalogue.utils.Consts;

/**
 * Specifications para búsquedas dinámicas de Book.
 * Permite combinar múltiples criterios de búsqueda de forma flexible.
 */
public class BookSpecification {

    /**
     * Busca libros cuyo título contenga el texto dado (case-insensitive).
     * SQL generado: WHERE LOWER(title) LIKE LOWER('%title%')
     */
    public static Specification<Book> titleContains(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return null; // Sin filtro, ignora este criterio
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Consts.TITLE)), // LOWER(title)
                    "%" + title.toLowerCase() + "%"); // %title%
        };
    }

    /**
     * Busca libros cuyo autor contenga el texto dado (case-insensitive).
     * SQL generado: WHERE LOWER(author) LIKE LOWER('%autor%')
     */
    public static Specification<Book> authorContains(String author) {
        return (root, query, criteriaBuilder) -> {
            if (author == null || author.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Consts.AUTHOR)), // LOWER(author)
                    "%" + author.toLowerCase() + "%"); // %author%
        };
    }

    /**
     * Busca libros de una categoría específica (búsqueda exacta).
     * SQL generado: WHERE category = 'category'
     */
    public static Specification<Book> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get(Consts.CATEGORY), category); // category = 'category'
        };
    }

    /**
     * Busca libros con el ISBN dado (búsqueda exacta).
     * SQL generado: WHERE isbn = 'isbn'
     */
    public static Specification<Book> hasIsbn(String isbn) {
        return (root, query, criteriaBuilder) -> {
            if (isbn == null || isbn.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get(Consts.ISBN), isbn); // isbn = 'isbn'
        };
    }

    /**
     * Busca libros con rating mayor o igual al dado.
     * SQL generado: WHERE rating >= ratingMin
     */
    public static Specification<Book> ratingGreaterThanOrEqual(Integer ratingMin) {
        return (root, query, criteriaBuilder) -> {
            if (ratingMin == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(Consts.RATING), ratingMin); // rating >= ratingMin
        };
    }

    /**
     * Busca libros con rating menor o igual al dado.
     * SQL generado: WHERE rating <= ratingMax
     */
    public static Specification<Book> ratingLessThanOrEqual(Integer ratingMax) {
        return (root, query, criteriaBuilder) -> {
            if (ratingMax == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get(Consts.RATING), ratingMax); // rating <= ratingMax
        };
    }

    /**
     * Busca libros visibles u ocultos.
     * SQL generado: WHERE visible = true/false
     */
    public static Specification<Book> isVisible(Boolean visible) {
        return (root, query, criteriaBuilder) -> {
            if (visible == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(Consts.VISIBLE), visible); // visible = true/false
        };
    }

    /**
     * Busca libros con precio mayor o igual al dado.
     * SQL generado: WHERE price >= priceMin
     */
    public static Specification<Book> priceGreaterThanOrEqual(BigDecimal priceMin) {
        return (root, query, criteriaBuilder) -> {
            if (priceMin == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(Consts.PRICE), priceMin); // price >= priceMin
        };
    }

    /**
     * Busca libros con precio menor o igual al dado.
     * SQL generado: WHERE price <= priceMax
     */
    public static Specification<Book> priceLessThanOrEqual(BigDecimal priceMax) {
        return (root, query, criteriaBuilder) -> {
            if (priceMax == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get(Consts.PRICE), priceMax); // price <= priceMax
        };
    }

    /**
     * Busca libros con fecha de publicación mayor o igual a la dada.
     * SQL generado: WHERE publicationDate >= dateFrom
     */
    public static Specification<Book> publicationDateAfterOrEqual(LocalDate dateFrom) {
        return (root, query, criteriaBuilder) -> {
            if (dateFrom == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(Consts.PUBLICATION_DATE), dateFrom); // publicationDate
                                                                                                      // >= dateFrom
        };
    }

    /**
     * Busca libros con fecha de publicación menor o igual a la dada.
     * SQL generado: WHERE publicationDate <= dateTo
     */
    public static Specification<Book> publicationDateBeforeOrEqual(LocalDate dateTo) {
        return (root, query, criteriaBuilder) -> {
            if (dateTo == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get(Consts.PUBLICATION_DATE), dateTo); // publicationDate
                                                                                                 // <= dateTo
        };
    }

    /**
     * Busca libros con stock mayor o igual al dado.
     * SQL generado: WHERE stock >= minStock
     */
    public static Specification<Book> stockGreaterThanOrEqual(Integer minStock) {
        return (root, query, criteriaBuilder) -> {
            if (minStock == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(Consts.STOCK), minStock); // stock >= minStock
        };
    }
}
