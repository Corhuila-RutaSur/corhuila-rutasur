# ADR-004 — Plataforma objetivo AWS para RutaSur V2

- **Estado:** Aceptada
- **Fecha:** 2026-10-31
- **Decisores:** Equipo RutaSur

## Contexto

La V2 adopta microservicios independientes con arquitectura hexagonal y CQRS. El aumento de consultas de catálogo no debe obligar a escalar reservas o pagos; además, cada bounded context requiere persistencia, despliegue y observabilidad propios. La implementación actual sigue siendo local y educativa, pero el diseño debe ser desplegable en una plataforma empresarial coherente.

## Decisión

El objetivo de producción será AWS con los siguientes componentes:

- **Route 53, CloudFront, S3 y API Gateway** como entrada: Route 53 resuelve el dominio, CloudFront distribuye Angular desde S3 y enruta `/api` hacia API Gateway.
- **VPC Link y Application Load Balancer** entre API Gateway y las tareas privadas; el ALB distribuye cada solicitud a la tarea ECS del microservicio correspondiente.
- **ECR y ECS Fargate** para construir, ejecutar y escalar cada microservicio por separado. Se elige Fargate para evitar administrar nodos de cómputo sin introducir Kubernetes en el alcance actual.
- **Aurora PostgreSQL** con almacenamiento privado por microservicio. Los comandos van al endpoint writer; las consultas que admitan consistencia eventual usarán readers o proyecciones CQRS.
- **SNS, SQS y DLQ** para integración asíncrona: un tópico por familia de eventos, una cola por consumidor y una DLQ por cola. Los consumidores serán idempotentes.
- **Amazon S3** para archivos de imágenes, con Catálogo como dueño de metadatos, autorización y rutas públicas o firmadas.
- **Secrets Manager e IAM de mínimo privilegio** para secretos y permisos de cada tarea.
- **CloudWatch** para logs con correlación, métricas, alarmas y diagnóstico operativo.
- **Floci en Docker únicamente en local** para emular SNS/SQS/DLQ. PostgreSQL local continúa fuera de Docker por decisión del entorno de desarrollo.

## Consistencia, ACID y CQRS

Cada comando mantiene una única transacción ACID dentro del Aurora del servicio dueño. No habrá joins, claves foráneas ni transacciones SQL distribuidas entre servicios. El patrón outbox publica los hechos ya confirmados. Las proyecciones de lectura se actualizan por eventos y tienen consistencia eventual. El servicio de Reservas conserva el control transaccional de cupos para que dos solicitudes concurrentes no vendan el mismo cupo.

## Consecuencias

- Catálogo y Consultas pueden escalar sus tareas y readers sin escalar Pagos ni Reservas.
- Los eventos aíslan fallos temporales, aunque obligan a gestionar idempotencia, reintentos, DLQ y observabilidad distribuida.
- Se incrementa el número de recursos y el costo de operación, aceptado por el supuesto de infraestructura empresarial del ejercicio.
- Crear una reserva sigue generando estado `PENDIENTE`; el pago se inicia solo por comando explícito. La arquitectura AWS no cambia ese flujo funcional.

## Alternativas descartadas

- Mantener una única base de datos: contradice la autonomía y la escalabilidad independiente de los bounded contexts.
- Ejecutar todos los componentes en una sola aplicación: conserva un monolito modular, no microservicios independientes.
- Usar LocalStack como decisión de plataforma: sirve para desarrollo local, pero se seleccionó Floci para esta emulación y AWS administrado como objetivo de producción.
