import { useNavigate } from "react-router-dom";
import { useCartStore } from "../../store/useCartStore";

/**
 * Resumen final del pedido y botón para confirmar compra.
 */
const CheckoutSummary = () => {
  const navigate = useNavigate();
  const getCartTotal = useCartStore((state) => state.getCartTotal);
  const total = getCartTotal();

  const handleConfirmOrder = () => {
    // Redirigir a confirmación con estado para permitir acceso
    navigate("/order-confirmation", { state: { orderCompleted: true } });
  };

  return (
    <div className="bg-surface p-6 rounded-xl shadow-sm border border-border h-fit sticky top-8">
      <h2 className="text-xl font-bold text-text-main mb-6">Resumen Final</h2>

      <div className="space-y-3 mb-6 text-text-body text-sm">
        <div className="flex justify-between">
          <span>Productos</span>
          <span>${total.toFixed(2)}</span>
        </div>
        <div className="flex justify-between">
          <span>Envío y manejo</span>
          <span className="text-success">Gratis</span>
        </div>
        <div className="flex justify-between">
          <span>Impuestos</span>
          <span>$0.00</span>
        </div>

        <hr className="border-border my-4" />

        <div className="flex justify-between text-lg font-bold text-text-main">
          <span>Total pedido</span>
          <span className="text-primary">${total.toFixed(2)}</span>
        </div>
      </div>

      <button
        onClick={handleConfirmOrder}
        className="w-full bg-primary text-white py-4 rounded-xl font-bold hover:bg-primary-dark transition-all transform hover:-translate-y-0.5 shadow-lg shadow-primary/30 uppercase tracking-widest text-sm"
      >
        Confirmar y Pagar
      </button>

      <p className="text-xs text-center text-text-muted mt-4">
        Transacción segura y encriptada
      </p>
    </div>
  );
};

export default CheckoutSummary;
