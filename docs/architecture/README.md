# Arquitectura V2

La V2 adopta microservicios por dominio de negocio. Cada servicio implementa arquitectura hexagonal y usa CQRS únicamente cuando la lectura requiere un modelo diferente del comando.

## Principios obligatorios

1. Cada servicio es dueño exclusivo de su base de datos, migraciones y modelo de escritura.
2. Ningún servicio consulta tablas, crea llaves foráneas ni abre transacciones sobre la base de otro servicio.
3. Los comandos que comprometen cupos se resuelven en Reservas con consistencia local ACID; las proyecciones no autorizan reservas.
4. La integración entre dominios ocurre por eventos versionados, publicados de forma confiable mediante outbox y consumidos de forma idempotente mediante inbox.
5. El dominio no depende de Spring, JPA, HTTP, SNS, SQS ni Floci. Esas tecnologías se implementan como adaptadores.
6. Las consultas que requieren escala o composición se sirven desde Catálogo o desde proyecciones de Consultas; se acepta consistencia eventual donde la regla de negocio lo permita.
7. La plataforma objetivo es AWS: CloudFront/API Gateway, ECS Fargate, Aurora PostgreSQL con writer/readers, SNS/SQS/DLQ, S3, Secrets Manager e IAM y CloudWatch. Floci y PostgreSQL local son exclusivamente el equivalente de desarrollo.

## Servicios objetivo

| Servicio | Responsabilidad principal | Tipo de carga |
| --- | --- | --- |
| Identidad | Usuarios, roles, sesiones y tokens | Seguridad y administración. |
| Catálogo | Experiencias, imágenes y servicios ofrecidos | Lectura pública intensiva. |
| Reservas | Salidas, cupos, bloqueos y reservas | Escritura consistente. |
| Pagos | Intentos y resultado de pago | Integración transaccional. |
| Operación | Guías, vehículos y ejecución de salidas | Operación interna. |
| Notificaciones | Entregas, reintentos y plantillas | Procesamiento asíncrono. |
| Consultas | Proyecciones de lectura para pantallas | Lectura optimizada. |

## Documentos relacionados

- [ADR-002: migración a microservicios](../adr/002-migracion-a-microservicios-v2.md)
- [ADR-004: plataforma objetivo AWS](../adr/004-plataforma-aws-objetivo.md)
- [C4 de contenedores V2](c4-contenedores-v2.md)
- [Modelo de datos V2](modelo-datos-v2.md)
- [Flujo de reserva](flujo-creacion-reserva-v2.md)
- [Secuencia de reserva](secuencia-creacion-reserva-v2.md)
- [Clases de Reservas](clases-reserva-v2.md)
- [Catálogo de eventos](../events/README.md)
