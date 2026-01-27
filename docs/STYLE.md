# Project Style Guide

## Principles

1. **Consistency**: Code should look like it was written by a single person.
2. **Automation**: Style should be enforced by tools (Spotless, Prettier), not arguments.
3. **Self-Documentation**: Avoid comments that explain "what" the code does. Use comments only for "why".

## Backend (Java)

- **Format**: Google Java Format (AOSP).
- **Indentation**: 4 spaces.
- **Line Length**: 100 characters (soft limit), 120 (hard limit).
- **Tooling**: Spotless Gradle Plugin.

## Frontend (React/TS) & Config

- **Format**: Prettier Defaults.
- **Indentation**: 2 spaces.
- **Quotes**: Single quotes preferred (where applicable).
- **Semi-colons**: No semi-colons (unless required).
- **Tooling**: Prettier + ESLint.

## Rules

### No Unnecessary Comments

❌ **Bad:**

```java
// Increment count
count++;

// Check if user is active
if (user.isActive()) { ... }
```

✅ **Good:**

```java
// Synchronizing to prevent race condition during high-load
synchronized(lock) {
  count++;
}
```

If the code is unclear, refactor the code instead of adding a comment.
