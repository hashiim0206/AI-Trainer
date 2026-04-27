# Task Manager API

A **production-ready RESTful backend API** built with Spring Boot, PostgreSQL, Docker, and GitHub Actions CI/CD.

---

## Architecture

```
HTTP Request
    │
    ▼
TaskController         ← REST layer (routes & HTTP)
    │
    ▼
TaskService            ← Business logic layer
    │
    ▼
TaskRepository         ← Data access layer (Spring Data JPA)
    │
    ▼
PostgreSQL Database    ← Persistence
```

---

## Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| JDK | 21+ | https://adoptium.net |
| Maven | 3.9+ | https://maven.apache.org |
| Docker Desktop | Latest | https://www.docker.com/products/docker-desktop |
| Git | Latest | https://git-scm.com |

---

## Quick Start (Docker — Recommended)

```bash
# 1. Clone the repo
git clone <your-repo-url>
cd task-manager-api

# 2. Start the app + PostgreSQL
docker-compose up --build

# 3. Test it
curl http://localhost:8080/tasks
```

**Swagger UI:** http://localhost:8080/swagger-ui.html

---

## Local Development (without Docker)

```bash
# Requires: JDK 21, Maven, PostgreSQL running locally

# 1. Create the database
createdb taskdb

# 2. Update application.properties with your DB credentials (or use defaults)
# spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
# spring.datasource.username=postgres
# spring.datasource.password=postgres

# 3. Run the app
mvn spring-boot:run
```

---

## Run Tests

```bash
# Tests use H2 in-memory database — no PostgreSQL needed!
mvn test
```

Expected output:
```
Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## API Endpoints

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `POST` | `/tasks` | Create a new task | 201 |
| `GET` | `/tasks` | Get all tasks | 200 |
| `GET` | `/tasks/{id}` | Get task by ID | 200 / 404 |
| `PUT` | `/tasks/{id}` | Update task | 200 / 404 |
| `DELETE` | `/tasks/{id}` | Delete task | 204 / 404 |
| `PATCH` | `/tasks/{id}/complete` | Mark as DONE | 200 / 404 |

### Example Requests

**Create Task:**
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Finish assignment", "description": "Backend API"}'
```

**Response:**
```json
{
  "id": 1,
  "title": "Finish assignment",
  "description": "Backend API",
  "status": "OPEN",
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00"
}
```

**Get All Tasks:**
```bash
curl http://localhost:8080/tasks
```

**Mark Complete:**
```bash
curl -X PATCH http://localhost:8080/tasks/1/complete
```

**Delete Task:**
```bash
curl -X DELETE http://localhost:8080/tasks/1
```

---

## Project Structure

```
task-manager-api/
├── src/main/java/com/example/taskmanager/
│   ├── TaskManagerApplication.java     ← App entry point
│   ├── controller/
│   │   └── TaskController.java         ← REST endpoints
│   ├── service/
│   │   └── TaskService.java            ← Business logic
│   ├── repository/
│   │   └── TaskRepository.java         ← DB access
│   ├── model/
│   │   └── Task.java                   ← JPA Entity
│   ├── dto/
│   │   ├── TaskRequest.java            ← Input DTO
│   │   └── TaskResponse.java           ← Output DTO
│   └── exception/
│       ├── ResourceNotFoundException.java
│       └── GlobalExceptionHandler.java
├── src/test/
│   ├── TaskServiceTest.java            ← 12 unit tests
│   └── TaskControllerTest.java         ← 11 integration tests
├── Dockerfile                          ← Multi-stage Docker build
├── docker-compose.yml                  ← App + PostgreSQL
├── .github/workflows/ci.yml            ← GitHub Actions CI
└── pom.xml                             ← Maven dependencies
```

---

## CI/CD

Every push to `main` triggers GitHub Actions:
1. ✅ Run 23 automated tests
2. ✅ Build JAR artifact
3. ✅ Validate Docker image build

---

## Task Status Values

| Status | Meaning |
|--------|---------|
| `OPEN` | Task just created |
| `IN_PROGRESS` | Work has started |
| `DONE` | Task completed |

---

## Error Responses

All errors return structured JSON:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 99",
  "timestamp": "2025-01-01T10:00:00"
}
```

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.2 |
| Language | Java 21 |
| Database | PostgreSQL 16 |
| ORM | Hibernate / Spring Data JPA |
| Testing | JUnit 5 + Mockito + MockMvc |
| Container | Docker + docker-compose |
| CI/CD | GitHub Actions |
| API Docs | SpringDoc OpenAPI (Swagger) |
