# ETAPA 05 - Observabilidad con OpenTelemetry

Esta etapa implementa un stack completo de observabilidad para los microservicios usando OpenTelemetry, con integración de Loki (logs), Tempo (trazas), Prometheus (métricas) y Grafana (visualización).

## Arquitectura de Observabilidad

### Componentes del Stack

1. **OpenTelemetry Collector**: Recolecta y procesa telemetría de los microservicios
2. **Tempo**: Almacena y consulta trazas distribuidas
3. **Loki**: Agrega y consulta logs de aplicaciones y contenedores
4. **Prometheus**: Recolecta y almacena métricas de tiempo
5. **Grafana**: Dashboard unificado para visualización
6. **Promtail**: Recolecta logs de contenedores Docker

### Flujo de Datos

```
Spring Boot Apps → OpenTelemetry Collector → {Tempo, Loki, Prometheus} → Grafana
      ↓
  Docker Logs → Promtail → Loki → Grafana
```

## Configuración de Microservicios

### Dependencias Agregadas

Cada servicio Spring Boot incluye estas dependencias:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
```

### Configuración de Application Properties

Cada microservicio tiene configuración para exportar telemetría:

```properties
# Management and Metrics
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.endpoints.enabled-by-default=true
management.metrics.export.prometheus.enabled=true
management.tracing.sampling.probability=1.0

# OpenTelemetry Configuration
otel.service.name=${spring.application.name}
otel.exporter.otlp.endpoint=http://otel-collector:4318
otel.exporter.otlp.protocol=http/protobuf
otel.metrics.exporter=otlp
otel.logs.exporter=otlp
otel.traces.exporter=otlp
```

## Configuración del Stack de Observabilidad

### OpenTelemetry Collector

**Archivo**: `infra/otel/otel-collector-config.yaml`

- **Receivers**: OTLP gRPC (4317) y HTTP (4318)
- **Processors**: Limitación de memoria y procesamiento por lotes
- **Exporters**:
  - Trazas → Tempo
  - Métricas → Prometheus
  - Logs → Loki

### Tempo (Trazas Distribuidas)

**Archivo**: `infra/tempo/tempo-config.yaml`

- **Puerto**: 3200 (API), 4317/4318 (OTLP)
- **Almacenamiento**: Local en `/var/tempo/traces`
- **Receptores**: Jaeger, Zipkin, OTLP

### Loki (Agregación de Logs)

**Archivo**: `infra/loki/loki-config.yaml`

- **Puerto**: 3100
- **Almacenamiento**: Sistema de archivos local
- **Schema**: v11 con BoltDB shipper

### Prometheus (Métricas)

**Archivo**: `infra/prometheus/prometheus.yml`

- **Puerto**: 9090
- **Targets configurados**:
  - OpenTelemetry Collector (8889)
  - Todos los microservicios (*/actuator/prometheus)
  - Eureka Server (8761/actuator/prometheus)

### Grafana (Visualización)

**Archivo**: `infra/grafana/provisioning/`

- **Puerto**: 3000 (admin/admin)
- **Datasources**:
  - Prometheus (métricas)
  - Tempo (trazas)
  - Loki (logs)
- **Dashboards**: Spring Boot Microservices Dashboard

## Comandos de Despliegue

### Opción 1: Stack Completo

```bash
# Iniciar todo el stack junto (microservicios + observabilidad)
cd infra
docker compose -f docker-compose.full.yml up -d
```

### Opción 2: Stack Separado

```bash
# 1. Iniciar stack de observabilidad
cd infra
./start-observability.sh

