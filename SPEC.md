# Project Specification

> **Status**: Beta Scoping
> **Last Updated**: 2026-01-27

## 1. Overview
The "On-Demand Learning Platform" is a mobile-first video streaming application designed to deliver educational content to a closed beta group of ~100 users. The system highlights a core loop of "Ingest -> Feed -> Watch -> Recommend -> Measure".

## 2. Goals & Scope
- **Primary Goal**: Validate engagement and content efficacy with a thin slice MVP.
- **Target Audience**: ~100 beta users (mobile only).
- **Key Constraints**:
    - Timeline: 6 weeks.
    - Team: 2 Devs, 1 PM.
    - No dedicated designer.

## 3. Architecture

### 3.1. High-Level Design
Monorepo structure containing a backend service and a web frontend.
- **Repository Strategy**: Monorepo (Frontend + Backend).
- **Frontend**: Web App (React).
- **Backend**: REST API (Java Spring Boot).
- **Database**: PostgreSQL.

### 3.2. Component View

#### Frontend (`/frontend`)
- **Technology**: React (Vite).
- **Responsibilities**:
    - User Authentication (Login/Signup).
    - Content Feed (Infinite scroll/Reels style).
    - Video Playback (HTML5/React Player).
    - Analytics collection.
    - User Preferences (Interests).

#### Backend (`/backend`)
- **Technology**: Java (Spring Boot).
- **Responsibilities**:
    - User Management & Auth.
    - Content Metadata Management (Video URLs, Tags).
    - Feed Generation Logic.
    - Analytics Ingestion.
    - Basic Recommendation Engine (Tag-based).

### 3.3. Data Model (High-Level)
- **User**: `id`, `email`, `interests[]`, `created_at`.
- **Content**: `id`, `title`, `video_url`, `tags[]`, `category`.
- **Engagement**: `user_id`, `content_id`, `watch_duration`, `completed`, `timestamp`.

## 4. Technical Stack Decisions (ADRs)

### ADR-001: Monorepo Structure
- **Decision**: Use a single repository for both frontend and backend.
- **Rationale**: Simplifies CI/CD for a small team, ensures version alignment between API and Client.

### ADR-002: Backend Technology
- **Decision**: Java Spring Boot.
- **Rationale**: Aligned with team expertise (User Rules favor Java), strongly typed, robust ecosystem for future scaling.

### ADR-003: Frontend Technology
- **Decision**: React (Vite).
- **Rationale**: User explicit request. Modern, robust web ecosystem.
- **Note**: Development environment supports NPM/Node.js.

### ADR-004: Database
- **Decision**: PostgreSQL.
- **Rationale**: Robust relational data handling for users and content metadata.

### ADR-005: Static Code Analysis
- **Decision**: Serverless static analysis using strict local tools.
- **Backend Tools**: SpotBugs (Bytecode analysis) & PMD (Source analysis).
- **Frontend Tools**: ESLint.
- **Enforcement**: CI Pipeline MUST fail if violations are found.
- **Rationale**: Zero-dependency analysis that runs entirely within the build pipeline without external servers. Validates both source patterns and compiled bytecode.

## 5. Security & Compliance
- **Authentication**: JWT-based stateless auth.
- **Privacy**: Minimal PII collection (Email only).
- **Content Security**: Public/Signed URLs for Beta; no DRM initially.

## 6. Open Questions
- Web Push Notifications strategy (PWA or basic browser notifications)?
- Exact video hosting solution (S3/CloudFront assumed).

## 7. Versioning & Governance

**Rule of Thumb**: Treat the spec like code (reviews + versioning).

**When to Push `SPEC.md`**:
- [x] It represents agreed product/feature requirements
- [x] Humans have reviewed/edited it (not a raw AI dump)
- [x] It serves as the source of truth for implementation
- [x] Traceability is needed (spec → code → PR)

**When NOT to Push**:
- [ ] It is ephemeral (regenerated every run)
- [ ] It contains prompt noise or hallucinations
- [ ] It is purely an intermediate AI artifact

> [!TIP]
> **Best Practice**: Commit a curated `SPEC.md` or `REQUIREMENTS.md`. Keep raw AI output ignored (e.g., `spec.generated.md` in `.gitignore`).
