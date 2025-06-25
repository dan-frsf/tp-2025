# ETAPA 04 - Implementación de Eureka Server y Client

## Resumen de la Etapa

- En esta etapa se implementó la funcionalidad de service discovery utilizando Netflix Eureka Server y se configuraron todos los microservicios como clientes de Eureka. 
- Se configuró Docker Compose para el despliegue de Eureka
- Se hicieron ajustes para ejecutar los servicios en multiples contenedores simultaneamente (escalado horizontal)
- Se agregó un endpoint de información en cada servicio para poder ver en que instancia esta ejecutando el request

## Cambios Realizados

### 1. Configuración de Eureka Server

#### Corrección de Hostname en Eureka Server
- **Archivo modificado**: `common/dan-eureka-server/src/main/resources/application.properties`
- **Cambio**: Se actualizó el hostname de `localhost` a `dan-eureka-server` para compatibilidad con Docker
- **Configuración**:
  ```properties
  eureka.client.serviceUrl.defaultZone=http://dan-eureka-server:8761/eureka/
  ```

#### Corrección de Anotación Deprecada en Gateway
- **Archivo modificado**: `common/dan-spring-gateway/src/main/java/com/example/demo/DanSpringGateway.java`
- **Cambio**: Se removió la anotación `@EnableEurekaClient` que está deprecada en versiones modernas de Spring Cloud
- **Razón**: Eureka Client se auto-configura cuando la dependencia está presente

### 2. Configuración de Puertos y Placeholders

#### Solución de Problema con Placeholders
- **Objetivo**: Configurar el numero de puerto de cada servicio desde propiedades externas
- **Acción**: Se agregó la propiedad `server.port` en todos los archivos de configuración

#### Configuración de Puertos por Servicio
Todos los servicios fueron configurados para usar el puerto **8080** por defecto:

**user-svc**:
- `application.properties`: `server.port=8080`
- `application-eureka.properties`: `server.port=8080`

**reservas-svc**:
- `application.properties`: `server.port=8080`
- `application-eureka.properties`: `server.port=8080`

**gestion-svc**:
- `application.properties`: `server.port=8080`
- `application-eureka.properties`: `server.port=8080`

**dan-spring-gateway**:
- `application.properties`: `server.port=8080`
- `application-eureka.properties`: Se agregó `eureka.instance.instance-id=${spring.application.name}:${server.port}`

#### Configuración de Instance ID Único para Escalado
Para permitir que cada instancia escalada tenga un identificador único en Eureka, se configuróla propiedad de eureka `instance-id` en todos los servicios:

**Configuración actualizada**:
```properties
eureka.instance.instance-id=${HOSTNAME:${spring.application.name}}:${server.port}
```

**Archivos modificados**:
- `services/user-svc/src/main/resources/application-eureka.properties`
- `services/reservas-svc/src/main/resources/application-eureka.properties`
- `services/gestion-svc/src/main/resources/application-eureka.properties`

**Funcionamiento**:
- `${HOSTNAME}` obtiene el nombre del contenedor (ej: `gestion-svc-1`, `gestion-svc-2`)
- Si `HOSTNAME` no está disponible, usa `${spring.application.name}` como fallback
- Cada instancia escalada tendrá IDs únicos como `gestion-svc-1:8080`, `gestion-svc-2:8080`

### 3. Configuración de Docker Compose

#### Mapeo de Puertos
Se actualizó el archivo `infra/docker-compose.yml` con la siguiente configuración:

```yaml
user-svc:
  ports:
    - "8081:8080"  # Host:Container
  environment:
    SERVER_PORT: 8080

reservas-svc:
  ports:
    - "8082:8080"  # Host:Container
  environment:
    SERVER_PORT: 8080

gestion-svc:
  ports:
    - "8083:8080"  # Host:Container
  environment:
    SERVER_PORT: 8080

dan-spring-gateway:
  ports:
    - "8080:8080"  # Host:Container
  environment:
    SERVER_PORT: 8080
```

#### Configuración de Eureka en Docker
- Todos los servicios configurados con `SPRING_PROFILES_ACTIVE: eureka`
- Dependencias correctas establecidas para garantizar que Eureka Server inicie primero
- Configuración de `container_name` removida para permitir escalado horizontal

