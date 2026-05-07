# Plan: Supply Chain Artifact Trust Hardening

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Implement one coherent supply-chain hardening plan from these checked `ROADMAP.md` items:
  - `Add GitHub CodeQL scanning in GitHub Actions with repository-owned configuration, uploaded SARIF results, and explicit guidance for how it complements rather than silently duplicates the existing SpotBugs/FindSecBugs and PMD quality gates`
  - `Sign published container images and attach provenance or attestations so the sample release story covers artifact authenticity, not only version tags`
- Keep the existing Gradle-owned Trivy, SpotBugs/FindSecBugs, PMD, SBOM, and external-smoke flow intact, then add GitHub-native signals for code scanning and release-artifact authenticity on top.
- Success is measured by: one dedicated CodeQL workflow with repo-owned configuration and SARIF upload, one tag-driven release workflow that publishes signed and attestable GHCR image artifacts by digest, and aligned README plus AI release guidance that explains how these new signals fit the existing release story.

## Scope
- In scope:
  - GitHub Actions workflow changes for CodeQL scanning and release-image trust metadata
  - repo-owned CodeQL configuration checked into `.github/`
  - release-workflow signing and provenance or attestation publication for GHCR images
  - documentation updates that explain how CodeQL complements the existing Gradle security gates
  - documentation updates that explain the signed-image and attestation verification story for released container artifacts
- Out of scope:
  - replacing Trivy, SpotBugs/FindSecBugs, PMD, or SBOM generation
  - changing application runtime behavior, API contracts, or Gradle security scan ownership
  - Kubernetes admission-policy enforcement or deployment-time signature verification
  - cosign key management outside the GitHub Actions keyless OIDC flow
  - SLSA-wide build-system redesign, reproducible-build hardening, or non-container artifact signing

## Current State
- `.github/workflows/ci.yml` runs `./gradlew build`, uploads vulnerability/static-analysis/SBOM artifacts, and runs `./gradlew externalSmokeTest`, but it does not run GitHub CodeQL or upload SARIF through GitHub code scanning.
- `.github/workflows/release.yml` builds and scans the tagged image, validates it with `externalSmokeTest`, pushes semantic and short-SHA GHCR tags, and creates the GitHub Release, but it does not sign the published image digest or publish provenance or attestations.
- `build.gradle.kts` already owns Trivy-backed dependency/image scans, SpotBugs plus FindSecBugs static security scanning, and CycloneDX SBOM generation; those are part of the current repo truth and should remain the source of truth for build-time security gates.
- `README.md` already documents the existing vulnerability scan, static-analysis, SBOM, CI, and release workflow behavior, but it does not mention CodeQL or artifact-authenticity verification for published GHCR images.
- `ai/RELEASES.md` already requires verifying published GHCR tags after push, but it does not yet require verifying CodeQL/code-scanning posture or signed and attestable release artifacts.

## Requirement Gaps And Open Questions
- No blocking user-input gap remains for planning.
- Non-blocking platform assumption:
  - this plan assumes the repository can use GitHub CodeQL and GitHub Actions OIDC-backed signing or attestation features on its current GitHub plan
  - why it matters: both roadmap items depend on GitHub-native capabilities outside the Java codebase itself
  - fallback if implementation proves blocked by repository or organization policy: fail fast with the exact unavailable capability, keep the repo-owned workflow and docs changes narrow, and do not silently substitute a materially different third-party hosted service

## Locked Decisions And Assumptions
- Include exactly the two checked roadmap items listed in `ROADMAP.md` and no additional unchecked roadmap work.
- Treat the two checked items as one coherent plan because they both modify the repository's GitHub-hosted supply-chain and release-trust surface.
- Keep CodeQL additive: it complements the current Trivy and SpotBugs/FindSecBugs plus PMD contract instead of replacing or duplicating the existing Gradle security gates.
- Use a repo-owned CodeQL configuration file so language scope, query packs, and exclusions are explicit in git history rather than implicit in GitHub defaults.
- Keep image trust publication tied to the tag-driven `Release` workflow and the actual pushed GHCR digest, not to a local pre-push approximation.
- Use GitHub Actions OIDC-based keyless signing and GitHub-native provenance or attestation publication so the repo does not gain long-lived signing secrets for this roadmap slice.
- Sign and attest the immutable pushed image digest, not mutable tags by name; semantic and short-SHA tags may both point at that one signed digest.

