# Modelo visual de datos V2

> Estado: diseño de referencia previo a la migración de la V1. No modifica la V1 etiquetada como `v1`.

## Decisiones de persistencia

- Cada microservicio es dueño exclusivo de su base de datos y de sus migraciones Flyway.
- En el entorno local se podrán ejecutar las bases en una sola instancia PostgreSQL, pero cada una tendrá base, usuario y migraciones independientes.
- En producción cada dueño se despliega sobre Aurora PostgreSQL: los comandos llegan al endpoint writer y las lecturas que admiten consistencia eventual se dirigen a readers o proyecciones CQRS.
- No hay llaves foráneas, consultas SQL ni transacciones distribuidas entre microservicios.
- Las referencias externas se identifican con el sufijo `_externo`; su consistencia se mantiene por contratos y eventos.
- Cada productor de eventos usa `outbox_evento`; cada consumidor persistente usa `inbox_evento` para tolerar entregas duplicadas.
- Los modelos de `Consultas` son proyecciones CQRS y no son fuente de verdad para comandos.

## Mapa global: propiedad de datos y comunicación

![Modelo visual de dominios y eventos de RutaSur V2](images/modelo-v2-dominios.png)

```mermaid
flowchart LR
    I[Identidad\nrutasur_identidad_db]
    C[Catalogo\nrutasur_catalogo_db]
    R[Reservas\nrutasur_reservas_db]
    P[Pagos\nrutasur_pagos_db]
    O[Operacion\nrutasur_operacion_db]
    N[Notificaciones\nrutasur_notificaciones_db]
    Q[Consultas CQRS\nrutasur_consultas_db]
    B{{SNS + SQS\nFloci local}}

    I -->|UsuarioCreado / RolActualizado| B
    C -->|ExperienciaPublicada / ServicioActualizado| B
    R -->|ReservaConfirmada / ReservaCancelada| B
    P -->|PagoAprobado / PagoRechazado| B
    O -->|SalidaEjecutada| B

    B --> R
    B --> P
    B --> O
    B --> N
    B --> Q
```

El diagrama representa comunicación por eventos, no acceso a tablas ajenas. El servicio que publica un hecho nunca conoce la base de datos de quien lo consume. En AWS, SNS distribuye a una SQS por consumidor y cada una tiene DLQ; Floci reproduce esa topología en local.

## 1. Identidad

Dueño de cuentas, roles y ciclo de vida de autenticación. Los perfiles operativos no duplican usuarios: Operación conserva únicamente `usuario_id_externo`.

```mermaid
erDiagram
    USUARIO ||--o{ USUARIO_ROL : tiene
    ROL ||--o{ USUARIO_ROL : asigna
    USUARIO ||--o{ SESION_AUTENTICACION : inicia
    SESION_AUTENTICACION ||--o{ REFRESH_TOKEN : rota
    SESION_AUTENTICACION ||--o{ ACCESS_TOKEN_REVOCADO : revoca
    USUARIO ||--o{ EVENTO_AUTENTICACION : audita

    USUARIO {
        uuid id PK
        string correo UK
        string password_hash
        boolean activo
    }
    ROL {
        uuid id PK
        string nombre UK
    }
    USUARIO_ROL {
        uuid usuario_id FK
        uuid rol_id FK
    }
    SESION_AUTENTICACION {
        uuid id PK
        uuid usuario_id FK
        string estado
        datetime expira_en
    }
    REFRESH_TOKEN {
        uuid id PK
        uuid sesion_id FK
        string token_hash UK
        boolean usado
    }
    ACCESS_TOKEN_REVOCADO {
        uuid id PK
        uuid sesion_id FK
        string jti UK
        datetime expira_en
    }
```

## 2. Catálogo

Dueño del contenido comercial de una experiencia. No administra cupos ni reservas.

