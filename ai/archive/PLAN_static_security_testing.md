# Plan: Static Security Testing

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Complete the selected supply-chain hardening roadmap work by adding repo-owned static application security testing on top of the already implemented dependency and image vulnerability scanning.
- Keep the existing Trivy-based scanning contract and explicit exception review model, then add a code-focused security signal that fits the Gradle and CI workflow.
- Success is measured by: a stable SAST task in the standard verification flow, explicit suppression policy, aligned CI reporting, and passing build verification.

## Scope
- In scope:
  - `Add static application security testing and keep it aligned with the existing Gradle and CI workflows`
  - `Keep dependency, image, and vulnerability exceptions reviewable with explicit policy files and documented expiration or revalidation expectations`
- Out of scope:
  - replacing the existing Trivy dependency/image scanning contract
  - SBOM generation, signing, provenance, or attestations
  - adding a hosted security platform as a mandatory repository dependency
  - broad non-security static-analysis changes unrelated to the selected roadmap items

## Current State
- The standard build already runs dependency and image vulnerability scans through [build.gradle.kts](D:\Projects\Jit\technical-interview-demo\build.gradle.kts).
- CI and release workflows already upload scan artifacts from `build/reports/security/` in [.github/workflows/ci.yml](D:\Projects\Jit\technical-interview-demo\.github\workflows\ci.yml) and [.github/workflows/release.yml](D:\Projects\Jit\technical-interview-demo\.github\workflows\release.yml).
- The current suppression contract is documented in [trivy.ignore](D:\Projects\Jit\technical-interview-demo\config\security\trivy.ignore) and surfaced in [README.md](D:\Projects\Jit\technical-interview-demo\README.md).
- The archived vulnerability-scanning plan in [PLAN_Vulnerability_Scanning.md](D:\Projects\Jit\technical-interview-demo\ai\archive\PLAN_Vulnerability_Scanning.md) explicitly left static application security testing out of scope, so that roadmap item is still open even though dependency/image scanning is already complete.

## Requirement Gaps And Open Questions
- The roadmap does not name the static application security engine.
  - Why it matters: the tool choice drives build wiring, report format, false-positive handling, and CI ergonomics.
  - Fallback if the user does not answer: use SpotBugs plus FindSecBugs so the new signal stays JVM-native, repo-owned, and Gradle-friendly.
- The roadmap does not define the failure threshold or suppression format for code-level findings.
  - Why it matters: noisy security scans will be bypassed unless the policy is explicit.
  - Fallback if the user does not answer: fail the standard verification flow on unsuppressed high-severity or high-confidence findings and keep suppressions in a checked-in policy file with rationale and revalidation metadata.

## Locked Decisions And Assumptions
- Keep the current Trivy contract as the source of truth for dependency and image vulnerability scanning.
- Add code-focused static security testing as a complement, not as a replacement for PMD, Error Prone, or Trivy.
- Reuse the repository's existing `build/reports/security/` reporting pattern if the selected tool can emit stable outputs there.
- Keep the implementation repo-owned and automatable from Gradle and GitHub Actions.

## Affected Artifacts
- Tests and validation:
  - [build.gradle.kts](D:\Projects\Jit\technical-interview-demo\build.gradle.kts)
  - possibly new build logic under `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/`
- Policy files:
  - [trivy.ignore](D:\Projects\Jit\technical-interview-demo\config\security\trivy.ignore)
  - likely a new checked-in SAST suppression or exclude file under `config/security/`
- CI and release workflows:
  - [ci.yml](D:\Projects\Jit\technical-interview-demo\.github\workflows\ci.yml)
  - [release.yml](D:\Projects\Jit\technical-interview-demo\.github\workflows\release.yml)
- Docs:
  - [README.md](D:\Projects\Jit\technical-interview-demo\README.md)
  - [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md)
  - possibly [CONTRIBUTING.md](D:\Projects\Jit\technical-interview-demo\CONTRIBUTING.md) if contributor review expectations change
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - targeted security-task runs for the selected tool

