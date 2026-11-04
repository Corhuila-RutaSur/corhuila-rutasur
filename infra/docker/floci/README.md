# Mensajería local con Floci

Floci emula SNS y SQS para RutaSur V2. Cada hecho se publica en un tópico; cada consumidor recibe su propia cola. Esto evita que un consumidor robe mensajes de otro y permite reintentos aislados.

La guía completa, incluido PostgreSQL local, orden de microservicios, variables y diagnóstico, está en [Ejecución local V2](../../../docs/operations/ejecucion-local-v2.md).

1. Levanta Floci: `docker compose -f docker-compose.yml up -d`.
2. Desde Git Bash ejecuta: `bash inicializar-recursos.sh`.

El script Bash usa la imagen temporal `amazon/aws-cli:2.39.3`; no requiere instalar AWS CLI en Windows. La alternativa PowerShell `inicializar-recursos.ps1` requiere AWS CLI instalado.

El script es idempotente para el desarrollo local: crea únicamente recursos ausentes y no repite una suscripción SNS/SQS existente. Los tópicos y sus colas están documentados en `docs/events/README.md`.
