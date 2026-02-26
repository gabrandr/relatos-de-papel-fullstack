# Plan Maestro - Actividad 3 (Contexto general del proyecto)

## 1) Propósito de este archivo

Este archivo es el punto de entrada para cualquier IA o miembro del equipo que retome el proyecto en un chat nuevo.

Flujo obligatorio de lectura:

1. Leer este archivo completo (`plan-desarrollo-actividad3.md`).
2. Leer después todos los archivos de `docs/fases/` para conocer el estado actual real.
3. Continuar el trabajo actualizando esos mismos `.md` de fase conforme se hagan cambios.

## 2) Estado inicial histórico (cómo empezó)

Al inicio de esta actividad:

- Frontend (`relatos-de-papel-frontend`) consumía `mocks` (`books.mock`) para catálogo/detalle.
- Backend (`relatos-de-papel-backend`) tenía arquitectura de microservicios:
  - `gateway`
  - `eureka-server`
  - `ms-books-catalogue`
  - `ms-books-payments`
- `ms-books-catalogue` trabajaba con modelo relacional local (JPA + H2).
- `ms-books-payments` trabajaba con H2.
- No había contenerización completa para ejecución integrada local.

## 3) Restricciones de diseño confirmadas (NO romper)

- Gateway debe mantener patrón de tunneling original:
  - Solo procesa operaciones de negocio por `POST`.
  - Método real dentro de `targetMethod`.
- Se permite `OPTIONS` solo para preflight CORS del navegador (no negocio).
- Mantener `Java 25` en backend.
- Solo `ms-books-catalogue` usa OpenSearch/Bonsai.
- `ms-books-payments` debe usar PostgreSQL (no H2).

## 4) Objetivo final (a dónde debe llegar)

- Integración completa frontend + backend.
- Buscador usando OpenSearch (full-text, suggest/search-as-you-type, facets).
- Operador de pagos usando PostgreSQL.
- Backend dockerizado y ejecutable en local por contenedores.
- Despliegue remoto público:
  - Backend en Railway.
  - Frontend en Vercel.
- Evidencias para videomemoria y entrega final con máxima puntuación.

## 5) Rúbrica de calificación (objetivo: 10/10)

- Criterio 1 (15%): modificación de microservicio buscador para usar Elasticsearch/OpenSearch.
- Criterio 2 (15%): uso para sugerencias/correcciones/full-text.
- Criterio 3 (15%): uso para facets.
- Criterio 4 (25%): integración frontend + backend en local.
- Criterio 5 (20%): integración frontend + backend en remoto.
- Criterio 6 (10%): videomemoria obligatoria, duración adecuada y participación de todos.

## 6) Plan por fases del proyecto

1. Fase 0 - Preparación y baseline.
2. Fase 1 - Migración `ms-books-payments` a PostgreSQL (local).
3. Fase 2 - Migración `ms-books-catalogue` a OpenSearch/Bonsai (local).
4. Fase 3 - Contenerización completa backend (local).
5. Fase 4 - Integración frontend + backend y pruebas e2e (local).
6. Fase 5 - Despliegue remoto (Railway + Vercel).
7. Fase 6 - Videomemoria y entrega final.

## 7) Archivos de fase (estado operativo)

- `docs/fases/fase-0-preparacion-baseline.md`
- `docs/fases/fase-1-postgres-ms-books-payments.md`
- `docs/fases/fase-2-migracion-opensearch-bonsai.md`
- `docs/fases/fase-3-docker-local-backend.md`
- `docs/fases/fase-4-integracion-front-back-local.md`
- `docs/fases/fase-5-despliegue-railway-vercel.md`
- `docs/fases/fase-6-videomemoria-entrega-final.md`

## 8) Regla de continuidad entre chats (obligatoria)

Cuando una IA retome el proyecto:

- Debe asumir este archivo como contrato de contexto.
- Debe actualizar los `.md` de fase cada vez que cambie código/configuración.
- No debe romper restricciones de gateway ni cambiar Java 25.
- Debe priorizar acciones que suban puntuación de rúbrica.

## 9) Entregable pedagógico obligatorio antes del guion final

Antes de generar el guion final de videomemoria, la IA debe generar un archivo `.md` adicional de aprendizaje técnico para el equipo, con:

- Qué se cambió en frontend y backend.
- Qué archivos se añadieron/modificaron/eliminaron.
- Por qué se hizo cada cambio.
- Impacto funcional de cada cambio.
- Qué validar para comprobar que está correcto.

Nombre sugerido del archivo:

- `docs/resumen-cambios-tecnicos.md`

Este documento debe ayudar a que el equipo entienda el proyecto y no dependa ciegamente de la IA.

## 10) Cierre de proyecto

Al terminar todos los criterios de rúbrica:

1. Generar `docs/resumen-cambios-tecnicos.md`.
2. Generar guion final de videomemoria dividido para 5 integrantes.
3. Verificar checklist de entrega final (código + evidencias + video).

## 11) Estado remoto actual (2026-02-26)

- C5 (integración remota) completado.
- Backend público (Railway Gateway):
  - `https://spring-cloud-gateway-production-7111.up.railway.app/`
- Frontend público (Vercel):
  - `https://relatos-de-papel-fullstack.vercel.app/`
