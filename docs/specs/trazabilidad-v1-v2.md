# Trazabilidad de migración V1 → V2

## Propósito

Este documento impide que la migración arquitectónica pierda comportamiento ya aprobado. V1 es la evidencia de origen; V2 define el destino operativo. Toda historia de migración debe citar su fila antes de modificar código, datos, API o eventos.

| Fuente histórica V1 | Especificación V2 destino | Dueño V2 | Tratamiento |
|---|---|---|---|
| 001 · Glosario del dominio | 001 · Lenguaje ubicuo y límites | Todos | Se conserva el vocabulario y se agregan límites de contexto. |
| 002 · Gestión de experiencias | 003 · Catálogo de experiencias | `catalogo-service` | Se conservan CRUD, imágenes y servicios; el catálogo publica cambios. |
| 003 · Gestión de salidas | 004 · Salidas y disponibilidad | `reservas-service` | Se conserva la planeación; capacidad y cupos pasan al dueño de reservas. |
| 004 · Reservas | 005 · Reservas, cupos y Saga | `reservas-service` | Se conservan participantes, detalles y cancelación; la reserva queda protegida por ACID y bloqueo local. |
| 005 · Pagos | 006 · Pagos, reembolsos y Saga | `pagos-service` | Se conserva pago simulado inicial y se desacopla del agregado Reserva mediante evento. |
| 006 · Operación y asistencia | 007 · Operación y asistencia | `operacion-service` | Se conservan ejecución, guía, conductor, vehículo y asistencia. |
| 007 · Calificaciones | 008 · Calificaciones | Reservas / Consultas | Se conserva una calificación por reserva; catálogo recibe agregado eventual. |
| 008 y 011 · Autorización, roles, sesiones | 002 · Identidad, autorización y sesiones | `identidad-service` | Se conservan BCrypt, JWT, refresh de un uso, revocación y roles. |
| 009 · Reglas de negocio | 005 y 010 | Según regla | Se preservan invariantes y se corrige explícitamente el paquete común. |
| 010 · Estados del dominio | 010 · Estados e invariantes | Según agregado | Se preservan transiciones válidas y responsables. |
| 012 · Gobierno documental | 011 · Gobierno y trazabilidad | Todos | Se amplía para exigir paridad repositorio/Wiki y ADR. |
| 013 · Gestión de usuarios | 002 · Identidad, autorización y sesiones | `identidad-service` | Se conserva registro público como CLIENTE y administración autorizada. |

## Ajuste explícito de paquete

Una reserva puede incluir varios participantes, pero **todos los participantes de una misma reserva reciben el mismo conjunto de servicios**. Los servicios seleccionados pertenecen a una sola salida. Si se requieren combinaciones distintas, se crean reservas separadas.

## Criterio de cierre

Una migración no termina hasta documentar: origen V1, servicio dueño, comando/consulta, datos propios, eventos, estrategia ACID o Saga y pruebas que demuestran la regla preservada.
