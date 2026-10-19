# 008 — Autorización y roles

## Objetivo

Definir las capacidades mínimas de cada rol para proteger datos personales, comerciales y operativos.

## Alcance

| Rol | Capacidades V1 |
| --- | --- |
| Cliente | Consultar catálogo y salidas activas; gestionar sus propias reservas, participantes, pagos y calificaciones. |
| Administrador | Gestionar catálogo, salidas, reservas, ejecución, usuarios operativos y recursos. |
| Guía | Consultar sus salidas asignadas y, si se habilita, registrar novedades y asistencia. |
| Conductor | Consultar los servicios de transporte que le fueron asignados. |

## Reglas de negocio

1. Un usuario puede tener varios roles.
2. Un cliente no puede consultar ni modificar reservas de otro cliente.
3. Un guía no adquiere permisos administrativos por tener ese rol.
4. La contraseña nunca debe exponerse en respuestas o registros funcionales.
5. Toda operación de modificación requiere usuario autenticado, excepto el catálogo público que se habilite expresamente.

## Criterios de aceptación

```gherkin
Escenario: Proteger una reserva ajena
Dado que un cliente inició sesión
Y existe una reserva creada por otro cliente
Cuando intenta consultarla o modificarla
Entonces el sistema niega el acceso.
```
