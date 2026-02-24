import { useState } from "react";
import { useParams, Link } from "react-router-dom";
import { books } from "../data/books.mock";
import { useCartStore } from "../store/useCartStore";
import Button from "../components/ui/Button";

/**
 * Muestra detalle del libro y permite añadir al carrito.
 */
const BookDetailPage = () => {
  const { id } = useParams(); //obtiene el id del libro desde la URL
  const addToCart = useCartStore((state) => state.addToCart); //funcion para añadir al carrito
  const [showToast, setShowToast] = useState(false); //estado para mostrar el toast

  // Buscar el libro por ID
  const book = books.find((b) => b.id === parseInt(id));

  // Si no existe el libro
  if (!book) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center p-8 text-center bg-background">
        <h2 className="text-2xl font-bold text-text-main mb-4">
          Libro no encontrado
        </h2>
        <Link to="/home" className="text-primary hover:underline">
          Volver al catálogo
        </Link>
      </div>
    );
  }

  //funcion para añadir al carrito y mostrar toast
  const handleAddToCart = () => {
    addToCart(book);
    setShowToast(true);
    setTimeout(() => setShowToast(false), 2000); //oculta el toast despues de 2 segundos
  };

  return (
    <div className="min-h-screen py-12">
      {/* Toast de confirmacion */}
      {showToast && (
        <div className="fixed top-20 right-4 bg-success text-white px-6 py-3 rounded-lg shadow-lg z-50">
          ✓ {book.title} añadido al carrito
        </div>
      )}

      {/* Contenedor principal */}
      <div className="container mx-auto px-4 w-11/12 lg:w-9/12 bg-surface p-6 lg:p-12 rounded-2xl shadow-sm border border-border">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          {/* Columna Izquierda: Imagen */}
          <div className="flex justify-center items-start">
            <img
              src={book.image}
              alt={book.title}
              className="w-full max-w-sm h-auto object-contain rounded-lg shadow-xl border-4 border-white transform hover:scale-[1.02] transition-transform duration-500"
            />
          </div>

          {/* Columna Derecha: Detalles */}
          <div className="flex flex-col justify-center space-y-6">
            <div>
              {/* Tags decorativos - dinámicos */}
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

              <h1 className="text-3xl lg:text-4xl font-bold text-text-main mb-2 leading-tight">
                {book.title}
              </h1>
              <p className="text-xl text-text-muted font-medium mb-6">
                Autor: <span className="text-text-main">{book.author}</span>
              </p>

              <p className="text-text-body leading-relaxed mb-8 text-lg">
                {book.description}
              </p>
            </div>

            {/* Caja de precio y acción */}
            <div className="space-y-6 bg-background p-6 rounded-xl border border-border">
              {/* Precio */}
              <div className="flex items-center gap-4">
                <span className="text-4xl font-bold text-text-main">
                  ${book.price}
                </span>
              </div>

              {/* Botón Añadir */}
              <Button
                onClick={handleAddToCart}
                className="w-full bg-primary text-white py-4 rounded-full font-bold text-lg hover:bg-primary-dark transition-all shadow-lg hover:shadow-primary/30 active:scale-95"
              >
                Añadir al carrito
              </Button>

              {/* Volver al catálogo */}
              <Link
                to="/home"
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
