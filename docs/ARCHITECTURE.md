# AI Trip Planner — System Architecture

## 1. Executive Summary

AI Trip Planner is a full-stack application that helps students and working professionals plan trips automatically. Users provide preferences; the system generates destination recommendations, day-wise itineraries, and budget estimates using AI.

The system follows a **microservice-oriented monorepo** pattern: a React SPA, a Spring Boot API gateway/backend, a dedicated FastAPI AI service, and PostgreSQL — all orchestrated via Docker Compose.

---

## 2. High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT LAYER                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  React SPA (TypeScript + Tailwind + React Query + React Router)     │   │
│  │  Port: 5173 (dev) / 80 (prod via Nginx)                             │   │
│  └───────────────────────────────┬─────────────────────────────────────┘   │
└──────────────────────────────────┼──────────────────────────────────────────┘
                                   │ HTTPS / REST (JWT Bearer)
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           APPLICATION LAYER                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Spring Boot 3 Backend (Java 21)                                    │   │
│  │  Port: 8080                                                         │   │
│  │  • JWT Authentication (Spring Security)                             │   │
│  │  • User & Profile Management                                        │   │
│  │  • Trip CRUD & Status Lifecycle                                     │   │
│  │  • Holiday Analyzer (server-side date logic)                        │   │
│  │  • AI Orchestration (calls FastAPI service)                         │   │
│  │  • Swagger/OpenAPI Documentation                                    │   │
│  └───────────────┬─────────────────────────────┬───────────────────────┘   │
└──────────────────┼─────────────────────────────┼────────────────────────────┘
                   │ JDBC                          │ HTTP (internal network)
                   ▼                               ▼
┌──────────────────────────────┐   ┌──────────────────────────────────────────┐
│  PostgreSQL 16               │   │  FastAPI AI Service (Python)             │
│  Port: 5432                  │   │  Port: 8000                              │
│  • users, profiles           │   │  • POST /recommend-destination           │
│  • trips, itineraries        │   │  • POST /generate-itinerary              │
│  • budget_breakdowns         │   │  • POST /estimate-budget                 │
│                              │   │  • POST /regenerate-itinerary            │
└──────────────────────────────┘   └──────────────────┬───────────────────────┘
                                                      │ HTTPS
                                                      ▼
                                        ┌─────────────────────────┐
                                        │  OpenAI API (GPT-4o)    │
                                        └─────────────────────────┘
```

---

## 3. Technology Stack

| Layer        | Technology                          | Purpose                              |
|-------------|--------------------------------------|--------------------------------------|
| Frontend    | React 18, TypeScript, Vite           | SPA with type safety                 |
| Styling     | Tailwind CSS                         | Responsive, modern UI                |
| Routing     | React Router v6                      | Client-side navigation               |
| Data Fetch  | Axios + TanStack React Query         | API calls with caching & mutations   |
| Backend     | Spring Boot 3, Java 21, Maven        | REST API, business logic             |
| Security    | Spring Security + JWT + BCrypt       | Authentication & authorization       |
| Database    | PostgreSQL 16                        | Persistent relational storage        |
| AI Service  | FastAPI, Python 3.11+, OpenAI SDK    | LLM-powered trip intelligence        |
| Docs        | SpringDoc OpenAPI 3                  | Interactive API documentation        |
| Containers  | Docker + Docker Compose              | Local dev & deployment parity        |

---

## 4. User Workflow

```
Register/Login → Create Profile → Enter Trip Details → Holiday Analysis Preview
       → Generate Trip → View Destination + Budget + Itinerary → Accept / Regenerate
