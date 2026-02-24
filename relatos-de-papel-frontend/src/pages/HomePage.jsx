import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

import BookCard from "../features/books/BookCard";
import { getBooks } from "../api/booksApi";

const HomePage = () => {
  const [searchParams] = useSearchParams();
  const search = searchParams.get("search") || "";

  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    const loadBooks = async () => {
      setLoading(true);
      setError("");
      try {
        const data = await getBooks({ search });
        if (active) {
          setBooks(data);
        }
      } catch (err) {
        if (active) {
          setError(err.message || "No se pudo cargar el catálogo");
          setBooks([]);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    loadBooks();

    return () => {
      active = false;
    };
  }, [search]);

  return (
    <div className="max-w-6xl mx-auto px-6 py-8">
      <h1 className="text-3xl font-bold text-text-main mb-6">Catálogo de Libros</h1>

      {loading && <p className="text-text-muted">Cargando libros...</p>}
      {!loading && error && <p className="text-red-600">{error}</p>}
      {!loading && !error && books.length === 0 && (
        <p className="text-text-muted">No se encontraron libros para esa búsqueda.</p>
      )}

      {!loading && !error && books.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {books.map((book) => (
            <BookCard key={book.id} book={book} />
          ))}
        </div>
      )}
    </div>
  );
};

export default HomePage;
