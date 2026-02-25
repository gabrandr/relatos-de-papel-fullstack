package com.relatosdepapel.ms_books_catalogue.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.ResponseException;
import org.opensearch.client.RestClient;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.relatosdepapel.ms_books_catalogue.config.OpenSearchProperties;
import com.relatosdepapel.ms_books_catalogue.dto.BookFacetsResponseDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookRequestDTO;
import com.relatosdepapel.ms_books_catalogue.dto.BookResponseDTO;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * Repositorio operativo de libros sobre OpenSearch.
 * Encapsula creación de índice, seed inicial y consultas de catálogo/suggest/facets.
 */
@Component
@RequiredArgsConstructor
public class OpenSearchBookStore {

    private final RestClient restClient;
    private final OpenSearchProperties properties;
    private final ObjectMapper objectMapper;

    /**
     * Inicializa el almacenamiento validando índice y cargando seed cuando está vacío.
     */
    @PostConstruct
    void initialize() {
        ensureIndex();
        if (isIndexEmpty()) {
            syncSeedData();
        }
    }

    /**
     * Evalúa si el índice no contiene documentos.
     *
     * @return `true` si el índice está vacío.
     */
    private boolean isIndexEmpty() {
        try {
            Response response = restClient.performRequest(
                    new Request("GET", "/" + properties.getIndex() + "/_count"));
            JsonNode root = objectMapper.readTree(response.getEntity().getContent());
            return root.path("count").asLong(0L) == 0L;
        } catch (IOException ex) {
            throw fail("Error consultando count del índice", ex);
        }
    }

    /**
     * Retorna el catálogo visible para consumo frontend.
     *
     * @return lista de libros visibles.
     */
    public List<BookResponseDTO> findAllVisible() {
        return search(null, null, null, null, null, null, true, null, null, null, null, null);
    }

    /**
     * Busca un libro por id de documento.
     *
     * @param id identificador de libro.
     * @return libro encontrado o `null` si no existe.
     */
    public BookResponseDTO findById(Long id) {
        try {
            Response response = restClient
                    .performRequest(new Request("GET", "/" + properties.getIndex() + "/_doc/" + id));
            JsonNode root = objectMapper.readTree(response.getEntity().getContent());
            if (!root.path("found").asBoolean(false)) {
                return null;
            }
            return parseBookSource(root.path("_source"));
        } catch (ResponseException ex) {
            if (ex.getResponse().getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return null;
            }
            throw fail("Error consultando libro por id", ex);
        } catch (IOException ex) {
            throw fail("Error leyendo respuesta de OpenSearch", ex);
        }
    }

