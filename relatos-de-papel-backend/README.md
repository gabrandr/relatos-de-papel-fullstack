# 📚 Relatos de Papel - Backend Microservicios

> **Proyecto Fullstack - Actividad 2**
> Sistema distribuido de gestión de librería y pagos, implementado con Spring Boot y Spring Cloud.

---

## 📖 Descripción del Proyecto

Este backend implementa la lógica de negocio para "Relatos de Papel", una librería online. La arquitectura está basada en **Microservicios** para desacoplar la gestión de catálogo y la gestión de compras, garantizando escalabilidad y mantenimiento independiente.

### Componentes Principales:

1.  **ms-books-catalogue (Buscador):** Gestiona el inventario de libros (CRUD, búsquedas avanzadas).
2.  **ms-books-payments (Operador):** Gestiona las transacciones de compra y control de stock.
3.  **Eureka Server:** Servidor de descubrimiento de servicios.
4.  **API Gateway:** Puerta de enlace única para todas las peticiones externas.

---

## 🏗️ Arquitectura del Sistema

Esta sección explica "cómo se conecta todo" para entender el flujo de datos.

### 1. Eureka Server (El Directorio)

Actúa como un "listín telefónico". Cuando los microservicios (`catalogue` y `payments`) inician, se **registran automáticamente** en Eureka con un nombre lógico (ej. `MS-BOOKS-CATALOGUE`).

- **¿Por qué?** Permite que los servicios se comuniquen entre sí sin conocer sus direcciones IP/puertos, los cuales pueden cambiar dinámicamente.

### 2. API Gateway (El Portero y Traductor)

Es el **único punto de entrada** para cualquier cliente (Postman, Frontend, Usuarios). Se ejecuta en el puerto `8762`.

- **Función de Tunneling (Request Translation):**
  Para cumplir con requisitos de seguridad y compatibilidad, el Gateway intercepta todas las peticiones **POST** y, basándose en la estructura del cuerpo, las transforma en la petición HTTP real que el microservicio necesita (GET, PUT, PATCH, DELETE).
- **Importante:** El Gateway **solo acepta POST**. El método real se envía en el body mediante `GatewayRequest.targetMethod`.

### 3. Comunicación Inter-Servicios

El microservicio de Pagos (`payments`) necesita consultar al Catálogo (`catalogue`) para validar stock antes de una compra.

- **Flujo:** `Payments` -> consulta a Eureka "¿Dónde está `MS-BOOKS-CATALOGUE`?" -> Eureka devuelve IP:Puerto -> `Payments` hace la petición HTTP.
- **Hito Técnico:** Esta comunicación es transparente, sin usar IPs fijas, gracias a la anotación `@LoadBalanced` en el `RestTemplate`.

---

## 🚀 API Reference (Endpoints)

A continuación se detallan las operaciones disponibles en cada microservicio, siguiendo estrictamente el estándar de documentación del curso.

### 📘 Microservicio Buscador (ms-books-catalogue)

**Base URL:** `/api/books`

| Método HTTP | URI                            | Query Params                                                                                                                       | Request Body   | Response Body           | Códigos       |
| ----------- | ------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------- | -------------- | ----------------------- | ------------- |
| POST        | `/api/books`                   | N/A                                                                                                                                | BookRequestDTO | BookResponseDTO         | 201, 400, 409 |
| GET         | `/api/books`                   | N/A                                                                                                                                | N/A            | List<BookResponseDTO>   | 200           |
| GET         | `/api/books/search`            | title, author, category, isbn, ratingMin, ratingMax, visible, minPrice, maxPrice, minStock, publicationDateFrom, publicationDateTo | N/A            | List<BookResponseDTO>   | 200           |
| GET         | `/api/books/{id}`              | N/A                                                                                                                                | N/A            | BookResponseDTO         | 200, 404      |
| PUT         | `/api/books/{id}`              | N/A                                                                                                                                | BookRequestDTO | BookResponseDTO         | 200, 400, 404 |
| PATCH       | `/api/books/{id}`              | N/A                                                                                                                                | BookPatchDTO   | BookResponseDTO         | 200, 400, 404 |
| DELETE      | `/api/books/{id}`              | N/A                                                                                                                                | N/A            | Void                    | 204, 404      |
| GET         | `/api/books/{id}/availability` | N/A                                                                                                                                | N/A            | AvailabilityResponseDTO | 200, 404      |
| PATCH       | `/api/books/{id}/stock`        | N/A                                                                                                                                | StockUpdateDTO | BookResponseDTO         | 200, 400, 404 |

