package com.relatosdepapel.ms_books_payments.utils;

/**
 * Clase de constantes que almacena los nombres de las columnas de la entidad
 * Payment.
 * Se utiliza para evitar hardcodear strings en las especificaciones y queries.
 */
public class Consts {
    // columnas de la tabla payment
    public static final String ID = "id";
    public static final String USER_ID = "userId";
    public static final String BOOK_ID = "bookId";
    public static final String BOOK_TITLE = "bookTitle";
    public static final String BOOK_ISBN = "bookIsbn";
    public static final String QUANTITY = "quantity";
    public static final String UNIT_PRICE = "unitPrice";
    public static final String TOTAL_PRICE = "totalPrice";
    public static final String PURCHASE_DATE = "purchaseDate";
    public static final String STATUS = "status";

    // constructor privado para evitar instanciación
    private Consts() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }
}
