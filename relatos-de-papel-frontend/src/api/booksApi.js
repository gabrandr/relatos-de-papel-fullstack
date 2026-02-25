import { gatewayRequest } from "./gatewayClient";

const FALLBACK_COVER = "/book-placeholder.svg";

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

const normalizeBook = (book) => ({
  ...book,
  image: buildCoverByIsbn(book?.isbn),
  imageFallback: FALLBACK_COVER,
  description:
    book?.description ||
    "Libro disponible en Relatos de Papel. Consulta disponibilidad y completa tu compra.",
  isBestSeller: Boolean(book?.rating && book.rating >= 5),
});

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

export const getBookById = async (id) => {
  const book = await gatewayRequest({
    path: `/api/books/${id}`,
    targetMethod: "GET",
  });
  return normalizeBook(book);
};

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
