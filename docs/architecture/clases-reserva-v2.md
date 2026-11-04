# Clases V2: dominio y casos de uso de Reservas

El diagrama muestra el interior hexagonal del microservicio de Reservas. Los controladores REST, JPA y SNS/SQS son adaptadores; no son dependencias del dominio.

![Diagrama visual de clases de Reservas V2](images/clases-reserva-v2.png)

> La imagen es la versión de presentación. El Mermaid siguiente conserva la fuente editable y precisa de las dependencias.

```mermaid
classDiagram
    class CrearBloqueoCupoUseCase {
        <<interface>>
        +crear(solicitud) BloqueoCupoResponse
    }
    class ConfirmarReservaUseCase {
        <<interface>>
        +confirmar(eventoPago) void
    }
    class LiberarBloqueoCupoUseCase {
        <<interface>>
        +liberar(id, motivo) void
    }
    class ReservaApplicationService {
        +crear(solicitud) BloqueoCupoResponse
        +confirmar(eventoPago) void
        +liberar(id, motivo) void
    }
    class SalidaRepositoryPort {
        <<interface>>
        +bloquearPorId(id) SalidaExperiencia
        +guardar(salida) void
    }
    class ReservaRepositoryPort {
        <<interface>>
        +guardar(reserva) Reserva
        +buscarPorBloqueo(id) Reserva
    }
    class BloqueoRepositoryPort {
        <<interface>>
        +guardar(bloqueo) BloqueoCupo
        +buscarActivo(id) BloqueoCupo
    }
    class EventPublisherPort {
        <<interface>>
        +publicar(evento) void
    }
    class SalidaExperiencia {
        +id UUID
        +cuposDisponibles int
        +bloquearCupos(cantidad) void
        +liberarCupos(cantidad) void
    }
    class ProgramacionServicio {
        +id UUID
        +cuposDisponibles int
        +precioUnitario BigDecimal
        +bloquearCupos(cantidad) void
    }
    class BloqueoCupo {
        +id UUID
        +estado EstadoBloqueo
        +venceEn OffsetDateTime
        +confirmar() void
        +liberar() void
        +estaVigente(reloj) boolean
    }
    class Reserva {
        +id UUID
        +codigo String
        +estado EstadoReserva
        +total BigDecimal
        +confirmar() void
        +cancelar() void
    }
    class DetalleReserva {
        +programacionServicioId UUID
        +cantidad int
        +precioUnitario BigDecimal
        +calcularSubtotal() BigDecimal
    }
    class ParticipanteReserva {
        +id UUID
        +nombres String
        +responsable boolean
    }
    class ReservaRestAdapter {
        +crearBloqueo(request) ResponseEntity
    }
    class ReservaJpaAdapter {
        +bloquearPorId(id) SalidaExperiencia
        +guardar(reserva) Reserva
    }
    class ReservaEventAdapter {
        +consumirPagoAprobado(evento) void
        +publicar(evento) void
    }

    CrearBloqueoCupoUseCase <|.. ReservaApplicationService
    ConfirmarReservaUseCase <|.. ReservaApplicationService
    LiberarBloqueoCupoUseCase <|.. ReservaApplicationService
    ReservaApplicationService --> SalidaRepositoryPort
    ReservaApplicationService --> ReservaRepositoryPort
    ReservaApplicationService --> BloqueoRepositoryPort
    ReservaApplicationService --> EventPublisherPort
    SalidaExperiencia "1" --> "0..*" ProgramacionServicio
    BloqueoCupo "1" --> "1" SalidaExperiencia
    Reserva "1" --> "1" BloqueoCupo
    Reserva "1" --> "1..*" DetalleReserva
    Reserva "1" --> "1..*" ParticipanteReserva
    ReservaRestAdapter --> CrearBloqueoCupoUseCase
    ReservaJpaAdapter ..|> SalidaRepositoryPort
    ReservaJpaAdapter ..|> ReservaRepositoryPort
    ReservaJpaAdapter ..|> BloqueoRepositoryPort
    ReservaEventAdapter --> ConfirmarReservaUseCase
    ReservaEventAdapter ..|> EventPublisherPort
```

## Dependencias permitidas

```text
adapter/in/rest        -> port/in
adapter/in/events      -> port/in
application/service    -> port/out y dominio
adapter/out/jpa        -> port/out
adapter/out/messaging  -> port/out
dominio                -> ninguna tecnología
```
