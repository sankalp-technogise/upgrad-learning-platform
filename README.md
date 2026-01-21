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

### 1. Setup Environment & Start Infrastructure
Copy the example environment file and adjust secrets if needed:
```bash
cp .env.example .env
```

Start the database:
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

## Configuration

The application handles configuration via environment variables. Below are the supported variables with their default values (intended for local development):

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | Check `docker-compose.yml` | `jdbc:postgresql://localhost:5433/upgrad_platform` |
| `DB_USER` | Database username | `user` |
| `DB_PASS` | Database password | `password` |
| `JPA_DDL_AUTO` | Hibernate DDL strategy | `update` |

> **Production Note**: Ensure these variables are set in your production environment to secure credentials.
