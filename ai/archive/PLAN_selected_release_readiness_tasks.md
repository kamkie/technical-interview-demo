# Plan: Selected Release-Readiness Tasks

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Remove permissive `prod` datasource defaults so production-style startup fails fast when required database credentials are missing, while keeping optional OAuth secrets optional unless the `oauth` profile is active.
- Enable Dependabot in a way that fits the existing GitHub Actions `CI` and tag-driven `Release` workflows instead of creating a parallel dependency-maintenance path.
- Extend the tag-driven release automation so `.github/workflows/release.yml` creates a GitHub Release whose notes come from the matching `CHANGELOG.md` section and include the published Docker image reference.
- Replace machine-specific setup examples with portable placeholders in `SETUP.md`, and align adjacent setup-facing artifacts that still leak workstation-specific paths.
- Success is measured by: documented plan-complete file targets, passing repository validation, production smoke validation still working with explicit env vars, and release/setup docs staying aligned with the automation changes.

## Scope
- In scope:
  - `prod` configuration hardening for required database credentials and session-cookie settings
  - targeted startup validation for missing required `prod` variables
  - Dependabot configuration for the ecosystems already used in this repository
  - CI/release workflow adjustments needed so Dependabot PRs use the normal verification path
  - release workflow automation for GitHub Releases using `CHANGELOG.md`
  - release and setup documentation updates required by the workflow/config changes
  - removal of machine-specific examples from `SETUP.md` and directly related setup templates
- Out of scope:
  - public API behavior, REST Docs, OpenAPI, or HTTP example changes
  - new secret-management infrastructure, vault integration, or deployment platform changes
  - changing the supported release trigger model away from semantic version tags
  - broader repo-wide portability cleanup outside setup-facing materials unless it blocks these tasks
  - roadmap reprioritization beyond creating this execution plan

## Current State
- Current behavior:
  - `src/main/resources/application-prod.properties` still falls back to `localhost`, `5432`, `technical_interview_demo`, `postgres`, and `changeme`, so a production-profile boot can start with insecure defaults instead of clearly failing on missing credentials.
  - `src/main/resources/application-oauth.properties` already requires `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` only when the `oauth` profile is active.
  - `.github/workflows/ci.yml` runs the repository build, Helm validation, and container smoke validation for pull requests and pushes to `main`.
  - `.github/workflows/release.yml` publishes Docker image tags to GitHub Container Registry, but it does not create a GitHub Release or publish release notes.
  - `.github/dependabot.yml` does not exist yet.
  - `SETUP.md` and `.env.example` still contain user-specific path examples such as `C:\Users\kamki\.jdks\azul-25.0.3`.
- Current constraints:
  - the repo is intentionally demo-sized, so the solution should stay direct: properties, small scripts, focused tests, and simple workflow steps instead of a large release-management layer
  - `CHANGELOG.md` is the human-facing release source of truth and already has predictable version headings that can drive generated GitHub Release notes
  - `README.md`, `SETUP.md`, and `ai/RELEASES.md` currently describe the release model and will become inaccurate if the workflow changes without doc updates
  - the existing smoke validation script explicitly passes the `prod` env vars, so fail-fast hardening must preserve that happy path
- Relevant existing specs and code:
  - roadmap source: `ROADMAP.md`
  - planning rules: `ai/PLAN.md`
  - release workflow guidance: `ai/RELEASES.md`
  - human-facing contract and CI/CD summary: `README.md`
  - setup and local verification guide: `SETUP.md`
  - release history source: `CHANGELOG.md`
  - workflow automation: `.github/workflows/ci.yml`, `.github/workflows/release.yml`
  - runtime config: `src/main/resources/application.properties`, `src/main/resources/application-prod.properties`, `src/main/resources/application-oauth.properties`
  - container smoke validation: `scripts/ci/smoke-container.ps1`
  - setup template: `.env.example`

## Locked Decisions And Assumptions
- User decisions:
  - plan only these four release-readiness tasks, not the whole remaining roadmap milestone
