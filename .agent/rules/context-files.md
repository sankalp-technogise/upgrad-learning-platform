---
trigger: always_on
---

# Context Files (Memory Banks)

These files act as "external memory" that prevents the AI from forgetting project constraints over long sessions.

---

## Context Loading Protocol

> [!IMPORTANT]
> **System Instruction**: At the start of a session, scan the `docs` directory for `SPEC.md` and `STYLE.md`. If these files exist, ingest them as the primary context.

If a user request contradicts the content of `docs/SPEC.md`, **stop and ask for clarification**:

> "This request contradicts the agreed architecture in docs/SPEC.md. Should we update the spec?"

---

## 1. The Living Specification (`docs/SPEC.md`)

**Also known as**: `PROJECT.md`

### Purpose

Contains the high-level design, architectural decisions, and approved feature plan.

### Rule

Treat this file as the **absolute source of truth**. Before writing code, read this file to ensure alignment with the master plan.

### Maintenance

If the user changes requirements during the chat, you **must update `docs/SPEC.md`** to reflect the new consensus before implementing code. This prevents 'context loss' and ensures the spec remains a living executable artifact.

### Versioning & Governance

**Rule of Thumb**: Treat the spec like code (reviews + versioning).

**When to Push `docs/SPEC.md`**:

- [x] It represents agreed product/feature requirements
- [x] Humans have reviewed/edited it (not a raw AI dump)
- [x] It serves as the source of truth for implementation
- [x] Traceability is needed (spec → code → PR)

**When NOT to Push**:

- [ ] It is ephemeral (regenerated every run)
- [ ] It contains prompt noise or hallucinations
- [ ] It is purely an intermediate AI artifact

> [!TIP]
> **Best Practice**: Commit a curated `docs/SPEC.md` or `docs/REQUIREMENTS.md`. Keep raw AI output ignored (e.g., `spec.generated.md` in `.gitignore`).

---

## 2. The Style Guide (`docs/STYLE.md`)

**Also known as**: `CONTRIBUTING.md`

### Purpose

Documents the project's specific naming conventions, preferred libraries, and formatting rules.

### Rule

Prioritize conventions in this file **over general language standards**. If this file provides examples (e.g., "Use `List<String>` instead of `var`"), you must follow them strictly.

### Anti-Pattern Check

Use this file to verify you are not introducing:

- Inconsistent abstractions
- Forbidden dependencies
- Style violations

---

## 3. The Project Entry Point (`README.md`)

### Purpose

Defines the project overview, build instructions, and component summary.

### Rule

- **Greenfield**: When initializing a project, generate this file first
- **Feature Development**: When adding new features or changing APIs, update this file to keep documentation synchronized with code

### Explainer Role

Use the 'Explainer Pattern' to generate summaries for this file based on the code you write.

---

## 4. Documentation Location

### Rule

**All markdown documentation files** MUST be placed in the `docs/` directory. Do not create `.md` files in the root directory (except `README.md`).

---

## Quick Reference

| File            | Purpose                               | When to Update                        |
| --------------- | ------------------------------------- | ------------------------------------- |
| `docs/SPEC.md`  | Architecture & design decisions       | When requirements change              |
| `docs/STYLE.md` | Coding conventions & standards        | When new patterns are adopted         |
| `README.md`     | Project overview & build instructions | When adding features or changing APIs |
