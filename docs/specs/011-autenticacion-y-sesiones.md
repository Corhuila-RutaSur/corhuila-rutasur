# 011 — Autenticación, sesiones y tokens

## Objetivo

Permitir que solo usuarios autenticados creen y gestionen reservas, mientras el catálogo público de experiencias y salidas permanece disponible para visitantes.

## Alcance

- Autenticar usuarios con credenciales.
- Emitir access tokens JWT de duración corta.
- Emitir refresh tokens rotables y de un único uso.
- Controlar sesiones, cierre de sesión, expiración e invalidación.
- Revocar access tokens antes de su expiración cuando sea necesario.
- Registrar eventos relevantes de autenticación sin almacenar secretos.

## Fuera de alcance

- Registro de usuarios, recuperación de contraseña y segundo factor de autenticación.
- Inicio de sesión con proveedores externos.
- Implementación técnica de JWT; esta especificación define el comportamiento que deberá cumplir.

## Acceso público y protegido

| Recurso | Acceso V1 |
| --- | --- |
| Catálogo de experiencias activas | Público. |
| Salidas abiertas y disponibilidad pública | Público. |
| Crear, pagar, consultar o calificar una reserva | Requiere usuario autenticado y propietario de la reserva. |
| Administración y operación | Requiere autenticación y rol autorizado. |

## Reglas de negocio

1. El access token JWT debe incluir un identificador único `jti`, el usuario y los roles necesarios para autorizar.
2. El access token debe tener vigencia corta; su duración exacta se definirá como parámetro seguro de despliegue.
3. El refresh token debe ser opaco, aleatorio, de uso único y almacenarse únicamente como hash.
4. Cada uso exitoso de un refresh token debe marcarlo como usado y emitir un nuevo refresh token para la misma sesión.
5. La reutilización de un refresh token ya usado se considera un incidente: se invalidará la sesión completa y se registrará el evento.
6. Cerrar sesión invalida la sesión y revoca el access token actual hasta su expiración.
7. Una sesión invalidada o expirada no puede renovar tokens ni acceder a recursos protegidos.
8. Nunca se deben registrar JWT, refresh tokens, contraseñas ni hashes en logs, respuestas de error o auditoría.
9. Un usuario desactivado no puede crear nuevas sesiones ni renovar tokens.

## Flujos y casos alternativos

### Inicio de sesión exitoso

1. El usuario presenta sus credenciales.
2. El sistema valida que la cuenta esté activa.
3. Se crea una sesión y se registra el evento.
4. Se entrega un access token JWT y un refresh token de un solo uso.

### Renovación válida

1. El cliente presenta un refresh token vigente y no usado.
2. El sistema valida el hash, la sesión y la expiración.
3. Marca el refresh token como usado.
4. Emite un nuevo par de tokens y relaciona el refresh anterior con el nuevo.

### Access token vencido y renovación desde el cliente

1. El cliente invoca un endpoint protegido con el access token en `Authorization: Bearer <access-token>`.
2. Si el JWT está vencido, la API responde `401` con el código `autenticacion.token.expirado`.
3. Solo ante ese código, el cliente puede invocar `POST /api/v1/public/autenticacion/refresh` enviando **únicamente** el `refreshToken`. El access token vencido no se envía ni participa en la renovación.
4. Si la renovación es correcta, el cliente reemplaza de forma atómica su par de tokens y repite una única vez la solicitud original.
5. Si la respuesta es `autenticacion.token.invalido`, el cliente no intenta renovar automáticamente: elimina sus credenciales locales y solicita un nuevo inicio de sesión.

### Vencimiento de refresh y sesión

- La vigencia del access token se configura con `JWT_ACCESS_TOKEN_MINUTES` (15 minutos por defecto).
- La vigencia del refresh token y de la sesión se configura con `JWT_REFRESH_TOKEN_MINUTES` (1.440 minutos o 24 horas por defecto).
- La sesión tiene un límite máximo contado desde el login. Rotar un refresh token no prolonga indefinidamente su expiración.
- Si el refresh token o la sesión vencieron, la API responde `401` con `autenticacion.refresh.expirado`. El cliente elimina sus tokens y redirige al login.
- Si el refresh no existe, fue revocado, ya fue usado o su sesión está invalidada, la API responde `401` con `autenticacion.refresh.invalido`. Ante un refresh ya usado se invalida toda la sesión para mitigar el replay de un token robado.

### Códigos que interpreta el frontend

| Código | Significado | Acción del frontend |
| --- | --- | --- |
| `autenticacion.token.expirado` | El access token superó su vigencia. | Intentar una renovación una sola vez. |
| `autenticacion.token.invalido` | El access token es inválido, revocado o está alterado. | Limpiar sesión y mostrar login. |
| `autenticacion.refresh.expirado` | La sesión o su refresh token vencieron. | Limpiar sesión y mostrar login. |
| `autenticacion.refresh.invalido` | Refresh inexistente, revocado o reutilizado. | Limpiar sesión y mostrar login. |

### Reutilización de refresh token

1. El sistema detecta un refresh token previamente usado o revocado.
2. Invalida la sesión asociada.
3. Registra un evento `REFRESH_TOKEN_REUTILIZADO`.
4. Requiere que el usuario vuelva a iniciar sesión.

## Criterios de aceptación

```gherkin
Escenario: Un visitante explora el catálogo
Dado que una persona no ha iniciado sesión
Cuando consulta experiencias activas y salidas abiertas
Entonces el sistema permite la consulta.

Escenario: Un visitante intenta reservar
Dado que una persona no ha iniciado sesión
Cuando intenta crear una reserva
Entonces el sistema responde que se requiere autenticación.

Escenario: Un refresh token no puede reutilizarse
Dado que un refresh token ya fue usado para renovar una sesión
Cuando se presenta nuevamente el mismo token
Entonces el sistema invalida la sesión y no entrega nuevos tokens.
```

## Preguntas abiertas y supuestos

- La duración de access token, refresh token y sesión debe definirse en configuración segura, no como valor fijo en código.
- Se requiere decidir si el refresh token viajará en cookie `HttpOnly` o en una respuesta para almacenamiento seguro del cliente; la recomendación web es cookie `HttpOnly`, `Secure` y `SameSite` apropiado.
