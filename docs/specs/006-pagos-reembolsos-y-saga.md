# 006 — Pagos, reembolsos y Saga

**Estado:** Borrador  
**Servicio dueño:** Pagos  
**Origen V1:** Spec 005.

## Alcance

Pagos recibe una solicitud explícita del cliente sobre una reserva pendiente, crea una intención simulada, confirma o rechaza el resultado y conserva auditoría propia. No consume `ReservaCreada` para generar pagos automáticamente. En una cancelación confirmada inicia o registra la reversión/reembolso aplicable.

## Reglas

1. Pagos no modifica la tabla ni estado local de Reserva.
2. Pagos solo inicia una intención tras una operación explícita `Pagar reserva`; crear una reserva no es una orden de pago.
2. Una misma correlación no genera dos cobros: consumidores y comandos son idempotentes.
3. El pago se asocia a `reservaId` externo y monto snapshot; no requiere FK interbase.
4. El resultado se publica mediante Outbox como `PagoAprobado`, `PagoRechazado`, `PagoExpirado` o `ReembolsoConfirmado`.

## Cancelación y reembolso

La cancelación es un comando explícito del propietario de la reserva, no un efecto de la creación de la reserva. Reservas libera los cupos ACID y publica `ReservaCancelada`. Pagos consume ese hecho mediante su propia cola `pagos-reserva` e Inbox idempotente. Si encuentra un pago `APROBADO` para la reserva y aún no hay un reembolso para ese pago, registra la devolución `CONFIRMADO` y publica `ReembolsoConfirmado` por Outbox. Si no hay pago aprobado, registra el evento en Inbox sin crear ninguna devolución.

La regla evita dos errores: no se generan pagos automáticamente al reservar y no se reembolsa dos veces ante reintentos de mensajes.

## Criterio de aceptación

```gherkin
Escenario: Confirmar pago de reserva
Dado que el cliente solicitó pagar una reserva pendiente
Cuando confirma la intención simulada
Entonces registra un único pago propio
Y publica un único PagoAprobado lógico para esa correlación.
```

## Implementación inicial V2

`pagos-service` registra `PagoAprobado` en `outbox_evento` dentro de la misma transacción que crea el pago simulado. Un publicador programado compone el envelope versionado y lo publica en SNS `pagos.pago`. La aceptación de SNS marca el evento como publicado; ante un fallo se conserva pendiente, con contador y último error, para reintento. Reservas consume una copia desde su propia cola `reservas-pago`; Pagos nunca modifica la base de datos de Reservas.

Para pruebas funcionales, la misma solicitud de pago admite los resultados simulados `APROBADO`, `RECHAZADO` y `EXPIRADO` —por defecto aprueba—. Los dos últimos generan respectivamente `PagoRechazado` y `PagoExpirado`; no cambian reservas directamente y activan la compensación local de Reservas.
