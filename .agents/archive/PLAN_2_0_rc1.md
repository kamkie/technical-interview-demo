# Plan: 2.0 RC1 Contract Freeze And Release

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Plan the `v2.0.0-RC1` milestone as a release-preparation and publication task on `main`, not as another feature batch.
- Freeze the current `2.0` published contract, fix only the remaining release-phase drift needed to describe the already-landed state accurately, and then cut and verify the RC1 tag.
- Success is measured by: the exact RC1 candidate audited and validated, release metadata aligned, annotated tag `v2.0.0-RC1` created from `main`, remote publication verified, and the manual post-deploy smoke run confirming the released identity.

## Scope
- In scope:
  - auditing the current `main` candidate for remaining pre-RC contract-doc drift before tagging
  - updating only the release-phase or release-history artifacts needed to represent the current `2.0` state correctly
  - exact-candidate validation for the RC1 commit, including release-impact review and local smoke proof
  - preparing the RC1 release commit, annotated tag, plan archival, and roadmap cleanup
  - pushing the release and verifying the remote GitHub Release, GHCR publication, signature, provenance, and post-deploy smoke result
- Out of scope:
  - adding new features, refactors, or intentional public API changes
  - stable `v2.0.0` release preparation or removal of the entire `2.0` roadmap track
  - workflow redesign beyond narrow fixes required to unblock the RC1 release
  - hiding any discovered contract bug or undocumented behavior change inside release prep instead of treating it as a blocker

## Current State
- At execution start, `ROADMAP.md` selected the RC1 freeze item: freeze the `2.0` contract and cut `v2.0.0-RC1` from `main` only after the exact candidate passes the required validation.
- Before release preparation, local `main` was ahead of `origin/main` and the `v2.0.0-M8` tag by unreleased documentation and AI-guidance commits plus the milestone-1 alignment commit.
- `README.md` now delegates the current release phase to `ROADMAP.md`, and `ROADMAP.md` `## Current Project State` now carries the active prerelease state, breaking-change policy, and next target version for the RC1 candidate.
- Before the RC1 release-preparation move, `CHANGELOG.md` carried `## [Unreleased]` notes for the post-`v2.0.0-M8` docs and AI-guidance delta, and `## [v2.0.0-M8] - 2026-05-05` was the latest released section.
- This executed RC1 plan is archived as `ai/archive/PLAN_2_0_rc1.md`. The related pre-RC implementation plans are already archived under `ai/archive/`.
- The tag-driven `Release` workflow already rebuilds and validates the tagged image with `./gradlew build` and `./gradlew externalSmokeTest`, publishes the semantic and short-SHA GHCR tags, signs the immutable digest, attests provenance, and creates a GitHub Release from `CHANGELOG.md`.
- The release-note workflow resolves the previous published GitHub Release from non-prerelease releases only, so RC1 release notes will be cumulative back to the last stable published release boundary rather than only back to `v2.0.0-M8`.
- Planning-time release-impact review for `v2.0.0-M8..HEAD` already reports `none` from `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag v2.0.0-M8 -CurrentRef HEAD`, so no migration-driven restore drill is currently expected unless the RC1 write set changes.

## Requirement Gaps And Open Questions
- No blocking user-input gaps remain. The RC1 roadmap item and release guides are specific enough to plan directly.
- Non-blocking execution lookup:
  - confirm whether the final RC1 candidate remains `HEAD` after the small release-phase doc alignment or whether another release-prep commit is needed
  - why it matters: all heavyweight validation and the tag must point to the exact same candidate commit
  - fallback: if any change lands after local validation, rerun the stale checks on the new candidate before tagging
- Non-blocking execution lookup:
  - confirm the latest default-branch CodeQL posture is healthy or intentionally reviewed before publication
  - why it matters: `ai/RELEASES.md` treats that as part of release readiness
  - fallback: stop and review the outstanding CodeQL result instead of tagging through unresolved security-scan noise

