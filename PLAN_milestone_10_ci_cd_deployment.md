# Plan: Milestone 10 CI/CD And Deployment

## Summary
- Add a complete but demo-sized delivery path for this application: CI validation, container publishing, Kubernetes deployment assets, a Helm chart, and monitoring/alerting setup.
- Keep the work vendor-neutral at the deployment layer while choosing one concrete CI/CD platform so the repository is executable rather than aspirational.
- Measure success by proving that the repository can validate itself in CI, build and smoke-test the production container, render valid Kubernetes and Helm assets, and document how to deploy and observe the application.

## Scope
- In scope:
  - choose and implement the CI/CD platform
  - automate the existing repository quality gates in CI
  - build and optionally publish the Docker image in CI
  - add base Kubernetes manifests for the application
  - add a Helm chart for the application
  - add monitoring and alerting assets for Prometheus, Grafana, and Alertmanager
  - document deployment, configuration, verification, and branch protection expectations
- Out of scope:
  - changing public API behavior
  - changing OpenAPI, REST Docs, or HTTP examples
  - introducing cloud-vendor-specific infrastructure such as Terraform modules, managed database provisioning, or ingress controller installation
  - implementing GitOps, multi-cluster promotion, or progressive delivery
  - resolving pre-`1.0` product decisions that are explicitly tracked in the release-readiness milestone

## Current State
- Current behavior:
  - `build.gradle.kts` already enforces a substantial verification pipeline with tests, PMD, JaCoCo, Asciidoctor, and Docker image build.
  - `Dockerfile` already packages the Boot jar, runs the `prod` profile by default, and exposes readiness via `/actuator/health/readiness`.
  - `docker-compose.yml` only supports local PostgreSQL development and does not define runtime deployment topology.
  - `application-prod.properties` already externalizes PostgreSQL connection values and session cookie security.
  - `application-oauth.properties` already externalizes GitHub OAuth credentials.
- Current constraints:
  - the project goal is still a small, readable interview-demo application, so deployment assets must stay understandable and not balloon into platform engineering sprawl
  - Milestone 10 is explicitly downstream of the pre-`1.0` release-readiness work in `ROADMAP.md`
  - production security posture is not fully locked yet for metrics exposure, OAuth defaults, CSRF posture, and fail-fast secret handling
- Relevant existing specs and code:
  - roadmap source: `ROADMAP.md`
  - planning rules: `PLAN.md`
  - repo rules: `AGENTS.md`
  - developer/deployment runbook: `SETUP.md`
  - human-facing project contract: `README.md`
  - build and Docker pipeline source: `build.gradle.kts`, `Dockerfile`, `docker-compose.yml`
  - runtime configuration: `src/main/resources/application.properties`, `src/main/resources/application-prod.properties`, `src/main/resources/application-oauth.properties`

## Locked Decisions And Assumptions
- User decision:
  - the repository should have a detailed execution plan for Milestone 10 rather than only roadmap bullets
- Planning assumptions:
  - CI/CD platform: use GitHub Actions because the repository already assumes GitHub as an identity provider, and GitHub-hosted workflows are the least surprising default for this codebase
  - deployment target: support generic Kubernetes manifests and a Helm chart rather than a cloud-specific platform
  - image naming: continue using the existing Gradle/Docker build flow and parameterize the image repository/tag in CI instead of replacing the current Docker build task
  - release flow: mainline CI runs validation on pull requests and protected branches; tag-based workflows build publishable artifacts
  - monitoring scope: add a minimal but real monitoring stack with standard upstream components, preferring composition and configuration over vendoring large generated assets
  - unresolved pre-`1.0` decisions remain prerequisites for final production defaults; Milestone 10 should not silently freeze security choices that the roadmap still marks as open

## Affected Artifacts
- Tests and verification:
  - `build.gradle.kts`
  - new CI workflow validation files under `.github/workflows/`
  - optional deployment validation scripts under `scripts/`
