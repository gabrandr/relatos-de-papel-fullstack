import { Link } from "react-router-dom";
import { useCartStore } from "../store/useCartStore";
import PaymentMethod from "../features/checkout/PaymentMethod";
import CheckoutSummary from "../features/checkout/CheckoutSummary";

/**
 * Página de checkout con método de pago y resumen final.
 */
const CheckoutPage = () => {
  const cart = useCartStore((state) => state.cart);

  return (
    <div className="min-h-screen py-8 lg:py-12">
      <div className="container mx-auto px-4 max-w-6xl">
        {/* Header de Checkout */}
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-2xl font-bold text-text-main">Checkout</h1>
          <div className="flex items-center gap-2">
            <span className="text-text-muted font-medium">Checkout Seguro</span>
            <span className="text-success">🔒</span>
          </div>
        </div>

        <div className="flex flex-col lg:flex-row gap-8">
          {/* Columna Izquierda: Resumen de libros + Método de pago */}
          <div className="flex-1 lg:flex-2 space-y-6">
            {/* Resumen de libros a comprar */}
            <div className="bg-surface p-6 rounded-xl shadow-sm border border-border">
              <h2 className="text-xl font-bold text-text-main mb-4">
                Resumen de tu pedido ({cart.length}{" "}
                {cart.length === 1 ? "libro" : "libros"})
              </h2>
              <div className="space-y-4">
                {cart.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center gap-4 border-b border-border pb-4 last:border-0"
                  >
                    <img
                      src={item.image}
                      alt={item.title}
                      className="w-16 h-20 object-cover rounded"
                    />
                    <div className="flex-1">
                      <h3 className="font-bold text-text-main">{item.title}</h3>
                      <p className="text-sm text-text-muted">{item.author}</p>
                      <p className="text-sm text-text-body">
                        Cantidad: {item.quantity}
                      </p>
                    </div>
                    <p className="font-bold text-primary">
                      ${(item.price * item.quantity).toFixed(2)}
                    </p>
                  </div>
                ))}
              </div>
            </div>

            {/* Método de pago */}
            <PaymentMethod />
          </div>

          {/* Columna Derecha: Resumen de pago */}
          <div className="flex-1">
            <CheckoutSummary />
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
