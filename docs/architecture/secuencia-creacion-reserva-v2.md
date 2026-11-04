# Secuencia V2: reserva pendiente, pago explícito y notificación

Esta secuencia representa el camino exitoso. El diagrama de flujo complementario muestra los caminos de rechazo, expiración y liberación del cupo.

![Secuencia visual de creación de reserva V2](images/secuencia-creacion-reserva-v2.png)

> La imagen es la versión de presentación. El Mermaid siguiente conserva la fuente editable y precisa de la secuencia.

```mermaid
sequenceDiagram
    participant Turista
    participant Frontend
    participant Gateway
    participant Catalogo
    participant Reservas
    participant DbReservas
    participant BusEventos
    participant Pagos
    participant Notificaciones

    Turista->>Frontend: Selecciona salida y servicios
    Frontend->>Gateway: POST /reservas
    Gateway->>Reservas: CrearReservaPendiente
    Reservas->>DbReservas: FOR UPDATE salida y programaciones
    DbReservas-->>Reservas: Cupos vigentes
    Reservas->>DbReservas: Guarda reserva pendiente, descuenta cupos y outbox
    Reservas-->>Gateway: Reserva pendiente
    Gateway-->>Frontend: 201 reserva creada
    Turista->>Frontend: Decide pagar posteriormente
    Frontend->>Gateway: POST /pagos
    Gateway->>Pagos: ProcesarPagoSimulado
    Pagos->>BusEventos: Publica PagoAprobado
    BusEventos->>Reservas: Consume PagoAprobado
    Reservas->>DbReservas: Confirma reserva y guarda outbox
    Reservas->>BusEventos: Publica ReservaConfirmada
    BusEventos->>Notificaciones: Consume ReservaConfirmada
    Notificaciones-->>Turista: Envia comprobante
    Gateway-->>Frontend: Consulta estado confirmado
```

## Mensajes relevantes

| Mensaje | Tipo | Garantía |
| --- | --- | --- |
| `POST /reservas` | Sincrónico | Crea una reserva pendiente y decide localmente si puede comprometer cupos. |
| `POST /pagos` | Sincrónico | Solo se invoca por la acción explícita de pago del cliente. |
| `PagoAprobado` | Asíncrono | Se publica desde el outbox de Pagos y se procesa de forma idempotente en Reservas. |
| `ReservaConfirmada` | Asíncrono | Permite notificar y actualizar proyecciones sin bloquear la reserva. |
