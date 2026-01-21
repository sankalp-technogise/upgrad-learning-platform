---
trigger: always_on
---

# Testing Requirements

## Minimum Test Coverage: 80%

Test Types (ALL required):
1. **Unit Tests** – Individual functions, utilities, components
2. **Integration Tests** – API endpoints, database operations
3. **E2E Tests** – Critical user flows (Playwright)

---

## Test-Driven Development (TDD)

MANDATORY workflow:
1. Write test first (RED)
2. Run test – it should FAIL
3. Write minimal implementation (GREEN)
4. Run test – it should PASS
5. Refactor (IMPROVE)
6. Verify coverage (80%+)

> This workflow **must be enforced using the `tdd-guide` skill** for all new features and non-trivial changes.

---

## Troubleshooting Test Failures

When tests fail:
1. **Invoke `tdd-guide` skill** to analyze failures
2. Check test isolation
3. Verify mocks and fixtures
4. Fix implementation, not tests (unless test logic is incorrect)

---

## E2E Testing

- All critical user flows **must be validated using Playwright**
- **Use `e2e-runner` skill** to:
  - Design E2E scenarios
  - Implement Playwright tests
  - Validate stability and flakiness