```mermaid
erDiagram
    TIPO_EXPERIENCIA ||--o{ EXPERIENCIA : clasifica
    EXPERIENCIA ||--o{ EXPERIENCIA_IMAGEN : muestra
    EXPERIENCIA ||--o{ SERVICIO_EXPERIENCIA : ofrece
    EXPERIENCIA ||--o| RESUMEN_CALIFICACION : proyecta

    TIPO_EXPERIENCIA {
        uuid id PK
        string nombre UK
        boolean activo
    }
    EXPERIENCIA {
        uuid id PK
        uuid tipo_experiencia_id FK
        uuid creador_id_externo
        string nombre
        string destino
        boolean activa
    }
    EXPERIENCIA_IMAGEN {
        uuid id PK
        uuid experiencia_id FK
        string url
        boolean principal
    }
    SERVICIO_EXPERIENCIA {
        uuid id PK
        uuid experiencia_id FK
        string tipo
        string nombre
        boolean activo
    }
    RESUMEN_CALIFICACION {
        uuid experiencia_id PK
        decimal promedio
        int cantidad
    }
```

## 3. Reservas

Es el límite de consistencia fuerte de RutaSur. Es dueño de salidas, disponibilidad, bloqueo temporal y reserva. Aquí se conserva el bloqueo pesimista local para impedir sobreventa.

```mermaid
erDiagram
    EXPERIENCIA_DISPONIBLE ||--o{ SERVICIO_DISPONIBLE : contiene
    EXPERIENCIA_DISPONIBLE ||--o{ SALIDA_EXPERIENCIA : programa
    SALIDA_EXPERIENCIA ||--o{ PROGRAMACION_SERVICIO : habilita
    SERVICIO_DISPONIBLE ||--o{ PROGRAMACION_SERVICIO : referencia
    SALIDA_EXPERIENCIA ||--o{ BLOQUEO_CUPO : bloquea
    BLOQUEO_CUPO ||--o| RESERVA : confirma
    RESERVA ||--o{ PARTICIPANTE_RESERVA : incluye
    RESERVA ||--o{ DETALLE_RESERVA : compra
    DETALLE_RESERVA ||--o{ DETALLE_PARTICIPANTE : asigna
    PARTICIPANTE_RESERVA ||--o{ DETALLE_PARTICIPANTE : recibe
    RESERVA ||--o| CALIFICACION : permite

    EXPERIENCIA_DISPONIBLE {
        uuid id PK
        uuid experiencia_catalogo_id UK
        string nombre_snapshot
        string destino_snapshot
        boolean activa
    }
    SERVICIO_DISPONIBLE {
        uuid id PK
        uuid servicio_catalogo_id UK
        uuid experiencia_disponible_id FK
        string nombre_snapshot
    }
    SALIDA_EXPERIENCIA {
        uuid id PK
        uuid experiencia_disponible_id FK
        int cupos_disponibles
        string estado
    }
    PROGRAMACION_SERVICIO {
        uuid id PK
        uuid salida_id FK
        uuid servicio_disponible_id FK
        decimal precio_unitario
        int cupos_disponibles
    }
    BLOQUEO_CUPO {
        uuid id PK
        uuid salida_id FK
        uuid cliente_id_externo
        int cantidad_participantes
        string estado
        datetime vence_en
    }
    RESERVA {
        uuid id PK
        string codigo UK
        uuid bloqueo_cupo_id FK
        uuid cliente_id_externo
        string estado
        decimal total
    }
```

`experiencia_disponible` y `servicio_disponible` son referencias locales alimentadas por eventos de Catálogo; no son copias que Catálogo pueda leer o modificar directamente.

## 4. Pagos

Pagos es dueño de sus intentos y de la decisión técnica del pago. La reserva se identifica con un UUID externo, sin FK entre bases.

```mermaid
erDiagram
    PAGO ||--o{ INTENTO_PAGO : registra

    PAGO {
        uuid id PK
        uuid reserva_id_externo UK
        uuid cliente_id_externo
        decimal valor
        string estado
    }
    INTENTO_PAGO {
        uuid id PK
        uuid pago_id FK
        string referencia UK
        string metodo
        string estado
        datetime procesado_en
    }
```

## 5. Operación

Administra los recursos y la ejecución real. Los identificadores de guía, conductor, salida y programación son referencias externas o proyecciones recibidas por eventos.