## Locked Decisions And Assumptions
- Treat `v2.0.0-RC1` as the `2.0` contract-freeze milestone, not as another prerelease for taking new feature work.
- Preserve the current published `2.0` behavior. Only fix doc, changelog, roadmap, or release-metadata drift needed to describe the already-landed state correctly.
- Use `v2.0.0-M8` as the previous release tag for migration-impact review during RC1 preparation.
- Treat the current local `main` history as the starting candidate. Do not rewrite history or tag around the unreleased `README.md` and `ROADMAP.md` delta; either include it intentionally in the RC1 candidate or supersede it with aligned follow-up edits on `main`.
- Run local `externalSmokeTest` before tagging even though the remote `Release` workflow also runs it. RC1 should fail locally before a tag is pushed, not only after publication starts.
- `.\gradlew.bat gatlingBenchmark` is not expected for this plan because RC1 should not change book search, localization lookup, or OAuth/session startup behavior. Reintroduce it only if the final RC1 write set unexpectedly touches one of those surfaces.
- Include remote publication and verification in the plan because the selected roadmap item is to cut `v2.0.0-RC1` from `main`, not only to draft local release metadata.
- If the contract audit uncovers a real behavior or documentation mismatch that needs more than a narrow alignment fix, stop and treat it as an RC1 blocker instead of silently expanding release prep into implementation work.

## Execution Mode Fit
- Recommended default mode: `Single Branch`
- Why that mode fits best:
  - release work must finish on `main`
  - the write set is small and coupled around one exact candidate commit, one validation story, and shared release metadata
  - splitting this work would create coordination overhead around `README.md`, `CHANGELOG.md`, `ROADMAP.md`, the plan file, and the final tag
- Coordinator-owned or otherwise shared files if the work later fans out:
  - `README.md`
  - `CHANGELOG.md`
  - `ROADMAP.md`
  - `ai/archive/PLAN_2_0_rc1.md`
- Candidate worker boundaries or plan splits if later delegation becomes necessary:
  - slice 1: contract-phase audit and narrow README or changelog alignment
  - slice 2: release-readiness validation and evidence capture
  - slice 3: release-metadata preparation, tagging, and post-push verification
  - even with those slices, keep final integration and tagging coordinator-owned on `main`

## Affected Artifacts
- Contract and release-phase audit targets:
  - `README.md`
  - `ROADMAP.md`
  - `src/docs/asciidoc/`
  - `src/test/resources/http/`
  - `src/test/resources/openapi/approved-openapi.json`
- Release metadata and plan tracking:
  - `CHANGELOG.md`
  - `ai/archive/PLAN_2_0_rc1.md`
- Release workflow and verification context:
  - `.github/workflows/release.yml`
  - `.github/workflows/post-deploy-smoke.yml`
  - `scripts/release/get-release-migration-impact.ps1`
  - `scripts/release/render-release-notes.ps1`
- Build or benchmark checks:
  - `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag v2.0.0-M8 -CurrentRef HEAD`
  - `.\gradlew.bat build --no-daemon`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`
  - `.\gradlew.bat gatlingBenchmark --no-daemon` only if the final RC1 write set touches book search, localization lookup, or OAuth or session startup behavior
  - manual `Post-Deploy Smoke` dispatch after publication

## Execution Milestones
### Milestone 1: Audit And Align The RC1 Candidate Narrative
- goal
  - freeze the current candidate story before validation by correcting only the remaining release-phase drift and by confirming whether any other contract artifact still describes the repo as pre-RC in a way that conflicts with the selected `v2.0.0-RC1` milestone
- owned files or packages
  - `README.md`
  - `ROADMAP.md` only if the selected RC1 wording needs a narrow cleanup beyond the already-landed selection change
  - `CHANGELOG.md` under `## [Unreleased]` if the candidate needs a short release-note input before preparation
  - inspect-only unless drift is found: `src/docs/asciidoc/`, `src/test/resources/http/`, `src/test/resources/openapi/approved-openapi.json`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `CHANGELOG.md`
  - `ROADMAP.md`
  - `ai/archive/PLAN_2_0_rc1.md`
- behavior to preserve
  - no public API behavior changes
  - no OpenAPI refresh, REST Docs rewrite, or HTTP example churn unless the audit proves a real published-doc mismatch
  - `README.md` stays a concise contract summary rather than absorbing setup or release-runbook detail from `SETUP.md`
- exact deliverables
  - confirm that `README.md` and `ROADMAP.md` already present one coherent RC1 release-phase story, and apply only any remaining narrow wording cleanup if the audit finds drift
  - confirm whether any other human-facing or contract-facing artifact still carries pre-RC drift outside archived plans and historical release notes
  - revise this plan if the current RC1 candidate facts differ from the original planning snapshot in a way that would otherwise force ad hoc execution decisions
  - if the unreleased local docs and AI-guidance delta is intended to ship in RC1, record the release-relevant note in `CHANGELOG.md` input text before release preparation; if it is not intended to ship, stop and clarify instead of tagging around undocumented drift
  - leave the repo with one coherent statement: RC1 is the current selected contract-freeze milestone
