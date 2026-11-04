# 013 — Seguridad de borde y autorización distribuida

**Estado:** Pendiente crítico  
**Servicios dueños:** Gateway, Identidad y todos los microservicios protegidos  
**Relacionado con:** ADR-002, spec 002, spec 011 y la ejecución local V2.

## Situación actual verificable

Identidad emite y valida JWT para sus propios flujos. El API Gateway todavía no concentra las rutas V2 ni valida los tokens en el borde. Por ello, durante la migración local algunos microservicios se consumen directamente por su puerto (`8092`–`8097`) y no deben considerarse protegidos ni aptos para exposición.

Esta condición existe únicamente para permitir migrar y probar los contratos por servicio. No representa el diseño objetivo ni una implementación válida para un ambiente desplegable.

## Decisión pendiente de implementar

El Gateway será el único punto público de entrada para las APIs V2. Antes de enviar una petición al microservicio destino deberá:

1. Validar firma, emisor, expiración y revocación del access token emitido por Identidad.
2. Resolver las autoridades del usuario y aplicar autorización por ruta y operación.
3. Mantener públicas exclusivamente las rutas de registro, inicio de sesión, renovación, catálogo/consultas públicas, salud y documentación técnica.
4. Rechazar con `401` la ausencia, manipulación, vencimiento o revocación del token; y con `403` un token válido sin el rol requerido.
5. Propagar al servicio únicamente el contexto mínimo y confiable de identidad, correlación y autorización.

Los microservicios quedarán en red privada en el despliegue objetivo. Se definirá, antes de producción, si además verifican JWT de forma independiente o si aceptan exclusivamente tráfico autenticado por Gateway mediante una credencial interna. Ningún cliente externo podrá llamar sus puertos directamente.

## Criterios de cierre

- El Gateway enruta todas las APIs V2 publicadas.
- Cada ruta protegida exige token y rol conforme a su spec funcional.
- Existe una prueba automatizada para `401` sin token, token vencido y token revocado; y para `403` por rol insuficiente.
- Las rutas de Scalar, Swagger/OpenAPI, salud y los casos públicos definidos permanecen accesibles según ambiente.
- Los microservicios no se exponen directamente en el entorno objetivo.
- La guía de ejecución y las pruebas de carga usan Gateway cuando el criterio anterior esté cumplido.

## Regla transitoria local

Mientras este spec esté pendiente, las llamadas directas a los puertos de cada microservicio se permiten solo para desarrollo, pruebas de migración y carga local. Deben identificarse como temporales en scripts, colecciones y documentación.
