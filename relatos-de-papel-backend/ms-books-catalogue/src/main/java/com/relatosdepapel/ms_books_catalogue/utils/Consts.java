package com.relatosdepapel.ms_books_catalogue.utils;

/**
 * Clase de constantes que almacena los nombres de las columnas de la entidad
 * Book.
 * Se utiliza para evitar hardcodear strings en las especificaciones y queries.
 */
public class Consts {
    // columnas de la tabla book
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String PUBLICATION_DATE = "publicationDate";
    public static final String CATEGORY = "category";
    public static final String ISBN = "isbn";
    public static final String RATING = "rating";
    public static final String VISIBLE = "visible";
    public static final String STOCK = "stock";
    public static final String PRICE = "price";

    // constructor privado para evitar instanciación
    private Consts() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }
}