## Affected Artifacts
- Tests:
  - no application tests under `src/test/java/` should need changes because public API and runtime behavior are unchanged
  - workflow validation may be added through a repo-owned script only if needed to keep the workflow changes reviewable; otherwise use workflow lint plus manual review
- Docs:
  - `README.md`
  - `ai/RELEASES.md`
  - optionally `CONTRIBUTING.md` if release or maintainer verification guidance overlaps there materially
- OpenAPI:
  - none expected
- HTTP examples:
  - none expected
- Source files:
  - `.github/workflows/ci.yml` only if the final CodeQL design needs CI coordination or documentation-facing artifact naming alignment
  - new `.github/workflows/codeql.yml`
  - new `.github/codeql/codeql-config.yml` or equivalent repo-owned configuration file
  - `.github/workflows/release.yml`
  - optionally `scripts/release/` if a small helper is needed to keep digest resolution or verification logic readable and non-duplicated
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - workflow lint for `.github/workflows/*.yml`
  - no OpenAPI refresh expected
  - no `gatlingBenchmark` rerun expected because no book/localization/OAuth-session runtime behavior changes are planned

## Execution Milestones
### Milestone 1: Lock The Security And Artifact-Trust Contract
- goal:
  - define the intended CodeQL and release-artifact-trust posture in the owning docs before changing workflows.
- files to update:
  - `README.md`
  - `ai/RELEASES.md`
  - optionally `CONTRIBUTING.md` if contributor-facing workflow wording overlaps materially
- behavior to preserve:
  - current Gradle build remains the owner of Trivy, SpotBugs/FindSecBugs, PMD, SBOM, and smoke-test verification
  - release notes and release publication flow stay tag-driven
- exact deliverables:
  - documented statement that CodeQL complements rather than replaces the current Gradle security gates
  - documented statement that released GHCR images are expected to be signed and published with provenance or attestations
  - explicit maintainer verification checklist additions for post-push signature, attestation, and code-scanning checks

### Milestone 2: Add Repo-Owned CodeQL Scanning
- goal:
  - add GitHub-native code scanning without turning the existing `CI` workflow into a second owner of the whole security story.
- files to update:
  - new `.github/workflows/codeql.yml`
  - new `.github/codeql/codeql-config.yml` or equivalent
  - optionally `README.md` if workflow naming or artifact wording needs final alignment after implementation detail is chosen
- behavior to preserve:
  - `CI` continues to own `./gradlew build`, external smoke validation, and artifact uploads
  - existing Trivy and SpotBugs/FindSecBugs signals remain unchanged
- exact deliverables:
  - CodeQL workflow triggered on `pull_request`, `push` to `main`, and a reasonable scheduled cadence
  - explicit GitHub permissions for code scanning such as `security-events: write`
  - repo-owned Java or Kotlin analysis configuration checked into `.github/`
  - explicit build strategy for CodeQL that is lighter than duplicating the full Gradle `build`, but still compiles the project accurately enough for Java analysis
  - SARIF results appearing through GitHub code scanning rather than as ad hoc uploaded build artifacts

### Milestone 3: Sign And Attest Published Release Images
- goal:
  - make the release workflow publish authenticity metadata for the actual shipped GHCR image digest.
- files to update:
  - `.github/workflows/release.yml`
  - optionally `scripts/release/` if a helper improves digest handling clarity
  - `README.md`
  - `ai/RELEASES.md`
- behavior to preserve:
  - release still builds the tagged image with Gradle, runs the existing scans, validates with `externalSmokeTest`, pushes both semantic and short-SHA GHCR tags, and creates the GitHub Release afterward
  - the pushed release artifacts remain the exact packaged image already validated by the workflow
- exact deliverables:
  - release workflow permissions expanded only as needed for OIDC signing and GitHub attestations or provenance publication
  - deterministic resolution of the pushed image digest after GHCR publication
  - keyless signing of that digest through GitHub Actions OIDC
  - provenance or attestation publication bound to the same digest in a GitHub-native verification path
  - documented verification path for maintainers and users, for example digest-first verification rather than tag-name trust

