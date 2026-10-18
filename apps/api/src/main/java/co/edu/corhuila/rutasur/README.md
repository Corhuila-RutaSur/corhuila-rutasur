# Estructura backend V1

RutaSur usa arquitectura por capas en su primera versión:

```text
controller → service → repository → PostgreSQL
```

Los DTOs, entidades, mappers, configuración y excepciones son capas de apoyo. Consulta `docs/architecture/estructura-del-proyecto.md` para las reglas completas.