- Docs:
  - `README.md`
  - `SETUP.md`
  - possibly `CONTRIBUTING.md` if branch protection and review expectations become more explicit
- OpenAPI:
  - no change expected
- HTTP examples:
  - no change expected
- Source and runtime configuration:
  - possibly `src/main/resources/application-prod.properties` if deployment-safe fail-fast behavior or explicit environment variable names need tightening after Milestone pre-`1.0`
- Deployment assets to add:
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
  - `k8s/base/`
  - `k8s/overlays/`
  - `helm/technical-interview-demo/`
  - `monitoring/` or `k8s/monitoring/`
  - deployment-specific docs and example env files
- Build or benchmark checks:
  - `./gradlew.bat build`
  - Docker container smoke validation
  - manifest rendering and dry-run validation
  - Helm lint/template validation
  - no Phase 9 benchmark rerun expected unless Milestone 10 work changes OAuth/session startup behavior in application code

## Execution Milestones
### Milestone 1: Lock The Deployment Contract
- Goal:
  - define the minimum supported deployment story so the remaining work has a stable target
- Files to update:
  - `PLAN_milestone_10_ci_cd_deployment.md`
  - `ROADMAP.md` only if execution sequencing must be clarified
  - `README.md`
  - `SETUP.md`
- Behavior to preserve:
  - no public API changes
  - no undocumented runtime requirement changes
- Exact deliverables:
  - decide that GitHub Actions is the CI/CD platform
  - define supported deployment artifacts: Docker image, Kubernetes manifests, Helm chart, monitoring stack
  - document required runtime environment variables for deployment:
    - `DATABASE_HOST`
    - `DATABASE_PORT`
    - `DATABASE_NAME`
    - `DATABASE_USER`
    - `DATABASE_PASSWORD`
    - `SESSION_COOKIE_SECURE`
    - optional `GITHUB_CLIENT_ID`
    - optional `GITHUB_CLIENT_SECRET`
    - optional `ADMIN_LOGINS`
  - explicitly mark unresolved pre-`1.0` items as blockers for final production defaults:
    - whether `/actuator/prometheus` remains public
    - whether OAuth is enabled in deployed environments
    - whether prod should fail fast on missing secrets
    - whether browser-session writes require CSRF changes before `1.0`

### Milestone 2: Implement CI Validation And Release Workflows
- Goal:
  - make repository validation repeatable on pull requests, protected branches, and releases
- Files to update:
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
  - optionally `.github/dependabot.yml` only if dependency maintenance is intentionally included
  - `README.md`
  - `SETUP.md`
- Behavior to preserve:
  - local `.\gradlew.bat build` remains the canonical full verification command
- Exact deliverables:
  - PR and push workflow that runs:
    - JDK 25 setup
    - Gradle dependency caching
    - `.\gradlew.bat build`
  - explicit Docker availability in CI so the existing build lifecycle continues to work
  - tag workflow that:
    - runs full verification
    - builds the container image
    - tags the image with git tag and immutable commit SHA
    - publishes the image to a chosen registry
  - workflow permissions and concurrency configuration
  - branch protection recommendations documented in repository docs:
    - required status checks
    - required review count
    - linear history or squash preference
    - tag/release ownership expectations

### Milestone 3: Add Runtime Smoke Validation
- Goal:
  - prove that the built container can start with production-like configuration and reach readiness
- Files to update:
  - `.github/workflows/ci.yml`
  - optional `scripts/ci/`
  - `SETUP.md`
- Behavior to preserve:
  - the existing Docker image remains the deployment artifact
- Exact deliverables:
  - a CI smoke stage that:
    - starts PostgreSQL for the container under test
    - runs the built image with `SPRING_PROFILES_ACTIVE=prod`
    - injects required `DATABASE_*` settings
    - waits for `/actuator/health/readiness`
    - fails fast on startup, migration, or configuration errors
  - explicit smoke assertions for:
    - readiness returns success
    - the app binds on port `8080`
    - Flyway migrations complete against PostgreSQL
  - documented local command sequence to reproduce the same smoke validation

