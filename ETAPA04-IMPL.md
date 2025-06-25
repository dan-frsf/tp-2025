# ETAPA 04 - Implementación de Comunicación entre Microservicios

## Resumen de la Implementación

En esta etapa se implementó la comunicación entre microservicios utilizando **OpenFeign** para crear un sistema de validación de reservas que demuestra cómo los servicios pueden intercambiar información de manera sincrónica a través de la red.

## Escenario Implementado: Validación de Reservas

### Descripción del Problema
Cuando un cliente intenta crear una reserva en el sistema hotelero, es necesario validar que el huésped (usuario) existe en el sistema de usuarios.

La información de hoteles y habitaciones se mantiene actualizada en `reservas-svc` a través de mensajería asíncrona (RabbitMQ) desde `gestion-svc`, por lo que no requiere validación sincrónica.

Sin comunicación entre microservicios para validar usuarios, el servicio de reservas tendría que duplicar datos de usuarios o confiar en información no verificada, lo que llevaría a inconsistencias y posibles reservas con usuarios inexistentes.

### Solución Implementada
Se implementó un sistema donde el **servicio de reservas** (`reservas-svc`) consulta al servicio de usuarios para validar la existencia del huésped antes de crear una reserva:

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐
│   reservas-svc  │ ──────────────> │    user-svc     │
│                 │                 │                 │
│ 1. Valida user  │                 │ Retorna datos   │
│ 2. Crea reserva │                 │ del huésped     │
│                 │                 │                 │
└─────────────────┘                 └─────────────────┘
         ▲                                   
         │ RabbitMQ (async)                        
         │                                   
┌─────────────────┐                         
│  gestion-svc    │                         
│                 │                         
│ Envía eventos   │                         
│ de hoteles y    │                         
│ habitaciones    │                         
└─────────────────┘                         
```

**Nota**: Los datos de hoteles y habitaciones se sincronizan de forma asíncrona mediante eventos de RabbitMQ, manteniendo una copia local en MongoDB para consultas rápidas.

## ¿Qué es OpenFeign y Cómo Funciona?

### Introducción a OpenFeign
**OpenFeign** es una biblioteca de Spring Cloud que simplifica la comunicación HTTP entre microservicios. Permite definir clientes REST de manera declarativa usando anotaciones, similar a como se definen los controladores REST.

### Características Principales
- **Declarativo**: Se define la interfaz, Feign genera la implementación
- **Integración con Spring**: Soporte nativo para anotaciones de Spring MVC
- **Service Discovery**: Integración automática with Eureka
- **Load Balancing**: Distribución automática de carga
- **Circuit Breaker**: Patrones de resilencia integrados
- **Serialización**: Manejo automático de JSON

### Cómo Funciona Internamente

1. **Definición de Cliente**:
```java
@FeignClient(name = "user-svc")
public interface UserServiceClient {
    @GetMapping("/huespedes/{id}")
    UserDto getHuesped(@PathVariable("id") Long id);
}
```

2. **Resolución de Servicio**:
   - Feign consulta Eureka para encontrar instancias de `user-svc`
   - Selecciona una instancia usando load balancing (round-robin por defecto)

3. **Construcción de Request**:
   - Convierte la llamada del método en HTTP GET request
   - Serializa parámetros y headers
   - Construye la URL: `http://user-svc-instance/huespedes/1`

4. **Ejecución y Respuesta**:
   - Ejecuta la petición HTTP
   - Deserializa la respuesta JSON a `UserDto`
   - Maneja errores y timeouts

## Implementación Detallada

### 1. Configuración del Proyecto

#### Dependencias Agregadas
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### Habilitación de Feign
```java
@SpringBootApplication
@EnableFeignClients  // ← Habilita Feign clients
public class ReservasSvcApplication {
```

### 2. Definición de Clientes Feign

#### Cliente para Servicio de Usuarios
```java
@FeignClient(name = "user-svc")  // ← Nombre del servicio en Eureka
public interface UserServiceClient {
    
    @GetMapping("/huespedes/{id}")
    UserDto getHuesped(@PathVariable("id") Long id);
}
```