## Edge Cases And Failure Modes
- CodeQL autobuild may try to run an unexpectedly broad Gradle lifecycle; the workflow should use an explicit compile-oriented command so analysis does not silently duplicate the full CI build cost.
- The repository already has multiple security signals; the new CodeQL docs must not imply that GitHub code scanning replaces Trivy or SpotBugs/FindSecBugs.
- Image signing must target the immutable digest after push. Signing mutable tags by name would weaken the authenticity story and make verification ambiguous.
- Semantic and short-SHA tags should continue to reference the same digest; the workflow must not accidentally sign one artifact and publish another.
- If GitHub artifact attestations or OIDC token minting are unavailable in the repository or organization policy, the implementation must fail clearly rather than silently publishing unsigned images.
- Release workflow changes must not break the current GitHub Release creation, external smoke validation, or GHCR publishing order.
- Documentation must distinguish between published authenticity metadata and actual downstream enforcement; this plan publishes trust material but does not enforce admission or runtime verification.

## Validation Plan
- commands to run:
  - `docker run --rm -v "${PWD}:/repo" -w /repo rhysd/actionlint:latest`
  - `.\gradlew.bat build`
- tests to add or update:
  - no application test additions expected unless a helper script is introduced and merits narrow local validation
- docs or contract checks:
  - confirm `README.md` and `ai/RELEASES.md` describe CodeQL as complementary to Trivy plus SpotBugs/FindSecBugs rather than a replacement
  - confirm docs describe signed and attestable GHCR release artifacts by digest, not only by tag name
  - confirm no OpenAPI, REST Docs, HTTP examples, or API-contract docs changed unexpectedly
- manual verification steps:
  - inspect the `CodeQL` workflow definition and verify triggers, Java analysis scope, explicit build step, and `security-events: write` permission are present
  - inspect the `Release` workflow and verify it still builds, scans, smoke-tests, pushes both tags, signs the pushed digest, and publishes provenance or attestations before or alongside GitHub Release creation as intended
  - after the first real tagged release using the new workflow, verify in GitHub that the CodeQL scan results are visible and the published GHCR artifact exposes the expected authenticity metadata path

## Better Engineering Notes
- Keep the implementation small and GitHub-native. This roadmap slice does not justify inventing a separate CI platform, replacing Gradle security tasks, or redesigning image publication around an entirely different builder.
- If the release workflow becomes hard to read because of digest resolution or verification steps, factor only the repeated shell logic into a small repo-owned helper under `scripts/release/` instead of letting YAML grow opaque.
- Do not widen this plan into deployment admission control, cluster policy, or build reproducibility hardening; those can be planned later once the repository first publishes verifiable release artifacts.

## Validation Results
- `docker run --rm -v "${PWD}:/repo" -w /repo rhysd/actionlint:latest`
  - passed on 2026-05-03
  - verified `.github/workflows/codeql.yml` and the updated `.github/workflows/release.yml` are syntactically valid for GitHub Actions
- `.\gradlew.bat build`
  - passed on 2026-05-03 after explicitly loading the local `.env` file into the shell environment so `JAVA_HOME` was available to Gradle
  - build completed successfully in 2m 35s and kept the existing Gradle-owned SpotBugs/FindSecBugs, PMD, Trivy, SBOM, tests, and Docker-image verification flow green
- Manual contract review
  - confirmed `README.md` describes CodeQL as additive to the Gradle-owned SpotBugs/FindSecBugs and PMD gates rather than a replacement
  - confirmed `README.md` and `ai/RELEASES.md` describe digest-first release verification with keyless signing and provenance attestation for the published GHCR digest
  - confirmed no OpenAPI baseline, REST Docs sources, or reviewer HTTP examples changed because public API and runtime behavior were unchanged

## User Validation
- Open the new plan and confirm it includes exactly these checked roadmap items and no unrelated unchecked work:
  - CodeQL scanning in GitHub Actions with repo-owned configuration
  - signed and attestable GHCR release artifacts
- After implementation, inspect the GitHub Actions workflows and confirm the new trust story is explicit: Gradle still owns build-time security gates, CodeQL adds GitHub-native code scanning, and released images are no longer trusted by tag name alone.
