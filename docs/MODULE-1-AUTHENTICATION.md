# Authentication Module — Module 1

JWT-based registration and login for AI Trip Planner.

## Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/register` | No | Register new user |
| POST | `/api/auth/login` | No | Login and get JWT |
| GET | `/actuator/health` | No | Health check |

## Request Examples

### Register
```json
POST /api/auth/register
{
  "name": "Priya Sharma",
  "email": "priya@example.com",
  "password": "Password123!"
}
```

### Login
```json
POST /api/auth/login
{
  "email": "priya@example.com",
  "password": "Password123!"
}
```

### Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": "uuid",
    "name": "Priya Sharma",
    "email": "priya@example.com",
    "role": "USER",
    "createdAt": "2026-06-21T10:00:00Z"
  }
}
```

## Run Locally

```bash
# Start PostgreSQL and apply schema
psql -U tripplanner -d tripplanner -f ../database/schema.sql

# Run backend
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Swagger UI: http://localhost:8080/swagger-ui.html

## Security

- Passwords hashed with BCrypt (strength 12)
- JWT in `Authorization: Bearer <token>` header
- All routes except `/api/auth/**`, health, and Swagger require authentication
