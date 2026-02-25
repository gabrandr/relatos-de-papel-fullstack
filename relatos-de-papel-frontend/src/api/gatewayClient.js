/**
 * URL base del gateway backend. Se normaliza para evitar doble slash al concatenar rutas.
 * @type {string}
 */
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || "http://localhost:8762").replace(/\/$/, "");

/**
 * Normaliza query params al formato esperado por el gateway:
 * cada key debe ser un array de strings.
 *
 * @param {Record<string, string | number | boolean | Array<string | number | boolean> | null | undefined>} [queryParams={}] Parámetros en formato simple.
 * @returns {Record<string, string[]>} Parámetros serializados para el contrato `queryParams` del gateway.
 */
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

/**
 * Ejecuta una petición contra el gateway utilizando el patrón POST + targetMethod.
 *
 * @param {Object} request Configuración de la petición.
 * @param {string} request.path Ruta del recurso backend (ejemplo: `/api/books`).
 * @param {"GET" | "POST" | "PUT" | "PATCH" | "DELETE"} request.targetMethod Método HTTP real que debe aplicar el gateway.
 * @param {Record<string, string | number | boolean | Array<string | number | boolean> | null | undefined>} [request.queryParams] Query params de la operación real.
 * @param {unknown} [request.body] Body de la operación real.
 * @param {AbortSignal} [request.signal] Señal opcional para cancelar la petición HTTP.
 * @returns {Promise<any>} Payload parseado (JSON o fallback con `message`).
 * @throws {Error} Error funcional cuando el backend responde estado no exitoso.
 */
export const gatewayRequest = async ({ path, targetMethod, queryParams, body, signal }) => {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    signal,
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
