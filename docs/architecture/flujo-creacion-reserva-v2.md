# Flujo V2: reserva pendiente y pago explícito

Crear una reserva y pagarla son acciones distintas. Reservas conserva el cupo y crea una reserva pendiente; el cliente decide después si inicia el pago simulado.

![Flujo visual de creación y confirmación de una reserva V2](images/flujo-creacion-reserva-v2.png)

> La imagen es la versión de presentación. El Mermaid siguiente conserva la fuente editable y precisa del flujo.

```mermaid
flowchart TD
    inicio(["Turista selecciona una salida"]) --> consulta["Consulta Catalogo y disponibilidad"]
    consulta --> autenticado{"Sesion valida?"}
    autenticado -->|"No"| acceso["Solicitar inicio de sesion"]
    acceso --> autenticado
    autenticado -->|"Si"| solicitud["Crear reserva pendiente"]

    subgraph reservas["Microservicio Reservas - comando ACID"]
        solicitud --> bloquear["Bloquear salida y validar servicios"]
        bloquear --> disponible{"Cupos suficientes?"}
        disponible -->|"No"| sinCupo["Responder cupos insuficientes"]
        disponible -->|"Si"| crearReserva["Crear RESERVA PENDIENTE"]
        crearReserva --> descontar["Descontar cupos"]
        descontar --> outboxReserva["Guardar RESERVA_CREADA en outbox"]
    end

    sinCupo --> finError(["Reserva no creada"])
    outboxReserva --> pendiente["Mostrar reserva pendiente"]
    pendiente --> decidir{"Cliente desea pagar?"}
    decidir -->|"No"| finPendiente(["Reserva pendiente"])
    decidir -->|"Si"| pago["Solicitar pago simulado"]

    subgraph pagos["Microservicio Pagos"]
        pago --> intento["Registrar intento de pago"]
        intento --> resultado{"Pago aprobado?"}
        resultado -->|"Si"| aprobado["Publicar PAGO_APROBADO"]
        resultado -->|"No"| rechazado["Publicar PAGO_RECHAZADO"]
    end

    aprobado --> confirmar["Confirmar reserva y publicar RESERVA_CONFIRMADA"]
    rechazado --> pendientePago["Mantener reserva pendiente e informar rechazo"]
    confirmar --> notificar["Notificaciones envia comprobante"]
    notificar --> finOk(["Reserva confirmada"])
    pendientePago --> finPago(["Pago no aprobado"])
```

## Regla de consistencia

La decisión `Cupos suficientes?` se toma únicamente en el modelo de escritura de Reservas. Las consultas del catálogo o de CQRS no pueden confirmar disponibilidad. Pagos solo participa después del comando explícito del cliente.