- validation checkpoint
  - manual consistency review across `README.md`, `ROADMAP.md`, `src/docs/asciidoc/`, HTTP examples, and approved OpenAPI
  - `git diff --check`
- commit checkpoint
  - one commit for the release-phase alignment and any required `CHANGELOG.md` unreleased-note input

### Milestone 2: Validate The Exact RC1 Candidate
- goal
  - prove the exact candidate commit is release-ready before any tag is created
- owned files or packages
  - no source-file changes are expected
  - update `ai/archive/PLAN_2_0_rc1.md` `Validation Results` with the concrete evidence gathered during execution
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `CHANGELOG.md`
  - `ROADMAP.md`
  - `ai/archive/PLAN_2_0_rc1.md`
- behavior to preserve
  - do not mutate the candidate between validation and tagging without rerunning stale checks
  - keep the RC1 scope contract-neutral; if validation reveals a real bug, stop and fix it explicitly in a separate follow-up before resuming release prep
- exact deliverables
  - run `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag v2.0.0-M8 -CurrentRef HEAD` and record the result
  - inspect migration files plus metadata sidecars only if the impact is not `none`
  - confirm the latest default-branch CodeQL run is healthy or intentionally reviewed
  - run `.\gradlew.bat build --no-daemon` on the exact candidate
  - run `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`
  - run `.\gradlew.bat gatlingBenchmark --no-daemon` only if milestone 1 or any blocker fix changed a surface that `ai/TESTING.md` marks as benchmark-sensitive
- validation checkpoint
  - migration impact recorded
  - build passes
  - external smoke passes
  - benchmark result recorded if it became required
  - CodeQL posture explicitly confirmed
- commit checkpoint
  - no standalone commit is expected if the candidate stays unchanged; carry the recorded validation evidence forward into the release-preparation commit in Milestone 3

### Milestone 3: Prepare And Tag `v2.0.0-RC1`
- goal
  - convert the validated candidate into the RC1 release commit and annotated tag on `main`
- owned files or packages
  - `CHANGELOG.md`
  - `ROADMAP.md`
  - `ai/archive/PLAN_2_0_rc1.md`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - all files in this milestone stay coordinator-owned because they define the canonical release state
- behavior to preserve
  - changelog ordering remains compatible with `scripts/release/render-release-notes.ps1`
  - `ROADMAP.md` keeps only active work after the RC1 item is complete
  - no hidden contract edits are introduced while preparing release metadata
- exact deliverables
  - move the RC1-relevant `CHANGELOG.md` text from `## [Unreleased]` into a dated `## [v2.0.0-RC1] - YYYY-MM-DD` section and leave a fresh `## [Unreleased]` at the top
  - update `ROADMAP.md` to remove the completed RC1 item while leaving the stable `v2.0.0` item active
  - update the plan lifecycle to `Phase | Closed` and `Status | Released`, complete `Validation Results`, then archive the plan under `ai/archive/`
  - locally preview cumulative release notes with `scripts/release/render-release-notes.ps1` using the same non-prerelease published-release boundary that the GitHub workflow will use
  - re-run `.\gradlew.bat build --no-daemon` only if milestone 1 changed build-relevant or docs-generation artifacts and made the earlier build evidence stale
  - commit with `Prepare v2.0.0-RC1 release`
  - create annotated tag `v2.0.0-RC1` with annotation `Release v2.0.0-RC1`
  - verify locally that `git branch --show-current`, `git status --short`, `git log --first-parent --decorate --oneline -n 5`, `CHANGELOG.md`, `ROADMAP.md`, and the archived plan all reflect the RC1 release correctly
- validation checkpoint
  - release-note preview succeeds against the resolved previous published stable-release boundary
  - local release-state checks reflect the intended commit and tag
  - any stale build evidence is refreshed if milestone 1 touched build-relevant inputs
- commit checkpoint
  - one release-preparation commit plus the annotated `v2.0.0-RC1` tag

### Milestone 4: Publish And Verify The RC1 Release
- goal
  - publish the prepared RC1 tag and verify the full remote release path
- owned files or packages
  - no local file changes are expected
- shared files that a `Shared Plan` worker must leave to the coordinator
  - publication remains coordinator-owned because it depends on the exact release commit and tag
- behavior to preserve
  - push `main` before pushing the tag
  - publish only the exact commit already validated and tagged locally
  - do not rewrite history or retag in place if verification fails; stop and diagnose the failure explicitly