```

### Detailed Flow

1. **Register/Login** — User creates account or authenticates; receives JWT access token.
2. **Create Profile** — User sets occupation, home city, travel style, budget preference, transport.
3. **Enter Trip Details** — Source city, dates, budget, travellers, travel style, extra preferences.
4. **Holiday Analyzer** — Before generation, frontend (or backend preview endpoint) shows:
   - Total trip days
   - Weekend overlap
   - Long weekend opportunity
5. **Generate Trip** — Backend orchestrates AI calls and persists results.
6. **Review Plan** — User sees destination, budget breakdown, day-wise itinerary.
7. **Accept or Regenerate** — Accept updates status; regenerate sends modification prompt to AI.

---

## 5. Service Boundaries & Responsibilities

### 5.1 Frontend (React)

| Responsibility              | Details                                                |
|----------------------------|--------------------------------------------------------|
| Authentication UI          | Login, Register forms with validation                  |
| Protected Routes           | Redirect unauthenticated users to login                |
| Token Management           | Store JWT; attach via Axios interceptor                |
| Dashboard                  | User info, recent trip cards                           |
| Trip Creation              | Form + holiday analyzer preview                        |
| Trip Details               | Itinerary, budget, accept/regenerate/delete actions    |
| Profile Management         | CRUD for user profile                                  |

**Does NOT:** Call OpenAI directly, store passwords in plain text, or contain business rules for holiday calculation (delegates to backend).

### 5.2 Backend (Spring Boot)

| Responsibility              | Details                                                |
|----------------------------|--------------------------------------------------------|
| Authentication             | Register, login, JWT issue/validate                    |
| Authorization              | Role-based (USER); users access only own resources     |
| Profile API                | CRUD scoped to authenticated user                      |
| Trip API                   | Create, list, get, update status, delete               |
| Holiday Analyzer           | Pure Java date logic (no AI needed)                    |
| AI Orchestration           | Aggregates context; calls FastAPI; persists results    |
| Validation                 | Bean Validation on all DTOs                          |
| Exception Handling         | Global `@ControllerAdvice` with consistent error JSON  |
| Logging                    | SLF4J structured logs for audit & debugging            |
| API Docs                   | Swagger UI at `/swagger-ui.html`                       |

**Does NOT:** Call OpenAI directly (delegates to AI service for separation of concerns).

### 5.3 AI Service (FastAPI)

| Responsibility              | Details                                                |
|----------------------------|--------------------------------------------------------|
| Destination Recommendation | Budget, duration, style, distance-aware suggestions    |
| Itinerary Generation       | Day-wise structured JSON itinerary                     |
| Budget Estimation          | Category-wise allocation (transport, hotel, food, etc.)|
| Regeneration               | Apply user modification prompts to existing plan       |
| Prompt Engineering         | Structured system/user prompts for consistent JSON     |
| Fallback                   | Graceful degradation if OpenAI unavailable           |

**Does NOT:** Handle authentication, user data persistence, or direct DB access.

### 5.4 Database (PostgreSQL)

| Responsibility              | Details                                                |
|----------------------------|--------------------------------------------------------|
| User storage               | Credentials (BCrypt hashed), roles                     |
| Profile storage            | 1:1 with users                                         |
| Trip lifecycle             | Status: GENERATED, ACCEPTED, REJECTED                  |
| Itinerary storage          | JSONB for flexible day-wise plans                      |
| Budget storage             | Normalized breakdown per trip                          |

---

## 6. Authentication & Security Architecture

### 6.1 JWT Flow

```
Client                    Spring Boot                    PostgreSQL
  │                            │                              │
  │── POST /api/auth/login ───►│                              │
  │                            │── validate credentials ─────►│
  │                            │◄── user record ──────────────│
  │◄── { accessToken, user } ──│                              │
  │                            │                              │
  │── GET /api/trips ─────────►│                              │
  │   Authorization: Bearer    │── validate JWT signature     │
  │                            │── extract userId & role      │
  │◄── 200 + trips ────────────│                              │
```

### 6.2 Security Measures

- **Password hashing:** BCrypt with strength 12
- **JWT:** HS256 signed, configurable expiry (default 24h)
- **CORS:** Whitelist frontend origin only
- **Stateless sessions:** No server-side session store
- **Resource ownership:** All trip/profile endpoints verify `userId` matches JWT subject
- **Input validation:** Jakarta Validation on all request bodies
- **Secrets:** Environment variables only (never committed)

### 6.3 Role Model

| Role | Permissions                                      |
|------|--------------------------------------------------|
| USER | Own profile, own trips, generate/regenerate trips|

---

## 7. Trip Generation Sequence

```
User          Frontend       Backend         AI Service       PostgreSQL
 │               │              │                │                │
 │─ Create Trip ─►│              │                │                │
 │               │─ POST /trips ►│                │                │
 │               │              │─ INSERT trip ──────────────────►│
 │               │              │◄────────────────────────────────│
 │               │              │                │                │
 │─ Generate ───►│              │                │                │
 │               │─ POST /trips/{id}/generate ─►│                │
 │               │              │─ recommend-destination ───────►│
 │               │              │◄─ destinations ──────────────────│
 │               │              │─ generate-itinerary ────────────►│
 │               │              │◄─ itinerary JSON ────────────────│
 │               │              │─ estimate-budget ───────────────►│
 │               │              │◄─ budget breakdown ──────────────│
 │               │              │─ INSERT itinerary + budget ────►│
 │               │              │─ UPDATE trip status=GENERATED ──►│
 │               │◄─ full trip ─│                │                │
 │◄─ display ────│              │                │                │
```

### Regeneration Flow

```
User provides modification prompt (e.g., "Reduce budget", "Add adventure")
  → Backend loads existing trip + itinerary + budget
  → POST /regenerate-itinerary to AI service with full context + prompt
  → AI returns updated itinerary + budget
  → Backend replaces stored records, status remains GENERATED
