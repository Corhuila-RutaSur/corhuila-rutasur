# 010 — Estados e invariantes transversales

**Estado:** Aprobada  
**Servicio dueño:** Cada agregado.

Cada servicio define sus propias transiciones y rechaza las inválidas en su dominio. Los estados ajenos se reciben como hechos de integración, no se cambian directamente. Reserva mantiene pendiente, confirmada y cancelada; Pago mantiene iniciado, confirmado, rechazado, expirado o reembolsado. Toda transición relevante deja miga de pan, outbox si cruza el límite y prueba de rechazo.