### Milestone 4: Add Kubernetes Base Manifests
- Goal:
  - provide a readable, vendor-neutral deployment baseline for the app
- Files to add or update:
  - `k8s/base/namespace.yaml`
  - `k8s/base/deployment.yaml`
  - `k8s/base/service.yaml`
  - `k8s/base/configmap.yaml`
  - `k8s/base/secret-example.yaml`
  - `k8s/base/kustomization.yaml`
  - `k8s/overlays/local/kustomization.yaml`
  - `k8s/overlays/local/patch-*.yaml`
  - `README.md`
  - `SETUP.md`
- Behavior to preserve:
  - app configuration stays environment-variable driven
  - no cloud-provider-specific annotations in the base manifests
- Exact deliverables:
  - deployment manifest with:
    - image repository/tag parameters
    - resource requests and limits
    - readiness probe on `/actuator/health/readiness`
    - liveness probe on `/actuator/health/liveness`
    - env vars mapped from ConfigMap and Secret
    - graceful shutdown settings matching Spring configuration
  - service manifest exposing the HTTP port
  - base Kustomize package that renders cleanly
  - one local/demo overlay with safe defaults for local cluster validation
  - documentation for:
    - required secrets
    - how to render manifests
    - how to apply to a cluster
    - how to verify readiness after deployment

### Milestone 5: Add Helm Chart Parity
- Goal:
  - offer a standard packaging format without duplicating business decisions between raw manifests and chart values
- Files to add or update:
  - `helm/technical-interview-demo/Chart.yaml`
  - `helm/technical-interview-demo/values.yaml`
  - `helm/technical-interview-demo/values-local.yaml`
  - `helm/technical-interview-demo/templates/_helpers.tpl`
  - `helm/technical-interview-demo/templates/deployment.yaml`
  - `helm/technical-interview-demo/templates/service.yaml`
  - `helm/technical-interview-demo/templates/configmap.yaml`
  - `helm/technical-interview-demo/templates/secret-example.yaml`
  - optionally `helm/technical-interview-demo/templates/servicemonitor.yaml`
  - `README.md`
  - `SETUP.md`
- Behavior to preserve:
  - chart defaults stay simple and mirror the raw Kubernetes deployment contract
- Exact deliverables:
  - Helm chart that exposes:
    - image repository/tag
    - replica count
    - resources
    - environment/config values
    - optional OAuth settings
    - optional metrics scraping configuration
  - `helm lint` and `helm template` validation in CI
  - clear statement in docs of when to use raw manifests vs Helm chart

### Milestone 6: Add Monitoring And Alerting Setup
- Goal:
  - make the app observable in a cluster with minimal but credible defaults
- Files to add or update:
  - `k8s/monitoring/` or `monitoring/`
  - `monitoring/grafana/dashboards/`
  - `monitoring/alertmanager/`
  - `README.md`
  - `SETUP.md`
- Behavior to preserve:
  - keep monitoring assets composable and easy to remove; do not entangle them with app deployment basics
- Exact deliverables:
  - choose upstream installation strategy:
    - either documented Helm-based install of `kube-prometheus-stack`
    - or small repo-owned manifests if the stack remains intentionally minimal
  - app metrics integration:
    - ServiceMonitor or equivalent scrape config for `/actuator/prometheus`
    - labels/annotations required for discovery
  - starter Grafana dashboard(s):
    - JVM memory and CPU
    - request latency/error rate if available from Micrometer
    - cache and domain metrics already exposed by the app
  - starter alert rules:
    - target down
    - readiness failing
    - repeated restart/crash behavior
    - high error rate if practical from existing metrics
  - documented local/demo deployment instructions for the monitoring stack

### Milestone 7: Documentation, Reviewability, And Handoff
- Goal:
  - finish Milestone 10 with clear operational documentation instead of only checked-in YAML
