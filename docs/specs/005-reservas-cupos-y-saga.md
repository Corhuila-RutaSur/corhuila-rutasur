# 005 — Reservas, cupos y Saga

**Estado:** Borrador  
**Servicio dueño:** Reservas  
**Origen V1:** Specs 004 y 009; diagramas V2 de reserva.

## Alcance

- Crear, consultar y cancelar reservas de una única salida.
- Registrar uno o más participantes y un único conjunto de servicios para todos ellos.
- Bloquear/disminuir y liberar cupos con consistencia ACID local.
- Coordinar pago y notificaciones por eventos, sin transacción distribuida.

## Reglas de negocio

1. Todos los participantes de una reserva comparten exactamente los mismos servicios de la salida.
2. Los servicios solicitados deben existir y estar habilitados en la misma salida.
3. La cantidad de participantes nunca supera los cupos disponibles.
4. Ante concurrencia por el último cupo, gana la primera transacción que obtiene el bloqueo y confirma la invariante; las demás se rechazan sin reserva parcial.
5. Cancelar una reserva vigente libera los cupos en una transacción local y publica `ReservaCancelada`.

La cancelación llega por `DELETE /api/v2/reservas/{reservaId}`. Requiere `X-Usuario-Id`; el caso de uso comprueba que sea el dueño y solo permite los estados `PENDIENTE` o `CONFIRMADA`.
6. Crear una reserva no inicia pago: queda pendiente hasta que el cliente ejecuta explícitamente la acción de pago.
7. El pago inicia una Saga solo después de la acción explícita del cliente. Ante rechazo o expiración se conserva o cancela la reserva según la regla funcional aprobada; cualquier liberación de cupos se realiza localmente en Reservas.

## Eventos

| Dirección | Evento | Resultado |
|---|---|---|
| Publica | `ReservaCreada` | Consultas y Notificaciones reflejan la reserva pendiente; Pagos no actúa. |
| Consume | `PagoAprobado` | Reservas verifica la misma reserva, el mismo monto snapshot y confirma la reserva de forma idempotente. |
| Consume | `PagoRechazado` o `PagoExpirado` | Valida el monto, cancela solo una reserva pendiente y devuelve sus cupos mediante actualización ACID condicional. |
| Publica | `ReservaConfirmada`, `ReservaCancelada` | Consultas, Operación y Notificaciones reaccionan. |

## Criterios de aceptación

```gherkin
Escenario: Dos compradores compiten por el último cupo
Dado que una salida tiene un cupo disponible
Cuando dos comandos de reserva se procesan concurrentemente
Entonces solo una reserva queda vigente
Y la otra recibe disponibilidad insuficiente
Y nunca quedan cupos negativos.

Escenario: Pago rechazado posterior a una solicitud explícita
Dado que el cliente inició el pago de una reserva pendiente
Cuando Pagos publica PagoRechazado para su correlación
Entonces Reservas cancela la reserva de forma idempotente
Y devuelve los cupos disponibles.
```

## Pruebas requeridas

- Concurrencia real contra PostgreSQL.
- Outbox, Inbox, duplicados y compensación Saga.
- ArchUnit: dominio de Reservas sin dependencia de Pagos/Catálogo.

## Implementación inicial V2

`reservas-service` implementa el consumidor SQS `reservas-pago`. Al recibir el envelope `PagoAprobado`, registra el `id` en `inbox_evento`, valida que el monto coincida exactamente con el total de la reserva y realiza `PENDIENTE -> CONFIRMADA` en la misma transacción. Allí mismo agrega `ReservaConfirmada` a `outbox_evento`. Un publicador programado entrega ese hecho al tópico SNS `reservas.reserva`; solo tras la aceptación de SNS marca el Outbox como publicado. Si SQS reentrega el mensaje, Inbox evita una segunda transición.

El mismo consumidor procesa `PagoRechazado` y `PagoExpirado`. Para una reserva `PENDIENTE`, actualiza su estado a `CANCELADA`, ejecuta la devolución ACID de cupos sin exceder la capacidad total y registra `ReservaCancelada` en el Outbox. Si la reserva ya estaba cancelada o confirmada, el evento queda registrado en Inbox sin repetir ni revertir una transición previa.
# Consulta de Mis reservas

Reservas expone `GET /api/v2/reservas/usuarios/{usuarioId}` para reconstruir la vista personal de reservas desde su propia base de datos. El resultado incluye código, estado, total, participantes, fecha de creación y un snapshot local de la salida (`salidaId`, experiencia, fecha programada). No consulta otros microservicios.

Durante la transición local el identificador se recibe por ruta. Cuando el borde distribuido de seguridad esté activado, el Gateway derivará el usuario del JWT y evitará que un cliente consulte reservas de otro usuario.