- Planning assumptions that the executor should not revisit:
  - `prod` must require explicit database host, port, name, user, and password values; do not keep production fallbacks such as `localhost` or `changeme`
  - `SESSION_COOKIE_SECURE` should remain explicitly configurable, but the plan should document whether it stays defaulted or becomes required; the executor should prefer an explicit choice and align docs, smoke validation, and deployment examples around it
  - bare `prod` must not start requiring GitHub OAuth secrets unless the `oauth` profile is also active; otherwise the change would silently turn optional OAuth into a mandatory deployment requirement
  - Dependabot should cover only the ecosystems already in normal maintenance flow for this repo: Gradle, GitHub Actions, and Docker
  - Dependabot PRs should be validated by the same `CI` workflow used for human PRs; they should not trigger release publication
  - GitHub Release notes should be generated from the exact tagged version section in `CHANGELOG.md`, not from duplicated workflow text or GitHub auto-generated notes
  - the GitHub Release body should include both the exact image reference `ghcr.io/<owner>/<repo>:<tag>` and a package-page link for discoverability
  - portable setup examples should use placeholders such as `<path-to-jdk-25>` or platform-neutral guidance, not personal paths or workstation-specific defaults
  - `.env.example` is part of the setup-facing cleanup because leaving the old path there would immediately reintroduce the same portability problem

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/TechnicalInterviewDemoApplicationTests.java`
  - likely new focused configuration test such as `src/test/java/team/jit/technicalinterviewdemo/technical/ProductionConfigurationTests.java`
  - `scripts/ci/smoke-container.ps1` and any new release-note rendering helper script used for validation
- Docs:
  - `README.md`
  - `SETUP.md`
  - `.env.example`
  - `ai/RELEASES.md`
- OpenAPI:
  - no change expected
- HTTP examples:
  - no change expected
- Source files:
  - `src/main/resources/application-prod.properties`
  - possibly `src/main/resources/application.properties` if profile comments or defaults need clarification
  - possibly a small helper under `scripts/ci/` or `scripts/release/` to extract the tagged changelog section and append image metadata for the GitHub Release body
- GitHub automation:
  - `.github/dependabot.yml`
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - `.\scripts\ci\smoke-container.ps1 -ImageName technical-interview-demo`
  - a local dry run of the release-notes extraction helper against an existing version section
  - no OpenAPI baseline refresh expected
  - no HTTP example refresh expected
  - no Phase 9 benchmark rerun expected unless execution ends up changing OAuth-enabled startup behavior rather than only `prod` env handling

## Execution Milestones
### Milestone 1: Harden `prod` Startup Requirements
- Goal:
  - make `prod` fail clearly when required database credentials are missing instead of booting against insecure defaults
- Files to update:
  - `src/main/resources/application-prod.properties`
  - `scripts/ci/smoke-container.ps1`
  - likely `src/test/java/team/jit/technicalinterviewdemo/technical/ProductionConfigurationTests.java`
  - `README.md`
  - `SETUP.md`
- Behavior to preserve:
  - local development still defaults to the `local` profile
  - the optional `oauth` profile remains optional
  - the container smoke validation continues to start successfully when explicit env vars are provided
- Exact deliverables:
  - remove fallback values for `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER`, and `DATABASE_PASSWORD` from `application-prod.properties`
  - decide and document whether `SESSION_COOKIE_SECURE` remains defaulted or becomes required; align the smoke script and deployment docs with that decision
  - add focused verification that a `prod` startup without the required database env vars fails fast with a clear configuration error
  - keep GitHub OAuth secrets out of the required-`prod` set unless `oauth` is active
  - update the deployment-contract documentation in `README.md` and `SETUP.md` so it no longer says the fail-fast behavior is unresolved

### Milestone 2: Enable Dependabot Without Forking The Maintenance Flow
- Goal:
  - add dependency-update automation that feeds into the normal PR review and CI path
- Files to update:
  - `.github/dependabot.yml`
  - `.github/workflows/ci.yml`
  - `README.md`
  - `SETUP.md`
- Behavior to preserve:
  - the existing `CI` workflow remains the single required status check for dependency PRs and human PRs alike
  - release publishing still happens only from semantic version tags
- Exact deliverables:
  - add `.github/dependabot.yml` with entries for:
    - Gradle dependencies
    - GitHub Actions workflow actions
    - Dockerfile base images
  - choose a low-noise update cadence such as weekly grouped updates per ecosystem
  - ensure CI permissions and workflow triggers do not assume non-Dependabot secrets for normal validation
  - document that Dependabot PRs are expected to pass `CI` before merge and are not themselves a release mechanism

### Milestone 3: Publish GitHub Releases From `CHANGELOG.md`
- Goal:
  - turn the tag workflow into a complete release publication path instead of only an image-push workflow
- Files to update:
  - `.github/workflows/release.yml`
  - likely new helper script such as `scripts/ci/render-release-notes.ps1` or `scripts/release/render-release-notes.ps1`
  - `README.md`
  - `SETUP.md`
  - `ai/RELEASES.md`
- Behavior to preserve:
  - image publication to GitHub Container Registry still happens before the GitHub Release is finalized
  - the release is still keyed off the semantic version tag that drives the Gradle version
- Exact deliverables:
  - extract the matching `## [vX.Y.Z]` section from `CHANGELOG.md`
  - append release metadata that includes:
    - the pushed image reference `ghcr.io/<owner>/<repo>:<tag>`
    - the immutable SHA-tag image reference
    - a package-page link to the published image
  - create the GitHub Release only after the image push succeeds
  - fail the release workflow if the tagged version section is missing from `CHANGELOG.md` instead of publishing an empty or misleading release
  - update human- and AI-facing release documentation so they describe the GitHub Release artifact alongside the image publication

