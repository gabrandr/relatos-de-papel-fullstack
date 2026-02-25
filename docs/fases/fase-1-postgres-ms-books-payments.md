# Fase 1 - Migración de ms-books-payments a PostgreSQL (local)

## Objetivo

Migrar el microservicio `ms-books-payments` desde H2 a PostgreSQL y dejarlo operativo en local.

## Alcance técnico

- Reemplazar dependencia H2 por driver PostgreSQL.
- Ajustar `application.yaml` de `ms-books-payments` para configuración por variables de entorno.
- Mantener scripts de inicialización (`data.sql`) para pruebas locales.
- Conectar el microservicio a un contenedor PostgreSQL local.

## Cambios de código previstos

- `ms-books-payments/pom.xml` (dependencias).
- `ms-books-payments/src/main/resources/application.yaml`.
- Configuración Docker para PostgreSQL local.

## Criterios de aceptación

- `ms-books-payments` inicia sin H2.
- Conexión exitosa contra PostgreSQL en local.
- Endpoints de pagos responden correctamente.

## Intervención requerida del equipo

- Confirmar credenciales iniciales de PostgreSQL para local.
- Validar si se usará persistencia de volumen o reinicio limpio por ejecución.

## Evidencias a capturar

- Logs de conexión JDBC a PostgreSQL.
- Prueba `POST /api/payments` exitosa.
- Prueba de lectura de pagos persistidos.

## Registro de cambios ejecutados

- [2026-02-24] Fase redefinida según orden oficial local -> remoto.
- [2026-02-24] `ms-books-payments/pom.xml`: se eliminó H2 y se agregó `org.postgresql:postgresql` como dependencia runtime.
- [2026-02-24] `ms-books-payments/src/main/resources/application.yaml`: configuración migrada de H2 a PostgreSQL usando variables `POSTGRES_*`, `JPA_*`, `SQL_INIT_MODE`, `EUREKA_URL`.
- [2026-02-24] `ms-books-payments/src/main/resources/application.yaml`: se añadieron flags `EUREKA_ENABLED`, `EUREKA_REGISTER`, `EUREKA_FETCH` para pruebas locales aisladas.
- [2026-02-24] `ms-books-payments/Dockerfile`: creado multi-stage build con Maven + Eclipse Temurin 25.
- [2026-02-24] `docker-compose.payments-postgres.yml`: creado entorno local mínimo `postgres + ms-books-payments`.
- [2026-02-24] Validación compose: `docker compose -f docker-compose.payments-postgres.yml config` ejecuta correctamente.
- [2026-02-24] Validación de compilación: el wrapper `mvnw` no es usable en este repo (falta `.mvn/`), y la compilación con Maven requiere red a Maven Central.
- [2026-02-25] Persistencia ajustada para evitar reinicio de dataset en cada arranque: `spring.jpa.hibernate.ddl-auto` pasó de `create-drop` a `update` y `spring.sql.init.mode` de `always` a `never` en `ms-books-payments/src/main/resources/application.yaml`.
- [2026-02-25] `docker-compose.backend-local.yml`: variables de `ms-books-payments` actualizadas a `JPA_DDL_AUTO=update` y `SQL_INIT_MODE=never`.
- [2026-02-25] Validación operativa tras reinicio de `ms-books-payments`: creación de pago de prueba y comprobación de permanencia posterior al restart del servicio (sin recarga automática de `data.sql`).
