import { Link } from "react-router-dom";
import { useCartStore } from "../store/useCartStore";
import CartItem from "../features/cart/CartItem";
import OrderSummary from "../features/cart/OrderSummary";

/**
 * Página del carrito con lista de items y resumen del pedido.
 */
const CartPage = () => {
  const cart = useCartStore((state) => state.cart);

  // Si el carrito está vacío
  if (cart.length === 0) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center p-8 text-center">
        <h2 className="text-2xl font-bold text-text-main mb-4">
          Tu carrito está vacío
        </h2>
        <Link to="/home" className="text-primary hover:underline">
          Volver a la tienda
        </Link>
      </div>
    );
  }

  return (
    <div className="flex flex-col lg:flex-row gap-8 w-full px-4 lg:w-9/12 lg:px-0 mx-auto py-8">
      {/* Lado Izquierdo: Lista de Items */}
      <div className="flex-1 lg:flex-2">
        {/* Header de la Tabla (Oculto en móvil) */}
        <div className="hidden sm:grid grid-cols-[2fr_1fr_1fr] bg-background p-4 rounded-t-lg text-text-body font-medium text-sm uppercase tracking-wider mb-2 border-b-2 border-primary/20">
          <div className="pl-4">Item</div>
          <div className="text-center">Cantidad</div>
          <div className="text-right pr-4">Precio</div>
        </div>

        <div className="bg-surface rounded-lg shadow-sm p-4 border border-border">
          {cart.map((item) => (
            <CartItem key={item.id} item={item} />
          ))}
        </div>
      </div>

      {/* Lado Derecho: Resumen */}
      <div className="flex-1">
        <OrderSummary />
      </div>
    </div>
  );
};

export default CartPage;
