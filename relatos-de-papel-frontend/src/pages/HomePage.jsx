import { useEffect, useRef, useState } from "react";
import { useLocation, useSearchParams } from "react-router-dom";

import BookCard from "../features/books/BookCard";
import { getBooks } from "../api/booksApi";

/**
 * Página principal de catálogo.
 * Soporta búsqueda por query string y restauración de scroll al volver desde detalle.
 *
 * @returns {JSX.Element} Vista de catálogo con estados de carga/error/sin resultados.
 */
const HomePage = () => {
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const search = searchParams.get("search") || "";
  const restoredScrollRef = useRef(false);

  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    /**
     * Carga libros desde backend aplicando el término de búsqueda actual.
     *
     * @returns {Promise<void>}
     */
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
          const isNetworkError = err.message === "Failed to fetch";
          setError(
            isNetworkError
              ? "No hay conexión con el backend. Levanta gateway y microservicios antes de usar el frontend."
              : err.message || "No se pudo cargar el catálogo"
          );
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

  // Reinicia la bandera cuando cambia la navegación para permitir futuras restauraciones.
  useEffect(() => {
    restoredScrollRef.current = false;
  }, [location.key]);

  useEffect(() => {
    if (loading || restoredScrollRef.current) {
      return;
    }

    // Restaura scroll de forma explícita cuando la navegación trae estado contextual.
    if (location.state?.restoreScroll && typeof location.state.scrollY === "number") {
      restoredScrollRef.current = true;
      requestAnimationFrame(() => {
        window.scrollTo({ top: location.state.scrollY, left: 0, behavior: "auto" });
        window.history.replaceState({}, document.title);
      });
    }
  }, [loading, location.state]);

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