```

---

## 8. Holiday Analyzer Design

Implemented in **Spring Boot** (deterministic, no AI cost).

### Input
- `startDate`, `endDate` (ISO-8601 dates)

### Output
```json
{
  "totalDays": 4,
  "includesWeekend": true,
  "longWeekendDetected": true,
  "weekendDays": 2,
  "message": "4 Days Available · Includes Weekend · Long Weekend Detected"
}
```

### Logic
- **Total days:** Inclusive count between start and end
- **Weekend overlap:** Count Saturday/Sunday within range
- **Long weekend:** Trip spans Fri–Mon (or Thu–Mon with holiday) with ≥1 weekend day and ≤5 total days, OR includes both Sat and Sun with trip ≤4 days adjacent to weekend

Exposed as: `GET /api/trips/holiday-analysis?startDate=&endDate=` (authenticated)

---

## 9. API Design (Backend REST)

Base URL: `/api`

### Auth
| Method | Endpoint              | Auth | Description        |
|--------|----------------------|------|--------------------|
| POST   | `/auth/register`     | No   | Register new user  |
| POST   | `/auth/login`        | No   | Login, get JWT     |

### Profile
| Method | Endpoint              | Auth | Description        |
|--------|----------------------|------|--------------------|
| POST   | `/profiles`          | Yes  | Create profile     |
| GET    | `/profiles/me`       | Yes  | Get own profile    |
| PUT    | `/profiles/me`       | Yes  | Update profile     |

### Trips
| Method | Endpoint                        | Auth | Description              |
|--------|--------------------------------|------|--------------------------|
| GET    | `/trips/holiday-analysis`      | Yes  | Analyze date range       |
| POST   | `/trips`                       | Yes  | Create trip request      |
| GET    | `/trips`                       | Yes  | List user's trips        |
| GET    | `/trips/{id}`                  | Yes  | Get trip with details    |
| POST   | `/trips/{id}/generate`         | Yes  | Generate AI plan         |
| POST   | `/trips/{id}/regenerate`       | Yes  | Regenerate with prompt   |
| PATCH  | `/trips/{id}/status`           | Yes  | Accept/Reject trip       |
| DELETE | `/trips/{id}`                  | Yes  | Delete trip              |

### AI Service (Internal)
| Method | Endpoint                  | Description                    |
|--------|--------------------------|--------------------------------|
| POST   | `/recommend-destination` | Suggest destinations           |
| POST   | `/generate-itinerary`    | Day-wise itinerary             |
| POST   | `/estimate-budget`       | Budget allocation              |
| POST   | `/regenerate-itinerary`  | Modified itinerary + budget    |

---

## 10. Data Model (Entity Relationships)

```
users (1) ──────── (0..1) profiles
  │
  └─────── (1..*) trips (1) ──────── (0..1) itineraries
                          │
                          └─────── (0..1) budget_breakdowns
```

### Trip Status State Machine

```
                    ┌─────────────┐
         create     │   DRAFT     │  (optional: trip saved before generate)
                    └──────┬──────┘
                           │ generate
                           ▼
                    ┌─────────────┐
              ┌────►│  GENERATED  │◄────┐
              │     └──────┬──────┘     │ regenerate
              │            │            │
              │     accept │ reject     │
              │            ▼            │
              │     ┌─────────────┐     │
              │     │  ACCEPTED   │     │
              │     └─────────────┘     │
              │            │            │
              │     ┌─────────────┐     │
              └─────│  REJECTED   │─────┘
                    └─────────────┘
