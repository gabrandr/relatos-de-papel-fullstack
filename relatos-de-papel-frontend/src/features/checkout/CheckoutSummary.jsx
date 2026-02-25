import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { createPayment } from "../../api/paymentsApi";
import { useCartStore } from "../../store/useCartStore";

/**
 * Resumen final de checkout con envío de pagos al backend.
 *
 * @returns {JSX.Element} Panel de resumen con confirmación de compra.
 */
const CheckoutSummary = () => {
  const navigate = useNavigate();
  const cart = useCartStore((state) => state.cart);
  const getCartTotal = useCartStore((state) => state.getCartTotal);

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  const total = getCartTotal();

  /**
   * Confirma la orden enviando un pago por cada ítem del carrito.
   * Si todo finaliza correctamente, navega a confirmación con los ids de pago.
   *
   * @returns {Promise<void>}
   */
  const handleConfirmOrder = async () => {
    if (cart.length === 0 || isSubmitting) {
      return;
    }

    setIsSubmitting(true);
    setError("");

    try {
      // Se mantiene secuencial para simplificar trazabilidad de errores por item.
      const results = [];
      for (const item of cart) {
        const payment = await createPayment({
          userId: 1,
          bookId: item.id,
          quantity: item.quantity,
        });
        results.push(payment);
      }

      navigate("/order-confirmation", {
        state: {
          orderCompleted: true,
          paymentIds: results.map((payment) => payment.id),
        },
      });
    } catch (err) {
      setError(err.message || "No se pudo confirmar la compra");
    } finally {
      setIsSubmitting(false);
    }
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

      {error && <p className="text-sm text-red-600 mb-4">{error}</p>}

      <button
        onClick={handleConfirmOrder}
        disabled={isSubmitting}
        className="w-full bg-primary text-white py-4 rounded-xl font-bold hover:bg-primary-dark transition-all transform hover:-translate-y-0.5 shadow-lg shadow-primary/30 uppercase tracking-widest text-sm disabled:opacity-60 disabled:cursor-not-allowed"
      >
        {isSubmitting ? "Procesando..." : "Confirmar y Pagar"}
      </button>

      <p className="text-xs text-center text-text-muted mt-4">Transacción segura y encriptada</p>
    </div>
  );
};

export default CheckoutSummary;
