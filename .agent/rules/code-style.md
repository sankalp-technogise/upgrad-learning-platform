---
trigger: always_on
---

# Code Style Rules (Java / Spring Boot)

## 1. Immutability

- **Domain Objects**: MUST be immutable. No setters.
- **DTOs**: MUST be Records or immutable classes.
- **Method Arguments**: MUST NOT be mutated.
- **Collections**: MUST use defensive copies or immutable collections.
- **State**: NO shared mutable state allowed in services or components.

## 2. File & Class Organization

- **Class Size**: HARD LIMIT of 800 lines. Refactor if exceeded.
- **Cohesion**: Classes MUST have a single responsibility.
- **Helpers**: Extract logic into helper classes or static utilities.

## 3. Error Handling

- **Exceptions**: NEVER swallow exceptions.
- **Catch Blocks**: NEVER catch `Exception` or `Throwable` broadly.
- **Custom Exceptions**: USE custom exceptions for domain errors.
- **Controllers**: MUST rely on global exception handling ( `@ControllerAdvice`).

## 4. Input Validation

- **Boundaries**: ALL external inputs MUST be validated at the controller level using `@Valid`.
- **Trust**: NEVER trust client input.
- **Fail Fast**: Applications MUST fail fast on invalid data.

## 5. Code Hygiene

- **Logging**: NO `System.out.println`. Use SLF4J.
- **Hardcoded Values**: NO hardcoded secrets or magic numbers. Use `@Value` or constants.
- **Nesting**: NO nesting deeper than 4 levels.
- **Method Size**: Methods SHOULD be small (<50 lines).
- **Comments**: NO unnecessary comments ("what"). Comment only "why".

## 6. Testing

- **Coverage**: Minimum 80% coverage required.
- **Assertions**: Tests MUST include assertions.
