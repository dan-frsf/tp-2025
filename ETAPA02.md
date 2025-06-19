# TP - ETAPA 02

Objetivos:
- Ejecutar los servicios de spring boot en docker
- Ejecutar mensajes asincronicos.
- Configurar servidores
   - Postgresql
   - MongoDB
   - JMS
- Crear los servicios "gestion de hoteles, habitaciones y tarifas", y "gestion de reservas"   
- Crear una librería "common" de clases java compartidas.

## Gestion de Hoteles, Habitaciones y Tarifas
- Gestión de Hoteles: endpoints para crear, obtener, actualizary eliminar hoteles.
- Gestión de Habitaciones: endpoints para crear, obtener, actualizary eliminar habitaciones.
- Gestión de Tipos de Tarifas: endpoints para crear, obtener, actualizary eliminar tarifas.
- Generacion de mensajes async para compartir informacion de habitaciones y tarifas con otros servicios.

## Gestion de Reservas
- Busqueda de habitaciones disponibles
- Gestión de reserva: creación, consulta por usuario, actualización de estado, gestión del pago.

Detalle de la implementacion