# Guion Videomemoria Actividad 3 (5 personas)

## Reglas de ejecucion de la grabacion

- Duracion total objetivo: 14:30 a 15:00 minutos.
- Rama usada durante toda la demo: `gabriel-andrade`.
- Enfoque: modificaciones frontend y backend, despliegue local, despliegue remoto y conclusiones.
- Formato: cada integrante lee literalmente su bloque.
- Estructura de cada bloque: primero interaccion visual, despues texto literal.

---

## Cronograma

- Persona 1: 0:00 a 2:30
- Persona 2: 2:30 a 5:40
- Persona 3: 5:40 a 8:40
- Persona 4: 8:40 a 11:50
- Persona 5: 11:50 a 14:50

---

## Persona 1 (0:00 - 2:30) - Contexto tecnico y arquitectura integrada

### Interaccion visual (hacer esto primero)

1. En terminal, mostrar rama activa:
   - `git rev-parse --abbrev-ref HEAD`
2. Abrir [relatos-de-papel-backend/gateway/src/main/resources/application.yml](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/gateway/src/main/resources/application.yml) y señalar rutas a `MS-BOOKS-CATALOGUE` y `MS-BOOKS-PAYMENTS`.
3. Abrir [relatos-de-papel-backend/gateway/src/main/java/com/unir/gateway/filter/RequestTranslationFilter.java](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/gateway/src/main/java/com/unir/gateway/filter/RequestTranslationFilter.java) y mostrar que el gateway bloquea metodos distintos de POST.
4. Abrir [relatos-de-papel-backend/eureka-server/src/main/resources/application.yaml](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/eureka-server/src/main/resources/application.yaml).

### Texto literal (leer exactamente)

"En esta actividad integramos la aplicacion completa Relatos de Papel en arquitectura de microservicios con frontend React y backend Spring Cloud.

La arquitectura final tiene cinco piezas principales: frontend, Eureka Server, API Gateway, microservicio de catalogo y microservicio de pagos.

El gateway se mantiene como punto unico de entrada y conserva el contrato del proyecto: todas las peticiones del cliente llegan por POST y el metodo real se indica en `targetMethod`.

Durante esta videomemoria vamos a cubrir exactamente cuatro puntos: modificaciones frontend y backend, evidencias de despliegue local, evidencias de despliegue remoto y conclusiones finales." 

---

## Persona 2 (2:30 - 5:40) - Modificaciones backend y consideraciones de migracion a OpenSearch

### Interaccion visual (hacer esto primero)

1. Abrir [relatos-de-papel-backend/ms-books-catalogue/src/main/resources/application.yaml](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/ms-books-catalogue/src/main/resources/application.yaml) y mostrar `opensearch.url`, `opensearch.index` y `recreate-on-incompatible-mapping`.
2. Abrir [OpenSearchProperties.java](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/ms-books-catalogue/src/main/java/com/relatosdepapel/ms_books_catalogue/config/OpenSearchProperties.java).
3. Abrir [OpenSearchConfig.java](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/ms-books-catalogue/src/main/java/com/relatosdepapel/ms_books_catalogue/config/OpenSearchConfig.java).
4. Abrir [OpenSearchBookStore.java](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/ms-books-catalogue/src/main/java/com/relatosdepapel/ms_books_catalogue/service/OpenSearchBookStore.java) y mostrar:
   - `buildIndexMappingDefinition`
   - `search`
   - `suggest`
   - `facets`
   - `facetsFallbackUsingSourceScript`
   - cache de facets y `sleepBackoff`
5. Abrir [BookController.java](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/ms-books-catalogue/src/main/java/com/relatosdepapel/ms_books_catalogue/controller/BookController.java) y enseñar endpoints `/search`, `/search/suggest`, `/search/facets`.
6. Abrir [relatos-de-papel-backend/ms-books-payments/src/main/resources/application.yaml](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/ms-books-payments/src/main/resources/application.yaml) y remarcar datasource PostgreSQL.
7. Abrir [RequestTranslationFilter.java](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/gateway/src/main/java/com/unir/gateway/filter/RequestTranslationFilter.java).

### Texto literal (leer exactamente)

"En backend, el cambio mas importante fue migrar el buscador desde un modelo relacional a OpenSearch.

