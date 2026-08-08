# OpenFGA Wrapper Service

A production-quality **Spring Boot 3** service that exposes a clean, opinionated
REST facade over [OpenFGA](https://openfga.dev/) — the open-source, fine-grained
authorization engine (a Zanzibar-style ReBAC system).

Internal callers get a small, stable HTTP API for permission checks and
relationship management without needing to depend on the OpenFGA SDK directly.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Running Locally with Docker](#running-locally-with-docker)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [CI/CD Pipeline](#cicd-pipeline)
- [Project Structure](#project-structure)

---

## Features

- **Permission checks** — "Can this user do this thing to this object?"
- **Relationship management** — write and delete relationship tuples
- **Object listing** — enumerate the objects a user can access
- **Health checks** — a lightweight endpoint plus Spring Boot Actuator
- **Consistent error handling** — a single JSON error envelope with meaningful
  HTTP status codes
- **Request validation** — bean validation on all inbound payloads
- **Full test coverage** of the web and service layers (JUnit 5 + Mockito)
- **Dockerised local stack** and a **GitHub Actions** CI pipeline

---

## Architecture

The service follows a conventional layered design:

```
HTTP request
    │
    ▼
AuthorizationController      ← @RestController: routing, validation, HTTP status
    │
    ▼
OpenFGAService               ← @Service: DTO ⇄ SDK translation, error normalising
    │
    ▼
OpenFgaClient (SDK)          ← configured by OpenFGAConfig from application.yml
    │
    ▼
OpenFGA server
```

- **`GlobalExceptionHandler`** (`@RestControllerAdvice`) centralises error
  translation for every endpoint.
- **DTOs** (`CheckRequest`, `CheckResponse`, `TupleRequest`, `TupleResponse`,
  `ListObjectsRequest`, `ListObjectsResponse`, `ErrorResponse`) keep the public
  contract decoupled from the SDK's internal model types.

---

## Prerequisites

| Tool | Version |
|------|---------|
| JDK | 17+ (the project targets Java 17 bytecode; Java 25 works fine) |
| Maven | 3.9+ |
| Docker & Docker Compose | recent |

> The build compiles to Java 17 bytecode via `<java.version>17</java.version>`,
> so the artifact runs on any JDK 17 or newer.

---

## Setup

```bash
# Clone and enter the project
git clone <your-repo-url>
cd openfga-wrapper

# Build and run the tests
mvn clean install
```

---

## Running Locally with Docker

The provided `docker-compose.yml` runs both OpenFGA and this service.

```bash
# Start OpenFGA (host :8080) and the wrapper (host :8081)
docker compose up --build

# ...or start only OpenFGA and run the app from your IDE / Maven
docker compose up openfga
mvn spring-boot:run
```

Once up:

- Wrapper service: <http://localhost:8081>
- OpenFGA HTTP API: <http://localhost:8080>
- OpenFGA Playground: <http://localhost:3000/playground>

### First-time OpenFGA setup

OpenFGA needs a **store** and an **authorization model** before checks return
meaningful results. Create them with the OpenFGA CLI or the HTTP API, then set
the resulting IDs on the wrapper (see [Configuration](#configuration)). A minimal
model to experiment with:

```dsl
model
  schema 1.1

type user

type document
  relations
    define reader: [user]
    define writer: [user]
```

---

## Configuration

Settings live in `application.yml`, with `dev` and `prod` profile overrides.
Everything can be overridden via environment variables:

| Property | Env var | Default | Description |
|----------|---------|---------|-------------|
| `openfga.api-url` | `OPENFGA_API_URL` | `http://localhost:8080` | OpenFGA server base URL |
| `openfga.store-id` | `OPENFGA_STORE_ID` | _(empty)_ | Target store id |
| `openfga.authorization-model-id` | `OPENFGA_MODEL_ID` | _(empty → latest)_ | Authorization model id |
| `server.port` | — | `8081` | Wrapper HTTP port |

Select a profile with `--spring.profiles.active=prod` or
`SPRING_PROFILES_ACTIVE=prod`. The `dev` profile is active by default and
enables debug logging; the `prod` profile expects all connection values to be
provided via environment variables.

---

## API Documentation

Base path: `/api/authorization`
All request/response bodies are JSON.

### 1. Check a permission

`POST /api/authorization/check`

**Request**
```json
{
  "user": "user:anne",
  "relation": "reader",
  "object": "document:roadmap"
}
```

**Response** — `200 OK`
```json
{
  "allowed": true,
  "user": "user:anne",
  "relation": "reader",
  "object": "document:roadmap"
}
```

```bash
curl -X POST http://localhost:8081/api/authorization/check \
  -H "Content-Type: application/json" \
  -d '{"user":"user:anne","relation":"reader","object":"document:roadmap"}'
```

---

### 2. Write a relationship tuple

`POST /api/authorization/tuples`

**Request**
```json
{
  "user": "user:anne",
  "relation": "reader",
  "object": "document:roadmap"
}
```

**Response** — `201 Created`
```json
{
  "message": "Tuple written successfully",
  "user": "user:anne",
  "relation": "reader",
  "object": "document:roadmap"
}
```

```bash
curl -X POST http://localhost:8081/api/authorization/tuples \
  -H "Content-Type: application/json" \
  -d '{"user":"user:anne","relation":"reader","object":"document:roadmap"}'
```

---

### 3. Delete a relationship tuple

`DELETE /api/authorization/tuples`

**Request**
```json
{
  "user": "user:anne",
  "relation": "reader",
  "object": "document:roadmap"
}
```

**Response** — `200 OK`
```json
{
  "message": "Tuple deleted successfully",
  "user": "user:anne",
  "relation": "reader",
  "object": "document:roadmap"
}
```

```bash
curl -X DELETE http://localhost:8081/api/authorization/tuples \
  -H "Content-Type: application/json" \
  -d '{"user":"user:anne","relation":"reader","object":"document:roadmap"}'
```

---

### 4. List accessible objects

`GET /api/authorization/objects?user={user}&relation={relation}&type={type}`

**Response** — `200 OK`
```json
{
  "user": "user:anne",
  "relation": "reader",
  "type": "document",
  "objects": ["document:roadmap", "document:budget"]
}
```

```bash
curl "http://localhost:8081/api/authorization/objects?user=user:anne&relation=reader&type=document"
```

---

### 5. Health check

`GET /api/authorization/health`

**Response** — `200 OK`
```json
{
  "status": "UP",
  "service": "openfga-wrapper"
}
```

Spring Boot Actuator also exposes `GET /actuator/health`.

---

### Error responses

All errors share a consistent envelope:

```json
{
  "timestamp": "2026-08-07T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/api/authorization/check",
  "validationErrors": {
    "user": "user must not be blank"
  }
}
```

| Status | When |
|--------|------|
| `400 Bad Request` | Validation failure or missing query parameter |
| `502 Bad Gateway` | The wrapper is healthy but OpenFGA could not be reached / failed |
| `500 Internal Server Error` | Unexpected error |

---

## Testing

```bash
mvn test
```

- **`AuthorizationControllerTest`** — web layer via `@WebMvcTest` + MockMvc, with
  the service mocked (`@MockBean`). Covers success paths, validation → 400,
  upstream failure → 502, and the health endpoint.
- **`OpenFGAServiceTest`** — service layer with the OpenFGA SDK client mocked
  (`@Mock`). Covers request/response mapping and error normalisation for check,
  write, delete, and list operations.

---

## CI/CD Pipeline

Defined in `.github/workflows/build.yml`, the GitHub Actions pipeline:

1. **Triggers** on every push to `main` and on all pull requests.
2. **Checks out** the source and **sets up JDK 17** (Temurin, with Maven caching).
3. Runs **`mvn clean install`** (compile + test + package).
4. Runs **`mvn test`** explicitly.
5. **Publishes the JUnit test report** so results are visible on the PR.
6. **Uploads the built JAR** as a downloadable workflow artifact.

The job's pass/fail status is reported back to the commit and PR automatically.

---

## Project Structure

```
openfga-wrapper/
├── src/main/java/com/jacob/openfga/
│   ├── OpenfgaWrapperApplication.java
│   ├── config/OpenFGAConfig.java
│   ├── controller/AuthorizationController.java
│   ├── service/OpenFGAService.java
│   ├── exception/GlobalExceptionHandler.java
│   ├── exception/OpenFGAException.java
│   └── model/  (CheckRequest, CheckResponse, TupleRequest, TupleResponse,
│                ListObjectsRequest, ListObjectsResponse, ErrorResponse)
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── application-prod.yml
├── src/test/java/com/jacob/openfga/
│   ├── controller/AuthorizationControllerTest.java
│   └── service/OpenFGAServiceTest.java
├── .github/workflows/build.yml
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```
