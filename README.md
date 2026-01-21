# On-Demand Learning Platform (Beta)

> **Status**: Bootstrapping
> **Tech Stack**: Java Spring Boot (Backend), React (Frontend), PostgreSQL (Database)

## Architecture
This project is a monorepo containing:
- `/backend`: Java Spring Boot REST API
- `/frontend`: React Web Application (Vite/TypeScript)

See [SPEC.md](./SPEC.md) for detailed architecture and design decisions.

## Quick Start
You need Java 21+ and Node.js 18+ installed.

### 1. Start Infrastructure
```bash
make docker-up
```

### 2. Run Backend
```bash
make run-backend
```

### 3. Run Frontend
```bash
make run-frontend
```

## Developer Commands

### Build
```bash
make build
```

### Test
```bash
make test
```
