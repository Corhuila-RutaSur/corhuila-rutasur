# Documentación RutaSur

La documentación se organiza por versión para conservar evidencia ejecutable de la V1 y presentar la evolución arquitectónica de la V2.

| Versión | Estado | Contenido |
| --- | --- | --- |
| [V1](v1/README.md) | Histórica, etiquetada como `v1` | Arquitectura por capas, especificaciones, estándares, modelo relacional y ADR inicial. |
| V2 | En implementación local | Migración a microservicios, hexagonal interna, CQRS y eventos. |

## Documentación vigente de V2

La V2 se encuentra en definición arquitectónica. Antes de implementar un microservicio o una capacidad, su `spec` debe estar aprobada y enlazada desde este índice.

| Documento | Propósito |
| --- | --- |
| [ADR-002: Migración a microservicios](adr/002-migracion-a-microservicios-v2.md) | Decisión, alcance, costos y estrategia de migración. |
| [Ejecución local V2](operations/ejecucion-local-v2.md) | Prerrequisitos, PostgreSQL, Floci, colas, variables, orden de arranque y resolución de problemas. |
| [Carga de datos de prueba V2](operations/carga-datos-prueba-v2.md) | Script y secuencia para crear catálogo, salida, reserva y pago coherentes. |
| [Modelo visual de datos V2](architecture/modelo-datos-v2.md) | Propiedad de datos, bases independientes y proyecciones CQRS. |
| [Modelo DBML V2](database/modelo-v2.dbml) | Modelo lógico detallado para dbdiagram.io. |
| [Flujo de creación de reserva](architecture/flujo-creacion-reserva-v2.md) | Decisiones y estados del proceso distribuido. |
| [Secuencia de creación de reserva](architecture/secuencia-creacion-reserva-v2.md) | Interacción entre frontend, microservicios, eventos y notificaciones. |
| [Clases del dominio de reservas](architecture/clases-reserva-v2.md) | Casos de uso, puertos, entidades y adaptadores del servicio de Reservas. |
| [Arquitectura V2](architecture/README.md) | Límites, principios y diagramas de la solución objetivo. |
| [C4 de contenedores V2](architecture/c4-contenedores-v2.md) | Vista estructural de clientes, Gateway, microservicios, bases y eventos. |
| [Persistencia y migraciones](database/README.md) | Propiedad de datos y reglas Flyway por microservicio. |
| [Contratos HTTP](api/README.md) | Convenciones para API Gateway, endpoints y respuestas. |
| [Catálogo de eventos](events/README.md) | Eventos, envelope, idempotencia y consumidores. |
| [Especificaciones V2](specs/README.md) | Fuente funcional, criterios de aceptación y plantillas para el desarrollo. |
| [Trazabilidad V1 → V2](specs/trazabilidad-v1-v2.md) | Mapea cada regla ya implementada hacia su dueño, spec y tratamiento V2. |
| [Estándares V2](standards/README.md) | Definición de terminado, trazabilidad y reglas transversales. |
| [Guía para agentes y desarrollo](../AGENTS.md) | Reglas obligatorias para cambios, límites de microservicios y sincronía documental. |

## Orden de consulta y actualización

1. Leer el `README` raíz y este índice para entender el estado del producto.
2. Consultar el ADR y el documento de arquitectura antes de cambiar límites entre servicios.
3. Crear o actualizar una especificación antes de implementar una capacidad V2.
4. Mantener alineados el spec, contrato HTTP o de evento, modelo de datos, pruebas y documentación de ejecución en el mismo cambio.
5. Publicar en la Wiki una copia de consulta de los documentos estables; `docs/` es la fuente versionada.
6. Antes de trasladar código V1, consultar la trazabilidad y completar el spec del servicio dueño.
