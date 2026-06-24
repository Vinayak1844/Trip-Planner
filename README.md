# AI Trip Planner

A production-ready full-stack application that helps students and working professionals plan trips automatically using AI.

## Architecture

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the complete system design.

## Project Structure

```
ai-trip-planner/
├── backend/          # Spring Boot 3 REST API (Java 21)
├── frontend/         # React + TypeScript SPA
├── ai-service/       # FastAPI AI microservice (Python)
├── database/         # PostgreSQL schema & seed data
├── docs/             # Architecture & design documents
└── docker/           # Docker Compose & service configs
```

## Tech Stack

| Layer    | Technology                          |
|----------|-------------------------------------|
| Frontend | React, TypeScript, Tailwind, Vite   |
| Backend  | Spring Boot 3, Java 21, JWT         |
| Database | PostgreSQL 16                       |
| AI       | FastAPI, OpenAI API                 |
| DevOps   | Docker, Docker Compose              |

## Implementation Phases

| Phase | Module               | Status       |
|-------|----------------------|--------------|
| 0     | Architecture & Schema| ✅ Complete  |
| 1     | Authentication       | ✅ Complete  |
| 2     | User Profile         | ✅ Complete  |
| 3     | Trip Planning        | 🔲 Pending   |
| 4     | AI Service           | 🔲 Pending   |
| 5     | AI Orchestration     | 🔲 Pending   |
| 6     | Frontend Foundation  | 🔲 Pending   |
| 7     | Frontend Pages       | 🔲 Pending   | 
| 8     | Docker & DevOps      | 🔲 Pending   |
| 9     | Polish & Docs        | 🔲 Pending   |



## Quick Start (Coming Soon)

```bash
# Copy environment files
cp .env.example .env

# Start all services
docker compose up -d

# Access
# Frontend:  http://localhost:5173
# Backend:   http://localhost:8080
# Swagger:   http://localhost:8080/swagger-ui.html
# AI Service: http://localhost:8000/docs
```

## Database Setup

```bash
psql -U tripplanner -d tripplanner -f database/schema.sql
psql -U tripplanner -d tripplanner -f database/seed.sql
```

## License

MIT