- exact deliverables
  - push `main`
  - push the annotated `v2.0.0-RC1` tag
  - monitor the tag-driven `Release` workflow until the tagged image build and external smoke validation succeed
  - verify the GitHub Release exists and contains the cumulative notes plus the semantic and short-SHA image references
  - verify GHCR published both `ghcr.io/<owner>/<repo>:v2.0.0-RC1` and `ghcr.io/<owner>/<repo>:sha-<12-char-commit>`
  - verify the immutable digest is signed and has provenance attestation
  - run the manual `Post-Deploy Smoke` workflow with `expected_build_version=v2.0.0-RC1`, the tagged short commit id, `expected_active_profile=prod`, `expected_session_store_type=jdbc`, and `expected_session_timeout=15m`
  - when the JDBC secret set is available, confirm the same smoke run proves `GET /api/session`, readable `XSRF-TOKEN` bootstrap, authenticated `PUT /api/account/language`, and persisted authenticated account access
- validation checkpoint
  - remote push accepted
  - `Release` workflow passed
  - GitHub Release body and GHCR tags are correct
  - signature and provenance are visible for the immutable digest
  - manual post-deploy smoke completed with the expected release identity
- commit checkpoint
  - none expected after publication; if remote verification uncovers a defect, stop and handle it as follow-up work instead of mutating the published RC1 in place

## Edge Cases And Failure Modes
- The current local `main` already includes unreleased docs changes. Tagging RC1 without explicitly including or superseding that state would publish undocumented drift.
- The release-note workflow resolves the previous published GitHub Release from non-prerelease releases only. If `CHANGELOG.md` ordering or section headings do not match that expectation, RC1 publication will fail closed during release-note rendering.
- `README.md` must not still advertise `v2.0.0-M8` as the next prerelease once RC1 is prepared.
- The current `v2.0.0-M8..HEAD` migration impact is `none`, but any newly introduced migration SQL file or metadata change before tagging reopens the restore-drill decision and requires rerunning the helper.
- If milestone 1 touches `src/docs/asciidoc/` or any other build-relevant artifact instead of only `README.md` or `ROADMAP.md`, the earlier build evidence becomes stale and must be rerun on the final candidate.
- `gatlingBenchmark` should stay skipped only while the write set remains contract-freeze metadata and docs. If a last-minute fix touches search, localization lookup, or OAuth or session startup behavior, the benchmark becomes part of the RC1 gate.
- Because RC1 is a contract-freeze milestone, any discovered need for controller, service, REST Docs, OpenAPI, or HTTP example behavior changes should be treated as a release blocker, not folded casually into the tag-preparation commit.
- Remote verification must use the immutable digest rather than trusting mutable GHCR tags alone.

## Validation Plan
- commands to run
  - `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag v2.0.0-M8 -CurrentRef HEAD`
  - `.\gradlew.bat build --no-daemon`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`
  - `.\gradlew.bat gatlingBenchmark --no-daemon` only if the final write set triggers the benchmark rule
  - local release-note preview with `pwsh ./scripts/release/render-release-notes.ps1 ...` after resolving the previous published stable-release boundary that the GitHub workflow will use
  - local release-state checks:
    - `git branch --show-current`
    - `git status --short`
    - `git log --first-parent --decorate --oneline -n 5`
- tests to add or update
  - none expected; this plan should stay in docs, metadata, validation, and release operations
  - if a blocker forces product changes, stop and create explicit RC1-blocker work instead of quietly extending this plan
- docs or contract checks
  - review `README.md`, `src/docs/asciidoc/`, HTTP examples, and approved OpenAPI for stale pre-RC wording or undocumented drift
  - keep `CHANGELOG.md`, `ROADMAP.md`, and the plan archive aligned with the final RC1 state
  - confirm the cumulative release-note preview matches the intended `CHANGELOG.md` section ordering before pushing the tag
- manual verification steps
  - confirm `README.md` describes RC1 as the current contract-freeze phase rather than treating `v2.0.0-M8` as upcoming
  - confirm `ROADMAP.md` keeps only the stable `v2.0.0` release item active after RC1 is prepared
  - inspect the GitHub Release, GHCR package page, digest signature, and provenance after publication
  - review the manual `Post-Deploy Smoke` run summary for the expected release identity and JDBC-backed session assertions when those secrets are configured

## Better Engineering Notes
- Keep RC1 narrow. This should be a release-readiness and publication pass, not a disguised final cleanup sprint.
- Prefer small wording alignment in `README.md` over broad documentation churn. If more than narrow phase cleanup is needed, that is evidence of an RC1 blocker worth handling separately.
- Preserve the exact-candidate discipline: if any file changes after local validation, rerun the stale checks before tagging.
- Let the existing release automation do the heavy lifting for image publication, signature, provenance, and GitHub Release creation. The plan should verify that automation, not work around it.
- Stable `v2.0.0` remains a separate release step after RC1 is accepted. Do not collapse the stable-release cleanup into this plan.

## Validation Results
- 2026-05-05: Milestone 1 current-candidate audit completed.
  - Confirmed that `README.md` now delegates release-phase status to `ROADMAP.md`, and `ROADMAP.md` `## Current Project State` now carries the RC1 prerelease state, breaking-change policy, and next target version.
  - Confirmed no remaining active human-facing or contract-facing artifact still describes `v2.0.0-M8` as upcoming; the remaining pre-RC references are limited to archived plans and historical changelog sections.
  - Revised this plan to match the actual RC1 candidate state after the post-`v2.0.0-M8` documentation and AI-guidance commits landed on `main`.
  - Added `CHANGELOG.md` `## [Unreleased]` input covering the post-`v2.0.0-M8` docs and AI-guidance changes intended to ship in RC1.
