# Fase 5 - Despliegue remoto (Railway + Vercel)

## Objetivo

Publicar backend en Railway y frontend en Vercel con conectividad completa.

## Alcance técnico

- Railway:
  - Definir estrategia de despliegue de los 4 servicios backend.
  - Configurar variables por servicio (Eureka, Gateway, Catalogue, Payments, PostgreSQL y Bonsai).
  - Publicar endpoints y validar salud.
- Vercel:
  - Configurar `VITE_API_BASE_URL` hacia Gateway remoto.
  - Validar build SPA y navegación.

## Cambios de código previstos

- Ajustes de configuración para entorno productivo.
- Archivos de soporte para despliegue (si aplica).

## Intervención requerida del equipo

- Proveer accesos del equipo a Railway y Vercel.
- Cargar secretos/credenciales en paneles cloud.
- Confirmar dominios/URLs finales.

## Evidencias a capturar

- URLs públicas activas.
- Prueba funcional remota de búsqueda y compra.
- Capturas de paneles de despliegue y logs de ejecución.

## Registro de cambios ejecutados

- [2026-02-24] Fase reasignada a despliegue remoto tras cierre de integración local.
