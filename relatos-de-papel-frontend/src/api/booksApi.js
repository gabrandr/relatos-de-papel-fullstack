import { gatewayRequest } from "./gatewayClient";

/**
 * Portada local de respaldo cuando el ISBN no está disponible
 * o la URL remota de OpenLibrary falla.
 * @type {string}
 */
const FALLBACK_COVER = "/book-placeholder.svg";

/**
 * Construye la URL de portada de OpenLibrary a partir del ISBN.
 *
 * @param {string | null | undefined} isbn ISBN crudo recibido desde backend.
 * @returns {string} URL de portada remota o fallback local.
 */
const buildCoverByIsbn = (isbn) => {
  if (!isbn) {
    return FALLBACK_COVER;
  }
  const normalizedIsbn = isbn.replace(/-/g, "").trim();
  if (!normalizedIsbn) {
    return FALLBACK_COVER;
  }
  return `https://covers.openlibrary.org/b/isbn/${normalizedIsbn}-L.jpg`;
};

/**
 * Adapta el DTO del backend al shape consumido por la UI.
 *
 * @param {Record<string, any>} book Libro recibido desde API.
 * @returns {Record<string, any>} Libro normalizado con campos derivados para front.
 */
const normalizeBook = (book) => ({
  ...book,
  image: buildCoverByIsbn(book?.isbn),
  imageFallback: FALLBACK_COVER,
  description:
    book?.description ||
    "Libro disponible en Relatos de Papel. Consulta disponibilidad y completa tu compra.",
  isBestSeller: Boolean(book?.rating && book.rating >= 5),
});

/**
 * Obtiene catálogo de libros visibles. Cuando existe `search`, primero intenta full-text
 * y, sin resultados, aplica fallback por sugerencia para corregir términos.
 *
 * @param {{ search?: string }} [params={}] Parámetros de consulta del catálogo.
 * @returns {Promise<Array<Record<string, any>>>} Listado normalizado para render en Home.
 */
export const getBooks = async ({ search } = {}) => {
  if (search && search.trim()) {
    const query = search.trim();
    const books = await gatewayRequest({
      path: "/api/books/search",
      targetMethod: "GET",
      queryParams: {
        title: query,
        visible: true,
      },
    });

    if (books.length > 0) {
      return books.map(normalizeBook);
    }

    // Fallback: usar suggest para corregir/auto completar y reintentar búsqueda.
    const suggestions = await getBookSuggestions(query);
    if (suggestions.length > 0) {
      const recovered = await gatewayRequest({
        path: "/api/books/search",
        targetMethod: "GET",
        queryParams: {
          title: suggestions[0],
          visible: true,
        },
      });
      return recovered.map(normalizeBook);
    }

    return [];
  }

  const books = await gatewayRequest({
    path: "/api/books",
    targetMethod: "GET",
  });
  return books.map(normalizeBook);
};

/**
 * Obtiene el detalle de un libro por id y lo normaliza para consumo de componentes.
 *
 * @param {number | string} id Identificador del libro.
 * @returns {Promise<Record<string, any>>} Libro normalizado.
 */
export const getBookById = async (id) => {
  const book = await gatewayRequest({
    path: `/api/books/${id}`,
    targetMethod: "GET",
  });
  return normalizeBook(book);
};

/**
 * Solicita sugerencias `search_as_you_type` para el buscador del header.
 *
 * @param {string} text Texto escrito por el usuario.
 * @returns {Promise<string[]>} Lista de títulos sugeridos por backend.
 */
export const getBookSuggestions = async (text) => {
  const value = text?.trim();
  if (!value) {
    return [];
  }

  const suggestions = await gatewayRequest({
    path: "/api/books/search/suggest",
    targetMethod: "GET",
    queryParams: {
      text: value,
      size: 8,
    },
  });
  return suggestions;
};
