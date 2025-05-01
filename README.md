# tp-2025
Trabajo práctico DAN 2025


# Organizacion de directorios

Este proyecto es un MONOREPO (es decir tendremos todos los elementos necesarios para ejecutar la aplicacion distribuida en un único repositorio)

## Infra
Contiene los archivos docker para iniciar los servicios de infraestructura (gateway - bases de datos - etc)

## Services
Tendremos los microservicios spring boot


# Ejecucion
Para levantar los servicios de infra

### MySQL y phpmyadmin
```
docker compose -f infra/docker-compose.yml up -d mysql phpmyadmin
````

```
docker compose -f infra/docker-compose.yml down -v
```