```mermaid
erDiagram
    GUIA ||--o{ EJECUCION_EXPERIENCIA : acompana
    SALIDA_OPERATIVA ||--o| EJECUCION_EXPERIENCIA : ejecuta
    EJECUCION_EXPERIENCIA ||--o{ EJECUCION_SERVICIO : contiene
    PROGRAMACION_OPERATIVA ||--o| EJECUCION_SERVICIO : ejecuta
    CONDUCTOR ||--o{ EJECUCION_SERVICIO : conduce
    VEHICULO ||--o{ EJECUCION_SERVICIO : transporta

    GUIA {
        uuid id PK
        uuid usuario_id_externo UK
        string numero_registro
        boolean activo
    }
    CONDUCTOR {
        uuid id PK
        uuid usuario_id_externo UK
        string numero_licencia UK
        boolean activo
    }
    VEHICULO {
        uuid id PK
        string placa UK
        int capacidad
    }
    SALIDA_OPERATIVA {
        uuid id PK
        uuid salida_reservas_id UK
        string experiencia_nombre_snapshot
    }
    EJECUCION_EXPERIENCIA {
        uuid id PK
        uuid salida_operativa_id FK
        uuid guia_id FK
        string estado
    }
    EJECUCION_SERVICIO {
        uuid id PK
        uuid ejecucion_experiencia_id FK
        uuid programacion_reservas_id_externo
        uuid conductor_id FK
        uuid vehiculo_id FK
    }
```

## 6. Notificaciones serverless

La función serverless consume eventos desde una cola propia. Guarda el evento recibido y cada intento de entrega para permitir idempotencia y reintentos.

```mermaid
erDiagram
    NOTIFICACION ||--o{ INTENTO_ENTREGA : intenta
    PLANTILLA ||--o{ NOTIFICACION : compone

    PLANTILLA {
        uuid id PK
        string codigo UK
        string canal
        string contenido
    }
    NOTIFICACION {
        uuid id PK
        uuid evento_id UK
        string destinatario
        string estado
    }
    INTENTO_ENTREGA {
        uuid id PK
        uuid notificacion_id FK
        int numero_intento
        string resultado
        datetime ejecutado_en
    }
    INBOX_EVENTO {
        uuid evento_id PK
        string tipo_evento
        datetime procesado_en
    }
```

## 7. Consultas CQRS

Estas tablas no participan en comandos de reserva, pago ni disponibilidad. Se regeneran mediante eventos y priorizan velocidad de lectura para Angular y el panel administrativo.

```mermaid
erDiagram
    EXPERIENCIA_PUBLICA ||--o{ EXPERIENCIA_PUBLICA_SERVICIO : detalla
    RESERVA_CLIENTE_CONSULTA ||--o{ RESERVA_CLIENTE_SERVICIO : muestra
    PANEL_OPERADOR_SALIDA ||--o{ PANEL_OPERADOR_SERVICIO : resume

    EXPERIENCIA_PUBLICA {
        uuid experiencia_id PK
        string nombre
        string destino
        decimal calificacion_promedio
        string imagen_principal_url
    }
    RESERVA_CLIENTE_CONSULTA {
        uuid reserva_id PK
        uuid cliente_id
        string codigo
        string estado
        decimal total
    }
    PANEL_OPERADOR_SALIDA {
        uuid salida_id PK
        string estado
        int cupos_disponibles
        int reservas_confirmadas
    }
    PROYECCION_EVENTO {
        uuid evento_id PK
        string proyeccion
        datetime procesado_en
    }
```

## Tablas transversales técnicas

Cada servicio productor tendrá `outbox_evento`; Reservas, Pagos, Operación, Notificaciones y Consultas tendrán `inbox_evento` o `proyeccion_evento` según su responsabilidad.

```mermaid
erDiagram
    OUTBOX_EVENTO {
        uuid id PK
        string tipo_evento
        string agregado_tipo
        uuid agregado_id
        json payload
        datetime ocurrido_en
        datetime publicado_en
    }
    INBOX_EVENTO {
        uuid evento_id PK
        string tipo_evento
        datetime procesado_en
    }
```

La especificación de atributos, tipos, índices y restricciones se encuentra en `docs/database/modelo-v2.dbml`.
