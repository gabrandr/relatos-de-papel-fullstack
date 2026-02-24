package com.relatosdepapel.ms_books_payments.exception;

/**
 * Excepción lanzada cuando el libro no existe en el catálogo.
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