- Files to update:
  - `README.md`
  - `SETUP.md`
  - `CONTRIBUTING.md`
  - optionally `.env.example` if deployment-relevant variables need clearer examples
- Behavior to preserve:
  - keep README high-level and keep setup/runbook detail in `SETUP.md`
- Exact deliverables:
  - README summary of CI/CD and deployment support
  - SETUP sections for:
    - local reproduction of CI
    - local container smoke verification
    - Kubernetes render/deploy steps
    - Helm install/upgrade steps
    - monitoring verification
    - troubleshooting for database, readiness, and metrics scraping failures
  - CONTRIBUTING guidance for required checks and branch protection expectations

## Edge Cases And Failure Modes
- Missing database credentials or wrong PostgreSQL host causes successful image build but failed runtime startup; smoke validation must catch this.
- Docker image build can pass locally while CI fails if Docker is not explicitly available in the runner environment.
- Kubernetes probes can flap if startup timing is too aggressive; probes and initial delays must be tuned against Flyway migration and Spring startup time.
- OAuth configuration may be optional in some environments; deployment assets must not hard-require GitHub secrets unless the selected profile needs them.
- Metrics exposure may change during the pre-`1.0` security work; monitoring assets must isolate that decision rather than baking in an unsafe assumption.
- Session persistence requires PostgreSQL-backed Spring Session tables; deployments that point at an empty or incompatible database must fail loudly.
- Alert rules can become noisy in a single-replica demo cluster; thresholds should prefer obvious operational failures over brittle SLO-style alerts.
- Helm and raw manifests can drift; plan execution should derive both from the same configuration contract and validate both in CI.

## Validation Plan
- Commands to run during implementation:
  - `.\gradlew.bat build`
  - `.\gradlew.bat dockerBuild`
  - Docker-based smoke run against PostgreSQL and `/actuator/health/readiness`
  - `kubectl kustomize k8s/base`
  - `kubectl kustomize k8s/overlays/local`
  - `kubectl apply --dry-run=client -k k8s/overlays/local`
  - `helm lint helm/technical-interview-demo`
  - `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml`
- Tests or checks to add:
  - CI workflow execution on pull requests
  - release workflow dry-run validation where feasible
  - container readiness smoke validation
  - manifest rendering validation
  - Helm lint/template validation
- Docs or contract checks:
  - README and SETUP alignment
  - no OpenAPI baseline refresh
  - no HTTP example updates
  - no benchmark rerun unless implementation touches application startup/security code
- Manual verification steps:
  - deploy the local overlay to a disposable cluster
  - verify `GET /actuator/health/readiness`
  - verify `GET /actuator/prometheus`
  - verify Grafana sees the app target
  - verify at least one intentional alert path in Alertmanager configuration

## Better Engineering Notes
- Milestone 10 should not quietly solve unresolved Milestone pre-`1.0` product decisions; if execution requires a hard production default, stop and resolve the release-readiness issue first.
- A focused container smoke test may be worth extracting into Milestone pre-`1.0` because it is release-grade verification even without Kubernetes.
- If the app still tolerates insecure production defaults when Milestone 10 starts, add deployment assets only after fail-fast behavior is clarified; otherwise the plan would encode weak production posture into every environment example.
- Prefer upstream monitoring packaging over checking in large generated manifests. The repo should explain how to compose the stack, not vendor thousands of lines of opaque YAML.

## Validation Results
- To be filled in during execution.

## User Validation
- Review the plan against `ROADMAP.md` and confirm the chosen defaults:
  - GitHub Actions as the CI/CD platform
  - vendor-neutral Kubernetes deployment assets
  - Helm chart for the application
  - upstream-style Prometheus/Grafana/Alertmanager integration
- When execution starts, verify the finished milestone by:
  - opening the CI workflow runs for a pull request
  - confirming a tagged build produces an image
  - rendering and applying the local Kubernetes overlay
  - checking readiness and metrics endpoints in the deployed app
  - opening Grafana and confirming the app dashboard populates
