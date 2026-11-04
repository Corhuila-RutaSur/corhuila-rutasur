# 007 — Operación y asistencia

**Estado:** Pendiente  
**Servicio dueño:** Operación  
**Origen V1:** Spec 006.

Operación consume reservas confirmadas para preparar ejecución, guía, conductor, vehículo y asistencia. No administra cobros ni cupos. Sus referencias a salida, reserva y usuarios son externas y se actualizan por eventos idempotentes.

```gherkin
Escenario: Registrar asistencia
Dado que existe una ejecución de salida y una reserva confirmada
Cuando el guía marca la asistencia de un participante
Entonces Operación guarda el hecho operativo
Y no altera el estado financiero de la reserva.
```
