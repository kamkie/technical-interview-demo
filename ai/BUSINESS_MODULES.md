# Business Modules Guide

`ai/BUSINESS_MODULES.md` is the AI-facing map of the repository's business feature packages.

Use this file when changes affect feature ownership, service boundaries, package placement, or cross-feature dependencies inside `team.jit.technicalinterviewdemo.business`.
This file is descriptive, not authoritative. Behavioral truth still lives in the executable and published spec artifacts described in `AGENTS.md`.

## Module Overview

The business layer is organized by feature, not by technical role.
Each feature package should keep its controller, service, repository, entities, request and response types, and feature-local exceptions close together unless the concern is clearly cross-cutting.

## Feature Packages

### `business.book`

- `BookController`
- `BookService`
- `BookRepository`
- `Book`
- request objects, search support, and domain exceptions
- owns pagination, filtering, optimistic locking, category assignment, and audit logging for books

### `business.category`

- `CategoryController`
- `CategoryService`
- `CategoryRepository`
- `Category`
- request object and startup seed initializer
- owns category creation, list ordering, cache eviction, and admin-only write control

### `business.localization`

- `LocalizationController`
- `LocalizationService`
- `LocalizationRepository`
- `Localization`
- request and response types, supported language policy, seed support, and domain exceptions
- owns localized message lookup, fallback behavior, filtering, write authorization, and cache eviction

### `business.user`

- `UserAccountController`
- `UserAccountService`
- `CurrentUserAccountService`
- `UserAccountRepository`
- `UserAccount`
- request and response types and role enum
- owns persisted user profile data, preferred-language updates, and synchronization of authenticated users into application state

### `business.audit`

- `AuditLogService`
- `AuditLogRepository`
- `AuditLog`
- enums for action and target type
- owns append-only write auditing for feature services

## Placement Rules

- keep non-trivial business rules in feature services
- keep controllers as HTTP adapters, not as business-rule owners
- keep repositories feature-local and concrete unless a real repeated problem justifies something broader
- prefer direct feature-to-feature service calls over repo-internal abstraction layers when the dependency is small and explicit
- move code into `technical.*` only when the concern is truly cross-cutting and no longer belongs to one business feature

## Review Questions

Before moving code across business packages, ask:

- which feature actually owns the behavior?
- would the move make the public or test-facing behavior harder to trace?
- is this a real cross-cutting concern, or just shared code that still belongs to one feature?
- is a new abstraction solving repeated repo reality, or only style preference?
