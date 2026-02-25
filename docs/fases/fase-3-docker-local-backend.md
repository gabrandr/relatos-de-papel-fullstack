# Fase 3 - Contenerización local del backend

## Objetivo

Crear imágenes Docker para los servicios backend y orquestarlos localmente.

## Alcance técnico

- Crear un `Dockerfile` por servicio backend:
  - `eureka-server`
  - `gateway`
  - `ms-books-catalogue`
  - `ms-books-payments`
- Crear `docker-compose.yml` para entorno local.
- Parametrizar por variables de entorno (puertos, URLs, credenciales).
- Incluir contenedor `postgres` para `ms-books-payments`.
- Configurar `ms-books-catalogue` para consumir Bonsai/OpenSearch desde variables de entorno.

## Cambios de código previstos

- Archivos `Dockerfile` en cada servicio.
- `docker-compose.yml` en raíz backend (o raíz repo).
- Ajustes en `application.yaml/yml` para usar variables de entorno.

## Criterios de aceptación

- Backend completo levanta con Docker local.
- Eureka registra servicios correctamente.
- Gateway enruta correctamente.

## Intervención requerida del equipo

- Validar puertos locales disponibles.
- Aportar máquina/entorno para prueba reproducible del stack dockerizado.

## Evidencias a capturar

- `docker compose ps` mostrando servicios arriba.
- Logs de registro en Eureka.
- Pruebas vía gateway.

## Registro de cambios ejecutados

- [2026-02-24] Fase ajustada para incluir PostgreSQL como contenedor obligatorio del entorno local.
- [2026-02-24] Dockerfiles creados para `eureka-server`, `gateway` y `ms-books-catalogue` (Java 25 + build Maven multi-stage).
- [2026-02-24] Se creó `docker-compose.backend-local.yml` con stack completo: `eureka-server`, `gateway`, `ms-books-catalogue`, `ms-books-payments`, `postgres-payments`.
- [2026-02-24] Se creó `.env.example` con variables `OPENSEARCH_*`.
- [2026-02-24] Validación de sintaxis compose ejecutada: `docker compose -f docker-compose.backend-local.yml config`.
- [2026-02-24] Guía local ampliada con instrucciones para importar colección Postman de pruebas backend.
- [2026-02-25] Ajuste de persistencia de pagos en compose local: `ms-books-payments` pasó a `JPA_DDL_AUTO=update` y `SQL_INIT_MODE=never` para evitar reinicialización de tabla/seed en reinicios.
- [2026-02-25] Rebuild/restart de servicios en local para aplicar cambios de sesión (`ms-books-catalogue`, `gateway`, `ms-books-payments`) y validación de estado `Running` en stack.
