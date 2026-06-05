# Sistema Web de Reserva de Laboratorios

## Cómo correr el proyecto

1. Crear base de datos en MySQL: `CREATE DATABASE reservas_db;`
2. Configurar credenciales en `src/main/resources/application.properties`
3. Correr `SistemaReservasApplication.java` desde Eclipse
4. El sistema queda disponible en `http://localhost:8080`





## Autenticación (JWT)

El sistema usa tokens JWT. Para usar endpoints protegidos:
1. Registrar usuario: `POST http://localhost:8080/api/auth/registro`
2. Hacer login: `POST http://localhost:8080/api/auth/login` → devuelve un **token**
3. En cada request protegida agregar en Headers:
   - Key: `Authorization`
   - Value: `Bearer TOKEN_AQUI`



## Roles y accesos
| Rol | Crear Reserva | Ver Historial | Reportes PDF/Excel |
|---|---|---|---|
| ALUMNO | ✅ | ✅ | ❌ |
| DOCENTE | ✅ | ✅ | ✅ |
| ADMIN | ✅ | ✅ | ✅ |



## Endpoints principales


| Método | URL | Descripción | Requiere Token |
|---|---|---|---|
| POST | `http://localhost:8080/api/auth/registro` | Crear usuario | No |
| POST | `http://localhost:8080/api/auth/login` | Obtener token | No |
| POST | `http://localhost:8080/api/reservas` | Crear reserva | Sí |
| GET | `http://localhost:8080/api/historial` | Ver historial | Sí |
| GET | `http://localhost:8080/api/reservas/disponibilidad` | Verificar disponibilidad | Sí |
| GET | `http://localhost:8080/api/reportes/pdf` | Descargar reporte PDF | Sí (DOCENTE/ADMIN) |
| GET | `http://localhost:8080/api/reportes/excel` | Descargar reporte Excel | Sí (DOCENTE/ADMIN) |
