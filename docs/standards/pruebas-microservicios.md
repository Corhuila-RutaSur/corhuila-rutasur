# Estándar de pruebas V2

1. **Dominio y casos de uso:** JUnit/Mockito, sin Spring cuando sea posible.
2. **Arquitectura:** ArchUnit protege el hexágono y prohíbe imports de otros servicios.
3. **Adaptadores:** integración con PostgreSQL/Flyway y Floci cuando aplique.
4. **Contratos:** valida OpenAPI y payloads de eventos entre productor y consumidor.
5. **Flujos críticos:** reserva concurrente, Outbox, Inbox duplicado, pago, compensación y cancelación.

Cada evento prueba versión, correlación, duplicado e idempotencia. Reserva prueba cupos suficientes, dos solicitudes por el último cupo, liberación al cancelar y rechazo sin capacidad. Una Saga prueba éxito, fallo y compensación real.
