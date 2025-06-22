# Configuración de rutas en Spring Cloud Gateway

El gateway permite enrutar de manera transparente las solicitudes a los distintos microservicios internos, simplificando el acceso y centralizando la gestión de rutas y filtros.

## Descripción general

El gateway utiliza Spring Cloud Gateway en modo WebMVC (servlet) y expone el puerto 8080. Su función es actuar como punto de entrada único para las peticiones HTTP, redirigiendo el tráfico a los microservicios internos según el path de la URL.

## Significado de los campos de configuración

- **ID de ruta:** Identificador único de la ruta dentro de la configuración del gateway. Sirve para distinguir y administrar cada ruta.
- **URI destino:** Dirección interna (DNS Docker) y puerto del microservicio al que se redirigirá la petición.
- **Predicado:** Condición que debe cumplir la URL de la petición para que la ruta sea aplicada. En este caso, se usa el predicado `Path`, que filtra por el prefijo del path.
- **Filtro:** Transformaciones que se aplican a la petición antes de reenviarla. `StripPrefix=1` elimina el primer segmento del path, permitiendo que el microservicio reciba la ruta relativa correcta.

## Rutas configuradas

### 1. user-svc

- **ID de ruta:** user-svc
- **URI destino:** `http://user-svc:8080`
- **Predicado:** `Path=/users/**`
- **Filtro:** `StripPrefix=1`

**Descripción:**  
Todas las peticiones que comiencen con `/users/` serán redirigidas al servicio `user-svc`. El filtro `StripPrefix=1` elimina el primer segmento (`/users`) antes de reenviar la petición al backend.

**Ejemplo:**  
- URL recibida por el gateway: `http://localhost:8080/users/actuator/health`
- URL reescrita y enviada a user-svc: `http://user-svc:8080/actuator/health`

---

### 2. reservas-svc

- **ID de ruta:** reservas-svc
- **URI destino:** `http://reservas-svc:8080`
- **Predicado:** `Path=/reservas/**`
- **Filtro:** `StripPrefix=1`

**Descripción:**  
Las peticiones que comiencen con `/reservas/` serán redirigidas al servicio `reservas-svc`, eliminando el prefijo `/reservas` antes de reenviar la solicitud.

**Ejemplo:**  
- URL recibida por el gateway: `http://localhost:8080/reservas/actuator/health`
- URL reescrita y enviada a reservas-svc: `http://reservas-svc:8080/actuator/health`

---

### 3. gestion-svc

- **ID de ruta:** gestion-svc
- **URI destino:** `http://gestion-svc:8080`
- **Predicado:** `Path=/gestion/**`
- **Filtro:** `StripPrefix=1`

**Descripción:**  
Las peticiones que comiencen con `/gestion/` serán redirigidas al servicio `gestion-svc`, eliminando el prefijo `/gestion` antes de reenviar la solicitud.

**Ejemplo:**  
- URL recibida por el gateway: `http://localhost:8080/gestion/actuator/health`
- URL reescrita y enviada a gestion-svc: `http://gestion-svc:8080/actuator/health`

---