Las consideraciones tecnicas de la migracion fueron las siguientes.

Primero, el modelado del indice: definimos `text` para campos de busqueda libre, `keyword` para filtros y agregaciones exactas, y `search_as_you_type` para autocompletado. Esto nos permite resolver full-text, suggest y facets en una sola plataforma.

Segundo, compatibilidad de datos y de mapping: agregamos validacion de mapping incompatible y una migracion opcional de mantenimiento para reconstruir indice cuando el mapping antiguo no soporta agregaciones.

Tercero, robustez operativa: implementamos fallback de facets por script sobre `_source` para indices legacy, reintentos con backoff ante `429 Too Many Requests` y cache de facets con TTL para reducir carga repetitiva.

Cuarto, coherencia funcional: mantenemos filtros compatibles por categoria y autor, visibilidad de catalogo, y conservamos el contrato del gateway basado en POST con `targetMethod`.

En paralelo, el microservicio de pagos se migro de H2 a PostgreSQL para persistencia real en entorno local y consistente con despliegue." 

---

## Persona 3 (5:40 - 8:40) - Modificaciones frontend e integracion front-back

### Interaccion visual (hacer esto primero)

1. Abrir [relatos-de-papel-frontend/src/api/gatewayClient.js](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/api/gatewayClient.js).
2. Abrir [relatos-de-papel-frontend/src/api/booksApi.js](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/api/booksApi.js) y mostrar `getBooks`, `getBookSuggestions`, `getBookFacets`.
3. Abrir [relatos-de-papel-frontend/src/api/paymentsApi.js](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/api/paymentsApi.js).
4. Abrir [relatos-de-papel-frontend/src/pages/HomePage.jsx](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/pages/HomePage.jsx).
5. Abrir [relatos-de-papel-frontend/src/components/layout/Header.jsx](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/components/layout/Header.jsx).
6. Abrir [relatos-de-papel-frontend/src/pages/BookDetailPage.jsx](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/pages/BookDetailPage.jsx), [BookCard.jsx](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/features/books/BookCard.jsx) y [ScrollToTop.jsx](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/components/layout/ScrollToTop.jsx).
7. Abrir [relatos-de-papel-frontend/src/features/checkout/CheckoutSummary.jsx](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/features/checkout/CheckoutSummary.jsx), [OrderConfirmationPage.jsx](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/pages/OrderConfirmationPage.jsx) y [useCountdown.js](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/src/hooks/useCountdown.js).

### Texto literal (leer exactamente)

"En frontend eliminamos la dependencia de mocks y conectamos todo el flujo a backend real.

Para lograrlo, construimos una capa API que respeta el contrato del gateway y encapsula todas las llamadas con POST mas `targetMethod`.

En catalogo incorporamos busqueda real, sugerencias en tiempo real y facets de categoria y autor. Tambien persistimos filtros en query params para mantener contexto de navegacion.

En experiencia de usuario, mejoramos limpieza de busqueda, restauracion de scroll entre catalogo y detalle, placeholder de portada por ISBN, y confirmacion de compra con contador de redireccion.

Con estos cambios el flujo funcional queda integrado de extremo a extremo: home, detalle, carrito, checkout, pago y confirmacion usando datos reales de los microservicios." 

---

## Persona 4 (8:40 - 11:50) - Despliegue local con evidencias de ejecucion correcta

### Interaccion visual (hacer esto primero)

1. Abrir Dockerfiles de backend:
   - [eureka-server/Dockerfile](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/eureka-server/Dockerfile)
   - [gateway/Dockerfile](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/gateway/Dockerfile)
   - [ms-books-catalogue/Dockerfile](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/ms-books-catalogue/Dockerfile)
   - [ms-books-payments/Dockerfile](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/ms-books-payments/Dockerfile)
2. Abrir [docker-compose.backend-local.yml](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/docker-compose.backend-local.yml).
3. Abrir [relatos-de-papel-backend/.env.example](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-backend/.env.example).
4. En terminal ejecutar y mostrar:
   - `cd relatos-de-papel-backend`
   - `docker compose -f docker-compose.backend-local.yml up --build -d`
   - `docker compose -f docker-compose.backend-local.yml ps`
