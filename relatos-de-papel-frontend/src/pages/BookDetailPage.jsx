import { useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";

import Button from "../components/ui/Button";
import { getBookById } from "../api/booksApi";
import { useCartStore } from "../store/useCartStore";

/**
 * Página de detalle de libro con carga remota y retorno contextual al catálogo.
 *
 * @returns {JSX.Element} Vista de detalle, estados de carga/error y acciones de compra.
 */
const BookDetailPage = () => {
  const { id } = useParams();
  const location = useLocation();
  const addToCart = useCartStore((state) => state.addToCart);

  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showToast, setShowToast] = useState(false);
  const [imageSrc, setImageSrc] = useState("");
  const returnPath = typeof location.state?.fromPath === "string" ? location.state.fromPath : "/home";
  const returnState =
    typeof location.state?.scrollY === "number"
      ? { restoreScroll: true, scrollY: location.state.scrollY }
      : undefined;

  useEffect(() => {
    let active = true;

    /**
     * Carga el libro por ID y ajusta estados de la vista.
     * Usa bandera `active` para evitar setState tras desmontar componente.
     *
     * @returns {Promise<void>}
     */
    const loadBook = async () => {
      setLoading(true);
      setError("");
      try {
        const data = await getBookById(id);
        if (active) {
          setBook(data);
          setImageSrc(data.image);
        }
      } catch (err) {
        if (active) {
          const isNetworkError = err.message === "Failed to fetch";
          setError(
            isNetworkError
              ? "No hay conexión con el backend. Verifica que gateway y microservicios estén levantados."
              : err.message || "No se pudo cargar el libro"
          );
          setBook(null);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    loadBook();

    return () => {
      active = false;
    };
  }, [id]);

  /**
   * Agrega el libro actual al carrito y muestra un toast breve de confirmación.
   *
   * @returns {void}
   */
  const handleAddToCart = () => {
    if (!book) {
      return;
    }
    addToCart(book);
    setShowToast(true);
    setTimeout(() => setShowToast(false), 2000);
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-text-muted">Cargando detalle del libro...</p>
      </div>
    );
  }

  if (error || !book) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center p-8 text-center bg-background">
        <h2 className="text-2xl font-bold text-text-main mb-4">Libro no encontrado</h2>
        <p className="text-text-muted mb-6">{error || "No existe un libro con ese identificador."}</p>
        <Link to={returnPath} state={returnState} className="text-primary hover:underline">
          Volver al catálogo
        </Link>
      </div>
    );
  }

  return (
    <div className="min-h-screen py-12">
      {showToast && (
        <div className="fixed top-20 right-4 bg-success text-white px-6 py-3 rounded-lg shadow-lg z-50">
          ✓ {book.title} añadido al carrito
        </div>
      )}

      <div className="container mx-auto px-4 w-11/12 lg:w-9/12 bg-surface p-6 lg:p-12 rounded-2xl shadow-sm border border-border">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          <div className="flex justify-center items-start">
            <img
              src={imageSrc}
              alt={book.title}
              className="w-full max-w-sm h-auto object-contain rounded-lg shadow-xl border-4 border-white transform hover:scale-[1.02] transition-transform duration-500"
              onError={() => setImageSrc(book.imageFallback || "/book-placeholder.svg")}
            />
          </div>

          <div className="flex flex-col justify-center space-y-6">
            <div>
              <div className="flex gap-2 mb-4">
                <span className="bg-background text-text-body px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide">
                  {book.category}
                </span>
                {book.isBestSeller && (
                  <span className="bg-primary-light/20 text-primary px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide">
                    Best Seller
                  </span>
                )}
              </div>

              <h1 className="text-3xl lg:text-4xl font-bold text-text-main mb-2 leading-tight">{book.title}</h1>
              <p className="text-xl text-text-muted font-medium mb-6">
                Autor: <span className="text-text-main">{book.author}</span>
              </p>

              <p className="text-text-body leading-relaxed mb-8 text-lg">{book.description}</p>
            </div>

            <div className="space-y-6 bg-background p-6 rounded-xl border border-border">
              <div className="flex items-center gap-4">
                <span className="text-4xl font-bold text-text-main">${book.price}</span>
              </div>

              <Button
                onClick={handleAddToCart}
                className="w-full bg-primary text-white py-4 rounded-full font-bold text-lg hover:bg-primary-dark transition-all shadow-lg hover:shadow-primary/30 active:scale-95"
              >
                Añadir al carrito
              </Button>

              <Link
                to={returnPath}
                state={returnState}
                className="block text-center text-primary hover:underline"
              >
                ← Volver al catálogo
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookDetailPage;
