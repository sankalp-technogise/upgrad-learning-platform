---
trigger: always_on
---

# Security Guidelines (Java / Spring Boot)

## Mandatory Security Checks

Before ANY commit:

- [ ] No hardcoded secrets (API keys, passwords, tokens, certificates)
- [ ] All user inputs validated (`@Valid`, Bean Validation)
- [ ] SQL injection prevention (JPA / parameterized queries only)
- [ ] XSS prevention (output encoding, no raw HTML rendering)
- [ ] CSRF protection enabled (Spring Security)
- [ ] Authentication & authorization verified (method or endpoint level)
- [ ] Rate limiting applied to public-facing endpoints
- [ ] Error responses do not leak sensitive or internal details
- [ ] Sensitive data masked in logs

---

## Secret Management

```java
// ❌ NEVER: Hardcoded secrets
private static final String API_KEY = "sk-proj-xxxxx";

// ✅ ALWAYS: Externalized configuration
@Value("${openai.api-key}")
private String apiKey;

@PostConstruct
void validateConfig() {
    if (apiKey == null || apiKey.isBlank()) {
        throw new IllegalStateException("OPENAI_API_KEY is not configured");
    }
}
```
