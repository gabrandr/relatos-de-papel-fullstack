import { useState } from "react";
import { useCartStore } from "../../store/useCartStore";
import { useNavigate } from "react-router-dom";
import Button from "../../components/ui/Button";

/**
 * Tarjeta de libro con imagen, título, precio y botón de añadir.
 */
const BookCard = ({ book }) => {
  const navigate = useNavigate();
  const addToCart = useCartStore((state) => state.addToCart);
  const [showToast, setShowToast] = useState(false); //estado para mostrar el toast

  //funcion para añadir al carrito y mostrar toast
  const handleAddToCart = () => {
    addToCart(book);
    setShowToast(true);
    setTimeout(() => setShowToast(false), 2000); //oculta el toast despues de 2 segundos
  };

  return (
    <>
      {/* Toast de confirmacion */}
      {showToast && (
        <div className="fixed top-20 right-4 bg-success text-white px-6 py-3 rounded-lg shadow-lg z-50">
          ✓ {book.title} añadido al carrito
        </div>
      )}

      <div className="bg-white p-4 rounded-lg shadow">
        {/* imagen */}
        <img
          src={book.image}
          alt={book.title}
          className="w-full h-48 object-contain mb-4 cursor-pointer hover:opacity-80"
          onClick={() => navigate(`/book/${book.id}`)}
        />
        {/* titulo */}
        <h3
          className="font-bold cursor-pointer hover:text-primary"
          onClick={() => navigate(`/book/${book.id}`)}
        >
          {book.title}
        </h3>
        <p className="text-text-body">{book.author}</p>
        <p className="text-primary font-bold">${book.price}</p>
        {/*boton agregar carrito*/}
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
