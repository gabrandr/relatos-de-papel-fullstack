# Plan maestro - Actividad 3 (Integración y despliegue)

## Estado actual detectado (diagnóstico)

- Frontend (`relatos-de-papel-frontend`) usa datos mock (`src/data/books.mock.js`) en catálogo y detalle.
- Backend (`relatos-de-papel-backend`) tiene 4 servicios:
  - `gateway` (Spring Cloud Gateway con filtro que solo acepta `POST` para túnel de métodos).
  - `eureka-server`.
  - `ms-books-catalogue` (buscador) con JPA + H2 en memoria.
  - `ms-books-payments` (operador) con JPA + H2 en memoria.
- No existen `Dockerfile` ni `docker-compose` actualmente.
- Todos los `pom.xml` están con `java.version=25`.

## Restricciones confirmadas por el equipo

- El `gateway` se mantiene sin cambios de compatibilidad:
  - solo recibe `POST`,
  - el método real viaja en `targetMethod`.
- Se mantiene `java.version=25` en todos los microservicios.
- Solo `ms-books-catalogue` se conecta a OpenSearch (Bonsai).
- `ms-books-payments` migra de H2 a PostgreSQL.

## Plan por fases

1. Fase 0 - Preparación, baseline y criterios de aceptación
2. Fase 1 - Migración de `ms-books-payments` a PostgreSQL (local)
3. Fase 2 - Migración de `ms-books-catalogue` a OpenSearch/Bonsai (local)
4. Fase 3 - Contenerización completa del backend (Docker local)
5. Fase 4 - Integración Frontend + Backend y pruebas end-to-end en local
6. Fase 5 - Despliegue remoto (Railway backend + Vercel frontend)
7. Fase 6 - Evidencias finales, empaquetado y videomemoria (guion 5 integrantes)

## Reglas de documentación durante la ejecución

- Cada fase tendrá su archivo en `docs/fases/`.
- En cada fase se documentará:
  - Cambios de código realizados (archivos exactos).
  - Variables de entorno agregadas/modificadas.
  - Comandos ejecutados.
  - Evidencias esperadas (capturas, URLs, logs).
  - Riesgos y decisiones técnicas.
  - Intervenciones requeridas por parte del equipo.

## Intervenciones tuyas (esperadas)

- Credenciales y alta de servicios:
  - Bonsai/OpenSearch (URL, usuario, contraseña, región).
  - Railway (proyecto, variables, dominio).
  - Vercel (proyecto, variables, dominio).
- Aprobaciones de despliegue en cuentas del equipo.
- Validación final funcional y grabación del video.

## Archivos de fase (nomenclatura actualizada)

- `docs/fases/fase-0-preparacion-baseline.md`
- `docs/fases/fase-1-postgres-ms-books-payments.md`
- `docs/fases/fase-2-migracion-opensearch-bonsai.md`
- `docs/fases/fase-3-docker-local-backend.md`
- `docs/fases/fase-4-integracion-front-back-local.md`
- `docs/fases/fase-5-despliegue-railway-vercel.md`
- `docs/fases/fase-6-videomemoria-entrega-final.md`

## Riesgos técnicos detectados desde ya

- El Gateway hoy bloquea peticiones que no sean `POST`.
- El buscador debe pasar de consultas SQL/Specification a consultas OpenSearch (full-text, suggest, facets).
- Debemos mantener consistencia de datos entre catálogo (buscador) y pagos (operador).
