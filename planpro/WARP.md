# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Common commands

### Build
- Build the app:
  - `./gradlew build`
- Clean + build (good first step when dependencies/IDE get weird):
  - `./gradlew clean build`
- Build without tests (used by `Dockerfile`):
  - `./gradlew build -x test`

### Run (Spring Boot)
- Run locally:
  - `./gradlew bootRun`
- Run with a specific Spring profile:
  - `SPRING_PROFILES_ACTIVE=local ./gradlew bootRun`
  - `SPRING_PROFILES_ACTIVE=production ./gradlew bootRun`

### Tests
- Run all tests:
  - `./gradlew test`
- Run one test class:
  - `./gradlew test --tests 'com.planprostructure.planpro.SomeTest'`
- Run one test method:
  - `./gradlew test --tests 'com.planprostructure.planpro.SomeTest.someMethod'`

### “Lint” / verification
- This repo does not define a separate formatter/linter task in Gradle; use:
  - `./gradlew check`

### Local Postgres (Docker)
- Start the Postgres container defined in `docker-compose.yml`:
  - `docker compose up -d postgres`
- Stop it:
  - `docker compose down`

## Runtime configuration (profiles, env vars)
- Spring profiles are defined in:
  - `src/main/resources/application.yml` (defaults to `local`)
  - `src/main/resources/application-local.yml`
  - `src/main/resources/application-production.yml`
- Production DB configuration uses Railway-style env vars (`PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`) in `application-production.yml`.
- JWT signing keys:
  - `application.yml` points `rsa.public-key`/`rsa.private-key` to `src/main/resources/certs/*`.
  - Security wiring is split between `config/SecurityConfig.java` (RSA) and `config/SecurityConfigFallback.java` (HMAC fallback).

Deployment-related files:
- `Dockerfile` builds the JAR with Gradle and runs it from `build/libs/`.
- `railway.toml` uses the Dockerfile builder and defines health checks at `/actuator/health`.

Note: multiple files in this repo contain hard-coded credentials/keys (e.g., `README.md`, `application-local.yml`). When summarizing or quoting config, redact secret values rather than copying them into logs/output.

## High-level architecture

### Tech stack
- Java 21 (Gradle toolchain) + Spring Boot 3.2
- Spring Web (REST), Spring Security (JWT + OAuth flows), Spring Data JPA (Postgres)
- WebSocket (STOMP + SockJS)
- OpenAPI/Swagger via `springdoc-openapi`
- Actuator health endpoints

### Package layout (big picture)
Code lives under `src/main/java/com/planprostructure/planpro/`.

- `PlanproApplication.java`: Spring Boot entry point; also enables scheduling + retry.
- `config/`: application wiring and cross-cutting infrastructure:
  - Security/JWT: `SecurityConfig.java`, `SecurityConfigFallback.java`, `JwtUtil.java`, handlers/converters.
  - WebSocket/STOMP: `WebSocketConfig.java` (+ listeners).
  - OpenAPI/Swagger: `OpenApiConfig.java`, `SwaggerConfig.java`.
  - Misc: CORS, RestTemplate config for the AI assistant, file config, etc.
- `controller/`: REST controllers grouped by feature (auth, users, trips, files, calendar, reminders, chat, telegram, AI assistant).
- `service/`: service layer (interfaces + `*Impl` implementations) that controllers call.
- `domain/`: feature-oriented domain modules. Each feature directory typically contains:
  - JPA entities/models
  - a Spring Data repository interface (often named `*Repository`)
  Examples: `domain/users/`, `domain/trips/`, `domain/chatRoom/`, `domain/calendar/`, etc.
- `payload/`: DTOs and request/response types used by controllers and services.

### Request flow (typical)
Controller → Service (`service/*`) → Repository (`domain/**/**Repository`) → Postgres.

### Auth & security (where to look)
- Stateless JWT resource server configuration lives in `config/SecurityConfig*.java`.
- OAuth2 Google login is implemented as a custom, controller-driven flow:
  - `controller/OAuth2Controller.java` initiates the Google auth redirect and handles callbacks.
  - `service/auth/CustomOAuth2UserService.java` maps provider attributes to a `Users` record.
  - `config/OAuth2AuthenticationSuccessHandler.java` turns an authenticated user into a JWT + frontend redirect.
- If OAuth-related behavior changes, also check documentation in `guide/` (see next section).

### Real-time messaging
- WebSocket endpoint is registered at `/ws` and uses STOMP destinations with prefixes:
  - app destinations: `/app/**`
  - broker destinations: `/topic/**`, `/queue/**`

### AI assistant integration
- `service/aiAssistant/AIAssistantService.java` calls a Gemini HTTP endpoint using a qualified `RestTemplate` from `config/OpenAIRestTemplateConfig.java`.
- Controller entry points are in `controller/AIAssistantController.java`.

## Project docs worth reading first
- OAuth2 docs and troubleshooting live in `guide/` (e.g. `guide/QUICK_START_OAUTH2.md`, `guide/OAUTH2_INTEGRATION_GUIDE.md`).
- Database bootstrap schema for local Docker Postgres is in `src/main/resources/schema.sql` (mounted by `docker-compose.yml`).