```

For MVP: trips start as `GENERATED` immediately after successful AI generation (no separate DRAFT unless user saves form first).

---

## 11. Frontend Architecture

### Route Structure

| Path              | Page           | Protected |
|-------------------|----------------|-----------|
| `/login`          | LoginPage      | No        |
| `/register`       | RegisterPage   | No        |
| `/dashboard`      | DashboardPage  | Yes       |
| `/trips/new`      | CreateTripPage | Yes       |
| `/trips/:id`      | TripDetailsPage| Yes       |
| `/profile`        | ProfilePage    | Yes       |

### State Management

- **Auth context:** JWT token, user info, login/logout
- **React Query:** Server state (trips, profile, dashboard data)
- **Local form state:** React Hook Form or controlled components

### Folder Organization

```
src/
├── api/           # Axios instance + API functions
├── components/    # Reusable UI (Layout, TripCard, etc.)
├── contexts/      # AuthContext
├── hooks/         # useAuth, useTrips, etc.
├── pages/         # Route-level components
├── types/         # TypeScript interfaces
└── utils/         # Helpers (date formatting, etc.)
```

---

## 12. Backend Package Structure (Clean Architecture)

```
com.tripplanner/
├── TripPlannerApplication.java
├── config/          # Security, CORS, OpenAPI, WebClient
├── controller/      # REST endpoints
├── dto/
│   ├── request/     # Incoming DTOs
│   └── response/    # Outgoing DTOs
├── entity/          # JPA entities
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── mapper/          # Entity ↔ DTO mappers
├── repository/      # Spring Data JPA
├── security/        # JWT filter, UserDetails, JwtUtil
├── service/         # Business logic
│   ├── ai/          # AI service client
│   └── analyzer/    # Holiday analyzer
└── util/            # Constants, enums
```

---

## 13. AI Service Structure

```
ai-service/
├── app/
│   ├── main.py              # FastAPI app, CORS, routes
│   ├── config.py            # Settings (OpenAI key, model)
│   ├── models/              # Pydantic request/response schemas
│   ├── routers/             # Endpoint routers
│   ├── services/
│   │   ├── openai_client.py # OpenAI wrapper
│   │   ├── destination.py
│   │   ├── itinerary.py
│   │   └── budget.py
│   └── prompts/             # Prompt templates
├── requirements.txt
└── Dockerfile
```

---

## 14. Docker Compose Topology

```yaml
services:
  postgres:     # PostgreSQL 16, volume for persistence
  backend:      # Spring Boot, depends on postgres + ai-service
  ai-service:   # FastAPI, depends on OpenAI API key
  frontend:     # Nginx serving built React app (prod)
                # OR Vite dev server (dev profile)
```

### Network
- All services on `trip-planner-network` (bridge)
- Only `frontend` and `backend` ports exposed to host
- `ai-service` and `postgres` internal only

### Environment Variables

| Variable              | Service   | Description                    |
|-----------------------|-----------|--------------------------------|
| `POSTGRES_*`          | postgres  | DB credentials                 |
| `JWT_SECRET`          | backend   | JWT signing key                |
| `AI_SERVICE_URL`      | backend   | http://ai-service:8000         |
| `OPENAI_API_KEY`      | ai-service| OpenAI authentication          |
| `VITE_API_BASE_URL`   | frontend  | Backend URL for Axios          |

---

## 15. Error Handling Strategy

### Backend Error Response Format
```json
{
  "timestamp": "2026-06-21T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/trips",
  "details": [
    { "field": "budget", "message": "must be greater than 0" }
  ]
}
```

### HTTP Status Codes
| Code | Usage                                      |
|------|--------------------------------------------|
| 200  | Success                                    |
| 201  | Created                                    |
| 400  | Validation error                           |
| 401  | Missing/invalid JWT                        |
| 403  | Forbidden (wrong user resource)            |
| 404  | Resource not found                         |
| 409  | Conflict (duplicate email, profile exists) |
| 500  | Internal server error                      |
| 503  | AI service unavailable                     |

---

## 16. Non-Functional Requirements

| Requirement    | Implementation                                      |
|----------------|-----------------------------------------------------|
| Scalability    | Stateless backend; AI service independently scalable|
| Security       | JWT, BCrypt, CORS, env secrets, input validation    |
| Observability  | Structured logging, health endpoints                |
| Testability    | Service layer unit tests; integration tests with Testcontainers |
| Documentation  | Swagger UI, README, architecture doc                |
| Deployment     | Docker Compose for dev; container-ready for cloud   |

---

## 17. Implementation Roadmap (Module-by-Module)

| Phase | Module                    | Deliverables                                      |
|-------|---------------------------|---------------------------------------------------|
| **0** | Foundation                | Architecture, schema, folder structure ✅          |
| **1** | Authentication            | User entity, JWT, register/login, security config |
| **2** | User Profile              | Profile CRUD, validation, ownership checks        |
| **3** | Trip Planning (Core)      | Trip entity, CRUD, holiday analyzer               |
| **4** | AI Service                | FastAPI endpoints, OpenAI integration             |
| **5** | AI Orchestration          | Backend AI client, generate/regenerate flow       |
| **6** | Frontend Foundation       | Vite setup, auth, routing, API layer              |
| **7** | Frontend Pages            | All 6 pages, dashboard, trip details              |
| **8** | Docker & DevOps           | Docker Compose, env files, seed data              |
| **9** | Polish                    | Swagger, logging, error handling, README          |

---

## 18. Design Decisions & Rationale

1. **Separate AI microservice** — Isolates LLM latency, prompt changes, and API keys from the main backend.
2. **JSONB for itinerary** — Flexible schema for variable day counts and activity structures.
3. **Holiday analyzer in Java** — Deterministic logic doesn't need AI; reduces cost and latency.
4. **JWT stateless auth** — Scales horizontally without session store.
5. **React Query** — Handles caching, refetch on focus, optimistic updates for trip status changes.
6. **Monorepo** — Single clone for interview demos; Docker Compose ties services together.

---

*Document version: 1.0 — Foundation Phase*
