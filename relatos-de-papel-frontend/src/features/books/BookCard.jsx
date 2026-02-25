import { useState } from "react";
import { useCartStore } from "../../store/useCartStore";
import { useLocation, useNavigate } from "react-router-dom";
import Button from "../../components/ui/Button";

/**
 * Card de libro utilizada en el catálogo.
 *
 * @param {{ book: Record<string, any> }} props Propiedades del componente.
 * @param {Record<string, any>} props.book Libro a renderizar.
 * @returns {JSX.Element} Tarjeta con imagen, datos básicos y acciones.
 */
const BookCard = ({ book }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const addToCart = useCartStore((state) => state.addToCart);
  const [showToast, setShowToast] = useState(false);
  const [imageSrc, setImageSrc] = useState(book.image);

  /**
   * Agrega el libro al carrito y muestra confirmación temporal.
   *
   * @returns {void}
   */
  const handleAddToCart = () => {
    addToCart(book);
    setShowToast(true);
    setTimeout(() => setShowToast(false), 2000);
  };

  /**
   * Navega al detalle preservando ruta y posición de scroll para retorno contextual.
   *
   * @returns {void}
   */
  const handleOpenDetail = () => {
    navigate(`/book/${book.id}`, {
      state: {
        fromPath: `${location.pathname}${location.search}`,
        scrollY: window.scrollY,
      },
    });
  };

  return (
    <>
      {showToast && (
        <div className="fixed top-20 right-4 bg-success text-white px-6 py-3 rounded-lg shadow-lg z-50">
          ✓ {book.title} añadido al carrito
        </div>
      )}

      <div className="bg-white p-4 rounded-lg shadow">
        <img
          src={imageSrc}
          alt={book.title}
          className="w-full h-48 object-contain mb-4 cursor-pointer hover:opacity-80"
          onError={() => setImageSrc(book.imageFallback || "/book-placeholder.svg")}
          onClick={handleOpenDetail}
        />
        <h3
          className="font-bold cursor-pointer hover:text-primary"
          onClick={handleOpenDetail}
        >
          {book.title}
        </h3>
        <p className="text-text-body">{book.author}</p>
        <p className="text-primary font-bold">${book.price}</p>
        <Button
          onClick={handleAddToCart}
          className="w-full bg-primary text-white py-2 rounded hover:bg-primary-dark"
        >
          Añadir al carrito
        </Button>
      </div>
    </>
  );
};

export default BookCard;