**Explicación**:
- `@FeignClient(name = "user-svc")`: Indica que este cliente se conecta al servicio registrado como "user-svc" en Eureka
- `@GetMapping`: Define que es una petición HTTP GET
- `@PathVariable`: Mapea el parámetro del método a la variable de la URL

**Nota**: Solo se implementó el cliente para el servicio de usuarios. Los datos de hoteles y habitaciones se obtienen directamente de la base de datos local (MongoDB) que se mantiene sincronizada via eventos de RabbitMQ.

### 3. DTOs de Comunicación

El DTO `UserDto` define la estructura de datos del usuario que se intercambia con el servicio de usuarios:

```java
public class UserDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;
    // getters y setters...
}
```

**Propósito**:
- Desacoplar la estructura interna de los modelos de usuarios de la comunicación externa
- Versioning: Permite cambios en modelos internos sin romper la comunicación
- Seguridad: Solo expone campos necesarios del usuario
- Consistencia: Garantiza formato estándar de datos de usuario

### 4. Lógica de Validación

```java
@Service
public class ReservaService {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    public Reserva save(Reserva reserva) {
        validateReservation(reserva);  // ← Validación antes de guardar
        return reservaRepository.save(reserva);
    }
    
    private void validateReservation(Reserva reserva) {
        // Validar que el huésped existe en user-svc
        if (reserva.getHuesped() != null && reserva.getHuesped().getIdUsuario() != null) {
            try {
                Long userId = Long.parseLong(reserva.getHuesped().getIdUsuario());
                UserDto user = userServiceClient.getHuesped(userId); // ← Llamada a user-svc
                if (user == null) {
                    throw new RuntimeException("Huésped no encontrado con ID: " + userId);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("ID de huésped inválido: " + reserva.getHuesped().getIdUsuario());
            } catch (Exception e) {
                throw new RuntimeException("Error validando huésped: " + e.getMessage());
            }
        }
        
        // Nota: La validación de hotel y habitación se realiza contra la base de datos local 
        // que se mantiene sincronizada mediante eventos de RabbitMQ desde gestion-svc
    }
}
```

## Beneficios de la Implementación

### 1. **Integridad de Datos de Usuario**
- Garantiza que las reservas solo se crean con usuarios válidos
- Previene referencias a usuarios inexistentes
- Mantiene consistencia de datos de usuario entre servicios

### 2. **Separación de Responsabilidades**
- Cada servicio mantiene su dominio específico
- `user-svc`: Fuente única de verdad para datos de usuarios
- `gestion-svc`: Gestión de hoteles y habitaciones + eventos via RabbitMQ
- `reservas-svc`: Gestión de reservas con validación de usuarios + datos locales sincronizados

### 3. **Arquitectura Híbrida**
- **Sincrónica**: Validación de usuarios via HTTP/REST con Feign
- **Asíncrona**: Sincronización de hoteles/habitaciones via RabbitMQ
- Mejor performance para consultas frecuentes (datos locales)
- Consistencia eventual para cambios de hoteles/habitaciones

### 4. **Escalabilidad**
- Service discovery automático via Eureka
- Load balancing entre instancias de user-svc
- Fault tolerance con circuit breakers
- Reducción de latencia con datos locales

### 5. **Mantenibilidad**
- Código declarativo y fácil de entender
- Patrón híbrido claro (sync para usuarios, async para hoteles)
- Fácil testing con mocks

## Testing de la Implementación

### Estrategia de Testing

#### 1. **Unit Tests** (`ReservaServiceTest.java`)
```java
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {
    
    @Mock
    private UserServiceClient userServiceClient;
    
    @Mock  
    private GestionServiceClient gestionServiceClient;
    
    @InjectMocks
    private ReservaService reservaService;
    
    @Test
    void testSave_ValidReservation_Success() {
        // Arrange
        when(userServiceClient.getHuesped(1L)).thenReturn(validUser);
        when(gestionServiceClient.getHotel(1L)).thenReturn(validHotel);
        when(gestionServiceClient.getHabitacion(1L)).thenReturn(validHabitacion);
        when(gestionServiceClient.isHabitacionDisponible(1L)).thenReturn(true);
        
        // Act
        Reserva result = reservaService.save(validReserva);
        
        // Assert
        assertNotNull(result);
        verify(userServiceClient).getHuesped(1L);
        verify(gestionServiceClient).getHotel(1L);
    }
}
```

