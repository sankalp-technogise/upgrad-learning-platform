---
description: New project setup from requirement
---

# Workflow: Project Spec → Buildable Monorepo Bootstrap

## Goal

Parse a **project description / product specification file** and generate a **production-ready monorepo** that:
- Contains frontend and backend in a single repo
- Builds, tests, and deploys independently
- May contain **no business features initially**
- Is CI-ready, testable, and deployable from day one

This workflow focuses on **correct ordering of agent personas**.

---

## Preconditions

- A project description file exists (e.g. `product-spec.md`, `requirements.md`)
- Repository may be empty or newly created
- No implementation code exists yet

---

## Step-by-Step Workflow (STRICT ORDER)

---

## Step 1: Parse & Normalize Requirements

### Persona: `product-clarifier` (if available)  
*(If not available, `planner` performs this step lightly)*

**Input**
- Project description file

**Actions**
- Read and summarize:
  - Product scope
  - Frontend needs (UI vs API-only)
  - Backend needs
  - Non-functional requirements
- Identify:
  - Assumptions
  - Constraints
  - Open questions

**Output**
- Normalized requirements summary
- Explicit assumptions documented
- Open questions listed (do NOT block bootstrap)

---

## Step 2: Define System Architecture

### Persona: `architect` (MANDATORY)

**Input**
- Normalized requirements
- Assumptions and constraints

**Actions**
- Decide:
  - Monorepo usage (confirmed)
  - Frontend vs backend responsibility split
  - High-level tech direction (not implementation yet)
  - Deployment independence
- Define:
  - Logical system boundaries
  - Data flow (even if empty initially)
  - Default patterns to follow

**Output**
- Architecture overview
- Repo-level decisions
- Any required ADRs

**Rule**
- No file-level planning yet
- No implementation steps

---

## Step 3: Create Execution Plan

### Persona: `planner` (MANDATORY)

**Input**
- Architecture decisions
- Product requirements (even if minimal)

**Actions**
- Produce a bootstrap implementation plan covering:
  - Repo creation
  - Folder structure
  - Build tooling
  - Test scaffolding
  - Deployment scaffolding
- Plan phases:
  1. Repo structure
  2. Frontend bootstrap
  3. Backend bootstrap
  4. Root-level orchestration
  5. CI / deploy readiness

**Output**
- Step-by-step implementation plan
- Success criteria:
  - `frontend` builds
  - `backend` builds
  - Root-level test command passes
  - Deployment artifacts produced

---

## Step 4: Generate Monorepo Structure

### Persona: `monorepo-fullstack` (MANDATORY)

**Input**
- Planner execution plan
- Architect constraints

**Actions**
- Create repository structure:
  - `/frontend`
  - `/backend`
  - `/scripts`
  - Root orchestration files
- Add:
  - Root build/test commands
  - README documentation
- Ensure:
  - Frontend and backend are fully isolated
  - No cross-dependencies

**Output**
- Monorepo skeleton
- Root-level commands functional

---

## Step 5: Bootstrap Backend (Empty but Healthy)

### Personas:
- `backend-dev-guidelines`
- `tdd-guide`

**Actions**
- Create backend application with:
  - Application entry point
  - Health endpoint
  - Config scaffolding
  - Empty domain
- Add:
  - Unit test (passes)
  - Integration test (passes)
- Ensure:
  - Backend builds
  - Backend tests pass
  - Backend can be deployed (Docker or equivalent)

**Output**
- Buildable, testable backend with zero features

---

## Step 6: Bootstrap Frontend (Empty but Healthy)

### Personas:
- `frontend-dev-guidelines`
- `tdd-guide`

**Actions**
- Create frontend application with:
  - App shell
  - Health/status page
- Add:
  - Unit/component test
- Ensure:
  - Frontend builds
  - Frontend tests pass
  - Frontend can be deployed independently

**Output**
- Buildable, testable frontend with zero features

---

## Step 7: Root-Level Verification

### Personas:
- `planner` (light check)
- `security-reviewer` (basic scan)

**Actions**
- Run:
  - Root-level build
  - Root-level test
- Verify:
  - No hardcoded secrets
  - Safe defaults
- Confirm success criteria

**Output**
- Verified green baseline
- Ready-for-feature-development repo

---

## Step 8: Handoff for Feature Development

### Outcome

The system is now:
- Architecturally defined
- Structurally sound
- Buildable
- Testable
- Deployable
- Feature-ready

Future work starts at:
→ `planner` → `architect` (if needed) → implementation skills

---

## Success Criteria (MANDATORY)

- [ ] `frontend` builds successfully
- [ ] `backend` builds successfully
- [ ] Root-level `test` command passes
- [ ] Both sides deploy independently
- [ ] No feature code required to pass CI

---

## Mental Model

> **Architecture first → Planning second → Structure before features → Green baseline always**

---

**Workflow Status**: READY  
**Use Case**: New product bootstrap  
**Supports**: Empty but production-ready repositories