    /**
     * Verifica existencia de un ISBN en el índice.
     *
     * @param isbn isbn a validar.
     * @return `true` si existe al menos un libro con ese ISBN.
     */
    public boolean existsByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return false;
        }
        ObjectNode body = objectMapper.createObjectNode();
        body.put("size", 1);
        ObjectNode term = body.putObject("query").putObject("term");
        term.putObject("isbn").put("value", isbn);
        return executeSearchAndParse(body).stream().findAny().isPresent();
    }

    /**
     * Crea un libro asignando el siguiente id incremental disponible.
     *
     * @param dto payload de creación.
     * @return libro creado.
     */
    public BookResponseDTO create(BookRequestDTO dto) {
        long nextId = getNextId();
        BookResponseDTO book = new BookResponseDTO(
                nextId,
                dto.getTitle(),
                dto.getAuthor(),
                dto.getPublicationDate(),
                dto.getCategory(),
                dto.getIsbn(),
                dto.getRating(),
                dto.getVisible(),
                dto.getStock(),
                dto.getPrice());

        indexBook(book);
        return book;
    }

    /**
     * Reemplaza campos editables de un libro existente.
     * Conserva el ISBN previo para mantener consistencia referencial.
     *
     * @param id identificador de libro.
     * @param dto payload de actualización.
     * @return libro actualizado o `null` si no existe.
     */
    public BookResponseDTO update(Long id, BookRequestDTO dto) {
        BookResponseDTO current = findById(id);
        if (current == null) {
            return null;
        }

        BookResponseDTO updated = new BookResponseDTO(
                id,
                dto.getTitle(),
                dto.getAuthor(),
                dto.getPublicationDate(),
                dto.getCategory(),
                current.getIsbn(),
                dto.getRating(),
                dto.getVisible(),
                dto.getStock(),
                dto.getPrice());

        indexBook(updated);
        return updated;
    }

    /**
     * Guarda un libro existente en OpenSearch.
     *
     * @param book entidad a indexar.
     * @return mismo libro tras persistencia.
     */
    public BookResponseDTO save(BookResponseDTO book) {
        indexBook(book);
        return book;
    }

    /**
     * Elimina un libro por id.
     *
     * @param id identificador de libro.
     * @return `true` si el documento se eliminó, `false` si no existía.
     */
    public boolean delete(Long id) {
        try {
            Request request = new Request("DELETE", "/" + properties.getIndex() + "/_doc/" + id);
            request.addParameter("refresh", "true");
            Response response = restClient.performRequest(request);
            JsonNode root = objectMapper.readTree(response.getEntity().getContent());
            String result = root.path("result").asText();
            return "deleted".equals(result);
        } catch (ResponseException ex) {
            if (ex.getResponse().getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return false;
            }
            throw fail("Error eliminando libro", ex);
        } catch (IOException ex) {
            throw fail("Error leyendo respuesta de OpenSearch", ex);
        }
    }

    /**
     * Ejecuta búsqueda full-text + filtros estructurados.
     *
     * @param title título parcial para búsqueda.
     * @param author autor parcial.
     * @param category categoría exacta.
     * @param isbn isbn exacto.
     * @param ratingMin rating mínimo.
     * @param ratingMax rating máximo.
     * @param visible filtro de visibilidad.
     * @param minPrice precio mínimo.
     * @param maxPrice precio máximo.
     * @param publicationDateFrom fecha inicial.
     * @param publicationDateTo fecha final.
     * @param minStock stock mínimo.
     * @return libros que cumplen la consulta.
     */
    public List<BookResponseDTO> search(
            String title,
            String author,
            String category,
            String isbn,
            Integer ratingMin,
            Integer ratingMax,
            Boolean visible,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            LocalDate publicationDateFrom,
            LocalDate publicationDateTo,
            Integer minStock) {

        ObjectNode body = objectMapper.createObjectNode();
        body.put("size", 200);

        ObjectNode query = body.putObject("query");
        ObjectNode bool = query.putObject("bool");
        ArrayNode must = bool.putArray("must");
        ArrayNode filter = bool.putArray("filter");

        addTitleQuery(must, title);
        addAuthorQuery(must, author);

        if (category != null && !category.isBlank()) {
            addTermFilter(filter, "category", category);
        }
        if (isbn != null && !isbn.isBlank()) {
            addTermFilter(filter, "isbn", isbn);
        }
        if (visible != null) {
            addTermFilter(filter, "visible", visible);
        }
        if (ratingMin != null || ratingMax != null) {
            addRangeFilter(filter, "rating", ratingMin, ratingMax);
        }
        if (minPrice != null || maxPrice != null) {
            addRangeFilter(filter, "price", minPrice, maxPrice);
        }
        if (publicationDateFrom != null || publicationDateTo != null) {
            addRangeFilter(filter, "publicationDate", publicationDateFrom, publicationDateTo);
        }
        if (minStock != null) {
            ObjectNode stockRange = objectMapper.createObjectNode();
            stockRange.put("gte", minStock);
            ObjectNode rangeNode = objectMapper.createObjectNode();
            rangeNode.set("stock", stockRange);
            filter.add(objectMapper.createObjectNode().set("range", rangeNode));
        }

        if (must.isEmpty() && filter.isEmpty()) {
            query.removeAll();
            query.putObject("match_all");
        }

        return executeSearchAndParse(body);
    }

    /**
     * Retorna sugerencias de título priorizando coincidencias relevantes visibles.
     *
     * @param text texto parcial del usuario.
     * @param size máximo de sugerencias.
     * @return títulos sugeridos únicos.
     */
    public List<String> suggest(String text, int size) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        ObjectNode body = objectMapper.createObjectNode();
        body.put("size", size);

        ObjectNode bool = body.putObject("query").putObject("bool");
        ArrayNode must = bool.putArray("must");
        ArrayNode filter = bool.putArray("filter");

        ObjectNode query = objectMapper.createObjectNode();
        query.put("query", text.trim());
        query.put("type", "bool_prefix");
        query.put("operator", "and");

        ArrayNode fields = query.putArray("fields");
        fields.add("title");
        fields.add("title.suggest");
        fields.add("title.suggest._2gram");
        fields.add("title.suggest._3gram");
        fields.add("author");
        fields.add("author.suggest");
        fields.add("author.suggest._2gram");
        fields.add("author.suggest._3gram");

        must.add(objectMapper.createObjectNode().set("multi_match", query));
        addTermFilter(filter, "visible", true);

        List<BookResponseDTO> books = executeSearchAndParse(body);
        Set<String> unique = new LinkedHashSet<>();
        String normalizedInput = normalize(text);
        for (BookResponseDTO book : books) {
            String title = book.getTitle();
            if (title == null) {
                continue;
            }
            String normalizedTitle = normalize(title);
            if (!normalizedTitle.contains(normalizedInput) && !normalizedInput.contains(normalizedTitle)) {
                continue;
            }
            unique.add(title);
            if (unique.size() >= size) {
                break;
            }
        }
        return new ArrayList<>(unique);
    }

    /**
     * Calcula facets de categorías y autores para filtros de UI.
     *
     * @param text texto base opcional.
     * @param visible visibilidad opcional.
     * @return respuesta con total y buckets agregados.
     */
    public BookFacetsResponseDTO facets(String text, Boolean visible) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("size", 0);

        if ((text != null && !text.isBlank()) || visible != null) {
            ObjectNode query = body.putObject("query").putObject("bool");
            ArrayNode must = query.putArray("must");
            ArrayNode filter = query.putArray("filter");

            if (text != null && !text.isBlank()) {
                ObjectNode multiMatch = objectMapper.createObjectNode();
                multiMatch.put("query", text);
                multiMatch.put("fuzziness", "AUTO");
                ArrayNode fields = multiMatch.putArray("fields");
                fields.add("title");
                fields.add("author");
                fields.add("category");
                must.add(objectMapper.createObjectNode().set("multi_match", multiMatch));
            }

            if (visible != null) {
                addTermFilter(filter, "visible", visible);
            }
        }

        ObjectNode aggs = body.putObject("aggs");
        aggs.putObject("by_category").putObject("terms").put("field", "category").put("size", 20);
        aggs.putObject("by_author").putObject("terms").put("field", "author.keyword").put("size", 20);

        try {
            JsonNode root = executeSearch(body);
            Map<String, Long> categories = parseTermsAgg(root, "by_category");
            Map<String, Long> authors = parseTermsAgg(root, "by_author");
            long total = root.path("hits").path("total").path("value").asLong(0L);
            return new BookFacetsResponseDTO(total, categories, authors);
        } catch (IOException ex) {
            throw fail("Error obteniendo facets", ex);
        }
    }

    /**
     * Garantiza que el índice exista con mapping compatible con búsquedas y suggest.
     */
    private void ensureIndex() {
        try {
            Response head = restClient.performRequest(new Request("HEAD", "/" + properties.getIndex()));
            if (head.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return;
            }
        } catch (ResponseException ex) {
            if (ex.getResponse().getStatusLine().getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                throw fail("Error validando índice de OpenSearch", ex);
            }
        } catch (IOException ex) {
            throw fail("Error de red validando índice", ex);
        }

        ObjectNode body = objectMapper.createObjectNode();
        ObjectNode mappings = body.putObject("mappings").putObject("properties");

        mappings.putObject("id").put("type", "long");
        mappings.putObject("isbn").put("type", "keyword");
        mappings.putObject("category").put("type", "keyword");
        mappings.putObject("rating").put("type", "integer");
        mappings.putObject("visible").put("type", "boolean");
        mappings.putObject("stock").put("type", "integer");
        mappings.putObject("price").put("type", "double");
        mappings.putObject("publicationDate").put("type", "date").put("format", "yyyy-MM-dd");

        ObjectNode title = mappings.putObject("title");
        title.put("type", "text");
        ObjectNode titleFields = title.putObject("fields");
        titleFields.putObject("keyword").put("type", "keyword");
        titleFields.putObject("suggest").put("type", "search_as_you_type");

        ObjectNode author = mappings.putObject("author");
        author.put("type", "text");
        ObjectNode authorFields = author.putObject("fields");
        authorFields.putObject("keyword").put("type", "keyword");
        authorFields.putObject("suggest").put("type", "search_as_you_type");

        try {
            Request create = new Request("PUT", "/" + properties.getIndex());
            create.setJsonEntity(body.toString());
            restClient.performRequest(create);
        } catch (IOException ex) {
            throw fail("No se pudo crear el índice de OpenSearch", ex);
        }
    }

    /**
     * Inserta catálogo semilla inicial mediante operación bulk NDJSON.
     */
    private void syncSeedData() {
        try {
            List<BookResponseDTO> seedBooks = List.of(
                    new BookResponseDTO(1L, "Don Quijote de la Mancha", "Miguel de Cervantes",
                            LocalDate.parse("1605-01-16"), "Clásicos", "9788484030300", 5, true, 15,
                            new BigDecimal("19.99")),
                    new BookResponseDTO(2L, "Cien Años de Soledad", "Gabriel García Márquez",
                            LocalDate.parse("1967-05-30"), "Clásicos", "9788497592208", 5, true, 8,
                            new BigDecimal("24.99")),
                    new BookResponseDTO(3L, "1984", "George Orwell", LocalDate.parse("1949-06-08"), "Ficción",
                            "9788499890944", 5, true, 12, new BigDecimal("18.50")),
                    new BookResponseDTO(4L, "Orgullo y Prejuicio", "Jane Austen", LocalDate.parse("1813-01-28"),
                            "Romance", "9788469833346", 4, true, 20, new BigDecimal("16.99")),
                    new BookResponseDTO(5L, "El Señor de los Anillos", "J.R.R. Tolkien", LocalDate.parse("1954-07-29"),
                            "Fantasía", "9780261102385", 5, true, 3, new BigDecimal("35.00")),
                    new BookResponseDTO(6L, "Dune", "Frank Herbert", LocalDate.parse("1965-08-01"), "Ciencia Ficción",
                            "9780441172719", 5, true, 10, new BigDecimal("28.99")),
                    new BookResponseDTO(7L, "Moby Dick", "Herman Melville", LocalDate.parse("1851-10-18"), "Clásicos",
                            "9780142437247", 4, false, 5, new BigDecimal("22.00")),
                    new BookResponseDTO(8L, "El Principito", "Antoine de Saint-Exupéry", LocalDate.parse("1943-04-06"),
                            "Ficción", "9788498381498", 5, true, 25, new BigDecimal("9.99")),
                    new BookResponseDTO(9L, "Fahrenheit 451", "Ray Bradbury", LocalDate.parse("1953-10-19"),
                            "Ciencia Ficción", "9781451673319", 5, true, 14, new BigDecimal("17.99")),
                    new BookResponseDTO(10L, "The Great Gatsby", "F. Scott Fitzgerald", LocalDate.parse("1925-04-10"),
                            "Clásicos", "9780743273565", 5, true, 18, new BigDecimal("15.99")),
                    new BookResponseDTO(11L, "To Kill a Mockingbird", "Harper Lee", LocalDate.parse("1960-07-11"),
                            "Clásicos", "9780061120084", 5, true, 12, new BigDecimal("18.99")),
                    new BookResponseDTO(12L, "Brave New World", "Aldous Huxley", LocalDate.parse("1932-01-01"),
                            "Ciencia Ficción", "9780060850524", 5, true, 13, new BigDecimal("16.99")),
                    new BookResponseDTO(13L, "Crime and Punishment", "Fyodor Dostoevsky", LocalDate.parse("1866-01-01"),
                            "Clásicos", "9780143058144", 5, true, 9, new BigDecimal("19.99")),
                    new BookResponseDTO(14L, "Anna Karenina", "Leo Tolstoy", LocalDate.parse("1878-01-01"), "Clásicos",
                            "9780143035008", 5, true, 10, new BigDecimal("20.99")),
                    new BookResponseDTO(15L, "The Catcher in the Rye", "J.D. Salinger", LocalDate.parse("1951-07-16"),
                            "Ficción", "9780316769488", 4, true, 15, new BigDecimal("17.49")),
                    new BookResponseDTO(16L, "The Hobbit", "J.R.R. Tolkien", LocalDate.parse("1937-09-21"), "Fantasía",
                            "9780547928227", 5, true, 11, new BigDecimal("21.99")),
                    new BookResponseDTO(17L, "The Alchemist", "Paulo Coelho", LocalDate.parse("1988-01-01"), "Ficción",
                            "9780061122415", 5, true, 16, new BigDecimal("16.49")),
                    new BookResponseDTO(18L, "The Name of the Rose", "Umberto Eco", LocalDate.parse("1980-01-01"),
                            "Misterio", "9780156001311", 5, true, 8, new BigDecimal("18.49")),
                    new BookResponseDTO(19L, "The Shadow of the Wind", "Carlos Ruiz Zafón",
                            LocalDate.parse("2001-01-01"), "Misterio", "9780143034902", 5, true, 12,
                            new BigDecimal("19.49")),
                    new BookResponseDTO(20L, "The Girl with the Dragon Tattoo", "Stieg Larsson",
                            LocalDate.parse("2005-01-01"), "Thriller", "9780307454546", 5, true, 10,
                            new BigDecimal("18.99")),
                    new BookResponseDTO(21L, "Sapiens A brief Story of Human Kind", "Yuval Noah Harari",
                            LocalDate.parse("2011-01-01"), "No Ficción", "9780062316097", 5, true, 14,
                            new BigDecimal("22.99")),
                    new BookResponseDTO(22L, "The metamorphosis", "Franz Kafka", LocalDate.parse("1915-01-01"),
                            "Clásicos", "9780553213690", 5, true, 19, new BigDecimal("12.99")),
                    new BookResponseDTO(23L, "War And Peace", "Leo Tolstoy", LocalDate.parse("1869-01-01"), "Clásicos",
                            "9780199232765", 5, true, 7, new BigDecimal("24.99")),
                    new BookResponseDTO(24L, "Dracula", "Bram Stoker", LocalDate.parse("1897-01-01"), "Terror",
                            "9780486411095", 5, true, 17, new BigDecimal("14.99")),
                    new BookResponseDTO(25L, "Frankenstein", "Mary Shelley", LocalDate.parse("1818-01-01"), "Terror",
                            "9780486282114", 5, true, 15, new BigDecimal("13.99")),
                    new BookResponseDTO(26L, "The Picture of Dorian Gray", "Oscar Wilde", LocalDate.parse("1890-01-01"),
                            "Clásicos", "9780141439570", 5, true, 13, new BigDecimal("15.49")),
                    new BookResponseDTO(27L, "The Brothers Karamazov", "Fyodor Dostoevsky",
                            LocalDate.parse("1880-01-01"), "Clásicos", "9780374528379", 5, true, 8,
                            new BigDecimal("23.99")),
                    new BookResponseDTO(28L, "Notre-Dame of Paris", "Victor Hugo", LocalDate.parse("1831-01-01"),
                            "Clásicos", "9780140443530", 4, true, 9, new BigDecimal("17.99")));

            StringBuilder bulk = new StringBuilder();
            for (BookResponseDTO book : seedBooks) {
                bulk.append("{\"index\":{\"_index\":\"")
                        .append(properties.getIndex())
                        .append("\",\"_id\":\"")
                        .append(book.getId())
                        .append("\"}}\n");
                bulk.append(objectMapper.writeValueAsString(book)).append("\n");
            }

            Request bulkReq = new Request("POST", "/_bulk");
            bulkReq.setEntity(new StringEntity(bulk.toString(), ContentType.create("application/x-ndjson")));
            bulkReq.addParameter("refresh", "true");
            restClient.performRequest(bulkReq);
        } catch (IOException ex) {
            throw fail("No se pudo inicializar catálogo en OpenSearch", ex);
        }
    }

    /**
     * Indexa o sobreescribe un libro por id y refresca índice para lectura inmediata.
     *
     * @param book libro a indexar.
     */
    private void indexBook(BookResponseDTO book) {
        try {
            Request request = new Request("PUT", "/" + properties.getIndex() + "/_doc/" + book.getId());
            request.setJsonEntity(objectMapper.writeValueAsString(book));
            request.addParameter("refresh", "true");
            restClient.performRequest(request);
        } catch (IOException ex) {
            throw fail("Error indexando libro en OpenSearch", ex);
        }
    }

    /**
     * Calcula el siguiente id secuencial basado en el mayor id actual.
     *
     * @return siguiente id disponible.
     */
    private long getNextId() {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("size", 1);
        ObjectNode query = body.putObject("query");
        query.putObject("match_all");
        ArrayNode sort = body.putArray("sort");
        sort.add(objectMapper.createObjectNode().set("id", objectMapper.createObjectNode().put("order", "desc")));

        List<BookResponseDTO> books = executeSearchAndParse(body);
        if (books.isEmpty()) {
            return 1L;
        }
        return books.get(0).getId() + 1L;
    }

    /**
     * Ejecuta una consulta `_search` y retorna el JSON raíz.
     *
     * @param body cuerpo de consulta OpenSearch.
     * @return respuesta parseada.
     * @throws IOException cuando falla llamada de red o parseo.
     */
    private JsonNode executeSearch(ObjectNode body) throws IOException {
        Request request = new Request("GET", "/" + properties.getIndex() + "/_search");
        request.setJsonEntity(body.toString());
        Response response = restClient.performRequest(request);
        return objectMapper.readTree(response.getEntity().getContent());
    }

    /**
     * Ejecuta búsqueda y convierte hits a DTO de libro.
     *
     * @param body cuerpo de consulta OpenSearch.
     * @return lista de libros parseados.
     */
    private List<BookResponseDTO> executeSearchAndParse(ObjectNode body) {
        try {
            JsonNode root = executeSearch(body);
            ArrayNode hits = (ArrayNode) root.path("hits").path("hits");
            List<BookResponseDTO> books = new ArrayList<>();
            for (JsonNode hit : hits) {
                books.add(parseBookSource(hit.path("_source")));
            }
            return books;
        } catch (IOException ex) {
            throw fail("Error ejecutando búsqueda en OpenSearch", ex);
        }
    }

    /**
     * Mapea un `_source` JSON a `BookResponseDTO`.
     *
     * @param source nodo fuente del documento.
     * @return dto de libro normalizado.
     */
    private BookResponseDTO parseBookSource(JsonNode source) {
        LocalDate publicationDate = null;
        if (source.hasNonNull("publicationDate") && !source.get("publicationDate").asText().isBlank()) {
            publicationDate = LocalDate.parse(source.get("publicationDate").asText());
        }

        BigDecimal price = source.hasNonNull("price")
                ? BigDecimal.valueOf(source.get("price").asDouble())
                : BigDecimal.ZERO;

        return new BookResponseDTO(
                source.path("id").asLong(),
                source.path("title").asText(null),
                source.path("author").asText(null),
                publicationDate,
                source.path("category").asText(null),
                source.path("isbn").asText(null),
                source.hasNonNull("rating") ? source.get("rating").asInt() : null,
                source.hasNonNull("visible") ? source.get("visible").asBoolean() : Boolean.FALSE,
                source.hasNonNull("stock") ? source.get("stock").asInt() : 0,
                price);
    }

    /**
     * Añade condición de título usando `bool_prefix` sobre campos `search_as_you_type`.
     *
     * @param must nodo de cláusulas obligatorias.
     * @param value término de título.
     */
    private void addTitleQuery(ArrayNode must, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        ObjectNode multiMatch = objectMapper.createObjectNode();
        multiMatch.put("query", value.trim());
        multiMatch.put("type", "bool_prefix");
        multiMatch.put("operator", "and");
        ArrayNode fields = multiMatch.putArray("fields");
        fields.add("title");
        fields.add("title.suggest");
        fields.add("title.suggest._2gram");
        fields.add("title.suggest._3gram");
        must.add(objectMapper.createObjectNode().set("multi_match", multiMatch));
    }

    /**
     * Añade condición por autor en modo `match_phrase_prefix`.
     *
     * @param must nodo de cláusulas obligatorias.
     * @param value término de autor.
     */
    private void addAuthorQuery(ArrayNode must, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        ObjectNode phrasePrefix = objectMapper.createObjectNode();
        phrasePrefix.putObject("author")
                .put("query", value.trim());
        must.add(objectMapper.createObjectNode().set("match_phrase_prefix", phrasePrefix));
    }

    /**
     * Añade filtro exacto por campo (`term` query).
     *
     * @param filter nodo de filtros.
     * @param field nombre de campo.
     * @param value valor exacto a comparar.
     */
    private void addTermFilter(ArrayNode filter, String field, Object value) {
        if (value == null) {
            return;
        }
        ObjectNode termNode = objectMapper.createObjectNode();
        ObjectNode valueNode = termNode.putObject(field);
        if (value instanceof String text) {
            valueNode.put("value", text);
        } else if (value instanceof Boolean bool) {
            valueNode.put("value", bool);
        } else if (value instanceof Integer number) {
            valueNode.put("value", number);
        } else {
            valueNode.put("value", Objects.toString(value));
        }
        filter.add(objectMapper.createObjectNode().set("term", termNode));
    }

    /**
     * Añade filtro de rango (`gte`/`lte`) para campo numérico o fecha.
     *
     * @param filter nodo de filtros.
     * @param field nombre del campo.
     * @param gte límite inferior opcional.
     * @param lte límite superior opcional.
     */
    private void addRangeFilter(ArrayNode filter, String field, Object gte, Object lte) {
        ObjectNode values = objectMapper.createObjectNode();
        if (gte != null) {
            putNodeValue(values, "gte", gte);
        }
        if (lte != null) {
            putNodeValue(values, "lte", lte);
        }
        ObjectNode rangeField = objectMapper.createObjectNode();
        rangeField.set(field, values);
        filter.add(objectMapper.createObjectNode().set("range", rangeField));
    }

    /**
     * Inserta un valor tipado en un nodo JSON para filtros de rango.
     *
     * @param node nodo destino.
     * @param field campo destino.
     * @param value valor a serializar.
     */
    private void putNodeValue(ObjectNode node, String field, Object value) {
        if (value instanceof Integer intValue) {
            node.put(field, intValue);
            return;
        }
        if (value instanceof Long longValue) {
            node.put(field, longValue);
            return;
        }
        if (value instanceof BigDecimal decimalValue) {
            node.put(field, decimalValue);
            return;
        }
        if (value instanceof LocalDate dateValue) {
            node.put(field, dateValue.toString());
            return;
        }
        node.put(field, Objects.toString(value));
    }

    /**
     * Convierte buckets de una terms aggregation en mapa ordenado.
     *
     * @param root respuesta raíz de OpenSearch.
     * @param aggName nombre de la agregación.
     * @return mapa `bucketKey -> docCount`.
     */
    private Map<String, Long> parseTermsAgg(JsonNode root, String aggName) {
        Map<String, Long> values = new LinkedHashMap<>();
        ArrayNode buckets = (ArrayNode) root.path("aggregations").path(aggName).path("buckets");
        for (JsonNode bucket : buckets) {
            values.put(bucket.path("key").asText(), bucket.path("doc_count").asLong());
        }
        return values;
    }

    /**
     * Estandariza excepciones internas del store con contexto funcional.
     *
     * @param message mensaje de dominio.
     * @param ex excepción original.
     * @return excepción runtime enriquecida.
     */
    private RuntimeException fail(String message, Exception ex) {
        return new IllegalStateException(message + ": " + ex.getMessage(), ex);
    }

    /**
     * Normaliza texto removiendo acentos y espacios extra para comparar sugerencias.
     *
     * @param input texto de entrada.
     * @return texto normalizado en minúsculas.
     */
    private String normalize(String input) {
        String normalized = Normalizer.normalize(input == null ? "" : input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().trim();
    }
}
