# Fase 4 - Integración Frontend + Backend y pruebas locales end-to-end

## Objetivo

Eliminar mocks del frontend, conectar contra el backend contenerizado y validar flujo completo en local.

## Alcance técnico

- Crear capa API HTTP en frontend.
- Reemplazar `books.mock` en catálogo y detalle.
- Conectar checkout con `ms-books-payments` vía gateway (POST + `targetMethod`).
- Probar búsquedas con OpenSearch y operaciones de pago en local.

## Cambios de código previstos

- Frontend: servicios HTTP, hooks/estado de carga/errores y variables `VITE_*`.
- Backend: ajustes menores de CORS/contratos si hiciera falta.

## Criterios de aceptación

- Frontend sin dependencia de mocks en flujo principal.
- Flujo catálogo -> detalle -> carrito -> checkout operativo en local.
- Gateway manteniendo su contrato POST-only.

## Intervención requerida del equipo

- Validar comportamiento funcional esperado del frontend.
- Confirmar mensajes y validaciones de error visibles al usuario.

## Evidencias a capturar

- Video corto de flujo completo en local.
- Capturas de peticiones a gateway.
- Evidencias de decremento/restauración de stock.

## Registro de cambios ejecutados

- [2026-02-24] Fase reasignada a integración local frontend-backend.
- [2026-02-24] Capa API frontend creada (`src/api/gatewayClient.js`, `booksApi.js`, `paymentsApi.js`) respetando gateway POST-only + `targetMethod`.
- [2026-02-24] `HomePage` migrada de `books.mock` a consumo backend con estados de carga/error.
- [2026-02-24] `BookDetailPage` migrada de `books.mock` a consumo backend por ID.
- [2026-02-24] `CheckoutSummary` integrado con `POST /api/payments` para confirmar compras reales.
- [2026-02-24] `OrderConfirmationPage` ajustada para mostrar referencia basada en IDs de pago reales.
- [2026-02-24] Añadido `.env.example` en frontend con `VITE_API_BASE_URL`.
- [2026-02-24] Validación frontend: `npm run build` ejecuta correctamente en `relatos-de-papel-frontend`.
- [2026-02-24] Estrategia de imágenes aplicada: portada por ISBN (`OpenLibrary`) + fallback local `/book-placeholder.svg`.
- [2026-02-24] Mensajes de error de red mejorados en Home/Detail para diagnosticar `Failed to fetch` cuando backend no está levantado.
- [2026-02-24] Header integrado con `search-as-you-type` real desde OpenSearch (`/api/books/search/suggest`) con sugerencias en tiempo real.
- [2026-02-24] `getBooks` ahora aplica fallback por sugerencia cuando una búsqueda tipográfica no retorna resultados directos.
- [2026-02-25] Diagnóstico end-to-end de inconsistencia portada/título: se confirmó que frontend construye imagen por ISBN desde OpenLibrary y que el desajuste provenía de ISBN antiguos servidos por backend no reiniciado.
- [2026-02-25] Validación de contrato gateway POST-only mantenido: frontend continúa enviando `POST` con `targetMethod` y el gateway traduce a método real.
- [2026-02-25] Verificación de integración `payments -> catalogue`: compra de prueba decrementa stock en catálogo (caso validado vía gateway en libro `id=9`, de 14 a 13 unidades).
- [2026-02-25] Revisión de colección Postman de gateway: continúa compatible con catálogo expandido (uso de variables `book_id`/`payment_id` sin hardcode de tamaño de catálogo).
- [2026-02-25] Mejora UX en `Header`: buscador sincronizado con `URL search params`, botón visible para limpiar búsqueda (`X`) con tooltip "Limpiar búsqueda", y reset explícito a `/home` sin query.
- [2026-02-25] Mejora UX de navegación catálogo/detalle: al abrir un libro se preserva contexto de retorno (`fromPath`, `scrollY`) y el enlace "Volver al catálogo" restaura ruta y posición previa del usuario.
- [2026-02-25] Ajuste de scroll global: `ScrollToTop` evita forzar `scrollTo(0,0)` cuando la navegación indica restauración contextual, manteniendo comportamiento estándar en el resto de rutas.
- [2026-02-25] Mejora post-compra: `OrderConfirmationPage` incorpora contador regresivo de 5 segundos reutilizando `useCountdown` para redirección automática a `/home`, conservando acción manual "Volver a la tienda".
- [2026-02-25] Robustez de countdown: `useCountdown` actualizado para evitar callbacks duplicados y prevenir dobles redirecciones cuando coincide click manual con auto-redirección.
- [2026-02-25] Validación frontend posterior a mejoras UX: `npm run build` ejecuta correctamente en `relatos-de-papel-frontend`.
