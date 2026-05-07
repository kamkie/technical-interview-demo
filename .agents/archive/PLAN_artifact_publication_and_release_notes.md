# Plan: Artifact Publication And Release Notes

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Complete the checked supply-chain and release-governance roadmap work that shares build, workflow, and release-artifact ownership.
- Keep SBOM generation, stable static-analysis artifact publication, and GitHub Release note policy in one plan because they all change the same Gradle and GitHub Actions release surfaces.
- Success is measured by: reproducible SBOM outputs for the packaged app and image, reviewable CI artifacts for quality failures, cumulative GitHub Release notes derived from the full unreleased change range, and passing build plus workflow-oriented validation.

## Scope
- In scope:
  - `Generate and publish an SBOM for the application artifact and container image as part of the build or release flow`
  - `Publish stable static-analysis artifacts from GitHub Actions runs so PMD and similar quality failures are reviewable from the Actions UI without reproducing them locally`
  - `Decide whether GitHub Release notes should keep mirroring only the exact tagged CHANGELOG.md section or instead become cumulative notes covering every unreleased user-visible change since the previous published GitHub Release`
  - `If cumulative GitHub Release notes are required, update release preparation and release-note rendering so delayed or batched releases publish the full change range from the previous published release to the new tag and fail closed when that range cannot be derived`
- Out of scope:
  - signing images, provenance attestations, or artifact attestations
  - public API behavior changes
  - replacing GitHub Actions as the delivery path
  - introducing a hosted release-management platform

## Current State
- `build.gradle.kts` already runs the standard build, Docker image build, vulnerability scans, and SpotBugs/FindSecBugs-based static security checks, but it does not generate an SBOM.
- `.github/workflows/ci.yml` uploads security scan reports from `build/reports/security/`, but it does not publish stable reviewer-facing artifacts for PMD or similar non-security analysis failures.
- `.github/workflows/release.yml` currently creates GitHub Releases from the exact matching `CHANGELOG.md` section for the pushed tag after image publication succeeds.
- `README.md` and `ai/RELEASES.md` describe that exact-tag release-note behavior as the current repository contract.
- The checked roadmap items still leave open whether that exact-tag model should remain the supported behavior or be replaced by cumulative notes derived from the gap between the previous published GitHub Release and the new tag.

## Requirement Gaps And Open Questions
- The roadmap asks for an SBOM but does not specify the required standard or publication target.
  - Why it matters: CycloneDX versus SPDX affects plugin choice, artifact naming, and downstream tooling.
  - Fallback if the user does not answer: publish CycloneDX JSON artifacts for both the packaged app and the container image because that keeps the implementation small and repo-owned.
- The roadmap names PMD and similar artifacts but does not define the exact artifact set.
  - Why it matters: uploading every transient build output would add noise, while uploading only one report may leave failed runs non-diagnosable.
  - Fallback if the user does not answer: publish stable artifact bundles for PMD plus any existing repo-owned static-analysis reports that already act as blocking gates in `.\gradlew.bat build`.

## Locked Decisions And Assumptions
- User decision:
  - GitHub Releases must publish cumulative notes covering every unreleased user-visible change since the previous published GitHub Release.
- Keep Gradle as the source of truth for generated build artifacts.
- Keep GitHub Actions as the publication path for CI and tagged-release artifacts.
- Preserve the current fail-closed behavior for release-note rendering when the chosen source text cannot be derived reliably.
- Treat cumulative release-note policy as a maintainer workflow contract and update `README.md` plus `ai/RELEASES.md` in the same change.
- Do not widen the plan into signing or provenance work; those remain separate roadmap items.

## Affected Artifacts
- Tests and build validation:
  - `build.gradle.kts`
  - possibly supporting build logic under `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/`
- Workflow automation:
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
- Docs:
  - `README.md`
  - `SETUP.md`
  - `ai/RELEASES.md`
- Release metadata:
  - `CHANGELOG.md` only if the chosen release-note policy requires clarified maintainer wording
  - possibly a checked-in helper under `scripts/release/` if cumulative rendering is implemented
- Build outputs and reviewer artifacts:
  - new SBOM output locations under `build/`
  - stable static-analysis artifact locations under `build/reports/`
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - any dedicated SBOM task introduced by execution
  - local release-note rendering smoke validation if workflow logic changes

## Execution Milestones
### Milestone 1: Decide And Lock The Release-Notes Policy
- Goal:
  - lock cumulative GitHub Release notes as the repository contract before changing automation.
- Files to update:
  - `README.md`
  - `ai/RELEASES.md`
  - `.github/workflows/release.yml`
- Behavior to preserve:
  - GitHub Release creation happens only after tagged image publication succeeds
  - release-note rendering fails closed on ambiguous or missing source content
- Exact deliverables:
  - cumulative GitHub Release notes as the explicit supported policy
  - aligned maintainer docs describing that policy
  - one explicit derivation rule based on the previous published GitHub Release

