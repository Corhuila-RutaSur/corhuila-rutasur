# Modelo entidad-relación V1

Este diagrama representa la estructura persistente definida por las migraciones `V1`, `V2` y `V3`. La migración SQL es la fuente ejecutable; el diagrama permite comprender las relaciones principales sin sobrecargarlo con atributos.

Los atributos, tipos, restricciones y descripciones de cada columna se consultan en las migraciones Flyway mediante los comentarios PostgreSQL (`COMMENT ON`) y en el [diccionario de datos](../wiki/Modelo-de-Datos-V1.md).

## Experiencias, reservas y operación

```mermaid
erDiagram
    TIPO_USUARIO ||--o{ USUARIO_TIPO : clasifica
    USUARIO ||--o{ USUARIO_TIPO : tiene
    USUARIO ||--o| GUIA : puede_ser
    USUARIO ||--o| CONDUCTOR : puede_ser

    TIPO_EXPERIENCIA ||--o{ EXPERIENCIA : clasifica
    EXPERIENCIA ||--o{ EXPERIENCIA_IMAGEN : muestra
    EXPERIENCIA ||--o{ SERVICIO_EXPERIENCIA : ofrece
    EXPERIENCIA ||--o{ SALIDA_EXPERIENCIA : programa
    SALIDA_EXPERIENCIA ||--o{ PROGRAMACION_SERVICIO : habilita
    SERVICIO_EXPERIENCIA ||--o{ PROGRAMACION_SERVICIO : se_ofrece_en

    USUARIO ||--o{ RESERVA : compra
    SALIDA_EXPERIENCIA ||--o{ RESERVA : corresponde_a
    RESERVA ||--o{ PARTICIPANTE_RESERVA : incluye
    RESERVA ||--o{ DETALLE_RESERVA : contiene
    PROGRAMACION_SERVICIO ||--o{ DETALLE_RESERVA : seleccionado_en
    DETALLE_RESERVA ||--o{ DETALLE_PARTICIPANTE : asigna
    PARTICIPANTE_RESERVA ||--o{ DETALLE_PARTICIPANTE : recibe
    RESERVA ||--o{ PAGO : registra
    RESERVA ||--o| CALIFICACION : recibe

    SALIDA_EXPERIENCIA ||--o| EJECUCION_EXPERIENCIA : se_ejecuta_como
    GUIA ||--o{ EJECUCION_EXPERIENCIA : acompana
    PROGRAMACION_SERVICIO ||--o| EJECUCION_SERVICIO : se_ejecuta_como
    CONDUCTOR ||--o{ EJECUCION_SERVICIO : conduce
    VEHICULO ||--o{ EJECUCION_SERVICIO : transporta
```

**Regla estructural principal:** todos los `DETALLE_RESERVA` de una `RESERVA` deben referir servicios programados para su misma `SALIDA_EXPERIENCIA`. Así se permiten combinaciones libres dentro de una experiencia y fecha, sin mezclar destinos incompatibles.

## Autenticación, sesiones y revocación

```mermaid
erDiagram
    USUARIO ||--o{ SESION_AUTENTICACION : inicia
    SESION_AUTENTICACION ||--o{ REFRESH_TOKEN : rota
    REFRESH_TOKEN o|--o| REFRESH_TOKEN : reemplaza
    USUARIO ||--o{ ACCESS_TOKEN_REVOCADO : posee
    SESION_AUTENTICACION ||--o{ ACCESS_TOKEN_REVOCADO : invalida
    USUARIO o|--o{ EVENTO_AUTENTICACION : genera
    SESION_AUTENTICACION o|--o{ EVENTO_AUTENTICACION : registra
```

Los tokens reales no aparecen en el modelo ni se persisten: solamente hashes de refresh token y el `jti` de JWT revocados.