### Milestone 4: Remove Workstation-Specific Setup Details
- Goal:
  - make setup documentation portable and reusable on any contributor machine
- Files to update:
  - `SETUP.md`
  - `.env.example`
  - `README.md` only if its setup pointers need clarification
- Behavior to preserve:
  - the guide remains concrete enough to be usable without guessing missing commands
  - PowerShell and Bash setup examples remain available
- Exact deliverables:
  - replace hard-coded personal paths with placeholders like `<path-to-jdk-25>` and `<path-to-intellij>`
  - remove any wording that assumes one contributor machine or one local filesystem layout
  - keep examples copyable by pairing placeholders with one short explanation of what belongs there
  - verify that `SETUP.md` still points readers to the right commands for local boot, CI reproduction, container smoke validation, Kubernetes/Helm validation, and OAuth setup

## Execution Task Split
- Coordinator ownership on `main`:
  - shared integration files: `CHANGELOG.md`, `ai/archive/PLAN_selected_release_readiness_tasks.md`
  - shared setup/release docs that span multiple milestones: `README.md`, `SETUP.md`
  - integration branch management, worker result review, final validation, and release creation
- Worker 1 task: Harden `prod` startup requirements
  - branch/worktree: `codex/release-ready-prod-hardening`, `..\technical-interview-demo-prod-hardening`
  - owned files:
    - `src/main/resources/application-prod.properties`
    - `scripts/ci/smoke-container.ps1`
    - `src/test/java/team/jit/technicalinterviewdemo/technical/ProductionConfigurationTests.java`
  - validation scope:
    - focused configuration test coverage for required `prod` variables
    - `.\scripts\ci\smoke-container.ps1 -ImageName technical-interview-demo` when the local image is available
- Worker 2 task: Enable Dependabot without forking the maintenance flow
  - branch/worktree: `codex/release-ready-dependabot`, `..\technical-interview-demo-dependabot`
  - owned files:
    - `.github/dependabot.yml`
    - `.github/workflows/ci.yml`
  - validation scope:
    - YAML validation by repository build and workflow review
- Worker 3 task: Publish GitHub Releases from `CHANGELOG.md`
  - branch/worktree: `codex/release-ready-release-automation`, `..\technical-interview-demo-release-automation`
  - owned files:
    - `.github/workflows/release.yml`
    - `scripts/release/render-release-notes.ps1`
    - `ai/RELEASES.md`
  - validation scope:
    - local dry run of the release-note rendering helper against an existing changelog section
- Worker 4 task: Remove workstation-specific setup details from setup-facing templates
  - branch/worktree: `codex/release-ready-setup-portability`, `..\technical-interview-demo-setup-portability`
  - owned files:
    - `.env.example`
  - validation scope:
    - verify setup-facing templates no longer contain machine-specific paths

Coordinator integration notes:
- Workers must not edit `CHANGELOG.md`, `ai/archive/PLAN_selected_release_readiness_tasks.md`, `README.md`, or `SETUP.md`.
- After each worker task lands on `main`, the coordinator updates `CHANGELOG.md` under `## [Unreleased]` and folds the completed behavior into the shared docs.
- Final repository validation and release creation happen only after all four worker tasks are integrated on `main`.

