import { useEffect, useRef, useState } from "react";
import { useLocation, useSearchParams } from "react-router-dom";

import BookCard from "../features/books/BookCard";
import { getBookFacets, getBooks } from "../api/booksApi";

/**
 * Deriva facets locales desde los libros visibles actuales.
 * Se usa como fallback cuando el endpoint de facets no está disponible.
 *
 * @param {Array<Record<string, any>>} books Listado actual del catálogo.
 * @returns {{ total: number, categories: Record<string, number>, authors: Record<string, number> }} Facets derivados.
 */
const deriveFacetsFromBooks = (books) => {
  const categories = {};
  const authors = {};

  books.forEach((book) => {
    if (book?.category) {
      categories[book.category] = (categories[book.category] || 0) + 1;
    }
    if (book?.author) {
      authors[book.author] = (authors[book.author] || 0) + 1;
    }
  });

  const sortedCategories = Object.fromEntries(
    Object.entries(categories).sort(([, left], [, right]) => right - left)
  );
  const sortedAuthors = Object.fromEntries(
    Object.entries(authors).sort(([, left], [, right]) => right - left)
  );

  return {
    total: books.length,
    categories: sortedCategories,
    authors: sortedAuthors,
  };
};

/**
 * Página principal de catálogo.
 * Soporta búsqueda por query string y restauración de scroll al volver desde detalle.
 *
 * @returns {JSX.Element} Vista de catálogo con estados de carga/error/sin resultados.
 */
const HomePage = () => {
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const search = searchParams.get("search") || "";
  const selectedCategory = searchParams.get("category") || "";
  const selectedAuthor = searchParams.get("author") || "";
  const restoredScrollRef = useRef(false);

  const [books, setBooks] = useState([]);
  const [facets, setFacets] = useState({ total: 0, categories: {}, authors: {} });
  const [facetsNotice, setFacetsNotice] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    const requestController = new AbortController();

    /**
     * Carga libros desde backend aplicando el término de búsqueda actual.
     *
     * @returns {Promise<void>}
     */
    const loadBooks = async () => {
      setLoading(true);
      setError("");
      setFacetsNotice("");
      try {
        const [booksResult, facetsResult] = await Promise.allSettled([
          getBooks({
            search,
            category: selectedCategory,
            author: selectedAuthor,
            signal: requestController.signal,
          }),
          // Facets se piden una sola vez para reducir carga y evitar picos de concurrencia.
          getBookFacets({
            search,
            category: selectedCategory || undefined,
            author: selectedAuthor || undefined,
            signal: requestController.signal,
          }),
        ]);

        if (booksResult.status !== "fulfilled") {
          throw booksResult.reason;
        }

        if (active) {
          setBooks(booksResult.value);
          if (facetsResult.status === "fulfilled") {
            const hasFacetBuckets =
              Object.keys(facetsResult.value.categories).length > 0 ||
              Object.keys(facetsResult.value.authors).length > 0;
            setFacets({
              total: booksResult.value.length,
              categories: facetsResult.value.categories,
              authors: facetsResult.value.authors,
            });
            if (!hasFacetBuckets && booksResult.value.length > 0) {
              const derivedFacets = deriveFacetsFromBooks(booksResult.value);
              setFacets(derivedFacets);
              const hasDerivedBuckets =
                Object.keys(derivedFacets.categories).length > 0 ||
                Object.keys(derivedFacets.authors).length > 0;
              if (!hasDerivedBuckets) {
                setFacetsNotice(
                  "Filtros temporalmente no disponibles. El catálogo sigue operativo; reintenta en unos segundos."
                );
              }
            }
          } else if (booksResult.value.length === 0) {
            // Si no hay resultados visibles, vaciamos facets para evitar opciones obsoletas.
            setFacets({ total: 0, categories: {}, authors: {} });
          } else {
            const derivedFacets = deriveFacetsFromBooks(booksResult.value);
            setFacets(derivedFacets);
            const hasDerivedBuckets =
              Object.keys(derivedFacets.categories).length > 0 ||
              Object.keys(derivedFacets.authors).length > 0;
            if (!hasDerivedBuckets) {
              setFacetsNotice(
                "Filtros temporalmente no disponibles. El catálogo sigue operativo; reintenta en unos segundos."
              );
            }
          }
        }
      } catch (err) {
        if (!active || err?.name === "AbortError") {
          return;
        }
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
      requestController.abort();
    };
  }, [search, selectedCategory, selectedAuthor]);

  /**
   * Limpia filtros de facets y vuelve al listado base de la búsqueda actual.
   *
   * @returns {void}
   */
  const clearFacetFilters = () => {
    const nextParams = new URLSearchParams(searchParams);
    nextParams.delete("category");
    nextParams.delete("author");
    setSearchParams(nextParams);
  };

  /**
   * Aplica o limpia el filtro de categoría persistiéndolo en la URL.
   *
   * @param {string} value Categoría seleccionada en la UI.
   * @returns {void}
   */
  const handleCategoryChange = (value) => {
    const nextParams = new URLSearchParams(searchParams);
    if (value) {
      nextParams.set("category", value);
    } else {
      nextParams.delete("category");
    }
    setSearchParams(nextParams);
  };

  /**
   * Aplica o limpia el filtro de autor persistiéndolo en la URL.
   *
   * @param {string} value Autor seleccionado en la UI.
   * @returns {void}
   */
  const handleAuthorChange = (value) => {
    const nextParams = new URLSearchParams(searchParams);
    if (value) {
      nextParams.set("author", value);
    } else {
      nextParams.delete("author");
    }
    setSearchParams(nextParams);
  };

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
      <div className="flex flex-wrap items-end justify-between gap-4 mb-6">
        <div>
          <h1 className="text-3xl font-bold text-text-main">Catálogo de Libros</h1>
          {!loading && !error && (
            <p className="text-sm text-text-muted mt-1">
              {facets.total} resultados para la búsqueda actual.
            </p>
          )}
        </div>
        <button
          type="button"
          onClick={clearFacetFilters}
          className="px-3 py-2 text-sm rounded-md border border-border text-text-body hover:bg-white transition-colors"
          disabled={!selectedCategory && !selectedAuthor}
        >
          Limpiar filtros
        </button>
      </div>

      {!loading && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-6">
          <label className="flex flex-col gap-1 text-sm text-text-body">
            Filtrar por categoría
            <select
              value={selectedCategory}
              onChange={(event) => handleCategoryChange(event.target.value)}
              className="px-3 py-2 border border-border rounded-md bg-white text-text-main"
              disabled={Object.keys(facets.categories).length === 0}
            >
              <option value="">Todas las categorías</option>
              {Object.entries(facets.categories).map(([name, count]) => (
                <option key={name} value={name}>
                  {name} ({count})
                </option>
              ))}
            </select>
          </label>

          <label className="flex flex-col gap-1 text-sm text-text-body">
            Filtrar por autor
            <select
              value={selectedAuthor}
              onChange={(event) => handleAuthorChange(event.target.value)}
              className="px-3 py-2 border border-border rounded-md bg-white text-text-main"
              disabled={Object.keys(facets.authors).length === 0}
            >
              <option value="">Todos los autores</option>
              {Object.entries(facets.authors).map(([name, count]) => (
                <option key={name} value={name}>
                  {name} ({count})
                </option>
              ))}
            </select>
          </label>
        </div>
      )}
      {!loading && facetsNotice && <p className="text-amber-700 text-sm mb-4">{facetsNotice}</p>}

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