- 2026-05-05: Milestone 2 exact-candidate validation completed.
  - Passed: `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag v2.0.0-M8 -CurrentRef HEAD`
  - Result: `none`
  - Confirmed `gh auth status` succeeded for `kamkie` with `repo` scope before remote publication.
  - Confirmed the latest default-branch CodeQL run on `main` succeeded for `Prepare v2.0.0-M8 release` at commit `0c3ddec67023aad11e13670743994831348adfcd`.
  - Passed: `. .\scripts\load-dotenv.ps1 -Quiet; .\gradlew.bat build externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`
  - The local image reported build version `v2.0.0-M8-11-g85e2356` because no RC1 tag existed yet during exact-candidate validation.
  - `.\gradlew.bat gatlingBenchmark --no-daemon` stayed skipped because the RC1 write set remained documentation and AI-guidance only and did not touch book search, localization lookup, or OAuth or session startup behavior.
- 2026-05-05: Milestone 3 release preparation completed.
  - Moved the RC1 notes from `## [Unreleased]` into `## [v2.0.0-RC1] - 2026-05-05` and reopened a fresh `## [Unreleased]` section.
  - Updated `ROADMAP.md` so the completed RC1 item is gone, the stable `v2.0.0` release remains active, and `## Current Project State` now points to the post-RC1 prerelease state with breaking changes disallowed and `v2.0.0` as the next target version.
  - Updated `ai/DESIGN.md` so the release-phase direction now describes the RC line as the active prerelease phase and stable `v2.0.0` as the next target.
  - Passed: `pwsh ./scripts/release/render-release-notes.ps1 -ChangelogPath CHANGELOG.md -CurrentTag v2.0.0-RC1 -PreviousPublishedTag v1.6.0 -TagImageReference ghcr.io/kamkie/technical-interview-demo:v2.0.0-RC1 -ShaImageReference ghcr.io/kamkie/technical-interview-demo:sha-preview -PackagePageUrl https://github.com/kamkie/technical-interview-demo/pkgs/container/technical-interview-demo -OutputPath build/tmp/release-notes-preview.md`
  - The release-note preview succeeded against the previous published stable-release boundary `v1.6.0`.
  - Did not rerun `.\gradlew.bat build --no-daemon` after release-preparation edits because the final RC1 write set stayed limited to docs and AI-guidance files, so the earlier exact-candidate build and external smoke evidence remained current.
- 2026-05-05: Archived this executed plan under `ai/archive/PLAN_2_0_rc1.md` as part of the RC1 release-preparation change.

## User Validation
- Review `README.md` and confirm `## Current Release Phase` still delegates current release status to `ROADMAP.md`.
- Review the final `CHANGELOG.md` and confirm `v2.0.0-RC1` has its own dated section while `## [Unreleased]` remains at the top.
- Review `ROADMAP.md` and confirm the RC1 item is gone while the stable `v2.0.0` release remains the only active `2.0` completion item and `## Current Project State` now targets stable `v2.0.0`.
- After publication, verify that tag `v2.0.0-RC1`, the GitHub Release, the GHCR semantic and short-SHA image tags, and the manual post-deploy smoke record all refer to the same commit and release identity.