# 2. Iniciar microservicios
docker compose -f docker-compose.yml up -d
```

### Opción 3: Solo Observabilidad

```bash
# Para desarrollo/testing del stack de observabilidad
cd infra
docker network create observability
docker compose -f observability-compose.yml up -d
```

## URLs de Acceso

Una vez desplegado el stack:

- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Loki**: http://localhost:3100
- **Tempo**: http://localhost:3200
- **Gateway (Microservicios)**: http://localhost:8080
- **Eureka Server**: http://localhost:8761

## Tipos de Telemetría Capturada

### 1. Métricas (Prometheus)

- **HTTP Requests**: Tasa de peticiones, latencia, códigos de respuesta
- **JVM**: Uso de memoria heap/non-heap, garbage collection
- **Database**: Pool de conexiones Hikari
- **Eureka**: Servicios registrados
- **Custom**: Métricas específicas de negocio

### 2. Trazas (Tempo)

- **Distributed Tracing**: Seguimiento de requests entre microservicios
- **HTTP Calls**: Peticiones entre user-svc ↔ reservas-svc via OpenFeign
- **Database Operations**: Consultas JPA/JDBC
- **Message Queue**: Operaciones RabbitMQ

### 3. Logs (Loki)

- **Application Logs**: Logs de Spring Boot con estructura JSON
- **Container Logs**: Logs de Docker containers
- **Correlation**: Logs correlacionados con trace IDs

## Dashboard de Grafana

El dashboard `Spring Boot Microservices Dashboard` incluye:

1. **HTTP Request Rate**: Tasa de peticiones por servicio
2. **Response Time (95th percentile)**: Latencia de respuesta
3. **JVM Memory Usage**: Uso de memoria heap por servicio
4. **Service Discovery**: Servicios registrados en Eureka
5. **Database Connections**: Pool de conexiones activas/idle

## Ejemplos de Uso

### 1. Rastrear una Reserva

1. Crear reserva: `POST /reservas/reservas`
2. En Grafana → Explore → Tempo
3. Buscar por service: `reservas-svc`
4. Ver trace completo: reservas-svc → user-svc (validación)

### 2. Monitorear Carga

1. En Grafana → Dashboards → Spring Boot Microservices
2. Observar métricas en tiempo real
3. Correlacionar con logs en panel de Loki

### 3. Debug de Performance

1. Identificar servicio lento en dashboard
2. Usar Tempo para encontrar trace específico
3. Analizar spans para identificar bottleneck
4. Revisar logs correlacionados en Loki

## Comandos de Troubleshooting

```bash
# Ver logs del collector
docker logs otel-collector

# Verificar configuración de Prometheus
curl http://localhost:9090/api/v1/label/__name__/values

# Verificar métricas de microservicio
curl http://localhost:8080/users/actuator/prometheus

# Verificar conectividad Tempo
curl http://localhost:3200/ready

# Verificar Loki
curl http://localhost:3100/ready
```

## Consideraciones de Producción

1. **Volumen de Datos**: Configurar retención adecuada en Prometheus y Loki
2. **Sampling**: Ajustar `management.tracing.sampling.probability` según carga
3. **Seguridad**: Configurar autenticación y HTTPS
4. **Escalabilidad**: Usar Tempo/Loki distribuidos para alta carga
5. **Almacenamiento**: Configurar volúmenes persistentes apropiados

## Archivos de Configuración

```
infra/
├── observability-compose.yml          # Stack de observabilidad
├── docker-compose.full.yml           # Stack completo
├── start-observability.sh            # Script de inicio
├── otel/
│   └── otel-collector-config.yaml    # Configuración OpenTelemetry
├── tempo/
│   └── tempo-config.yaml             # Configuración Tempo
├── loki/
│   └── loki-config.yaml              # Configuración Loki
├── prometheus/
│   └── prometheus.yml                # Configuración Prometheus
├── promtail/
│   └── promtail-config.yaml          # Configuración Promtail
└── grafana/
    └── provisioning/
        ├── datasources/
        │   └── datasources.yaml      # Datasources automáticos
        └── dashboards/
            ├── dashboards.yaml       # Configuración dashboards
            └── spring-boot-dashboard.json  # Dashboard microservicios
```

Este setup proporciona observabilidad completa para el stack de microservicios, permitiendo monitoreo proactivo, debugging eficiente y análisis de performance en tiempo real.