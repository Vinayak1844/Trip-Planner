# Docker Configuration

Docker Compose orchestration for all services.

## Files

```
docker/
├── docker-compose.yml       # Main compose file (all services)
├── docker-compose.dev.yml   # Dev overrides (hot reload)
└── init-db/
    └── 01-init.sql          # Auto-run schema on first start
```

## Services

| Service    | Image/Build     | Port  | Description        |
|------------|-----------------|-------|--------------------|
| postgres   | postgres:16     | 5432  | Database           |
| backend    | ./backend       | 8080  | Spring Boot API    |
| ai-service | ./ai-service    | 8000  | FastAPI AI         |
| frontend   | ./frontend      | 5173  | React (Vite/Nginx) |

## Usage

```bash
# Production-like
docker compose -f docker/docker-compose.yml up -d

# Development (with hot reload)
docker compose -f docker/docker-compose.yml -f docker/docker-compose.dev.yml up
```

## Environment

Copy `.env.example` to `.env` in project root before starting.
