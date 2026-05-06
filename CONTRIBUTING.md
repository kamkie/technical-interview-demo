# Contributing Guide

Technical Interview Demo is a small, spec-driven Spring Boot application for interview exercises. Contributions should keep it readable, direct, and easy to reason about.

## Read This First

Use the human-facing docs deliberately:

- `README.md` for the project overview, implemented scope, and contract map
- `SETUP.md` for local prerequisites, `.env` loading, run commands, CI reproduction, and troubleshooting
- `WORKING_WITH_AI.md` for how to use AI across discovery, planning, implementation, verification, and release
- `ROADMAP.md` for active planned work only

Use the AI-facing docs only when they are the owner for the workflow or rule you are changing:

- `AGENTS.md` for repository-local AI rules, spec priority, required artifact updates, and definition of done
- `ai/PLAN.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, and `ai/RELEASES.md` for the AI-side planning, execution, validation, and release workflow

## Project Ground Rules

Keep these repository constraints intact unless the change explicitly redefines them:

- keep the demo small, readable, and easy to reason about
- prefer direct Spring MVC, Spring Data JPA, and `@Service` code over extra abstraction
- keep package names under `team.jit.technicalinterviewdemo`
- use PostgreSQL for runtime work and keep the local developer path Docker-friendly
- keep the public application contract centered on `/api/**`
- treat `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, and `/actuator/**` as internal or deployment-scoped surfaces unless a reviewed contract change says otherwise
- treat executable specs, REST Docs, the approved OpenAPI baseline, and reviewer HTTP examples as part of the product rather than optional documentation

When documentation changes, keep the right human-facing files aligned:

- `README.md` owns the concise project and contract summary
- `SETUP.md` owns environment, onboarding, runbooks, and troubleshooting
- `WORKING_WITH_AI.md` owns the human-facing AI collaboration lifecycle
- `CONTRIBUTING.md` owns contributor workflow and maintainer expectations

## Spec-Driven Development

Contribute spec-first, not implementation-first.

Normal flow:

1. Identify the behavior being changed.
2. Identify the governing spec or contract artifact.
3. Update or add the spec first when behavior is intentionally changing.
4. Implement the smallest coherent change that satisfies the updated spec.
5. Verify the executable and published artifacts still agree.

Authoritative artifact map:

- `src/test/java/` for executable behavior specs
- `src/docs/asciidoc/` for published REST Docs content
- `src/test/resources/openapi/approved-openapi.json` for the approved machine-readable public contract
- `src/test/resources/http/` for reviewer-facing runnable request examples
- `README.md` for the supported human-facing contract summary
- `ROADMAP.md` for active work only
- `CHANGELOG.md` for released history only

Change-routing rules:

- public API change: update implementation, tests, REST Docs, HTTP examples, OpenAPI when intentionally changed, and `README.md` if the supported contract changed
- internal refactor with no contract change: keep existing specs green and avoid unnecessary OpenAPI, HTTP example, or README edits
- setup or tooling change: update `SETUP.md`
- AI workflow or AI guidance change: update the owning AI guide and keep `AGENTS.md` aligned when the AI document set or maintenance rules changed
- roadmap reprioritization: update `ROADMAP.md`
- released history: update `CHANGELOG.md`

## Working With AI

If you are using AI in this repository, `WORKING_WITH_AI.md` is the human-facing starting point.

Use AI with the same discipline as manual work:

- frame the request in terms of behavior, not only code
- give the AI the correct owner documents for the current lifecycle phase
- keep work milestone-sized
- review the diff, validation, and contract impact yourself
- keep release work separate from implementation work

Human responsibilities do not move to the AI. The developer still owns:

- scope and product intent
- approval of assumptions and tradeoffs
- review of the resulting diff
- validation choices and acceptance of the evidence
- release decisions

For multi-step work, planning should happen before implementation. When the work is large enough to justify a real plan, create or revise an `ai/PLAN_*.md` file and follow the workflow described in `WORKING_WITH_AI.md` plus the owning `ai/` guides.

## Branches And Commit Messages

Use short, descriptive branch names:

- `feat/book-search`
- `fix/session-bootstrap`
- `docs/contributing-refresh`
- `refactor/localization-cache`

Recommended pattern:

```text
<type>/<short-kebab-description>
```

Suggested branch types:

- `feat`
- `fix`
- `docs`
- `chore`
- `refactor`
- `test`

Use concise, imperative commit subjects that describe one logical change.

Good examples:

- `Add admin user management examples`
- `Document post-deploy smoke expectations`
- `Refresh contributor workflow guide`

## Pull Request Expectations

Keep pull requests narrow enough to review quickly.

Before opening a PR:

1. Finish the intended local implementation scope first.
2. Run the required local validation for the actual change type.
3. Rebase or merge so the branch reflects the current target branch.
4. Update the right specs and docs when behavior or workflow changed.

Opening a PR is the handoff after local execution is complete. It is not a substitute for local validation, review, or documentation updates.

Each PR should include:

- a short summary of what changed
- the reason for the change
- the commands you ran to validate it
- any follow-up work intentionally left out of scope
- any security-sensitive changes such as auth, secrets, workflow permissions, logging of sensitive data, or container publication behavior

Self-review and reviewer focus should stay on bugs, regressions, spec drift, missing validation, and security-sensitive changes before style-only cleanup.

If the change affects public API behavior, include example requests or responses, or point reviewers to the updated REST Docs and OpenAPI change.

Default-branch expectations:

- require the `CI` workflow to pass
- require at least one reviewer
- prefer squash merges or another linear-history policy
- keep release tagging with maintainers who also own release validation

## Worktrees, Plans, And Integration

Treat `main` as the integration branch for completed work.

If you are executing a multi-step plan in a branch or git worktree:

- keep the work there until the full planned scope is complete
- push that branch and open a PR instead of trying to release directly from a worktree-only branch tip
- consider worktree-based execution complete only when the finished branch has been pushed and the PR is open or already merged onto `main`

Do not cut a release from unmerged worktree state.

## Validation Expectations

Default quality gate before asking for review:

```powershell
./build.ps1 build
```

Exception:

- `./build.ps1 build` performs the local uncommitted-change classifier check and exits successfully with manual-review guidance for lightweight-only changes; use `./build.ps1 -FullBuild build` to force the full Gradle build
- the same classifier also drives the `CI` short-circuit for lightweight-only push and pull-request ranges

Additional validation rules:

- use `SETUP.md` for JDK 25, Docker, `.env`, and command prerequisites
- use `./build.ps1 compileJava` or a similarly focused task for fast checks while editing, then use `./build.ps1 build` for final verification
- use `./build.ps1 -SkipTests build`, `./build.ps1 -SkipChecks build`, or both only for local loops, not final verification
- `-SkipChecks` skips formatting, PMD, SpotBugs, Error Prone, coverage verification, vulnerability scans, and SBOM checks
- rerun `./build.ps1 gatlingBenchmark` when changing book list or search behavior, localization lookup behavior, or OAuth or session startup behavior
- when both `build` and `gatlingBenchmark` are required, prefer one invocation such as `./build.ps1 build gatlingBenchmark --no-daemon` so Gradle reuses the same task graph instead of repeating work in separate runs
- do not run overlapping Gradle validation tasks in parallel, including `build` with `gatlingBenchmark`, `externalSmokeTest`, `externalDeploymentCheck`, or `scheduledExternalCheck`
- refresh the approved OpenAPI baseline only after intentional contract review with:

```powershell
./build.ps1 refreshOpenApiBaseline
```

- keep pull requests green on the `CI` workflow before asking for review

When deployment assets are part of the change, also run the relevant checks from `SETUP.md`, usually including:

- `helm lint infra/helm/technical-interview-demo`
- `helm template technical-interview-demo infra/helm/technical-interview-demo -f infra/helm/technical-interview-demo/values-local.yaml`
- `kubectl kustomize infra/k8s/overlays/local`
- `kubectl apply --dry-run=client -k infra/k8s/overlays/local`
- `kubectl kustomize infra/k8s/monitoring`
- `kubectl kustomize infra/monitoring/grafana`

## Documentation Expectations

Documentation is part of the change. Update the owning artifact instead of spreading partial updates across unrelated files.

Common routing:

- `README.md` for supported project scope and public contract summary
- `SETUP.md` for local setup, CI reproduction, deployment runbooks, and troubleshooting
- `WORKING_WITH_AI.md` for the human-facing AI collaboration lifecycle
- `AGENTS.md` and the relevant `ai/` guide when AI rules, ownership, workflow, or execution guidance changed
- `src/docs/asciidoc/` and the related REST Docs tests when public API behavior changed
- `src/test/resources/http/` when reviewer-facing request examples changed
- `ROADMAP.md` when active work changed
- `CHANGELOG.md` when preparing or documenting a release

## Release And Maintainer Expectations

Release preparation starts only after the approved implementation PR has been merged onto `main`.

Use `ai/RELEASES.md` for the detailed release workflow.

At a minimum, release preparation should include:

- reviewing modified Flyway migrations together with their metadata sidecars under `src/main/resources/db/migration/metadata/`
- classifying the release with `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag <previous-tag> -CurrentRef HEAD`
- capturing restore-drill evidence with `pwsh ./scripts/release/invoke-restore-drill.ps1 ...` for any `restore-sensitive` release
- confirming the exact release candidate passed `./build.ps1 -FullBuild build`
- deciding whether `./build.ps1 gatlingBenchmark` is required for the scoped changes
- running the manual `Post-Deploy Smoke` workflow with the expected build version and short commit id before promotion
- updating `CHANGELOG.md`, `ROADMAP.md`, and any executed `ai/PLAN_*.md` files before tagging
- verifying the remote `Release` workflow published the semantic tag, immutable short-SHA tag, and GitHub Release notes

## Formatting Expectations

Spotless is the formatting entry point.

Java formatting is CI-owned through the repository Spotless configuration and does not require IntelliJ IDEA or local formatter environment variables.

Use `./build.ps1 spotlessApply` to normalize formatting before review, and use `SETUP.md` for local IDE configuration.
