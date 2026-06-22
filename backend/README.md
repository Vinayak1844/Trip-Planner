# Backend — Spring Boot 3 API

Java 21 · Maven · Spring Security · JWT · PostgreSQL

## Package Structure

```
src/main/java/com/tripplanner/
├── TripPlannerApplication.java
├── config/              # Security, CORS, OpenAPI, WebClient
├── controller/          # REST endpoints
├── dto/
│   ├── request/         # Incoming request DTOs
│   └── response/        # Outgoing response DTOs
├── entity/              # JPA entities
├── exception/           # Custom exceptions + GlobalExceptionHandler
├── mapper/              # Entity ↔ DTO mappers
├── repository/          # Spring Data JPA repositories
├── security/            # JWT filter, UserDetailsService, JwtUtil
├── service/             # Business logic
│   ├── ai/              # FastAPI client orchestration
│   └── analyzer/        # Holiday date analyzer
└── util/                # Enums, constants, helpers

src/main/resources/
├── application.yml
├── application-dev.yml
└── application-prod.yml

src/test/java/com/tripplanner/
├── controller/
├── service/
└── integration/
```

## Modules (Implementation Order)

1. **Authentication** — User entity, JWT, register/login
2. **Profile** — Profile CRUD
3. **Trip Planning** — Trip CRUD, holiday analyzer
4. **AI Orchestration** — Generate/regenerate via FastAPI

## Run Locally

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## API Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`
