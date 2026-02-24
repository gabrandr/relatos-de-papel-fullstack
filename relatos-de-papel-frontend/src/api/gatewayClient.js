const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || "http://localhost:8762").replace(/\/$/, "");

const normalizeQueryParams = (queryParams = {}) => {
  const normalized = {};
  Object.entries(queryParams).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") {
      return;
    }
    normalized[key] = Array.isArray(value)
      ? value.map((entry) => String(entry))
      : [String(value)];
  });
  return normalized;
};

export const gatewayRequest = async ({ path, targetMethod, queryParams, body }) => {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      targetMethod,
      queryParams: normalizeQueryParams(queryParams),
      body: body ?? null,
    }),
  });

  const responseText = await response.text();
  let parsedBody = null;
  if (responseText) {
    try {
      parsedBody = JSON.parse(responseText);
    } catch {
      parsedBody = { message: responseText };
    }
  }

  if (!response.ok) {
    const message = parsedBody?.message || `Error HTTP ${response.status}`;
    throw new Error(message);
  }

  return parsedBody;
};
