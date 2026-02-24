import { Navigate, useLocation } from "react-router-dom";
import { useCartStore } from "../store/useCartStore";

/**
 * Protege la ruta de checkout.
 * Solo permite acceso si hay items en el carrito.
 */
export const ProtectedCheckout = ({ children }) => {
  const cart = useCartStore((state) => state.cart);

  if (cart.length === 0) {
    return <Navigate to="/home" replace />;
  }

  return children;
};

/**
 * Protege la ruta de confirmación de orden.
 * Solo permite acceso si se ha completado una orden (verificado por location state).
 */
export const ProtectedOrderConfirmation = ({ children }) => {
  const location = useLocation();

  // Verificamos si existe el flag orderCompleted en el state de la navegación
  if (!location.state?.orderCompleted) {
    return <Navigate to="/home" replace />;
  }

  return children;
};