## Execution Milestones
### Milestone 1: Add Code-Focused Security Analysis To Gradle
- Goal: introduce a stable SAST task and wire it into the repository's verification flow.
- Files to update:
  - [build.gradle.kts](D:\Projects\Jit\technical-interview-demo\build.gradle.kts)
  - possibly `buildSrc` task helpers if the selected tool needs custom orchestration
- Behavior to preserve:
  - existing `dependencyVulnerabilityScan` and `imageVulnerabilityScan` task names and semantics
  - existing `.\gradlew.bat build` flow as the main verification entry point
- Exact deliverables:
  - one explicit static application security task
  - stable report outputs
  - standard build and CI integration

### Milestone 2: Formalize Security Exception Handling Across Scan Types
- Goal: make code-level suppressions as reviewable as the existing vulnerability suppressions.
- Files to update:
  - [trivy.ignore](D:\Projects\Jit\technical-interview-demo\config\security\trivy.ignore) if shared wording or cross-references need cleanup
  - likely new repo-owned suppression config for the selected SAST engine
  - [README.md](D:\Projects\Jit\technical-interview-demo\README.md)
  - [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md)
- Exact deliverables:
  - explicit suppression format and ownership rules
  - documented expiration or revalidation expectations
  - reviewer-facing artifact locations

### Milestone 3: Align CI And Release Reporting
- Goal: keep the new security signal consistent across local, CI, and tag-driven release verification.
- Files to update:
  - [ci.yml](D:\Projects\Jit\technical-interview-demo\.github\workflows\ci.yml)
  - [release.yml](D:\Projects\Jit\technical-interview-demo\.github\workflows\release.yml)
  - [README.md](D:\Projects\Jit\technical-interview-demo\README.md)
- Exact deliverables:
  - CI artifact upload and failure semantics that include the new SAST reports
  - release workflow alignment if the new task should run there as part of the standard build

## Edge Cases And Failure Modes
- The SAST tool must not duplicate dependency/image vulnerability findings so heavily that the security signal becomes unusable.
- Generated code, test fixtures, or framework boilerplate can create noisy findings and may need explicit exclusions.
- Suppression policy must stay narrow; a broad allowlist would defeat the checked roadmap item rather than completing it.
- CI artifact locations must stay stable so blocked builds remain diagnosable.

## Validation Plan
- Commands to run:
  - the dedicated SAST task once introduced
  - `.\gradlew.bat vulnerabilityScan`
  - `.\gradlew.bat build`
- Tests to add or update:
  - build-logic coverage only if custom Gradle task code is introduced in `buildSrc`
- Docs or contract checks:
  - confirm `README.md` and `SETUP.md` describe local and CI security verification consistently
- Manual verification steps:
  - inspect `build/reports/security/` and confirm all expected report types are present and reviewer-usable
  - confirm a deliberate unsuppressed finding would fail the intended verification gate

## Better Engineering Notes
- The existing Trivy work already covers dependency/image vulnerability scanning, so this plan should not re-solve that problem.
- A JVM-native SAST choice is the smallest coherent move unless the user explicitly wants a hosted or polyglot platform.
- If the selected SAST engine cannot produce stable repo-owned artifacts, reconsider the tool choice rather than weakening artifact reviewability.

## Validation Results
- 2026-05-03: Ran `.\gradlew.bat staticSecurityScan vulnerabilityScan build --no-daemon` on `codex/unfinished-plans-integration` with Java 25 (`C:\Users\kamki\.jdks\azul-25.0.3`).
- Result: passed.
- Notes:
  - `staticSecurityScan` executed `spotbugsMain` with the checked-in SpotBugs include/exclude policy files and produced reports under `build/reports/security/static/main/`.
  - `vulnerabilityScan` executed both Trivy-backed dependency and image scans and kept the build green.
  - `build` also passed the full repository gate, including tests, REST Docs/OpenAPI generation, and Docker image build.

## User Validation
- Run the new security analysis task directly and then run `.\gradlew.bat build`.
- Confirm the build surfaces code-focused security findings separately from dependency/image vulnerability findings.
- Confirm the documented suppression path is explicit, narrow, and reviewable in git.
