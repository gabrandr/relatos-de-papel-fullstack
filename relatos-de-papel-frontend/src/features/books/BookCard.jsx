import { useState } from "react";
import { useCartStore } from "../../store/useCartStore";
import { useNavigate } from "react-router-dom";
import Button from "../../components/ui/Button";

const BookCard = ({ book }) => {
  const navigate = useNavigate();
  const addToCart = useCartStore((state) => state.addToCart);
  const [showToast, setShowToast] = useState(false);
  const [imageSrc, setImageSrc] = useState(book.image);

  const handleAddToCart = () => {
    addToCart(book);
    setShowToast(true);
    setTimeout(() => setShowToast(false), 2000);
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
          onClick={() => navigate(`/book/${book.id}`)}
        />
        <h3
          className="font-bold cursor-pointer hover:text-primary"
          onClick={() => navigate(`/book/${book.id}`)}
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