### 4. Actualización de Rutas del Gateway

#### Rutas Actualizadas
Se corrigieron las rutas en `application.properties` del gateway:

```properties
spring.cloud.gateway.server.webmvc.routes[0].uri=http://user-svc:8080
spring.cloud.gateway.server.webmvc.routes[1].uri=http://reservas-svc:8080
spring.cloud.gateway.server.webmvc.routes[2].uri=http://gestion-svc:8080
```

### 5. Implementación de InfoController

Se creó un nuevo controlador `InfoController` en cada servicio para proporcionar información del servidor:

#### Funcionalidad del Endpoint
- **Ruta**: `GET /info`
- **Respuesta**: Objeto JSON con información del servidor

#### Ejemplo de Respuesta
```json
{
  "time": "2025-06-25T06:55:18.123",
  "timestamp": 1719315318123,
  "server": "gestion-svc-1",
  "ip": "172.18.0.5"
}
```

**Nota**: El campo `server` ahora muestra el hostname único del contenedor (ej: `gestion-svc-1`, `gestion-svc-2`) cuando los servicios están escalados, facilitando la identificación de cada instancia específica.

#### Archivos Creados
- `services/user-svc/src/main/java/edu/utn/frsf/isi/dan/user/controller/InfoController.java`
- `services/reservas-svc/src/main/java/edu/utn/frsf/isi/dan/reservas_svc/controller/InfoController.java`
- `services/gestion-svc/src/main/java/edu/utn/frsf/isi/dan/gestion/controller/InfoController.java`

#### Características del InfoController
- Utiliza `@Value("${HOSTNAME:${spring.application.name}}")` para obtener el hostname del contenedor
- Si `HOSTNAME` no está disponible, usa `spring.application.name` como fallback
- Obtiene la IP del contenedor usando `InetAddress.getLocalHost()`
- Proporciona timestamp actual en formato ISO y epoch
- Manejo de excepciones para casos donde no se puede obtener la IP

## Arquitectura Resultante

### Service Discovery
- **Eureka Server**: Ejecutándose en puerto 8761
- **Eureka Clients**: Todos los servicios registrados automáticamente
- **Load Balancing**: Gateway utiliza `lb://service-name` para descubrimiento automático

### Mapeo de Puertos
| Servicio | Puerto Host | Puerto Container | Acceso |
|----------|-------------|------------------|--------|
| Gateway | 8080 | 8080 | http://localhost:8080 |
| User Service | 8081 | 8080 | http://localhost:8081 |
| Reservas Service | 8082 | 8080 | http://localhost:8082 |
| Gestion Service | 8083 | 8080 | http://localhost:8083 |
| Eureka Server | 8761 | 8761 | http://localhost:8761 |

### Endpoints de Información
- `GET http://localhost:8081/info` - Información del servicio de usuarios
- `GET http://localhost:8082/info` - Información del servicio de reservas
- `GET http://localhost:8083/info` - Información del servicio de gestión

## Comandos para Ejecutar

### Construcción y Ejecución
```bash
# Construir todos los servicios
docker compose -f infra/docker-compose.yml build

# Ejecutar todos los servicios
docker compose -f infra/docker-compose.yml up -d

# Verificar estado de los servicios
docker compose -f infra/docker-compose.yml ps
```

### Escalado de Servicios
Para permitir el escalado horizontal de servicios, se removió la propiedad `container_name` del servicio `gestion-svc`:

```bash
# Escalar gestion-svc a 2 réplicas
docker compose -f infra/docker-compose.yml up -d --scale gestion-svc=2

# Escalar gestion-svc a 3 réplicas
docker compose -f infra/docker-compose.yml up -d --scale gestion-svc=3

# Verificar las instancias escaladas
docker compose -f infra/docker-compose.yml ps gestion-svc

# Ver los registros en Eureka para verificar las instancias únicas
curl http://localhost:8761/eureka/apps/GESTION-SVC
```

