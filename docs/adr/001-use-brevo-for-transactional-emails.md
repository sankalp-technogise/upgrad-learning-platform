# 1. Use Brevo for Transactional Emails

Date: 2026-01-28

## Status

Accepted

## Context

The platform requires a reliable method to send transactional emails (OTPs) for user authentication. The current implementation (`ConsoleEmailService`) only logs OTPs to the console, which is suitable for development but not for production or realistic testing. We needed a free SMTP provider that offers a generous daily limit and good deliverability.

## Decision

We will use **Brevo** (formerly Sendinblue) as our transactional email provider.

## Consequences

### Positive

- **High Free Tier Limit**: Brevo offers 300 emails/day on the free tier, which is significantly higher than competitors like SendGrid or Mailgun (often 100/day).
- **Standard Protocol**: Supports standard SMTP, allowing us to use `spring-boot-starter-mail` without vendor-specific SDKs if desired (though APIs are also available).
- **Scalability**: Can be upgraded to paid plans as the platform grows.

### Negative

- **Dependency**: We introduce an external dependency on Brevo's availability.
- **Configuration**: Requires managing API keys/SMTP credentials securely.

### Compliance

- **Security**: Credentials must be injected via environment variables (`BREVO_SMTP_USER`, `BREVO_SMTP_PASSWORD`) and never committed to source code.
