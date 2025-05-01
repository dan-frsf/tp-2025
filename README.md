# tp-2025
Trabajo práctico DAN 2025

## Herramientas necesarias

### IDE
- VSCode: https://code.visualstudio.com/ 
- Brackets *IDE para frontend* https://brackets.io/
- Phoenix Code *IDE para frontend* https://phcode.dev/

### Entornos de ejecucion backend
- Java 21: https://adoptium.net/es/temurin/releases/?version=21&package=jdk 
- Docker Desktop: https://docs.docker.com/desktop/setup/install/windows-install/

### Entornos de ejecucion frontend
- Node 22: https://nodejs.org/es/download 

### Gestión de código
- Git: https://git-scm.com/
- Usuario en Github: https://github.com/ 


# Organizacion de directorios

Este proyecto es un MONOREPO (es decir tendremos todos los elementos necesarios para ejecutar la aplicacion distribuida en un único repositorio)

## Infra
Contiene los archivos docker para iniciar los servicios de infraestructura (gateway - bases de datos - etc)
- mysql
- phpmyadmin

### Levantar y bajar MySQL & phpmyadmin

Para levantar ambos servicios simplemente
```
docker compose -f infra/docker-compose.yml up -d mysql phpmyadmin
````

Para bajar ***todos*** los servicios de docker-compose
```
docker compose -f infra/docker-compose.yml down
```
Para bajar y borrar los datos de ***todos*** los servicios de docker-compose
```
docker compose -f infra/docker-compose.yml down -v
```

## Services
Tendremos los microservicios spring boot
- user-svc

# Ejecucion
Para levantar los servicios spring boot
```
cd service\user-svc
./mvnw spring-boot:run -DskipTests
```

## Enunciado 

### TP Etapa 01
- Tareas necesarias para completar el servicio user-svc [ETAPA01.md](./ETAPA01.md).