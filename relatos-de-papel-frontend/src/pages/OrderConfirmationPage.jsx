import { useEffect, useMemo } from "react";
import { Link, useLocation } from "react-router-dom";

import { useCartStore } from "../store/useCartStore";

const OrderConfirmationPage = () => {
  const location = useLocation();
  const clearCart = useCartStore((state) => state.clearCart);

  const orderReference = useMemo(() => {
    const ids = location.state?.paymentIds;
    if (Array.isArray(ids) && ids.length > 0) {
      return `#${ids.join("-")}`;
    }
    return `#${Math.floor(100000 + Math.random() * 900000)}`;
  }, [location.state]);

  useEffect(() => {
    clearCart();
    window.history.replaceState({}, document.title);
  }, [clearCart]);

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <div className="bg-surface p-8 rounded-2xl shadow-lg max-w-md w-full text-center border border-border">
        <div className="mb-6 flex justify-center">
          <div className="bg-success/20 p-4 rounded-full">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              className="size-16 text-success"
            >
              <path
                fillRule="evenodd"
                d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm13.36-1.814a.75.75 0 1 0-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 0 0-1.06 1.06l2.25 2.25a.75.75 0 0 0 1.14-.094l3.75-5.25Z"
                clipRule="evenodd"
              />
            </svg>
          </div>
        </div>

        <h1 className="text-3xl font-bold text-text-main mb-2">¡Gracias por tu compra!</h1>
        <p className="text-text-muted mb-8">
          Referencia de pago: <span className="font-mono font-bold text-text-body">{orderReference}</span>
        </p>

        <div className="space-y-4">
          <button className="w-full bg-primary text-white py-3.5 rounded-xl font-bold hover:bg-primary-dark transition-all shadow-lg shadow-primary/30 flex items-center justify-center gap-2 group">
            Descargar E-book Ahora
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth={2}
              stroke="currentColor"
              className="size-5 group-hover:translate-y-1 transition-transform"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M3 16.5v2.25A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75V16.5M16.5 12 12 16.5m0 0L7.5 12m4.5 4.5V3"
              />
            </svg>
          </button>

          <Link
            to="/home"
            className="w-full bg-background text-text-body py-3.5 rounded-xl font-bold hover:bg-border transition-colors block"
          >
            Volver a la tienda
          </Link>
        </div>
      </div>
    </div>
  );
};

export default OrderConfirmationPage;
