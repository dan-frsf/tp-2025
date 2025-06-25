# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a microservices-based hotel management system built with Spring Boot 3.5.0 and Java 21. The project follows a monorepo structure with multiple services, shared libraries, and infrastructure components.

## Development Commands

### Build Commands
```bash
# Build all modules from root
./mvnw clean compile

# Build specific service
cd services/user-svc && ./mvnw clean compile
cd services/gestion-svc && ./mvnw clean compile
cd services/reservas-svc && ./mvnw clean compile

# Build with Docker
docker compose -f infra/docker-compose.yml build
```

### Running Services
```bash
# Run individual service locally (default profile)
cd services/user-svc && ./mvnw spring-boot:run -DskipTests
cd services/gestion-svc && ./mvnw spring-boot:run -DskipTests
cd services/reservas-svc && ./mvnw spring-boot:run -DskipTests
cd common/dan-eureka-server && ./mvnw spring-boot:run -DskipTests

# Run with Eureka service discovery (eureka profile)
cd common/dan-eureka-server && ./mvnw spring-boot:run -DskipTests
cd services/user-svc && ./mvnw spring-boot:run -DskipTests -Dspring.profiles.active=eureka
cd services/gestion-svc && ./mvnw spring-boot:run -DskipTests -Dspring.profiles.active=eureka
cd services/reservas-svc && ./mvnw spring-boot:run -DskipTests -Dspring.profiles.active=eureka
cd common/dan-spring-gateway && ./mvnw spring-boot:run -DskipTests -Dspring.profiles.active=eureka

# Run with Docker Compose (all services)
docker compose -f infra/docker-compose.yml up -d
```

### Testing
```bash
# Run tests for specific service
cd services/user-svc && ./mvnw test
cd services/gestion-svc && ./mvnw test
cd services/reservas-svc && ./mvnw test

# Run all tests from root
./mvnw test
```

### Infrastructure Management
```bash
# Start databases and discovery server only
docker compose -f infra/docker-compose.yml up -d mysql phpmyadmin postgres pgadmin mongodb mongo-express rabbitmq dan-eureka-server

# Stop all services
docker compose -f infra/docker-compose.yml down

# Stop and remove volumes (data loss)
docker compose -f infra/docker-compose.yml down -v
```

## Architecture

### Service Structure
- **user-svc** (port 8081): User management service using MySQL
- **gestion-svc** (port 8083): Hotel and room management using PostgreSQL
- **reservas-svc** (port 8082): Booking management using MongoDB
- **dan-spring-gateway** (port 8080): API Gateway using Spring Cloud Gateway MVC
- **dan-eureka-server** (port 8761): Service discovery server using Netflix Eureka

### Shared Components
- **dan-common-lib**: Shared DTOs and events for inter-service communication
- **common/**: Shared libraries, gateway implementation, and service discovery

### Databases
- **MySQL** (port 3306): User service data with PHPMyAdmin (port 6080)
- **PostgreSQL** (port 5432): Hotel management data with pgAdmin (port 6081)
- **MongoDB** (port 27017): Reservations data with Mongo Express (port 6091)
- **RabbitMQ** (port 5672): Message broker with management console (port 15672)

### Communication Patterns
- **REST APIs**: Synchronous communication between services
- **RabbitMQ**: Asynchronous messaging for events (HabitacionEvent, etc.)
- **Spring Cloud Gateway**: Request routing and cross-cutting concerns
- **Netflix Eureka**: Service discovery and registration

## Key Technologies
- Spring Boot 3.5.0 with Java 21
- Spring Data JPA, JDBC, and MongoDB
- Spring Cloud Gateway MVC
- Netflix Eureka for service discovery
- RabbitMQ for messaging
- Docker and Docker Compose
- Lombok for boilerplate reduction
- SpringDoc OpenAPI for documentation
- Testcontainers for integration testing

## Development Workflow

### Service Ports
- Gateway: 8080
- User Service: 8081
- Reservas Service: 8082
- Gestion Service: 8083
- Eureka Server: 8761

### Database Access
- MySQL: `mysql://usr_app:usrapp@localhost:3306/users`
- PostgreSQL: `postgresql://appuser:apppwd@localhost:5432/appdb`
- MongoDB: `mongodb://localhost:27017`
- RabbitMQ: `amqp://admin:admin@localhost:5672`
- Eureka Server: `http://localhost:8761`

### Testing Strategy
- Unit tests with JUnit 5
- Integration tests with Testcontainers
- Each service has its own test suite
- Postman collections available for API testing

## Important Notes
- All services extend the parent POM configuration
- Services use different database technologies by design
- Message passing uses shared DTOs from dan-common-lib
- Local development requires Docker for infrastructure services
- Each service can be run independently for development

### Service Discovery with Eureka
- Use the `eureka` profile to enable service discovery: `-Dspring.profiles.active=eureka`
- Gateway routes use `lb://service-name` format when Eureka is enabled
- Default profile maintains direct HTTP routing for development without Eureka
- Start Eureka server before other services for proper registration
- Gateway automatically discovers and load balances between service instances