> **Nota:** `GET /api/books` devuelve **solo libros visibles** (`visible=true`).

---

### 💳 Microservicio Operador (ms-books-payments)

**Base URL:** `/api/payments`

| Método HTTP | URI                    | Query Params           | Request Body      | Response Body      | Códigos            |
| ----------- | ---------------------- | ---------------------- | ----------------- | ------------------ | ------------------ |
| POST        | `/api/payments`        | N/A                    | PaymentRequestDTO | PaymentResponseDTO | 201, 400, 404, 500 |
| GET         | `/api/payments/{id}`   | N/A                    | N/A               | PaymentResponseDTO | 200, 404           |
| GET         | `/api/payments`        | N/A                    | N/A               | List<Payment...>   | 200                |
| GET         | `/api/payments/search` | userId, bookId, status | N/A               | List<Payment...>   | 200                |
| PATCH       | `/api/payments/{id}`   | N/A                    | PaymentStatusDTO  | PaymentResponseDTO | 200, 400, 404      |
| DELETE      | `/api/payments/{id}`   | N/A                    | N/A               | Void               | 204, 404, 409, 500 |

> **Nota:** un pago creado exitosamente se persiste con estado `COMPLETED`.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java 21 / 25
- **Framework:** Spring Boot 3.4.1
- **Cloud:** Spring Cloud (Gateway, Eureka)
- **Base de Datos:** H2 Database (En memoria)
- **Herramientas:** Maven, Lombok, Postman

---

## ⚙️ Guía de Ejecución (Paso a Paso)

Para levantar el ecosistema correctamente, sigue este orden estricto:

1.  **Levantar Eureka Server:**
    - Ejecutar `EurekaServerApplication`.
    - Verificar en: `http://localhost:8761`

2.  **Levantar API Gateway:**
    - Ejecutar `GatewayAndFiltersApplication`.
    - Puerto: `8762`

3.  **Levantar Microservicios:**
    - Ejecutar `MsBooksCatalogueApplication`.
    - Ejecutar `MsBooksPaymentsApplication`.
    - **Verificar:** Refresca `http://localhost:8761`, deberías ver `MS-BOOKS-CATALOGUE` y `MS-BOOKS-PAYMENTS` registrados.

---

## 🧪 Cómo realizar Pruebas

Se incluye una **Colección de Postman** y datos de prueba (`data.sql`) cargados automáticamente al inicio.

### Pasos para probar:

1.  **Importar JSON:** Tienes dos colecciones en la raíz del proyecto para importar en Postman:
    - `MS-Books-Catalogue-Postman.json`
    - `MS-Books-Payments-Postman.json`

2.  **Crear un Libro (Vía Gateway - Tunneling):**

    > ⚠️ **IMPORTANTE:** Al usar el Gateway para **POST**, el cuerpo debe seguir la estructura `GatewayRequest` (Tunneling) para que el Gateway sepa qué método HTTP real ejecutar (POST, GET, PATCH, etc.) en el microservicio destino.
    - Llama al endpoint `POST http://localhost:8762/api/books`.
    - **Body (Estructura GatewayRequest):**
      ```json
      {
        "targetMethod": "POST",
        "queryParams": {},
        "body": {
          "title": "Libro de Prueba",
          "author": "Tester",
          "isbn": "111-222",
          "stock": 10,
          "price": 25.5,
          "visible": true
        }
      }
      ```
      _El Gateway extraerá el objeto "body" y lo enviará como un POST normal al microservicio._

3.  **Realizar una Compra (Flow Completo):**
    - Llama al endpoint `POST http://localhost:8762/api/payments`.
    - Body:
      ```json
      {
        "targetMethod": "POST",
        "queryParams": {},
        "body": {
          "userId": 1,
          "bookId": 1,
          "quantity": 2
        }
      }
      ```
    - **Resultado esperado:** `201 Created` y estado `COMPLETED`. El stock del libro (ID 1) debería bajar automáticamente de 10 a 8.

4.  **Verificar Búsqueda Avanzada:**
    - Prueba `POST http://localhost:8762/api/books/search` usando tunneling:
      ```json
      {
        "targetMethod": "GET",
        "queryParams": {
          "minPrice": ["20"],
          "visible": ["true"]
        },
        "body": null
      }
      ```

---

> **Nota para Evaluadores/Desarrolladores:**
> Este proyecto cumple con los criterios de **Gateway Tunneling** (interceptación de POST) y **Service Discovery** (Eureka sin IPs fijas).
