---
name: monorepo-fullstack
description: Full-stack monorepo development skill for building frontend and backend in a single repository with clear separation. Use when creating, modifying, or maintaining projects where frontend and backend live together but are built, tested, and deployed independently. Supports different languages, frameworks, and deployment pipelines per side.
tools: Read, Grep, Glob, Bash
model: opus
---

# Monorepo Full-Stack Development Skill

## Purpose

Enable consistent, scalable development of **frontend and backend code in a single repository** while keeping:
- Clear separation of concerns
- Independent build, test, and deploy pipelines
- Language and framework freedom per project

This skill enforces structure, conventions, and root-level tooling.

---

## Repository Structure (MANDATORY)

repo-root/
├── frontend/ # Frontend application
│ ├── src/
│ ├── tests/
│ ├── package.json | build.gradle | etc
│ └── README.md
├── backend/ # Backend application
│ ├── src/
│ ├── tests/
│ ├── pom.xml | build.gradle | package.json | etc
│ └── README.md
├── scripts/ # Root-level helper scripts
├── docs/ # Architecture & ADRs
├── .gitignore
├── README.md # Monorepo overview
└── Makefile | taskfile.yml # Root-level commands


Rules:
- Frontend code MUST live only in `frontend/`
- Backend code MUST live only in `backend/`
- No cross-imports between frontend and backend
- Shared contracts (if any) go in `/docs` or `/contracts`

---

## Language & Framework Independence

- Frontend and backend MAY use different languages
- Frontend examples: React, Next.js, Vue, Angular
- Backend examples: Spring Boot, Micronaut, Node.js, Go
- Each side owns its own dependencies, build, and runtime

---

## Root-Level Build & Test Tools

### Standard Root Commands (REQUIRED)

Root-level commands MUST delegate to the correct subproject.

#### Example: Makefile

```makefile
.PHONY: frontend backend test build clean

frontend:
	cd frontend && npm install && npm run build

backend:
	cd backend && ./gradlew build

test:
	cd frontend && npm test
	cd backend && ./gradlew test

clean:
	cd frontend && rm -rf dist node_modules
	cd backend && ./gradlew clean
Alternative: Taskfile
version: "3"

tasks:
  frontend:build:
    cmds:
      - cd frontend && npm run build

  backend:build:
    cmds:
      - cd backend && ./gradlew build

  test:
    cmds:
      - cd frontend && npm test
      - cd backend && ./gradlew test
Rules:

Root commands MUST NOT contain business logic

Root only orchestrates sub-project commands

CI must call root-level commands

Build & Deploy Separation
Frontend and backend MUST be deployable independently

Separate Dockerfiles allowed:

frontend/Dockerfile

backend/Dockerfile

CI pipelines may share triggers but not artifacts

Development Workflow
New Feature Workflow
Identify affected side(s): frontend, backend, or both

Implement changes inside respective folder only

Run root-level tests

Update root README if contracts change

Commit with scoped message (frontend: / backend:)

Testing Rules
Frontend:

Unit + component tests mandatory

Backend:

Unit + integration tests mandatory

Root-level test command MUST run all tests

No feature is complete without passing root tests

Configuration Rules
Each project manages its own config

No shared .env across frontend and backend

Secrets managed per deployment unit

Environment-specific configs stay local to project

Anti-Patterns to Avoid
❌ Mixing frontend and backend code
❌ Shared runtime dependencies
❌ Root-level application logic
❌ Single deployment for entire repo
❌ Cross-project imports
❌ Implicit coupling via filesystem access

Documentation Requirements
Root README.md:

Repo overview

How to build/test from root

Deployment strategy (high-level)

Each subproject:

Own README

Local setup instructions

Tech stack details

Related Skills
backend-dev-guidelines – Backend architecture & coding rules

frontend-dev-guidelines – Frontend structure & patterns

architect – System-level decisions & trade-offs

tdd-guide – Test-first enforcement

Skill Scope: Full-stack monorepo
Deployment Model: Independent frontend & backend
Scalability: High
Recommended For: SaaS, internal platforms, microservice-backed UIs