# Manual Testing Commands for User Validation via Microservice Communication

## Prerequisites
1. Start all services with Docker Compose:
```bash
docker compose -f infra/docker-compose.yml up -d
```

2. Verify services are registered in Eureka:
```bash
curl http://localhost:8761/eureka/apps
```

## Test Scenarios

### 1. Test Valid Reservation with User Validation (Happy Path)
```bash
curl -X POST http://localhost:8080/reservas/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "idHabitacion": "1",
    "hotelId": 1,
    "createdAt": "2025-06-25T10:00:00Z",
    "checkIn": "2025-06-26T15:00:00Z",
    "checkOut": "2025-06-28T11:00:00Z",
    "precioNoche": 150.0,
    "precioTotal": 300.0,
    "status": "PENDING",
    "huesped": {
      "idUsuario": "1",
      "nombreApellido": "Juan Pérez",
      "email": "juan.perez@email.com"
    }
  }'
```

### 2. Test Invalid User ID (User doesn't exist)
```bash
curl -X POST http://localhost:8080/reservas/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "idHabitacion": "1",
    "hotelId": 1,
    "huesped": {
      "idUsuario": "999",
      "nombreApellido": "Usuario Inexistente",
      "email": "noexiste@email.com"
    }
  }'
```

### 3. Test Invalid User ID Format
```bash
curl -X POST http://localhost:8080/reservas/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "idHabitacion": "1",
    "hotelId": 1,
    "huesped": {
      "idUsuario": "invalid_id",
      "nombreApellido": "Usuario con ID inválido",
      "email": "invalid@email.com"
    }
  }'
```

### 4. Test Reservation without User (Optional validation)
```bash
curl -X POST http://localhost:8080/reservas/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "idHabitacion": "1",
    "hotelId": 1,
    "createdAt": "2025-06-25T10:00:00Z",
    "checkIn": "2025-06-26T15:00:00Z",
    "checkOut": "2025-06-28T11:00:00Z",
    "precioNoche": 150.0,
    "precioTotal": 300.0,
    "status": "PENDING"
  }'
```

## Verify Service Communication

### Check User Service Communication
```bash
# Direct call to user service
curl http://localhost:8081/huespedes/1
curl http://localhost:8081/usuarios/1

# Through gateway
curl http://localhost:8080/users/huespedes/1
curl http://localhost:8080/users/usuarios/1
```

### Check Load Balancing with User Service Scaling
```bash
# Scale user service to 2 instances
docker compose -f infra/docker-compose.yml up -d --scale user-svc=2

# Make multiple requests to see load balancing
for i in {1..10}; do
  curl http://localhost:8080/users/info
  echo ""
done
```

### Verify Feign Client Load Balancing
```bash
# Create multiple reservations to see different user-svc instances being called
for i in {1..5}; do
  curl -X POST http://localhost:8080/reservas/reservas \
    -H "Content-Type: application/json" \
    -d '{
      "idHabitacion": "1",
      "hotelId": 1,
      "huesped": {
        "idUsuario": "1",
        "nombreApellido": "Test User"
      }
    }'
  echo ""
done
```

## Expected Responses

### Success Response (201 Created)
```json
{
  "_id": "generated-id",
  "idHabitacion": "1",
  "hotelId": 1,
  "huesped": {
    "idUsuario": "1",
    "nombreApellido": "Juan Pérez",
    "email": "juan.perez@email.com"
  },
  "createdAt": "2025-06-25T10:00:00Z",
  ...
}
```

### Error Responses (500 Internal Server Error)
```json
{
  "error": "Error validando huésped: Huésped no encontrado con ID: 999"
}
```

```json
{
  "error": "ID de huésped inválido: invalid_id"
}
```

```json
{
  "error": "Error validando huésped: Service unavailable"
}
```

## Monitoring Commands

### Check Eureka Dashboard
```bash
open http://localhost:8761
```

### Check Service Info Endpoints
```bash
curl http://localhost:8081/info  # User service
curl http://localhost:8082/info  # Reservas service  
curl http://localhost:8083/info  # Gestion service
```

### Check Service Logs
```bash
docker compose -f infra/docker-compose.yml logs -f reservas-svc
docker compose -f infra/docker-compose.yml logs -f user-svc
docker compose -f infra/docker-compose.yml logs -f gestion-svc
```