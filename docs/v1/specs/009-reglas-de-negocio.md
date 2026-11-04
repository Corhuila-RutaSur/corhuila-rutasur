# 009 — Reglas de negocio transversales

## Objetivo

Consolidar las reglas que afectan más de un módulo y que deben mantenerse coherentes en interfaz, API, servicios y base de datos.

## Reglas

1. Una experiencia es un catálogo; una salida es una fecha programada de esa experiencia.
2. Una reserva corresponde a una sola salida.
3. Una reserva puede incluir varios servicios, pero todos deben pertenecer a su misma salida.
4. Un usuario puede comprar varios cupos mediante participantes de reserva.
5. Cada participante puede tomar una combinación distinta de los servicios comprados.
6. La asistencia se registra para cada participante y servicio.
7. El guía acompaña la experiencia; el conductor solo aplica a servicios de transporte.
8. Una actividad de caminata puede ejecutarse sin conductor ni vehículo.
9. Los precios de una reserva no se recalculan por cambios posteriores de precio en la programación.
10. El historial de reservas no se elimina al inactivar catálogos, usuarios operativos o recursos.
11. Los cupos no pueden quedar negativos.
12. El sistema debe validar permisos en el servidor, incluso si la interfaz oculta acciones no autorizadas.
