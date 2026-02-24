import { gatewayRequest } from "./gatewayClient";

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
