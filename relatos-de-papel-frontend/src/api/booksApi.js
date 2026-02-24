import { gatewayRequest } from "./gatewayClient";

const normalizeBook = (book) => ({
  ...book,
  image: book?.isbn
    ? `https://covers.openlibrary.org/b/isbn/${book.isbn.replace(/-/g, "")}-L.jpg`
    : "https://via.placeholder.com/300x450?text=Sin+portada",
  description:
    book?.description ||
    "Libro disponible en Relatos de Papel. Consulta disponibilidad y completa tu compra.",
  isBestSeller: Boolean(book?.rating && book.rating >= 5),
});

export const getBooks = async ({ search } = {}) => {
  if (search && search.trim()) {
    const books = await gatewayRequest({
      path: "/api/books/search",
      targetMethod: "GET",
      queryParams: {
        title: search.trim(),
        visible: true,
      },
    });
    return books.map(normalizeBook);
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
  const suggestions = await gatewayRequest({
    path: "/api/books/search/suggest",
    targetMethod: "GET",
    queryParams: {
      text,
      size: 8,
    },
  });
  return suggestions;
};
