package com.relatosdepapel.ms_books_catalogue.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookFacetsResponseDTO {
    private long total;
    private Map<String, Long> categories;
    private Map<String, Long> authors;
}
