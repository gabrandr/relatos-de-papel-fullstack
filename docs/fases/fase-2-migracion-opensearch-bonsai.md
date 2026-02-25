# Fase 2 - Migración del buscador a OpenSearch (Bonsai)

## Objetivo

Migrar `ms-books-catalogue` desde búsqueda relacional (JPA Specifications) a OpenSearch en Bonsai, incluyendo full-text, sugerencias y facets para uso local previo al despliegue remoto.

## Alcance técnico

- Integrar cliente OpenSearch en `ms-books-catalogue`.
- Diseñar índice y mapping de libros:
  - Campos `text` para full-text.
  - Campos `keyword` para filtros exactos/facets.
  - Campo `search_as_you_type` para autocompletado.
- Implementar:
  - búsqueda full-text,
  - sugerencias/autocomplete,
  - facets (por ejemplo categoría, autor, rango precio).
- Definir sincronización de datos iniciales al índice.
- Mantener compatibilidad con Gateway POST-only (`targetMethod`).

## Cambios de código previstos

- `pom.xml` de `ms-books-catalogue` (dependencias OpenSearch).
- Nueva capa repository/service para consultas OpenSearch.
- Nuevos DTO de respuesta de búsqueda (si aplica).
- Variables de entorno para Bonsai/OpenSearch.

## Criterios de aceptación (rúbrica)

- Se usa OpenSearch para búsquedas del buscador.
- Se soportan sugerencias/correcciones/full-text.
- Se soportan facets.

## Intervención requerida del equipo

- Entregar credenciales Bonsai/OpenSearch:
  - endpoint,
  - usuario,
  - contraseña,
  - políticas de acceso.
- Confirmar nombre del índice definitivo.

## Evidencias a capturar

- Resultado de creación de índice/mapping.
- Query de búsqueda full-text.
- Query de sugerencias.
- Query con facets.

## Registro de cambios ejecutados

- [2026-02-24] Fase confirmada con restricción de uso exclusivo de OpenSearch en `ms-books-catalogue`.
- [2026-02-24] `ms-books-catalogue/pom.xml`: agregado cliente `opensearch-rest-client` y retirada dependencia runtime de H2.
- [2026-02-24] `ms-books-catalogue/src/main/resources/application.yaml`: configuración por variables `OPENSEARCH_URL`, `OPENSEARCH_USERNAME`, `OPENSEARCH_PASSWORD`, `OPENSEARCH_INDEX`.
- [2026-02-24] `ms-books-catalogue/src/main/java/.../config/OpenSearchConfig.java`: cliente OpenSearch con autenticación basic para Bonsai.
- [2026-02-24] `ms-books-catalogue/src/main/java/.../service/OpenSearchBookStore.java`: implementación CRUD, full-text, suggest y facets sobre índice OpenSearch.
- [2026-02-24] `ms-books-catalogue/src/main/java/.../service/BookServiceImpl.java`: lógica de negocio migrada de JPA a OpenSearch.
- [2026-02-24] `ms-books-catalogue/src/main/java/.../controller/BookController.java`: nuevos endpoints `GET /api/books/search/suggest` y `GET /api/books/search/facets`.
- [2026-02-24] Se retiró la capa JPA residual del catálogo (`entity`, `repository`, `specification`, `utils/Consts`) para evitar dependencia relacional en el buscador.
- [2026-02-24] Credenciales de Bonsai recibidas del equipo y preparadas para uso por variables de entorno (sin hardcode en repositorio).
- [2026-02-24] Ajuste de relevancia de búsqueda: `title` ahora usa prefijo sobre campos `search_as_you_type` (menos ruido en resultados).
- [2026-02-24] Ajuste de sugerencias: filtrado de resultados visibles y pertinencia textual para evitar sugerencias no relacionadas.
- [2026-02-24] Corrección de ISBN semilla para mejorar resolución de portadas por OpenLibrary.
- [2026-02-24] La semilla pasó de \"solo si índice vacío\" a sincronización de catálogo base para corregir datos existentes en el índice.
