# Catálogo de eventos V2

## Propósito

Los eventos comunican hechos de negocio entre microservicios sin acceso compartido a sus bases de datos. En desarrollo se usarán SNS/SQS mediante Floci: un tópico distribuye cada hecho a una cola por consumidor. Floci es el emulador AWS oficial del proyecto local; LocalStack no hace parte de la infraestructura configurada.

## Envelope mínimo

Todo evento debe incluir un envelope versionado y datos de negocio separados:

```json
{
  "id": "uuid-del-evento",
  "tipo": "ReservaConfirmada",
  "version": 1,
  "ocurridoEn": "2026-10-29T15:30:00Z",
  "origen": "reservas",
  "correlacionId": "uuid-de-la-solicitud",
  "datos": {}
}
```

No se incluyen contraseñas, tokens, información de tarjetas ni datos personales innecesarios.

## Eventos iniciales

| Evento | Productor | Consumidores iniciales | Propósito |
| --- | --- | --- | --- |
| `ExperienciaPublicada` | Catálogo | Reservas, Consultas | Crear o actualizar la referencia comercial local. |
| `ServicioActualizado` | Catálogo | Reservas, Consultas | Sincronizar servicios seleccionables. |
| `ReservaCreada` | Reservas | Notificaciones, Consultas | Reflejar una reserva pendiente; no inicia ningún pago. |
| `PagoAprobado` | Pagos | Reservas, Consultas | Confirmar la reserva. |
| `PagoRechazado` | Pagos | Reservas, Consultas | Liberar cupos mediante la compensación de Reservas. |
| `ReservaConfirmada` | Reservas | Notificaciones, Operación, Consultas | Confirmación definitiva del viaje. |
| `ReservaCancelada` | Reservas | Pagos, Notificaciones, Consultas | Liberar recursos y actualizar vistas. |
| `ExperienciaFinalizada` | Operación | Reservas, Consultas | Habilitar calificación y cierre. |

## Regla de preservación del flujo V1

Crear una reserva y pagarla son acciones distintas. Reservas crea la reserva en estado pendiente y comunica únicamente ese hecho. El pago se inicia después, por una acción explícita del cliente sobre su reserva. Pagos publica su resultado solo después de procesar esa solicitud; entonces Reservas confirma o mantiene/cancela según sus reglas. Ningún consumidor de `ReservaCreada` genera una intención de pago automáticamente.

## Topología local en Floci

Los recursos se crean mediante `infra/docker/floci/inicializar-recursos.sh`. Cada tópico tiene una cola independiente por consumidor; por ejemplo, `pagos.pago` distribuye `PagoAprobado` a `reservas-pago`, `notificaciones-pago` y `consultas-pago`, sin que Pagos acceda a la base de datos de sus consumidores. Ningún microservicio consume directamente la cola de otro.

## Flujo implementado: aprobación del pago

1. El cliente invoca explícitamente `POST /api/v2/pagos`; crear una reserva no llama este endpoint.
2. Pagos confirma su transacción local: persiste `pago` y `outbox_evento(PagoAprobado)`.
3. El publicador de Pagos entrega el envelope a SNS `pagos.pago` y marca su Outbox como publicado.
4. SQS entrega una copia a `reservas-pago`; Reservas guarda el identificador en Inbox, valida reserva y monto, y confirma la reserva.
5. En esa misma transacción Reservas añade `ReservaConfirmada` a su Outbox. Su publicador la distribuye por SNS `reservas.reserva` a los consumidores posteriores.

La entrega es al menos una vez. La combinación Outbox + Inbox hace idempotente la transición de estado aunque SNS/SQS reintente el mensaje.

## Flujo implementado: pago rechazado o expirado

1. El simulador de Pagos registra el pago con estado `RECHAZADO` o `EXPIRADO` y su respectivo evento en el mismo Outbox.
2. SNS `pagos.pago` entrega una copia a la cola `reservas-pago`.
3. Reservas registra el evento en Inbox, verifica el monto y, únicamente desde `PENDIENTE`, cambia la reserva a `CANCELADA`.
4. En la misma transacción libera los cupos mediante una actualización condicional que no permite superar `cupos_totales` y registra `ReservaCancelada` en Outbox.
5. `ReservaCancelada` se publica a `reservas.reserva` para Notificaciones, Consultas y los consumidores configurados.
6. Pagos consume esa cancelación por su cola exclusiva `pagos-reserva`; si la reserva tuvo pago aprobado, persiste el reembolso y publica `ReembolsoConfirmado` en `pagos.pago`.

## Garantías y manejo de fallos

- El productor registra el evento en `outbox_evento` en la misma transacción de su cambio local.
- Cada consumidor registra `id` en `inbox_evento`; procesar dos veces el mismo evento no cambia el resultado.
- Un evento no reemplaza la validación local de reglas de negocio.
- Los contratos son compatibles hacia atrás dentro de una misma versión. Un cambio incompatible crea una nueva versión.
- La traza usa `correlacionId` desde la solicitud HTTP hasta los consumidores asíncronos.

El flujo concreto de reserva está documentado en [flujo](../architecture/flujo-creacion-reserva-v2.md) y [secuencia](../architecture/secuencia-creacion-reserva-v2.md).

## Flujo implementado: notificación interna

Notificaciones consume `ReservaCreada`, `ReservaConfirmada` y `ReservaCancelada` desde su cola exclusiva `notificaciones-reserva`. Cada hecho crea un aviso interno para el usuario indicado por el evento y registra Inbox en la misma transacción. El buzón se consulta por API y admite el marcado de lectura; no depende aún de correo, SMS o WhatsApp.

## Flujo implementado: sincronización de Catálogo

Catálogo registra `ExperienciaActualizada`, `ServicioActualizado` y `ActividadActualizada` en su Outbox cuando crea, edita o inactiva esos recursos. SNS `catalogo.experiencia` distribuye cada evento a dos colas independientes: `reservas-catalogo` crea referencias locales para programar salidas y `consultas-catalogo` reconstruye las proyecciones públicas. Ambos consumidores usan Inbox, por lo que un reintento no duplica ni altera el resultado.