## Edge Cases And Failure Modes
- Removing `prod` fallbacks can break the container smoke test if any required env var is omitted there; the plan must update validation scripts in the same change.
- Making OAuth secrets required for plain `prod` would be a behavior regression because current docs describe them as optional deployment variables.
- If `SESSION_COOKIE_SECURE` becomes required, local smoke validation and local-cluster values must set it explicitly or startup will fail for the wrong reason.
- Dependabot PRs run with restricted permissions; CI must avoid steps that implicitly require publish credentials or write tokens.
- Grouping too many ecosystems into one Dependabot PR would make review noisy; grouping by ecosystem keeps failures easier to isolate.
- GitHub Release generation must handle Windows line endings and preserve markdown structure when reading `CHANGELOG.md`.
- The release workflow must not create a GitHub Release before the image push succeeds, or the published notes could point to an image that is not yet available.
- The changelog parser must fail closed when the tag section is missing or duplicated.
- Setup cleanup must not silently change documented runtime defaults; it should replace examples, not invent new setup requirements.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat build`
  - `docker version`
  - `helm lint helm/technical-interview-demo`
  - `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml`
  - `.\scripts\ci\smoke-container.ps1 -ImageName technical-interview-demo`
  - the new release-note rendering helper against an existing version, for example the latest released tag section in `CHANGELOG.md`
- Tests to add or update:
  - a focused test that asserts `prod` startup fails when required database env vars are absent
  - update smoke validation if the required env var set changes
  - keep the existing repository build green with the new workflow-related files present
- Docs or contract checks:
  - `README.md`, `SETUP.md`, `.env.example`, and `ai/RELEASES.md` must agree on:
    - required deployment variables
    - Dependabot expectations
    - release publication outputs
  - no OpenAPI baseline refresh
  - no HTTP example updates
  - no benchmark rerun unless execution materially changes OAuth startup behavior
- Manual verification steps:
  - inspect the generated GitHub Release body in a test run or dry-run output and confirm it matches the intended `CHANGELOG.md` section plus image references
  - confirm the Docker package link resolves to the repository package location format expected by GitHub Container Registry
  - confirm `SETUP.md` no longer contains personal paths or workstation-specific examples

## Better Engineering Notes
- The smallest coherent `prod` hardening is property-placeholder removal plus one focused startup validation test. Avoid introducing a custom secret framework or environment abstraction.
- For release notes, prefer a small checked-in script over embedding complex markdown parsing inline in YAML; that keeps the workflow readable and makes local validation possible.
- `.env.example` should be cleaned together with `SETUP.md` because it is part of the same setup surface. Leaving it unchanged would preserve the exact portability issue the task is trying to remove.
- `scripts/run-phase-9-benchmarks.ps1` and historical plan validation notes still reference a personal JDK path. They are adjacent cleanup candidates, but they should stay out of this task unless the user expands the portability scope beyond setup-facing artifacts.

## Validation Results
- Worker task integration:
  - integrated `9e5850b` (`Make env example setup paths portable`) from worker commit `9f72d2db506565a99d2dc9ec71c926c52ea1442b`
  - integrated `ef150ad` (`Automate GitHub release publication`) from worker commit `d3f76e5211aa9d02f916471f9d56ceac71e9ae8d`
  - integrated `304cc1c` (`ci: add grouped dependabot updates`) from worker commit `cc642f7ab3a683c509263389e2d7418ba460b38b`
  - integrated `063ad9e` (`Harden prod datasource configuration`) from worker commit `bf71844819c4f2979878473958a675f8fd5d15c0`
- Coordinator-owned doc alignment on `main`:
  - updated `README.md` and `SETUP.md` to document:
    - required `prod` database variables
    - optional `SESSION_COOKIE_SECURE` with default `true`
    - grouped weekly Dependabot coverage for Gradle, GitHub Actions, and Docker
    - tag-driven GitHub Release publication from `CHANGELOG.md`
    - portable setup placeholders instead of workstation-specific paths
- Final validation run on `main` on 2026-05-01:
  - `docker version`: passed
  - `helm lint helm/technical-interview-demo`: passed
  - `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml`: passed
  - `.\gradlew.bat build`: passed with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`
  - `.\scripts\ci\smoke-container.ps1 -ImageName technical-interview-demo`: passed
  - `.\scripts\release\render-release-notes.ps1 -Tag v0.24.0 -TagImageReference ghcr.io/kamkie/technical-interview-demo:v0.24.0 -ShaImageReference ghcr.io/kamkie/technical-interview-demo:sha-6b1bc65 -PackagePageUrl https://github.com/kamkie/technical-interview-demo/pkgs/container/technical-interview-demo -OutputPath build\tmp\release-notes-preview.md`: passed
- Manual validation:
  - confirmed the rendered release-note preview used the exact `CHANGELOG.md` `v0.24.0` section plus appended container metadata
  - confirmed `SETUP.md` and `.env.example` no longer contain personal workstation paths
- Contract and benchmark checks:
  - no OpenAPI baseline refresh performed
  - no HTTP example updates required
  - no Phase 9 benchmark rerun performed because OAuth startup behavior was not changed

## User Validation
- Review the plan and confirm these defaults before implementation starts:
  - `prod` must require explicit database credentials
  - OAuth secrets stay optional unless `oauth` is active
  - Dependabot covers Gradle, GitHub Actions, and Docker on a low-noise cadence
  - GitHub Releases use `CHANGELOG.md` as the release-notes source of truth
  - portable setup examples should also include `.env.example`
