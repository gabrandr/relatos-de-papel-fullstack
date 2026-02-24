package com.relatosdepapel.ms_books_payments.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para respuestas de error estándar
 * Se utiliza en validaciones y excepciones del Controller
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
        /** Código HTTP del error (400, 404, 409, etc.) */
        private Integer code;

        /** Mensaje descriptivo del error */
        private String message;
}