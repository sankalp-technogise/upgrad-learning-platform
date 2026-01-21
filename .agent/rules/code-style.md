---
trigger: always_on
---

# Coding Style (Java / Spring Boot)

## Immutability (CRITICAL)

ALWAYS prefer immutable objects.  
NEVER mutate shared state.

```java
// ❌ WRONG: Mutation
public User updateUser(User user, String name) {
    user.setName(name); // MUTATION
    return user;
}

// ✅ CORRECT: Immutability
public User updateUser(User user, String name) {
    return new User(
        user.getId(),
        name,
        user.getEmail()
    );
}
Rules:

Prefer immutable domain objects

Use constructors / builders instead of setters

Avoid mutating method arguments

Defensive copies for collections

File & Class Organization
MANY SMALL CLASSES > FEW LARGE CLASSES:

High cohesion, low coupling

Typical size: 200–400 lines

Hard limit: 800 lines

Extract helpers, mappers, utilities

Organize by feature/domain, not technical layer alone

Error Handling
ALWAYS handle errors explicitly and consistently:

try {
    return riskyOperation();
} catch (SpecificException ex) {
    log.error("Operation failed", ex);
    throw new BusinessException("User-friendly error message", ex);
}
Rules:

Never swallow exceptions

Never catch Exception or Throwable broadly

Use custom exceptions for domain errors

Controllers must rely on global exception handling

Input Validation
ALWAYS validate external input at boundaries:

public record CreateUserRequest(
    @NotBlank @Email String email,
    @Min(0) @Max(150) int age
) {}
Rules:

Validate at controller level using @Valid

Never trust client input

Fail fast on invalid data

Code Quality Checklist
Before marking work complete:

 Code is readable and intention-revealing

 Classes are focused and cohesive

 Methods are small (<50 lines)

 Files are focused (<800 lines)

 No deep nesting (>4 levels)

 Proper exception handling

 No System.out.println

 No hardcoded values

 No shared mutable state


---

### My opinion

This rule is **deliberately strict** because Java codebases degrade through:
- mutable domain models
- oversized service classes
- silent exception handling

Enforcing immutability and small, focused classes keeps Spring Boot systems **maintainable for years**, not months.

If you want, I can next:
- Add a **Kotlin-first variant**
- Create an **immutability enforcement checklist**
- Align this rule with **DDD aggregates**
- Map this to **Sonar / Detekt / Checkstyle rules**