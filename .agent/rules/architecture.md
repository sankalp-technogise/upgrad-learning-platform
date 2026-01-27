---
trigger: always_on
---

# Architecture Governance

## Mandatory Architectural Review

The `architect` skill MUST be used when:

- Designing new features that introduce new components or services
- Refactoring large or cross-cutting parts of the system
- Making scalability, performance, or security-impacting decisions
- Introducing new infrastructure, databases, or integration patterns
- Defining or modifying core domain models

## Decision Documentation

- Significant architectural decisions MUST result in an ADR
- Trade-offs must be explicitly documented (pros, cons, alternatives)
- Decisions must favor simplicity unless justified otherwise

## Consistency & Quality Gates

- Architectural changes must align with existing system patterns
- Avoid premature optimization and unnecessary abstractions
- Prefer well-known, boring solutions unless requirements demand otherwise
