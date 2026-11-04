# Documentación de la API

RutaSur mantiene una sola especificación OpenAPI y dos interfaces para consultarla:

| Interfaz | URL local | Uso |
| --- | --- | --- |
| Swagger UI | `http://localhost:8090/swagger-ui/index.html` | Inspección y pruebas clásicas de la API. |
| Scalar | `http://localhost:8090/scalar` | Exploración y pruebas con una interfaz alternativa. |
| OpenAPI JSON | `http://localhost:8090/v3/api-docs` | Contrato consumible por herramientas y clientes. |

Scalar se sirve desde el starter `springdoc-openapi-starter-webmvc-scalar`; no se debe mantener un HTML manual. Tanto Swagger como Scalar leen el mismo documento OpenAPI, por lo que no se duplican endpoints ni contratos.

Los endpoints futuros protegidos declararán el esquema `bearerAuth` de tipo JWT. Las rutas públicas permanecen sin requisito de token.
