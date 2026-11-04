# 001 — Lenguaje ubicuo y límites de contexto

**Estado:** Aprobada  
**Servicio dueño:** Todos  
**Origen V1:** Spec 001 y modelo V1.

## Objetivo

Establecer un vocabulario común y asignar un único dueño a cada dato antes de extraer funcionalidades desde V1.

## Reglas

1. **Experiencia** es la oferta turística publicada; pertenece a Catálogo.
2. **Salida** es la fecha/capacidad comercializable de una experiencia; pertenece a Reservas.
3. **Reserva** es el compromiso de cupos y servicios comunes para uno o más participantes; pertenece a Reservas.
4. **Pago** es una transacción financiera asociada a una reserva por referencia externa; pertenece a Pagos.
5. **Ejecución** representa la realización operativa de una salida; pertenece a Operación.
6. **Usuario, rol, sesión y token** pertenecen exclusivamente a Identidad.
7. Una proyección no es fuente de verdad: puede reconstruirse a partir de eventos.

## Criterios de aceptación

```gherkin
Escenario: Un servicio requiere identidad de un usuario
Dado que el usuario pertenece a Identidad
Cuando Reservas necesita registrarlo como comprador
Entonces guarda su identificador externo y el snapshot estrictamente necesario
Y no consulta ni crea una clave foránea hacia la base de Identidad.
```

## Pruebas requeridas

- ArchUnit que impida dependencias de dominio entre servicios.
- Revisión de migraciones sin claves foráneas interbase.
