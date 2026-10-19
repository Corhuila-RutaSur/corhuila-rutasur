# 006 — Operación y asistencia

## Objetivo

Registrar la ejecución real de una salida y controlar la asistencia de cada participante a cada servicio.

## Alcance

- Asignar un guía a la ejecución de una salida.
- Registrar horas reales, estado y novedades de la experiencia.
- Asignar conductor y vehículo cuando un servicio requiera transporte.
- Registrar asistencia individual por servicio.

## Fuera de alcance

- Planificación de turnos o nómina de guías y conductores.
- Mantenimiento mecánico de vehículos.

## Actores y permisos

| Actor | Puede hacer |
| --- | --- |
| Administrador | Programar y cerrar la ejecución; asignar recursos. |
| Guía | Consultar la salida asignada y registrar novedades o asistencia, según autorización final. |
| Conductor | Consultar los transportes que le fueron asignados. |

## Reglas de negocio

1. Una ejecución de experiencia corresponde a una única salida y tiene un guía asignado.
2. Guía y conductor son roles distintos; pueden ser personas diferentes.
3. Conductor y vehículo son opcionales para actividades sin transporte, como caminatas.
4. La asistencia se registra por participante y por servicio, no solamente por reserva.
5. Una ejecución solo puede iniciar para una salida confirmada y no cancelada.

## Criterios de aceptación

```gherkin
Escenario: Ejecutar una caminata sin vehículo
Dado que una salida tiene programada una caminata lunar
Cuando el administrador inicia su ejecución
Entonces puede registrar guía, horas y asistencia sin exigir conductor ni vehículo.

Escenario: Ejecutar transporte
Dado que una salida tiene programado transporte ida y vuelta
Cuando el administrador prepara la ejecución del transporte
Entonces puede asociar un conductor y un vehículo activos.
```
