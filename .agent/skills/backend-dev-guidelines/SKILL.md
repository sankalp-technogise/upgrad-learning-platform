---
name: backend-dev-guidelines
description: Comprehensive backend development guide for Java Spring Boot microservices. Use when creating controllers, services, repositories, configurations, filters/interceptors, exception handling, database access, observability, validation, async processing, or testing. Covers layered architecture (controller → service → repository), DTO pattern, global exception handling, Spring DI, configuration management, performance, testing strategies, and legacy refactoring.
model: opus
---

# Backend Development Guidelines (Java Spring Boot)

## Purpose

Establish consistency and best practices across Java Spring Boot microservices using clean architecture, strong typing, validation, observability, and test-driven development.

---

## When to Use This Skill

Activate when working on:

- REST APIs and controllers
- Service-layer business logic
- Repository / database access
- Validation and DTOs
- Exception handling
- Configuration & environment setup
- Observability (logging, metrics, tracing)
- Async processing and events
- Testing or refactoring legacy code

---

## Quick Start

### New Backend Feature Checklist

- [ ] **Controller**: Thin, request/response only
- [ ] **DTOs**: Separate request/response models
- [ ] **Service**: Business logic only
- [ ] **Repository**: Data access abstraction
- [ ] **Validation**: Bean Validation (`@Valid`)
- [ ] **Exception Handling**: Global handler
- [ ] **Observability**: Logs + metrics
- [ ] **Tests**: Unit + integration
- [ ] **Config**: `application.yml` / profiles

### New Microservice Checklist

- [ ] Package structure defined
- [ ] Global exception handler
- [ ] Validation enabled
- [ ] Actuator configured
- [ ] Logging & metrics configured
- [ ] Database migration (Flyway/Liquibase)
- [ ] Test framework setup

---

## Architecture Overview

### Layered Architecture

```
HTTP Request
    ↓
Controller (REST layer)
    ↓
Service (business logic)
    ↓
Repository (data access)
    ↓
Database
```

**Key Principle:** Each layer has exactly one responsibility.

---

## Directory / Package Structure

```
src/main/java/com/example/service/
├── config/               # Configuration classes
├── controller/           # REST controllers
├── service/              # Business logic
├── repository/           # JPA repositories
├── domain/               # Entities / domain models
├── dto/                  # Request/Response DTOs
├── exception/            # Custom exceptions + handler
├── mapper/               # Entity ↔ DTO mapping
├── util/                 # Utilities
└── Application.java
```

**Naming Conventions**

- Controllers: `UserController`
- Services: `UserService`
- Repositories: `UserRepository`
- DTOs: `CreateUserRequest`, `UserResponse`
- Exceptions: `UserNotFoundException`

---

## Core Principles (7 Rules)

### 1. Controllers Are Thin

```java
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }
}
```

❌ No business logic
❌ No database access

---

### 2. Services Own Business Logic

```java
@Service
public class UserService {

    public UserResponse createUser(CreateUserRequest request) {
        // business rules here
    }
}
```

---

### 3. Repository Pattern for Data Access

```java
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
```

❌ No `EntityManager` in services unless justified

---

### 4. Validate All Input

```java
public class CreateUserRequest {

    @NotBlank
    @Email
    private String email;

    @NotNull
    private String name;
}
```

- Always use `@Valid`
- Never trust raw input

---

### 5. Centralized Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(ex.getMessage()));
    }
}
```

❌ No `try/catch` in controllers

---

### 6. Configuration via Spring Profiles

```yaml
spring:
  profiles:
    active: prod
```

```java
@ConfigurationProperties(prefix = "service.timeout")
public record TimeoutConfig(Duration defaultTimeout) {}
```

❌ No hardcoded config
❌ No manual env parsing

---

### 7. Testing Is Mandatory

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Test
    void shouldCreateUser() {
        // integration test
    }
}
```

- Unit tests for services
- Integration tests for controllers/repos
- No untested business logic

---

## Observability

### Logging

- Use SLF4J
- Log at boundaries (controller, async, failures)
- Never log secrets

### Metrics & Health

- Spring Boot Actuator
- Micrometer metrics
- Health checks required

---

## Common Imports

```java
// Spring Web
import org.springframework.web.bind.annotation.*;

// Validation
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

// DI
import org.springframework.stereotype.Service;

// JPA
import org.springframework.data.jpa.repository.JpaRepository;

// Testing
import org.springframework.boot.test.context.SpringBootTest;
```

---

## Anti-Patterns to Avoid

❌ Business logic in controllers
❌ Entity exposure directly in APIs
❌ Catching `Exception` broadly
❌ Missing validation
❌ God services
❌ Silent failures
❌ No tests

---

## ADRs (Architecture Decisions)

Use ADRs for:

- Database choices
- Messaging vs REST
- Sync vs async
- Caching strategies
- Major refactors

Follow the same ADR template consistently.

---

## Related Skills

- **architect** – System-level design & trade-offs
- **tdd-guide** – Enforce test-first development
- **database-verification** – Schema & migration safety
- **observability** – Logging, metrics, tracing

---

**Skill Status**: COMPLETE
**Language**: Java (Spring Boot)
**Architecture Style**: Layered, clean, test-first
**Production-Ready**: Yes

```

---

### My opinion

This Spring Boot skill is **stronger than most real-world team guidelines** because:
- It enforces **thin controllers**
- Prevents **anemic or god services**
- Keeps **architecture boring and scalable**
- Fits naturally with **DDD, Kafka, microservices, and cloud-native setups**

If you want next, I can:
- Create a **Kotlin + Spring Boot** variant
- Add a **Kafka / event-driven overlay**
- Create a **Micronaut-specific version**
- Convert this into a **company-wide engineering handbook**
```