**Ventajas**:
- Ejecución rápida (sin red)
- Control total sobre respuestas
- Testing de edge cases

#### 2. **Integration Tests con WireMock** (`FeignClientIntegrationTest.java`)
```java
@SpringBootTest
class FeignClientIntegrationTest {
    
    private WireMockServer userServiceMock;
    
    @BeforeEach
    void setUp() {
        userServiceMock = new WireMockServer(9001);
        userServiceMock.start();
        
        // Configurar respuestas mock
        userServiceMock.stubFor(get(urlEqualTo("/huespedes/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"nombre\": \"Juan\"}")));
    }
    
    @Test
    void testUserServiceClient_Success() {
        UserDto result = userServiceClient.getHuesped(1L);
        assertEquals("Juan", result.getNombre());
    }
}
```

**Ventajas**:
- Testea serialización/deserialización real
- Simula condiciones de red
- Verifica configuración de Feign

#### 3. **Manual Testing**

**Comandos de prueba**:
```bash
# Reserva válida
curl -X POST http://localhost:8080/reservas/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "idHabitacion": "1",
    "hotelId": 1,
    "huesped": {
      "idUsuario": "1",
      "nombreApellido": "Juan Pérez"
    }
  }'

# Test de escalado
docker compose up -d --scale gestion-svc=2
curl http://localhost:8080/gestion/info  # Ver diferentes instancias
```

#### 4. **Escenarios de Testing**

| Escenario | Resultado Esperado | Propósito |
|-----------|-------------------|-----------|
| Reserva válida | 201 Created | Happy path |
| Usuario inexistente | 500 + error message | Validación user-svc |
| ID de usuario inválido | 500 + error message | Validación formato |
| Servicio user-svc caído | 500 + timeout | Resilencia |
| Usuario nulo | 201 Created | Reserva sin validación (opcional) |

## Patrones y Mejores Prácticas Implementadas

### 1. **Circuit Breaker Pattern**
- Feign incluye circuit breaker por defecto
- Previene cascading failures
- Fallback automático en caso de fallas

### 2. **Service Discovery**
- Registro automático en Eureka
- Resolución dinámica de servicios
- Health checks automáticos

### 3. **Load Balancing**
- Round-robin por defecto
- Distribución automática entre instancias
- Failover en caso de instancias caídas

### 4. **Error Handling**
- Manejo centralizado de excepciones
- Mensajes descriptivos para debugging
- Logging de errores de comunicación

### 5. **Data Transfer Objects**
- Contratos estables entre servicios
- Versionado de APIs
- Seguridad (solo datos necesarios)

## Consideraciones de Producción

### 1. **Timeouts y Retries**
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
      user-svc:
        readTimeout: 15000  # Timeout específico para user-svc
```

### 2. **Circuit Breaker Configuration**
```yaml
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 10000
      circuitBreaker:
        requestVolumeThreshold: 20
        errorThresholdPercentage: 50
```

### 3. **Monitoring y Observabilidad**
- Métricas de latencia de llamadas
- Dashboards de health de servicios
- Alertas de circuit breaker abierto
- Tracing distribuido

### 4. **Security**
- Autenticación entre servicios (JWT, OAuth2)
- Encriptación de comunicación (HTTPS/TLS)
- Rate limiting
- Validación de input

## Conclusiones

La implementación de comunicación entre microservicios con OpenFeign demuestra:

1. **Simplicidad**: Definición declarativa de clientes REST
2. **Robustez**: Patrones de resilencia integrados
3. **Escalabilidad**: Service discovery y load balancing automático
4. **Testabilidad**: Múltiples niveles de testing disponibles
5. **Mantenibilidad**: Código limpio y fácil de entender

Esta implementación sirve como base para futuras comunicaciones entre servicios y establece patrones consistentes para el desarrollo de microservicios en el proyecto.

## Próximos Pasos

1. **Implementar circuit breaker personalizado**
2. **Agregar métricas y monitoring**
3. **Implementar autenticación entre servicios**
4. **Crear más clientes Feign para otras comunicaciones**
5. **Implementar async communication con mensajería**