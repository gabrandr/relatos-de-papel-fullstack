import { Link } from "react-router-dom";
import { useCartStore } from "../../store/useCartStore";

/**
 * Resumen del pedido con total y botón para ir a checkout.
 */
const OrderSummary = () => {
  const getCartTotal = useCartStore((state) => state.getCartTotal);
  const total = getCartTotal();

  return (
    <div className="bg-background p-8 rounded-xl shadow-sm h-fit border border-border">
      <h2 className="text-xl font-bold text-text-main mb-6">
        Resumen del Pedido
      </h2>

      {/* Resumen Financiero */}
      <div className="space-y-3 mb-6 text-text-body">
        <div className="flex justify-between">
          <span>Subtotal</span>
          <span className="font-medium">${total.toFixed(2)}</span>
        </div>
      </div>

      <div className="flex justify-between items-end mb-8 border-t border-border pt-6">
        <span className="font-bold text-lg text-text-main">Total</span>
        <div className="text-right">
          <span className="block text-2xl font-bold text-primary">
            ${total.toFixed(2)}
          </span>
          <span className="text-xs text-text-muted">Impuestos incluidos</span>
        </div>
      </div>

      <Link
        to="/checkout"
        className="block text-center w-full bg-primary text-white py-4 rounded-xl font-bold hover:bg-primary-dark transition-all transform hover:-translate-y-0.5 shadow-lg shadow-primary/30 uppercase tracking-widest text-sm"
      >
        Proceder al Pago
      </Link>
    </div>
  );
};

export default OrderSummary;