**Notas importantes sobre escalado**:
- Para que un servicio sea escalable con Docker Compose, no debe tener definida la propiedad `container_name` dado q si fijamos esa propiedad todos los contenedores tendrán el mismo nombre y Docker requiere nombres únicos para cada contenedor
- Los contenedores escalados recibirán nombres automáticos como `gestion-svc-1`, `gestion-svc-2`, etc.
- Cada instancia se registrará en Eureka con un `instance-id` único basado en el hostname del contenedor
- El gateway automáticamente distribuirá la carga entre todas las instancias registradas

### Verificación de Funcionalidad
```bash
# Verificar registro en Eureka
curl http://localhost:8761/eureka/apps

# Probar endpoints de información
curl http://localhost:8081/info
curl http://localhost:8082/info
curl http://localhost:8083/info
```

## Beneficios Obtenidos

1. **Service Discovery**: Los servicios se registran automáticamente en Eureka
2. **Load Balancing**: El gateway puede distribuir carga entre instancias
3. **Configuración Simplificada**: Uso de nombres de servicio en lugar de IPs
4. **Monitoreo**: Endpoints de información para debugging y monitoreo
5. **Escalabilidad**: Facilita el escalado horizontal de servicios con comando `--scale`
6. **Containerización**: Configuración optimizada para despliegue en Docker
7. **Alta Disponibilidad**: Múltiples instancias del mismo servicio registradas en Eureka

## 6. Comunicación entre Microservicios con OpenFeign

### Escenario Implementado: Validación de Reservas
Se implementó comunicación sincrónica entre microservicios usando OpenFeign en el servicio `reservas-svc` para validar datos al crear una reserva.

#### Dependencias Agregadas
**Archivo modificado**: `services/reservas-svc/pom.xml`
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### Habilitación de Feign Clients
**Archivo modificado**: `ReservasSvcApplication.java`
```java
@SpringBootApplication
@EnableFeignClients
public class ReservasSvcApplication {
```

#### Clientes Feign Creados

**UserServiceClient**: Para validar huéspedes
```java
@FeignClient(name = "user-svc")
public interface UserServiceClient {
    @GetMapping("/huespedes/{id}")
    UserDto getHuesped(@PathVariable("id") Long id);
}
```

**GestionServiceClient**: Para validar hoteles y habitaciones
```java
@FeignClient(name = "gestion-svc")
public interface GestionServiceClient {
    @GetMapping("/hoteles/{id}")
    HotelDto getHotel(@PathVariable("id") Long id);
    
    @GetMapping("/habitaciones/{id}")
    HabitacionDto getHabitacion(@PathVariable("id") Long id);
    
    @GetMapping("/habitaciones/{id}/disponible")
    Boolean isHabitacionDisponible(@PathVariable("id") Long id);
}
```

#### DTOs de Comunicación
Se crearon DTOs específicos para la comunicación entre servicios:
- `UserDto`: Datos básicos del huésped
- `HotelDto`: Información del hotel
- `HabitacionDto`: Datos de la habitación

#### Validación Implementada
El método `validateReservation()` en `ReservaService` realiza las siguientes validaciones:

1. **Validación de Huésped**: Verifica que el huésped existe en `user-svc`
2. **Validación de Hotel**: Confirma que el hotel existe en `gestion-svc`
3. **Validación de Habitación**: 
   - Verifica que la habitación existe
   - Confirma que pertenece al hotel especificado
   - Valida que está disponible para reserva

#### Beneficios de la Implementación
- **Service Discovery**: Los clientes Feign usan automáticamente Eureka para encontrar servicios
- **Load Balancing**: Distribución automática de carga entre instancias escaladas
- **Circuit Breaker**: Protección contra fallos de servicios dependientes
- **Validación Robusta**: Garantiza integridad de datos en reservas

## Notas Técnicas

- Se utilizó Spring Cloud Gateway MVC en lugar de WebFlux para mayor compatibilidad
- Los servicios mantienen compatibilidad con ejecución local (sin Eureka) y con Docker
- La configuración permite sobrescribir puertos mediante variables de entorno
- Eureka Server configurado con self-preservation deshabilitado para desarrollo
- OpenFeign proporciona load balancing automático y circuit breaker out-of-the-box