# Documentación de la API

RutaSur genera un único contrato OpenAPI y lo muestra en dos interfaces:

| Interfaz | Dirección local |
| --- | --- |
| Swagger UI | `http://localhost:8090/swagger-ui/index.html` |
| Scalar | `http://localhost:8090/scalar` |
| OpenAPI JSON | `http://localhost:8090/v3/api-docs` |

Swagger UI y Scalar pueden coexistir porque ambos leen el mismo contrato. Scalar se habilita mediante el starter oficial de Springdoc; no se debe crear ni mantener un HTML manual.
