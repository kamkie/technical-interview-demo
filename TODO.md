# Project TODO & Roadmap

This file outlines planned features, improvements, and refactoring tasks for the technical-interview-demo Spring Boot application.

## ✅ Completed Infrastructure

- ✅ [Development Container (Dev Containers)](#development-container-setup) - VS Code dev container with Java 25, Docker, and supporting services
- ✅ [Phase 2.1: Profile-Based Configuration Split](#phase-21-profile-based-configuration-split) - Environment-specific configuration for local, prod, and test
- ✅ [Phase 1.1: PostgreSQL Migration](#phase-11-migrate-from-h2-to-postgresql) - PostgreSQL driver and production configuration ready

## Legend

- 🟢 **Ready** - No blocking dependencies
- 🟡 **Blocked** - Waiting for dependencies
- ✅ **Done** - Already implemented
- 📋 **In Progress** - Currently being worked on

---

## Development Container Setup ✅

A comprehensive dev container setup is available for VS Code Remote development with zero-friction setup.

**Included:**
- **Java 25 JDK** (official Microsoft dev container image)
- **Gradle** (via wrapper)
- **Docker & Docker Compose** (Docker-in-Docker)
- **PostgreSQL service** (port 5432, auto-starts via docker-compose)
- **Prometheus service** (port 9090, auto-starts via docker-compose)
- **Preconfigured VS Code extensions** (Java Pack, Spring Boot, Docker, GitLens, SonarLint, Copilot)
- **Helper commands** (20+ shortcuts via `.devcontainer/commands.sh`)
- **Environment variables** (properly configured for all tools)
- **Lifecycle scripts** (automatic setup, health checks, and cleanup)

**Usage:**
1. Install VS Code and "Dev Containers" extension
2. Open project in VS Code
3. Press `Ctrl+Shift+P` → "Dev Containers: Reopen in Container"
4. Wait for initialization (5-10 minutes first time)

**Key Features:**
- **Zero local setup required** - Everything runs in containers
- **Consistent environment** - All developers use identical tooling
- **Isolated development** - No interference with local machine
- **Auto-starting services** - PostgreSQL and Prometheus start automatically
- **Helper scripts** - Convenient aliases for common Gradle tasks
- **Pre-configured extensions** - Java, Spring Boot, Docker, and more
- **Proper Java 25 support** - Official Microsoft image with latest JDK

See [.devcontainer/README.md](.devcontainer/README.md) for complete documentation and [.devcontainer/QUICK_START.md](.devcontainer/QUICK_START.md) for a one-page reference.

---

## Phase 1: Infrastructure & Database Migration

### 1.1 Migrate from H2 to PostgreSQL ✅

A production-ready relational database migration to replace the in-memory H2 database.

**Status:** Completed

**Implementation Details:**
- ✅ Added PostgreSQL JDBC driver to `build.gradle.kts`
- ✅ Created PostgreSQL configuration in `application-prod.properties`
- ✅ Configured HikariCP connection pooling (20 max, 5 min)
- ✅ Support environment variable override (`DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER`, `DATABASE_PASSWORD`)
- ✅ Created `docker-compose.yml` for local PostgreSQL development
- ✅ Updated README with PostgreSQL setup instructions
- ✅ Verified existing Flyway migration is PostgreSQL-compatible
- ✅ All tests pass (44 tests)
- ✅ Spotless formatting passes
- ✅ PMD checks pass

**Production Profile Configuration:**
```properties
spring.datasource.url=jdbc:postgresql://${DATABASE_HOST:localhost}:${DATABASE_PORT:5432}/${DATABASE_NAME:technical_interview_demo}
spring.datasource.username=${DATABASE_USER:postgres}
spring.datasource.password=${DATABASE_PASSWORD:changeme}
```

**Local Testing:**
```powershell
# Start PostgreSQL
docker-compose up -d

# Run app with prod profile
.\gradlew.bat bootRun --args='--spring.profiles.active=prod'

# Stop PostgreSQL
docker-compose down
```

**Commit:** 4d99e58

---

### 1.2 Add Testcontainers for Integration Testing 🟢

Depends on: 1.1 (PostgreSQL migration) ✅ **COMPLETED**

Use Testcontainers to run PostgreSQL in Docker during tests for better database testing without relying on in-memory H2.

**Tasks:**
- [ ] Add Testcontainers dependency to `build.gradle.kts` (`org.testcontainers:testcontainers`, `org.testcontainers:postgresql`)
- [ ] Create a `@TestcontainersTest` annotation or base test class for database integration tests
- [ ] Refactor existing database tests to use Testcontainers
- [ ] Configure test profile (`application-test.properties`) to use Testcontainers PostgreSQL
- [ ] Add Testcontainers initialization in test suite setup
- [ ] Document the Testcontainers setup for team members
- [ ] Optionally add Testcontainers support for other services (Redis, RabbitMQ if added later)

**Definition of Done:**
- All integration tests run with Testcontainers PostgreSQL
- Test execution time is acceptable
- Tests are reproducible across environments
- `test` task passes successfully

---

## Phase 2: Configuration Management

### 2.1 Profile-Based Configuration Split ✅

Reorganize application configuration for local development, testing, and production environments.

**Status:** Completed

**Implementation Details:**
- ✅ Created `src/main/resources/application-local.properties` with H2 and debug logging
- ✅ Created `src/main/resources/application-prod.properties` with production settings (ready for PostgreSQL migration)
- ✅ Created `src/test/resources/application-test.properties` with test isolation
- ✅ Moved H2 configuration to local profile
- ✅ Kept common defaults in main `application.properties`
- ✅ Set `spring.profiles.active=local` as default
- ✅ Updated Dockerfile to use `prod` profile
- ✅ Updated README.md with profile documentation
- ✅ Updated AGENTS.md with profile documentation
- ✅ All tests pass (44 tests)
- ✅ Spotless formatting passes
- ✅ PMD checks pass

**Profiles Available:**
- `local` (default) - Development with H2, debug logging
- `prod` - Production with PostgreSQL (ready for Phase 1.1)
- `test` - Testing with isolated H2 database

**Commit:** 9e5185c

---

## Phase 3: Internationalization (i18n) & Localization Messages

### 3.1 Create LocalizationMessage Entity 🟡

Depends on: 2.1 (Profiles ready) and 1.2 (Testcontainers ready—optional for testing)

Add a new entity to store localized error and info messages in the database for multi-language support.

**Tasks:**
- [ ] Create `LocalizationMessage` JPA entity with fields:
  - `id` (Long, primary key, auto-generated)
  - `messageKey` (String, unique, e.g., "error.book.not_found")
  - `language` (String, e.g., "en", "es", "de", "fr")
  - `messageText` (String or Text, the actual translated message)
  - `description` (optional, for documentation)
  - `createdAt` (Timestamp)
  - `updatedAt` (Timestamp)
- [ ] Add composite unique constraint on `(messageKey, language)`
- [ ] Create `LocalizationMessageRepository` extending `JpaRepository`
- [ ] Create Flyway migration script to create `localization_messages` table
- [ ] Create `LocalizationMessageService` with methods:
  - `getMessage(messageKey: String, language: String): String`
  - `getMessageWithFallback(messageKey: String, language: String, fallbackLanguage: String): String`
  - `getAllMessages(language: String): Map<String, String>`
- [ ] Add AOP logging for message retrieval (optional, for debugging)
- [ ] Add test seed data: common error messages in English and another language

**Entity Design Considerations:**
- Consider lazy-loading or caching for performance
- Consider adding `active` flag to soft-delete messages
- Consider versioning for message updates

**Definition of Done:**
- Entity compiles and passes Error Prone checks
- Migration creates table in both H2 and PostgreSQL
- Service layer properly retrieves and caches messages
- Tests verify message retrieval with fallback behavior
- Seed data includes sample messages for en, es, de

---

### 3.2 Create Localization REST API 🟡

Depends on: 3.1 (LocalizationMessage entity)

Build a full REST API for managing localization messages.

**Tasks:**
- [ ] Create `LocalizationMessageRequest` DTO with fields: `messageKey`, `language`, `messageText`, `description`
- [ ] Create `LocalizationMessageResponse` DTO with fields: `id`, `messageKey`, `language`, `messageText`, `description`, `createdAt`, `updatedAt`
- [ ] Create `LocalizationMessageController` with endpoints:
  - `GET /api/localization-messages` (list all, paginated)
  - `GET /api/localization-messages/{id}`
  - `GET /api/localization-messages/key/{messageKey}/lang/{language}` (get by key and language)
  - `POST /api/localization-messages` (create new message)
  - `PUT /api/localization-messages/{id}` (update message)
  - `DELETE /api/localization-messages/{id}`
  - `GET /api/localization-messages/language/{language}` (get all messages for a language)
- [ ] Add validation:
  - `messageKey` is required and must match pattern `^[a-z0-9._-]+$`
  - `language` is required and must be ISO 639-1 code (en, es, de, fr, etc.)
  - `messageText` is required and length > 0
- [ ] Implement pagination and sorting
- [ ] Add Spring REST Docs tests for all endpoints
- [ ] Add integration tests for CRUD operations
- [ ] Add error handling:
  - `404` when message not found
  - `409` when duplicate `(messageKey, language)` on create
  - `400` for validation failures

**Definition of Done:**
- All endpoints respond with proper HTTP status codes
- Responses use `LocalizationMessageResponse` DTOs
- Tests document all endpoints with Spring REST Docs
- No validation errors escape to the client
- API can create, retrieve, update, delete messages

---

### 3.3 Seed Initial Localization Messages 🟡

Depends on: 3.2 (API created)

Populate the database with initial localization messages for all current error scenarios.

**Tasks:**
- [ ] Analyze current `ExceptionHandler` and identify all error message keys
- [ ] Create comprehensive message key naming convention documentation
- [ ] Seed messages for:
  - Book not found: `error.book.not_found`
  - Duplicate ISBN: `error.book.isbn_duplicate`
  - Validation failures: `error.validation.field`
  - Stale update: `error.book.stale_version`
  - Invalid request: `error.request.invalid`
  - Internal server error: `error.server.internal`
  - And any others discovered in step 1
- [ ] Provide messages in at least: English (en), Spanish (es), German (de), French (fr)
- [ ] Create Flyway or data loader for seed data
- [ ] Document message keys in AGENTS.md and README.md

**Definition of Done:**
- All error scenarios have localized message keys
- Seed data covers at least 4 languages
- Seed data loads on application startup
- Teams can easily add new message keys

---

## Phase 4: Error Response Localization

### 4.1 Integrate Localization into Exception Handler 🟡

Depends on: 3.3 (Seed data loaded)

Refactor the existing exception handler to use localized messages from the database.

**Tasks:**
- [ ] Update `GlobalExceptionHandler` to inject `LocalizationMessageService`
- [ ] Add request context to capture client's preferred language (via header, cookie, or default)
- [ ] Create `LocalizationContext` component to manage language per-request (optional but recommended)
- [ ] Modify each exception handler method to:
  - Extract the messageKey from the exception
  - Retrieve localized message via `LocalizationMessageService`
  - Fall back to English if translation not available
  - Include messageKey in error response for client-side lookup
- [ ] Update `ProblemDetail` response to include:
  - `messageKey`: identifier for the error (kept for debugging/analytics)
  - `message`: localized message text
  - `language`: the language code used for the message
- [ ] Update error test cases to verify localized responses
- [ ] Add new tests for multi-language error scenarios

**Response Format Example:**
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "messageKey": "error.book.not_found",
  "message": "El libro solicitado no fue encontrado.",
  "language": "es"
}
```

**Definition of Done:**
- Error responses include localized messages
- Fallback to English for unsupported languages
- All tests pass with new response format
- messageKey is preserved for debugging

---

### 4.2 Add Language Negotiation 🟡

Depends on: 4.1 (Exception handler integrated)

Allow clients to request responses in their preferred language.

**Tasks:**
- [ ] Add language detection from HTTP headers:
  - `Accept-Language` header as primary source
  - Query parameter `lang` as override: `?lang=es`
  - Cookie `language` as fallback
  - User preferences (if authentication is added later)
- [ ] Create `LanguageResolver` component to extract preferred language from request
- [ ] Store language preference in `ThreadLocal` or Spring's `RequestContext`
- [ ] Update `LocalizationMessageService` to use context language
- [ ] Add validation for supported languages (en, es, de, fr)
- [ ] Document language codes in API documentation
- [ ] Add tests for all language negotiation scenarios

**Definition of Done:**
- `Accept-Language: es` returns Spanish messages
- `?lang=de` returns German messages
- Invalid language codes fall back to English gracefully
- Language preference is correctly captured in all requests

---

## Phase 5: Security & OAuth Integration

### 5.1 Add Spring Security with OAuth 2.0 🟡

Depends on: 2.1 (Profiles configured)

Implement OAuth 2.0 authentication with a demo-friendly provider (e.g., GitHub, Google, or Keycloak).

**Tasks:**
- [ ] Add Spring Security and Spring Security OAuth2 dependencies:
  - `spring-boot-starter-security`
  - `spring-security-oauth2-client` (for OAuth client)
  - `spring-security-oauth2-login` (for login flow)
  - Optionally: `spring-security-oauth2-resource-server` (for API protection)
- [ ] Choose OAuth provider for demo:
  - **GitHub** (easiest for local dev, free, widely used)
  - **Google** (good for multi-platform demo)
  - **Keycloak** (self-hosted, full control, requires Docker)
  - Recommendation: Start with GitHub for simplicity
- [ ] Create security configuration class `SecurityConfig` with:
  - OAuth 2.0 login configuration
  - Authorization rules (open endpoints vs. secured)
- [ ] Create `application-oauth.properties` with OAuth provider credentials
- [ ] Update `application.properties` to document OAuth settings
- [ ] Protect localization management endpoints:
  - `POST /api/localization-messages` requires admin role
  - `PUT /api/localization-messages/{id}` requires admin role
  - `DELETE /api/localization-messages/{id}` requires admin role
  - `GET` endpoints remain public
- [ ] Protect book management endpoints (optional, or protect only create/update/delete):
  - `POST /api/books` requires `ROLE_USER`
  - `PUT /api/books/{id}` requires `ROLE_USER`
  - `DELETE /api/books/{id}` requires `ROLE_USER`
  - `GET` endpoints remain public
- [ ] Add `@EnableWebSecurity` and configure `HttpSecurity`
- [ ] Add test configuration for OAuth in tests

**OAuth Provider Setup (GitHub Example):**
- Create OAuth App in GitHub Settings > Developer Settings > OAuth Apps
- Set redirect URI to `http://localhost:8080/login/oauth2/code/github`
- Add credentials to `application-oauth.properties`:
  ```properties
  spring.security.oauth2.client.registration.github.client-id=YOUR_CLIENT_ID
  spring.security.oauth2.client.registration.github.client-secret=YOUR_CLIENT_SECRET
  ```

**Definition of Done:**
- Application requires OAuth login to access protected endpoints
- Public endpoints (hello, books GET) remain accessible
- OAuth provider integration is properly tested
- Security headers are configured (CSRF, XSS, etc.)
- Documentation includes OAuth setup instructions

---

### 5.2 Add User Entity & Management 🟡

Depends on: 5.1 (OAuth configured)

Track authenticated users and their roles in the database.

**Tasks:**
- [ ] Create `User` JPA entity with fields:
  - `id` (Long, primary key)
  - `oauthProviderId` (String, e.g., "github", "google")
  - `oauthId` (String, external user ID from provider)
  - `email` (String, unique)
  - `name` (String)
  - `role` (Enum: USER, ADMIN)
  - `createdAt` (Timestamp)
  - `lastLoginAt` (Timestamp)
- [ ] Create `UserRepository` extending `JpaRepository`
- [ ] Create `UserService` with methods:
  - `findOrCreateUser(oauthProviderId, oauthId, email, name): User`
  - `updateLastLogin(userId)`
  - `getUser(userId): User`
- [ ] Create Flyway migration for `users` table
- [ ] Update OAuth login flow to create/update `User` records
- [ ] Create `UserController` for user profile endpoints (optional):
  - `GET /api/users/me` - get current user
  - `GET /api/users/{id}` - get user by ID (admin only)
- [ ] Add seed data: test users with different roles
- [ ] Update tests to handle user context

**Definition of Done:**
- Users are persisted on first OAuth login
- User roles are stored and enforced
- Last login timestamps are updated
- Admin and regular user roles are distinguished

---

### 5.3 Secure Audit Logging 🟡

Depends on: 5.2 (User management)

Track who made changes to what, when, and (optionally) why.

**Tasks:**
- [ ] Create `AuditLog` JPA entity with fields:
  - `id` (Long, primary key)
  - `userId` (Long, reference to User)
  - `entityType` (String, e.g., "Book", "LocalizationMessage")
  - `entityId` (Long, ID of modified entity)
  - `action` (Enum: CREATE, UPDATE, DELETE)
  - `oldValue` (JSON text, optional, for tracking changes)
  - `newValue` (JSON text, optional, for tracking changes)
  - `timestamp` (Timestamp)
  - `ipAddress` (String, optional)
- [ ] Create `AuditLogRepository` and `AuditLogService`
- [ ] Create AOP aspect to log entity changes automatically
- [ ] Log all changes to `Book` and `LocalizationMessage`
- [ ] (Optional) Create admin endpoint to view audit logs: `GET /api/audit-logs` (paginated)
- [ ] Update Flyway migration for `audit_logs` table

**Definition of Done:**
- All create/update/delete operations are logged
- Current user is captured for each operation
- Audit logs are tamper-evident (read-only after inserted)
- Optional audit retrieval endpoint works

---

## Phase 6: Enhanced Book API

### 6.1 Add Search & Filtering to Books ✅

**Status:** Completed

Enhanced search capabilities for the book API without breaking existing endpoints.

**Tasks:**
- [x] Enhance `GET /api/books` to support filtering:
  - `?title=*search*` - filter by title (case-insensitive contains)
  - `?author=*search*` - filter by author (case-insensitive contains)
  - `?isbn=*search*` - filter by ISBN (exact or partial)
  - `?year=2022` or `?yearFrom=2020&yearTo=2023` - filter by publication year
  - `?sort=title,asc&sort=year,desc` - multiple sort fields
- [x] Implement filtering at repository layer (using `Specification<Book>` or custom query)
- [x] Update `BookRepository` with filtering methods
- [x] Add validation to prevent SQL injection or abuse
- [x] Document new query parameters in API docs
- [x] Add tests for filtering scenarios
- [x] Add tests for sorting combinations

**Definition of Done:**
- Filtering works without breaking existing `?sort=id,asc` API
- Pagination still works correctly with filters
- Query parameters are validated
- API docs updated with examples

---

### 6.2 Add Book Categories/Tags 🟡

Depends on: 6.1 (Filtering)

Allow books to be organized by category and tagged for better discovery.

**Tasks:**
- [ ] Create `Category` entity (e.g., "Fiction", "Non-Fiction", "Technology")
- [ ] Create `BookCategory` junction table for many-to-many relationship
- [ ] Update `Book` entity to include categories
- [ ] Create `CategoryRepository` and `CategoryService`
- [ ] Update `BookService` to handle category assignment
- [ ] Create `GET /api/categories` endpoint
- [ ] Create `GET /api/categories/{id}` endpoint
- [ ] Create `POST /api/categories` endpoint (admin only)
- [ ] Update book create/update requests to include categories
- [ ] Update `GET /api/books?category=technology` to filter by category
- [ ] Add seed categories: Fiction, Non-Fiction, Technology, Science, Self-Help, etc.

**Definition of Done:**
- Books can be assigned to categories
- Categories are manageable via API
- Filtering by category works
- Seed data includes category assignments

---

## Phase 7: Performance & Monitoring

### 7.1 Add Caching Layer 🟡

Depends on: 3.3 (Localization loaded)

Implement caching for frequently accessed data to reduce database load.

**Tasks:**
- [ ] Add Spring Cache abstraction dependency (already included via Spring Boot)
- [ ] Implement in-memory caching for:
  - `LocalizationMessage` lookups (cache by key+language)
  - `Category` lookups
  - `User` profiles (by ID)
- [ ] Add cache invalidation on updates
- [ ] Configure cache settings (TTL, max size) in properties
- [ ] Add cache stats endpoint (optional): `GET /actuator/metrics/cache.*`
- [ ] Document caching strategy in AGENTS.md
- [ ] Add tests to verify cache behavior

**Definition of Done:**
- Frequently accessed data is cached
- Cache is invalid after updates
- Application performance improves for read-heavy operations

---

### 7.2 Enhance Prometheus Metrics 🟡

Depends on: 7.1 (Caching)

Add application-specific metrics for better observability.

**Tasks:**
- [ ] Add custom metrics:
  - `books.total` - total number of books
  - `books.created.total` - cumulative books created
  - `books.deleted.total` - cumulative books deleted
  - `localization.messages.total` - total localization messages
  - `users.active` - active users (last 30 days)
  - `api.response.time` - API response times (histogram)
  - `cache.hit.ratio` - cache hit ratio
- [ ] Use Micrometer `MeterRegistry` to record metrics
- [ ] Add metrics to service methods
- [ ] Add tests to verify custom metrics are recorded
- [ ] Document metrics in README.md
- [ ] Optionally: add Grafana dashboard JSON in docs

**Definition of Done:**
- Custom metrics are exported to Prometheus
- Metrics accurately reflect application behavior
- No performance degradation from metrics collection

---

## Phase 8: Documentation & Developer Experience

### 8.1 Update API Documentation 🟡

Depends on: Phases 3, 4, 5 completion

Update Spring REST Docs and Asciidoctor documentation to reflect new features.

**Tasks:**
- [ ] Add documentation for LocalizationMessage API
- [ ] Add documentation for OAuth 2.0 flow and setup
- [ ] Add documentation for User endpoints
- [ ] Add documentation for book search/filtering
- [ ] Add documentation for language negotiation
- [ ] Add security warnings and best practices
- [ ] Update examples in `index.adoc`
- [ ] Add internationalization section
- [ ] Add deployment guide for different profiles (local, prod)
- [ ] Generate HTML docs: `./gradlew.bat asciidoctor`

**Definition of Done:**
- All new endpoints documented
- Examples include error cases
- Setup instructions clear for developers
- HTML docs build without warnings

---

### 8.2 Create Developer Setup Guide ✅

**Status:** Completed

Step-by-step guide for new developers to get the project running locally.

**Tasks:**
- [x] Create `SETUP.md` or expand README with:
  - Environment prerequisites (Java 25, Docker, etc.)
  - IDE setup (IntelliJ, VS Code, etc.)
  - Local database setup (H2 vs. Docker PostgreSQL)
  - OAuth setup (GitHub registration steps)
  - Environment variables / `.env` file example
  - Running the application
  - Running tests
  - Building Docker images
  - Accessing H2 console, Prometheus, etc.
- [x] Create `.env.example` file with template
- [x] Document troubleshooting section (common issues)
- [x] Add quick-start commands for bash and PowerShell

**Definition of Done:**
- New developers can follow guide and run app in < 15 minutes
- No missing steps or unclear instructions
- Troubleshooting section covers common environment issues

---

### 8.3 Add Contribution Guidelines ✅

**Status:** Completed

Clear guidelines for contributing to the project.

**Tasks:**
- [x] Create `CONTRIBUTING.md` with:
  - Code style and formatting expectations
  - Branch naming conventions
  - Commit message conventions
  - Pull request process
  - Testing requirements
  - Documentation expectations
  - Quality gates (spotlessCheck, pmdMain, tests, asciidoctor)
- [x] Add pre-commit hooks (optional, improve DX)
- [x] Reference AGENTS.md as authoritative source for tech decisions

**Definition of Done:**
- Clear expectations are documented
- Easy for new contributors to follow

---

## Phase 9: Testing & Quality

### 9.1 Add Contract Testing (Spring Cloud Contract) 🟡

Depends on: APIs stabilized (Phase 3-5)

Ensure API contracts between frontend and backend remain consistent.

**Tasks:**
- [ ] Add Spring Cloud Contract Verifier dependency
- [ ] Create contract definitions (Groovy or YAML) for:
  - Book API endpoints
  - LocalizationMessage API endpoints
  - User endpoints
- [ ] Generate contract tests from definitions
- [ ] Publish contracts/stubs for frontend consumption
- [ ] Add to CI/CD pipeline

**Definition of Done:**
- Contract tests pass for all critical endpoints
- Front-end team can use generated stubs for independent development

---

### 9.2 Increase Test Coverage Target 🟡

Depends on: Phase 3-5 implementations

Aim for higher test coverage (e.g., 80%+).

**Tasks:**
- [ ] Run coverage report: `./gradlew.bat test jacocoTestReport`
- [ ] Review `build/reports/jacoco/test/html/index.html`
- [ ] Identify untested code paths
- [ ] Add tests for:
  - Service layer edge cases
  - Exception handling paths
  - Validation failure scenarios
  - Caching behavior (if added in Phase 7)
  - Multi-language scenarios
- [ ] Update Gradle CI to enforce minimum coverage threshold (e.g., `check` fails below 75%)
- [ ] Document coverage expectations in CONTRIBUTING.md

**Definition of Done:**
- Test coverage ≥ 75% (or target % of choice)
- Critical business logic has tests
- Coverage reports are accessible in CI/CD

---

### 9.3 Add Load & Performance Testing 🟡

Depends on: 1.2 (PostgreSQL/Testcontainers ready)

Benchmark application performance under load.

**Tasks:**
- [ ] Add JMeter or Gatling for load testing
- [ ] Create test scenarios:
  - Book list with pagination (100-1000 concurrent requests)
  - Book search/filtering
  - LocalizationMessage lookups
  - OAuth login flow
- [ ] Establish baseline performance metrics
- [ ] Document performance expectations in README
- [ ] Add performance regression checks to CI (optional)

**Definition of Done:**
- Performance benchmarks are documented
- Response times meet expectations under load
- Database query performance is acceptable

---

## Phase 10: DevOps & Deployment

### 10.1 Add CI/CD Pipeline 🟡

Depends on: Phase 1-2 complete (database, profiles)

Automate building, testing, and deployment.

**Tasks:**
- [ ] Choose CI/CD platform (GitHub Actions, GitLab CI, Jenkins, etc.)
- [ ] Create pipeline configuration:
  - Trigger on push/PR
  - Checkout code
  - Set up Java 25
  - Run `spotlessCheck`
  - Run `pmdMain`
  - Run `test` (with PostgreSQL via Testcontainers)
  - Run `asciidoctor`
  - Optional: `qodanaScan`
  - Build Docker image
  - Push to registry (Docker Hub, ECR, Artifactory, etc.)
- [ ] Add branch protection rules (require CI to pass)
- [ ] Document CI/CD flow in README or CONTRIBUTING.md

**Definition of Done:**
- CI pipeline runs on every PR
- All quality gates must pass
- Docker image is automatically built and pushed

---

### 10.2 Add Kubernetes Manifests 🟡

Depends on: 10.1 (CI/CD pipeline)

Prepare application for Kubernetes deployment.

**Tasks:**
- [ ] Create `k8s/` directory with manifests:
  - Deployment (app)
  - Service (port exposure)
  - ConfigMap (environment-specific config)
  - Secret (sensitive data: DB credentials, OAuth secrets)
  - Ingress (optional, for routing)
  - PersistentVolumeClaim (for PostgreSQL data, if using StatefulSet)
- [ ] Use `application-prod.properties` in ConfigMap
- [ ] Add health checks / liveness and readiness probes (already configured in app)
- [ ] Add resource requests/limits
- [ ] Document Kubernetes deployment in README
- [ ] Verify manifests with `kubectl validate` or `kube-score`

**Definition of Done:**
- Application deploys to Kubernetes cluster
- Health probes work correctly
- Configuration is externalized via ConfigMap
- Secrets are not committed to repo

---

### 10.3 Add Helm Chart (Optional) 🟡

Depends on: 10.2 (Kubernetes manifests)

Make deployment easier for teams/customers using Helm package manager.

**Tasks:**
- [ ] Create Helm chart structure
- [ ] Define values.yaml with sensible defaults
- [ ] Create templates for Deployment, Service, ConfigMap, Secret, Ingress
- [ ] Test chart with: `helm template . | kubectl apply -f -`
- [ ] Document Helm deployment in README
- [ ] Publish chart to Helm registry (optional)

**Definition of Done:**
- Helm chart successfully deploys application
- Chart is customizable via values.yaml
- Deployment on any K8s cluster is simplified

---

### 10.4 Add Monitoring & Alerting Setup 🟡

Depends on: 7.2 (Prometheus metrics)

Provide production monitoring and alerting infrastructure.

**Tasks:**
- [ ] Create Docker Compose for monitoring stack:
  - Prometheus (scrapes app metrics)
  - Grafana (visualizes metrics)
  - AlertManager (sends alerts)
- [ ] Create Prometheus scrape config
- [ ] Create Grafana dashboards (or export JSON)
- [ ] Define alerting rules (e.g., app down, high response time, errors)
- [ ] Document monitoring stack setup
- [ ] Add alerts for critical issues

**Definition of Done:**
- Metrics are collected and visualized
- Alerts trigger for defined conditions
- Team can monitor application health

---

## Phase 11: Future Enhancements

### 11.1 Add Batch Processing (Optional) 🟢

If bulk operations are needed later:
- [ ] Add Spring Batch dependency
- [ ] Create job to bulk import books
- [ ] Create job to clean up old audit logs

---

### 11.2 Add Async Message Processing (Optional) 🟢

If event-driven architecture is desired:
- [ ] Add RabbitMQ or Kafka
- [ ] Async book creation notifications
- [ ] Async audit log processing

---

### 11.3 Add Full-Text Search (Optional) 🟢

If search capabilities need major enhancement:
- [ ] Add Elasticsearch
- [ ] Index books and localization messages
- [ ] Implement advanced search API

---

### 11.4 Add GraphQL API (Optional) 🟢

Alternative to REST API for advanced use cases:
- [ ] Add Spring GraphQL dependency
- [ ] Define schema for books, users, localizations
- [ ] Implement queries and mutations

---

---

## Quick Reference: Quality Gates

Before completing any phase, ensure:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

.\gradlew.bat spotlessCheck
.\gradlew.bat --no-problems-report pmdMain
.\gradlew.bat --no-problems-report test
.\gradlew.bat asciidoctor
.\gradlew.bat qodanaScan  # Optional but recommended
```

---

## Notes

- This roadmap is flexible and can be adjusted based on business priorities and resource availability.
- Some phases have dependencies; these are marked with 🟡 (Blocked) or refer to prerequisite tasks.
- Prefer incremental delivery where possible to gather feedback early.
- Keep the project simple and focused on demo/interview purposes—avoid over-engineering.
- Always maintain alignment between README.md and AGENTS.md when design changes occur.
