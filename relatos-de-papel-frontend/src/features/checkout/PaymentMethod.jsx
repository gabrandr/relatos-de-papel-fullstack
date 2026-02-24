import { useState } from "react";

/**
 * Selector de método de pago (Tarjeta o PayPal).
 */
const PaymentMethod = () => {
  const [method, setMethod] = useState("card"); // 'card' | 'paypal'
  const [cardData, setCardData] = useState({
    number: "",
    expiry: "",
    cvc: "",
    name: "",
  });

  const handleCardChange = (e) => {
    const { name, value } = e.target;
    setCardData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  return (
    <div className="bg-surface p-6 rounded-xl shadow-sm border border-border">
      <h2 className="text-xl font-bold text-text-main mb-6 flex items-center gap-2">
        💳 Método de pago
      </h2>

      {/* Tabs */}
      <div className="flex gap-4 mb-6">
        <button
          onClick={() => setMethod("card")}
          className={`flex-1 py-3 px-4 rounded-lg border text-sm font-bold flex items-center justify-center gap-2 transition-all
            ${
              method === "card"
                ? "bg-background border-primary text-primary"
                : "border-border text-text-muted hover:bg-background"
            }
          `}
        >
          💳 Tarjeta
        </button>
        <button
          onClick={() => setMethod("paypal")}
          className={`flex-1 py-3 px-4 rounded-lg border text-sm font-bold flex items-center justify-center gap-2 transition-all
            ${
              method === "paypal"
                ? "bg-background border-primary text-primary"
                : "border-border text-text-muted hover:bg-background"
            }
          `}
        >
          🅿️ PayPal
        </button>
      </div>

      {/* Content */}
      <div className="bg-background p-6 rounded-lg border border-border min-h-50 flex flex-col justify-center">
        {method === "card" && (
          <div className="space-y-4">
            <div>
              <input
                type="text"
                name="number"
                value={cardData.number}
                onChange={handleCardChange}
                placeholder="Número de tarjeta"
                className="w-full p-3 bg-surface border border-border rounded-lg focus:ring-2 focus:ring-primary focus:outline-none"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <input
                type="text"
                name="expiry"
                value={cardData.expiry}
                onChange={handleCardChange}
                placeholder="MM/YY"
                className="w-full p-3 bg-surface border border-border rounded-lg focus:ring-2 focus:ring-primary focus:outline-none"
              />
              <input
                type="text"
                name="cvc"
                value={cardData.cvc}
                onChange={handleCardChange}
                placeholder="CVC"
                className="w-full p-3 bg-surface border border-border rounded-lg focus:ring-2 focus:ring-primary focus:outline-none"
              />
            </div>
            <div>
              <input
                type="text"
                name="name"
                value={cardData.name}
                onChange={handleCardChange}
                placeholder="Nombre en la tarjeta"
                className="w-full p-3 bg-surface border border-border rounded-lg focus:ring-2 focus:ring-primary focus:outline-none"
              />
            </div>
          </div>
        )}

        {method === "paypal" && (
          <div className="text-center space-y-4">
            <p className="text-text-body mb-2">
              Serás redirigido a PayPal para completar tu compra de forma
              segura.
            </p>
            <button className="bg-[#FFC439] text-text-main px-8 py-3 rounded-full font-bold hover:brightness-105 transition-all w-full max-w-xs mx-auto shadow-sm">
              Pagar con PayPal
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default PaymentMethod;
