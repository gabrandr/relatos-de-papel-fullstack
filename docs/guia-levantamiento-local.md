# Guía detallada de levantamiento local

## 1. Requisitos previos

- Docker Desktop instalado y levantado.
- Node.js 20+ y npm.
- Puertos disponibles en tu máquina:
  - `8761` (Eureka)
  - `8762` (Gateway)
  - `8081` (`ms-books-catalogue`)
  - `8082` (`ms-books-payments`)
  - `5432` (PostgreSQL)

## 2. Estructura relevante del proyecto

- Backend: `relatos-de-papel-backend/`
- Frontend: `relatos-de-papel-frontend/`
- Compose completo local backend: `relatos-de-papel-backend/docker-compose.backend-local.yml`
- Compose aislado payments + postgres: `relatos-de-papel-backend/docker-compose.payments-postgres.yml`

## 3. Configurar variables de OpenSearch (Bonsai)

### 3.1. Crear `.env` en backend

Ubicación: `relatos-de-papel-backend/.env`

Contenido esperado:

```env
OPENSEARCH_URL=https://TU_USER:TU_PASS@TU_CLUSTER.bonsaisearch.net
OPENSEARCH_USERNAME=TU_USER
OPENSEARCH_PASSWORD=TU_PASS
OPENSEARCH_INDEX=relatos
```

Notas:

- El archivo `.env` ya está ignorado en git para no subir credenciales.
- `OPENSEARCH_INDEX=relatos` es el índice que usa el buscador.

## 4. Levantar backend completo en local

Desde `relatos-de-papel-backend/`:

```bash
docker compose -f docker-compose.backend-local.yml up --build
```

Esto levanta:

- `eureka-server`
- `gateway`
- `ms-books-catalogue` (OpenSearch/Bonsai)
- `ms-books-payments` (PostgreSQL)
- `postgres-payments`

## 5. Verificar que backend esté arriba

### 5.1. Ver contenedores

```bash
docker compose -f docker-compose.backend-local.yml ps
```

### 5.2. Revisar Eureka

Abrir en navegador:

- `http://localhost:8761`

Deberías ver registrados al menos:

- `MS-BOOKS-CATALOGUE`
- `MS-BOOKS-PAYMENTS`
- `gateway`

### 5.3. Probar gateway (recordatorio importante)

El gateway **solo recibe `POST`** y el método real va en `targetMethod`.

### 5.4. Verificación explícita de restricción POST-only (recomendado para evidencia)

Probar método directo `GET` al gateway:

```bash
curl -i http://localhost:8762/api/books
```

Resultado esperado: `405 Method Not Allowed`.

Probar preflight CORS (`OPTIONS`):

```bash
curl -i -X OPTIONS http://localhost:8762/api/books
```

Resultado esperado: respuesta de preflight (el gateway no ejecuta lógica de negocio por `OPTIONS`).

## 6. Pruebas rápidas por API (vía Gateway)

Antes de ejecutar pruebas manuales, importa la colección:

- Archivo: `docs/postman/Relatos-Backend-Gateway.postman_collection.json`
- En Postman: `Import` -> selecciona el JSON.
- Variables de colección:
  - `gateway_url` = `http://localhost:8762`
  - `book_id` = `1` (o el libro que quieras probar)
  - `payment_id` = `1` (actualízalo según resultados)

Recomendación: usar primero la carpeta **01 - Catalogo OpenSearch** y luego **02 - Payments**.

Adicionalmente, en frontend el buscador del header está conectado a `search_as_you_type` del cluster vía endpoint:

- `POST /api/books/search/suggest` con `targetMethod=GET`

### 6.1. Listar libros visibles

`POST http://localhost:8762/api/books`

Body:

```json
{
  "targetMethod": "GET",
  "queryParams": {},
  "body": null
}
```

### 6.2. Búsqueda full-text

`POST http://localhost:8762/api/books/search`

Body:

```json
{
  "targetMethod": "GET",
  "queryParams": {
    "title": ["quijote"],
    "visible": ["true"]
  },
  "body": null
}
```

### 6.3. Sugerencias

`POST http://localhost:8762/api/books/search/suggest`

Body:

```json
{
  "targetMethod": "GET",
  "queryParams": {
    "text": ["qui"],
    "size": ["8"]
  },
  "body": null
}
```

### 6.4. Facets

`POST http://localhost:8762/api/books/search/facets`

Body:

```json
{
  "targetMethod": "GET",
  "queryParams": {
    "visible": ["true"]
  },
  "body": null
}
```

### 6.5. Crear pago

`POST http://localhost:8762/api/payments`

Body:

```json
{
  "targetMethod": "POST",
  "queryParams": {},
  "body": {
    "userId": 1,
    "bookId": 1,
    "quantity": 1
  }
}
```

## 7. Levantar frontend en local

Desde `relatos-de-papel-frontend/`:

### 7.1. Configurar `.env`

Crear `relatos-de-papel-frontend/.env` con:

```env
VITE_API_BASE_URL=http://localhost:8762
```

### 7.2. Instalar dependencias

```bash
npm install
```

### 7.3. Ejecutar frontend

```bash
npm run dev
```

Abrir:

- URL de Vite (normalmente `http://localhost:5173`)

## 8. Flujo funcional esperado (end-to-end)

1. Entrar a Home y ver libros cargados desde backend (no mocks).
2. Buscar por título en header.
3. Entrar a detalle de libro.
4. Añadir al carrito.
5. Ir a checkout y confirmar pago.
6. Ver confirmación con referencia de pagos.

## 9. Comandos útiles de operación

### 9.1. Detener stack backend

```bash
docker compose -f docker-compose.backend-local.yml down
```

### 9.2. Detener y borrar volúmenes (reinicio limpio PostgreSQL)

```bash
docker compose -f docker-compose.backend-local.yml down -v
```

### 9.3. Ver logs de un servicio

```bash
docker logs -f ms-books-catalogue
docker logs -f ms-books-payments
docker logs -f gateway
```

## 10. Troubleshooting rápido

- Error de conexión a OpenSearch:
  - revisar `OPENSEARCH_URL`, `OPENSEARCH_USERNAME`, `OPENSEARCH_PASSWORD` en `relatos-de-papel-backend/.env`.
- `503 Service Unavailable` transitorio desde gateway tras reiniciar servicios:
  - esperar 20-60 segundos a que Eureka refresque registros y volver a probar.
  - validar `docker compose -f docker-compose.backend-local.yml ps` y `http://localhost:8761/eureka/apps`.
- Mensaje frontend: "Filtros temporalmente no disponibles":
  - suele estar asociado a limitación temporal del proveedor OpenSearch (`429 Too Many Requests`).
  - el catálogo principal sigue operativo y el frontend aplica fallback local de filtros; reintentar unos segundos después.
- `ms-books-payments` no levanta:
  - validar que `postgres-payments` esté healthy en `docker compose ps`.
- Front no conecta al backend:
  - revisar `VITE_API_BASE_URL` y que gateway esté en `http://localhost:8762`.
- Error por puertos ocupados:
  - liberar puertos o cambiar mapeos en `docker-compose.backend-local.yml`.
