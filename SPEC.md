# Project Specification

> **Status**: Beta Scoping
> **Last Updated**: 2026-01-21

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

## 5. Security & Compliance
- **Authentication**: JWT-based stateless auth.
- **Privacy**: Minimal PII collection (Email only).
- **Content Security**: Public/Signed URLs for Beta; no DRM initially.

## 6. Open Questions
- Web Push Notifications strategy (PWA or basic browser notifications)?
- Exact video hosting solution (S3/CloudFront assumed).
