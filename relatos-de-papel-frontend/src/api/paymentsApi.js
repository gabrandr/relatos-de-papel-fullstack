import { gatewayRequest } from "./gatewayClient";

/**
 * Crea un pago en `ms-books-payments` a través del gateway.
 *
 * @param {{ userId: number, bookId: number, quantity: number }} params Payload mínimo requerido por la API de pagos.
 * @returns {Promise<Record<string, any>>} Respuesta del pago creado.
 */
export const createPayment = async ({ userId, bookId, quantity }) => {
  return gatewayRequest({
    path: "/api/payments",
    targetMethod: "POST",
    body: {
      userId,
      bookId,
      quantity,
    },
  });
};