### Milestone 2: Generate And Publish SBOM Artifacts
- Goal:
  - add reproducible SBOM generation for the packaged application artifact and the published container image.
- Files to update:
  - `build.gradle.kts`
  - possibly `buildSrc/...` if custom orchestration is needed
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
  - `README.md`
  - `SETUP.md`
- Exact deliverables:
  - one documented SBOM format and output layout
  - build or release wiring that generates app and image SBOMs from the actual shipped artifacts
  - artifact publication from GitHub Actions so failed or successful runs remain reviewable

### Milestone 3: Publish Stable Static-Analysis Artifact Bundles
- Goal:
  - make PMD and similar blocking quality failures diagnosable directly from the Actions UI.
- Files to update:
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml` if tagged builds should expose the same artifacts
  - `README.md`
  - `SETUP.md`
  - `build.gradle.kts` only if current report outputs are unstable or incomplete
- Exact deliverables:
  - stable upload locations for PMD and other build-blocking static-analysis reports
  - artifact upload steps that still run on failure
  - docs describing where maintainers find those artifacts

### Milestone 4: Implement Cumulative Release Rendering If Required
- Goal:
  - update release-note rendering to cover the full unreleased range from the previous published GitHub Release to the new tag.
- Files to update:
  - `.github/workflows/release.yml`
  - likely a small helper under `scripts/release/`
  - `ai/RELEASES.md`
  - `README.md`
- Exact deliverables:
  - cumulative note derivation from the previous published GitHub Release to the new tag
  - fail-closed handling when the previous-release boundary cannot be derived
  - explicit maintainer guidance for delayed or batched releases

## Edge Cases And Failure Modes
- SBOM generation must target the packaged boot jar and the actual tagged image, not source-only or pre-build inputs.
- Artifact uploads must still happen when PMD or another analysis task fails, or the new visibility requirement is not actually met.
- The workflow must distinguish between git tags and published GitHub Releases; missing or unpublished prior releases must fail clearly rather than silently truncating the cumulative notes.
- Release-note policy and `ai/RELEASES.md` must stay aligned; otherwise maintainers will prepare releases using the wrong model.
- Adding new reporting steps must not create a second non-Gradle source of truth for build artifacts.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat build`
  - any dedicated SBOM task added during execution
  - a local smoke validation of the release-note rendering logic if `.github/workflows/release.yml` changes
- Tests to add or update:
  - build-logic tests only if new custom Gradle task code is introduced in `buildSrc`
- Docs or contract checks:
  - keep `README.md`, `SETUP.md`, and `ai/RELEASES.md` aligned on:
    - where SBOMs are produced
    - where CI static-analysis artifacts are published
    - how GitHub Release notes are sourced
  - no OpenAPI baseline refresh expected
  - no HTTP example changes expected
- Manual verification steps:
  - inspect a local or simulated artifact bundle and confirm SBOM plus static-analysis reports are reviewer-usable
  - run the cumulative release-note renderer against a known previous-release-to-new-tag range and confirm the full intended unreleased section is included

## Better Engineering Notes
- SBOM generation and release-note rendering both need to stay tied to the exact shipped artifacts, not a looser approximation.
- Keep the implementation small: one repo-owned SBOM format, stable report paths, and a readable release workflow.
- Keep the cumulative-note derivation logic explicit and fail-closed rather than trying to infer missing release boundaries heuristically.

## Validation Results
- `& .\scripts\release\render-release-notes.ps1 -ChangelogPath CHANGELOG.md -CurrentTag v1.3.0 -PreviousPublishedTag v1.2.1 ... -OutputPath build/tmp/release-notes-smoke.md` (pass): rendered cumulative notes covering `v1.3.0` and `v1.2.2` plus release metadata.
- `& .\scripts\release\render-release-notes.ps1 -ChangelogPath CHANGELOG.md -CurrentTag v1.3.0 -PreviousPublishedTag v9.9.9 ...` (expected fail): failed closed with `No CHANGELOG.md section matched tag 'v9.9.9'.`
- `.\gradlew.bat sbom --no-daemon`:
  - first attempt (fail): local `JAVA_HOME` pointed to Java 11, so Gradle refused to run.
  - retry with `JAVA_HOME=C:\Users\kamki\AppData\Local\Programs\IntelliJ IDEA Ultimate\jbr` (pass): generated app and image CycloneDX outputs.
- `.\gradlew.bat build --no-daemon` with Java 25:
  - first two attempts (fail): `spotlessKotlinGradleCheck` flagged trailing-blank-line formatting in `build.gradle.kts`.
  - `.\gradlew.bat spotlessApply --no-daemon` (pass) to normalize formatting.
  - final retry of `.\gradlew.bat build --no-daemon` (pass).

## User Validation
- Review one CI-style artifact bundle and confirm it contains the promised SBOM and static-analysis outputs.
- Confirm the documented GitHub Release notes policy matches the workflow behavior.
- If cumulative notes are chosen, verify one dry-run example spans the full previous-release-to-new-tag range.
