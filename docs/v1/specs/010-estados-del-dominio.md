# 010 — Estados del dominio

## Objetivo

Definir los estados permitidos para reservas, pagos, salidas, ejecuciones y asistencia.

## Estados

### Reserva

```text
PENDIENTE -> CONFIRMADA -> FINALIZADA
PENDIENTE -> CANCELADA
CONFIRMADA -> CANCELADA
```

### Pago

```text
PENDIENTE -> APROBADO
PENDIENTE -> RECHAZADO
APROBADO -> REEMBOLSADO
```

### Salida

```text
PROGRAMADA -> ABIERTA -> CERRADA -> EN_EJECUCION -> FINALIZADA
PROGRAMADA -> CANCELADA
ABIERTA -> CANCELADA
CERRADA -> CANCELADA
```

### Ejecución

```text
PENDIENTE -> EN_CURSO -> FINALIZADA
PENDIENTE -> CANCELADA
EN_CURSO -> CANCELADA
```

### Asistencia

```text
PENDIENTE -> ASISTIO
PENDIENTE -> NO_ASISTIO
PENDIENTE -> CANCELADO
```

## Reglas de negocio

1. Las transiciones no listadas requieren una decisión funcional antes de implementarse.
2. Una salida cancelada no admite reservas ni puede iniciar ejecución.
3. Una reserva finalizada no puede volver a estado pendiente o confirmada.
4. Una calificación solo se habilita cuando la reserva está finalizada.
