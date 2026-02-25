# Auditoría de Cumplimiento Local - Actividad 3 (Relatos de Papel)

- Fecha de auditoría base: 2026-02-25 00:56:47 -05
- Fecha de reauditoría (post-remediación): 2026-02-25 01:14:00 -05
- Alcance: frontend + backend + documentación, con validación E2E en local
- Criterios evaluados en local: C1, C2, C3, C4
- Criterios fuera de alcance local (pero reportados): C5, C6

## 1) Cambios aplicados en esta iteración (pasos 1, 2 y 3)

### Paso 1 - Corrección de mapping/reindex para facets (backend)
- Se añadió migración opcional de índice incompatible por configuración:
  - `opensearch.recreate-on-incompatible-mapping` (`OPENSEARCH_RECREATE_ON_INCOMPATIBLE_MAPPING`).
- Se añadió detección de mapping incompatible para facets (campos `text` sin `keyword`).
- Se implementó reindexación controlada (source -> temp -> source) para operación de mantenimiento.
- Se implementó fallback de facets por script sobre `_source` cuando el índice legacy no permite aggregations por campo.
- Se hizo compatible el filtro de categoría para índices legacy y nuevos (`category`, `category.keyword`, `match_phrase`).

### Paso 2 - Integración facets en frontend
- Se añadió consumo de endpoint `/api/books/search/facets`.
- Se añadieron filtros UI en Home por:
  - categoría,
  - autor.
- Se añadió botón de limpieza de filtros.
- Se conectaron filtros con búsqueda real backend (`/api/books/search`).

### Paso 3 - Reejecución de auditoría E2E
- Se reconstruyó y reinició `ms-books-catalogue` con los cambios.
- Se revalidaron endpoints críticos de catálogo, facets y filtros.

## 2) Evidencia estática (código)

### Backend
- Config de migración opcional:
  - `relatos-de-papel-backend/ms-books-catalogue/src/main/resources/application.yaml`
  - `relatos-de-papel-backend/ms-books-catalogue/src/main/java/com/relatosdepapel/ms_books_catalogue/config/OpenSearchProperties.java`
- Lógica de facets robusta + migración/reindex + compatibilidad categoría:
  - `relatos-de-papel-backend/ms-books-catalogue/src/main/java/com/relatosdepapel/ms_books_catalogue/service/OpenSearchBookStore.java`

### Frontend
- API de facets y filtros en búsqueda:
  - `relatos-de-papel-frontend/src/api/booksApi.js`
- UI de filtros de facets:
  - `relatos-de-papel-frontend/src/pages/HomePage.jsx`

## 3) Evidencia de ejecución (reauditoría)

### 3.1 Build frontend
- Comando: `npm run build` en `relatos-de-papel-frontend`.
- Resultado: OK.

### 3.2 Backend actualizado
- Comando: `docker compose -f docker-compose.backend-local.yml up -d --build ms-books-catalogue`.
- Resultado: OK (imagen reconstruida y servicio reiniciado).

### 3.3 Facets operativo
- Comando: `POST /api/books/search/facets` vía gateway.
- Resultado: `{"total":27,"categoryBuckets":9,"authorBuckets":20}`.

### 3.4 Filtros operativos para UI de facets
- Filtro categoría (`Clásicos`): devuelve `11` resultados.
- Filtro autor (`Leo Tolstoy`): devuelve `2` resultados.

### 3.5 Regresión funcional controlada
- Full-text (`title=quijote`) sigue devolviendo resultados (`1`).

## 4) Matriz final de cumplimiento (semáforo)

| Criterio | Estado | Evidencia principal | Conclusión |
|---|---|---|---|
| C1 - Buscador con OpenSearch | Cumple | OpenSearch activo + mapping gestionado + migración opcional | Cumple |
| C2 - Full-text/suggest/correcciones | Cumple | Endpoints operativos + frontend conectado | Cumple |
| C3 - Facets | Cumple | Endpoint facets responde + frontend usa facets en UI | Cumple |
| C4 - Integración local front-back | Cumple | Stack local activo + gateway contrato + compra/stock + build frontend | Cumple |
| C5 - Integración remota | No evaluable (pendiente) | Fuera de alcance local | Pendiente despliegue |
| C6 - Videomemoria | No evaluable (pendiente) | Fuera de alcance de programación local | Pendiente entrega final |

## 5) Estado actual y siguiente foco

- Estado local de programación: **apto para pasar a fase de despliegue**.
- Foco recomendado siguiente: resolver C5 (deploy backend/frontend públicos) y cerrar C6 (videomemoria).

## 6) Actualización de estabilidad (2026-02-25, cierre de sesión)

### Problema observado en uso real
- En picos de interacción de frontend se detectaron casos intermitentes de:
  - `500` en Home (origen real: `429 Too Many Requests` del proveedor Bonsai/OpenSearch),
  - aviso de filtros temporales mostrado aun cuando existían opciones útiles.

### Ajustes aplicados
- Backend (`ms-books-catalogue`):
  - reintentos con backoff ante `429`,
  - degradación controlada para evitar escalado a `500`,
  - caché de facets en memoria con TTL e invalidación por mutación.
- Frontend (`HomePage` + API):
  - cancelación de requests obsoletas (`AbortController`),
  - caché/deduplicación de consultas de facets,
  - fallback de facets derivadas desde libros cuando facets remoto falla,
  - aviso de indisponibilidad mostrado solo si no hay buckets utilizables.

### Revalidación técnica posterior
- `npm run build` (frontend): OK.
- Rebuild de `ms-books-catalogue`: OK.
- Smoke test con stack estabilizado:
  - `POST /api/books`: respuestas `200` consistentes.
  - `POST /api/books/search/facets`: respuestas `200` consistentes.