5. Abrir navegador en `http://localhost:8761` y mostrar registro de `MS-BOOKS-CATALOGUE`, `MS-BOOKS-PAYMENTS` y `gateway`.
6. En terminal mostrar evidencia de contrato gateway:
   - `curl -i http://localhost:8762/api/books`
   - resultado esperado: `405 Method Not Allowed`
7. En terminal mostrar evidencias funcionales locales:
   - `curl -i -X POST 'http://localhost:8762/api/books/search' -H 'Content-Type: application/json' -d '{"targetMethod":"GET","queryParams":{"title":["quijote"],"visible":["true"]},"body":null}'`
   - `curl -i -X POST 'http://localhost:8762/api/books/search/suggest' -H 'Content-Type: application/json' -d '{"targetMethod":"GET","queryParams":{"text":["qui"],"size":["8"]},"body":null}'`
   - `curl -i -X POST 'http://localhost:8762/api/books/search/facets' -H 'Content-Type: application/json' -d '{"targetMethod":"GET","queryParams":{"visible":["true"]},"body":null}'`
8. Abrir [relatos-de-papel-frontend/.env.example](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/.env.example).
9. En terminal ejecutar y mostrar:
   - `cd ../relatos-de-papel-frontend`
   - `npm install`
   - `npm run dev`
10. En navegador, mostrar evidencia end-to-end local:
   - buscar libro
   - aplicar filtro categoria o autor
   - abrir detalle
   - anadir al carrito
   - confirmar compra
   - ver referencia en confirmacion

### Texto literal (leer exactamente)

"En el despliegue local mostramos primero la contenerizacion del backend con un Dockerfile por servicio.

Despues levantamos el stack completo con Docker Compose y verificamos servicios activos y registrados en Eureka.

La primera evidencia de ejecucion correcta es el comportamiento del gateway: rechaza GET directo con 405 y acepta el contrato oficial por POST con `targetMethod`.

La segunda evidencia es funcional: consultas de busqueda, sugerencias y facets devolviendo respuestas correctas en local.

La tercera evidencia es de integracion completa: el frontend en local consume el gateway local y ejecuta el flujo de compra de extremo a extremo." 

---

## Persona 5 (11:50 - 14:50) - Despliegue remoto con evidencias y conclusiones

### Interaccion visual (hacer esto primero)

1. Abrir [relatos-de-papel-frontend/vercel.json](/Users/gabriel/Documents/Master/Desarrollo-Fullstack/Actividad-3/relatos-de-papel-frontend/vercel.json).
2. En terminal, ejecutar evidencia remota de backend:
   - `curl -i -X POST 'https://spring-cloud-gateway-production-7111.up.railway.app/api/books/search' -H 'Content-Type: application/json' -d '{"targetMethod":"GET","queryParams":{"title":["quijote"],"visible":["true"]},"body":null}'`
3. En navegador abrir `https://relatos-de-papel-fullstack.vercel.app/`.
4. Mostrar evidencia remota en frontend:
   - busqueda por titulo
   - sugerencias visibles
   - apertura de detalle
   - navegacion de regreso al catalogo
5. En terminal, mostrar trazabilidad tecnica de cambios en la rama:
   - `git diff --name-status 8703163..gabriel-andrade`
   - desplazar salida para evidenciar archivos de frontend, gateway, catalogo, pagos y docker.

### Texto literal (leer exactamente)

"En remoto desplegamos backend en Railway y frontend en Vercel, con conectividad publica entre ambos.

La evidencia tecnica de backend remoto es una consulta real al gateway publicado, respondiendo correctamente sobre el catalogo.

La evidencia funcional de frontend remoto es la navegacion completa en la aplicacion publicada, consumiendo servicios reales.

Tambien mostramos trazabilidad de cambios en la rama de trabajo para demostrar la evolucion del proyecto hasta su estado actual.

Como conclusion, el proyecto cumple los objetivos de la actividad: integracion front-back, migracion efectiva a OpenSearch con consideraciones de modelado y resiliencia, ejecucion validada en local y operacion validada en remoto." 

---

## Cierre final de 15 segundos

"Con esto cerramos la videomemoria mostrando codigo, arquitectura y evidencias reales de ejecucion en local y remoto. Gracias." 
