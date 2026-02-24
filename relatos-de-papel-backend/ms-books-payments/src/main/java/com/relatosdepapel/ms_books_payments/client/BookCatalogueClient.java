package com.relatosdepapel.ms_books_payments.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.relatosdepapel.ms_books_payments.dto.BookAvailabilityDTO;
import com.relatosdepapel.ms_books_payments.dto.StockUpdateDTO;
import lombok.RequiredArgsConstructor;

/**
 * Cliente HTTP para comunicarse con MS Books Catalogue.
 * Permite verificar disponibilidad y actualizar stock de libros.
 */
@Component // Registra como componente de Spring (se puede inyectar)
@RequiredArgsConstructor // Lombok: crea constructor con dependencias
public class BookCatalogueClient {
    // URL base del microservicio de catálogo (nombre Eureka, NO IP:puerto)
    private static final String CATALOGUE_SERVICE_URL = "http://MS-BOOKS-CATALOGUE";

    // RestTemplate inyectado automáticamente
    private final RestTemplate restTemplate;

    /**
     * Verifica la disponibilidad de un libro antes de crear un pago.
     * 
     * Llama a: GET http://MS-BOOKS-CATALOGUE/api/books/{id}/availability
     * 
     * @param bookId ID del libro a verificar
     * @return BookAvailabilityDTO con información de disponibilidad
     * @throws HttpClientErrorException.NotFound si el libro no existe (404)
     */
    public BookAvailabilityDTO checkAvailability(Long bookId) {
        // Construir la URL completa
        String url = CATALOGUE_SERVICE_URL + "/api/books/" + bookId + "/availability";

        // Hacer petición GET y convertir respuesta a BookAvailabilityDTO
        // @LoadBalanced convierte MS-BOOKS-CATALOGUE a IP:puerto real
        return restTemplate.getForObject(url, BookAvailabilityDTO.class);
    }

    /**
     * Decrementa el stock de un libro después de crear un pago exitoso.
     * 
     * Llama a: PATCH http://MS-BOOKS-CATALOGUE/api/books/{id}/stock
     * Body: {\"quantity\": -N} (negativo para decrementar)
     * 
     * @param bookId   ID del libro
     * @param quantity Cantidad a decrementar (se envía como NEGATIVO)
     * @throws HttpClientErrorException.NotFound   si el libro no existe (404)
     * @throws HttpClientErrorException.BadRequest si no hay suficiente stock (400)
     */
    public void decrementStock(Long bookId, Integer quantity) {
        String url = CATALOGUE_SERVICE_URL + "/api/books/" + bookId + "/stock";

        // Crear DTO con cantidad NEGATIVA para decrementar
        StockUpdateDTO stockUpdate = new StockUpdateDTO(-quantity); // Signo negativo

        // Hacer petición PATCH (actualización parcial)
        restTemplate.patchForObject(url, stockUpdate, Void.class);
    }

    /**
     * Restaura el stock de un libro cuando se cancela un pago.
     * 
     * Llama a: PATCH http://MS-BOOKS-CATALOGUE/api/books/{id}/stock
     * Body: {\"quantity\": +N} (positivo para incrementar)
     * 
     * @param bookId   ID del libro
     * @param quantity Cantidad a restaurar (se envía como POSITIVO)
     * @throws HttpClientErrorException.NotFound si el libro no existe (404)
     */
    public void restoreStock(Long bookId, Integer quantity) {
        String url = CATALOGUE_SERVICE_URL + "/api/books/" + bookId + "/stock";

        // Crear DTO con cantidad POSITIVA para incrementar
        StockUpdateDTO stockUpdate = new StockUpdateDTO(quantity); // Signo positivo

        // Hacer petición PATCH
        restTemplate.patchForObject(url, stockUpdate, Void.class);
    }
}