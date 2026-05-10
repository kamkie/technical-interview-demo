# Plan: Frontend AI Contract

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | D7 |
| Ready For Execution | Yes |
| Last Updated | 2026-05-09 |

## Summary
- Create a generated import-ready frontend AI contract document under `docs/` that gives agents in a separate first-party UI repository the backend contract, API, OpenAPI source, and integration constraints they must preserve.
- Reference external OWASP, OpenAI security, and Anthropic frontend design skills by URL only; do not inline or synthesize skill bodies or copied recommendation blocks into the contract.
- Keep this plan scoped to the backend repository source artifact and its local documentation indexes; keep destination-repository importing as a separate task instead of editing an unknown external repository now.
- Success is measured by a discoverable source file in this repository, aligned repository AI/documentation indexes, updated active-work tracking, and validation evidence showing no backend contract artifacts changed accidentally.

## Scope
- In scope:
  - create `docs/FRONTEND_AI_CONTRACT.md` in this repository as the generated import-ready instruction artifact
  - encode the supported `/api/**`, same-site browser, session, CSRF, authentication, authorization, error, localization, and deployment assumptions that frontend AI must respect
  - list the external OWASP security, OpenAI security best practices, and Anthropic frontend design skill URLs without copying their bodies or recommendation blocks
  - keep the generated document limited to instructions useful to an AI agent importing it in a frontend repository
  - register the new source artifact in the owning AI and human-facing documentation indexes
  - remove live non-exempt mentions of `upgrade-1x-to-2-0.adoc` from setup and generated REST Docs source pages
  - keep `ROADMAP.md` aligned with this plan's ready state and deferred external-copy decision
- Out of scope:
  - changing backend runtime behavior, public API shape, OpenAPI, REST Docs snippets, database schema, or application code
  - creating a frontend implementation, UI components, generated assets, or a frontend design system
  - copying or adapting the file into an external frontend repository during this plan
  - changing this repository's setup, release process, branch workflow, or validation policy
  - vendoring, inlining, or summarizing external skill bodies or copied recommendation blocks

## Current State
- `ROADMAP.md` selected `Frontend AI contract` as the next workstream and now points to this ready source-contract plan.
- The frontend repository destination and target AI-instruction path are not identified in this backend repository, so the external copy has been deferred by accepted fallback D7.
- `docs/DESIGN.md` states this backend remains a compact technical interview demo, not a frontend-heavy product, and any first-party UI stays in a separate repository.
- `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/index.adoc`, and `src/test/resources/openapi/approved-openapi.json` publish the current same-site browser contract for the separate UI.
- The frontend contract must preserve the current `2.x` stable-line policy: no breaking public behavior unless a later major-version plan explicitly changes that policy.
- External security and design guidance is linked by URL only from the frontend contract, so the current skill source should be loaded by the destination frontend agent when needed.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Which frontend repository path, branch, or remote should receive the copied instruction file? | The original roadmap intent included copying into a frontend repository, but this backend repository does not identify the destination. | User/Future Agent | Deferred | D7 narrows this plan to the backend-owned source artifact. Copying into a frontend repository becomes a separate task after the destination is known. | No |
| Q2 | What filename and AI-instruction location should be used inside the frontend repository? | The destination may already have `AGENTS.md`, `.agents/`, Claude skills, or another AI instruction convention that should be extended rather than overwritten. | User/Future Agent | Deferred | D7 defers destination placement. A future task must inspect the destination repo and preserve its local AI-instruction convention before editing. | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Plan `.agents/plans/PLAN_frontend_ai_contract.md` owns execution detail for the selected roadmap item. | `ROADMAP.md` selected row and planning guide | 2026-05-09 | If roadmap priority changes |
| D2 | Use `docs/FRONTEND_AI_CONTRACT.md` as the source artifact. It is a copyable cross-repository source, not only a Codex-private reference. | User follow-up request and documentation ownership | 2026-05-09 | If the user wants the file under `.agents/`, a plugin, or another frontend handoff location |
| D3 | Avoid vendoring complete external skill text in the frontend contract; current skill-handling policy is superseded by D9's URL-only references. | External source review and copyright/license caution | 2026-05-09 | If the user explicitly wants a licensed vendored skill and license review supports that |
| D4 | The frontend AI contract must defer to backend executable specs, REST Docs, OpenAPI, and README when those artifacts define API behavior. | `AGENTS.md` spec priority and published contract docs | 2026-05-09 | If public API ownership changes |
| D5 | The instruction file should emphasize same-origin browser usage, `GET /api/session` bootstrap, provider `authorizationPath`, session-cookie auth, CSRF cookie/header mirroring, and no promised CORS/JWT/bearer-token contract. | `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/index.adoc`, approved OpenAPI | 2026-05-09 | If the backend session/auth contract changes |
| D6 | This is documentation and AI-guidance work; no backend code, OpenAPI baseline, REST Docs snippets, or benchmark fixtures should change. | Documentation and testing guides | 2026-05-09 | If implementation discovers a real contract mismatch |
| D7 | Replan the work as a backend-repository source contract plus local discoverability updates. Defer copying into a separate frontend repository until a concrete destination and instruction convention are provided. | User replan request and planning readiness review | 2026-05-09 | If the user provides the frontend repository and asks to include the external copy in this plan before execution starts |
| D8 | Move the source artifact from repository root to `docs/FRONTEND_AI_CONTRACT.md` and update ownership/discoverability references to the docs path. | User follow-up request | 2026-05-09 | If the repository later promotes this guidance to a plugin, skill, or frontend-repo-local artifact |
| D9 | Supersede inline security/design guidance in the frontend contract with URL-only references to the OWASP security, OpenAI security best practices, and Anthropic frontend design skill sources. | User follow-up request | 2026-05-09 | If the user later requests a licensed vendored skill copy or destination-specific frontend instructions |
| D10 | Mark the frontend contract as a generated import-ready document and remove content meant only for maintaining or copying it, including the migration-guide pointer. | User follow-up request | 2026-05-09 | If a real generator script or destination repository convention is introduced |

## Execution Shape And Shared Files
- Recommended shape: one local branch in this repository.
- This work is small and documentation-heavy, so delegation is not useful.
- No external repository is edited by this plan. A future task can use `docs/FRONTEND_AI_CONTRACT.md` as the source and adapt it to the destination repo's AI-instruction convention.
- Coordinator-owned shared files if delegation later becomes necessary: `ROADMAP.md`, `.agents/plans/PLAN_frontend_ai_contract.md`, `docs/FRONTEND_AI_CONTRACT.md`, `AGENTS.md`, `README.md`, `WORKING_WITH_AI.md`, and `.agents/references/documentation.md`.

## Affected Artifacts
- Source AI contract: `docs/FRONTEND_AI_CONTRACT.md` (new)
- Human/AI indexes likely needing alignment: `README.md`, `WORKING_WITH_AI.md`, `AGENTS.md`, `.agents/references/documentation.md`
- Live documentation references needing cleanup: `SETUP.md`, `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/technical-overview-controller.adoc`
- Release history staging: `CHANGELOG.md` `## [Unreleased]`
- Roadmap tracking: `ROADMAP.md`
- Backend contract references to read, not edit unless a contradiction is found: `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/upgrade-1x-to-2-0.adoc`, `src/test/resources/openapi/approved-openapi.json`, `README.md`, `docs/DESIGN.md`
- External frontend repository files are not affected by this plan
- Not affected unless replan is required: `src/main/`, `src/test/java/`, OpenAPI baseline, REST Docs snippets, database migrations, build logic, release notes

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Draft source frontend AI contract | Done | Agent | `53ad6d0` | Passed | Source artifact drafted and manually checked against session/API contract terms |
| 2: Register source artifact in repo docs | Done | Agent | `53ad6d0` | Passed | Source artifact registered in human/AI indexes and unreleased changelog |
| 3: Validate and hand off | Done | Agent | Final tracking commit | Passed | Final validation passed; external frontend-repo copy remains deferred |
| 4: Rewrite as generated import document | Done | Agent | Final follow-up commit | Passed | Frontend contract rewritten for imported AI-agent use only; live migration-guide mentions removed from non-exempt docs |

## Execution Tasks
### Task 1: Draft source frontend AI contract
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Create the source instruction file that tells frontend AI agents how to design and build against this backend without violating the supported contract. |
| Owned Files Or Packages | `docs/FRONTEND_AI_CONTRACT.md` |
| Coordinator-Owned Shared Files | `ROADMAP.md`, `.agents/plans/PLAN_frontend_ai_contract.md` |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, `ROADMAP.md`, `docs/DESIGN.md`, `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/upgrade-1x-to-2-0.adoc`, `README.md`, `src/test/resources/openapi/approved-openapi.json`, and the external skill URLs named by D9 |
| Behavior To Preserve | No backend behavior or public contract changes; frontend guidance must defer to backend specs and published docs. |
| Deliverables | `docs/FRONTEND_AI_CONTRACT.md` with generated-document notice, required backend inputs, external skill URL references, OpenAPI source snapshot, API/session/CSRF rules, and integration anti-patterns for a frontend AI agent importing the document. |
| Validation Checkpoint | Passed: manual consistency review against the session docs, API overview, and OpenAPI session schema; `rg` confirmed required and forbidden session/auth terms are present in the source artifact. |
| Commit Checkpoint | Completed in `53ad6d0` with Task 2. |

### Task 2: Register source artifact in repo docs
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Make the new source artifact discoverable and owned by the repository's documentation map. |
| Owned Files Or Packages | `README.md`, `WORKING_WITH_AI.md`, `AGENTS.md`, `.agents/references/documentation.md`, `CHANGELOG.md` |
| Coordinator-Owned Shared Files | `docs/FRONTEND_AI_CONTRACT.md`, `ROADMAP.md`, `.agents/plans/PLAN_frontend_ai_contract.md` |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, `.agents/references/documentation.md`, `.agents/references/references-rules.md`, `README.md`, and `WORKING_WITH_AI.md` |
| Behavior To Preserve | Keep setup details out of AI docs, keep active-work detail in the plan, and avoid making the new frontend file a higher-priority source than executable specs or published backend contracts. |
| Deliverables | Done: short references in the appropriate AI/human-facing indexes, documentation ownership guidance for `docs/FRONTEND_AI_CONTRACT.md`, and an unreleased changelog entry. |
| Validation Checkpoint | Passed: `rg -n "FRONTEND_AI_CONTRACT|Frontend AI contract|frontend AI contract" README.md WORKING_WITH_AI.md AGENTS.md .agents/references/documentation.md docs/FRONTEND_AI_CONTRACT.md CHANGELOG.md`; manual check found no duplicated setup or contract policy. |
| Commit Checkpoint | Completed in `53ad6d0`. |

### Task 3: Validate and hand off
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Prove the completed docs/AI-guidance work is internally consistent and did not alter backend behavior. |
| Owned Files Or Packages | Validation records in this plan, `ROADMAP.md` |
| Coordinator-Owned Shared Files | `.agents/plans/PLAN_frontend_ai_contract.md` |
| Context Required | `AGENTS.md`, `.agents/references/testing.md`, `.agents/references/execution.md`, and this plan |
| Behavior To Preserve | No backend code or contract drift; roadmap and plan lifecycle stay aligned. |
| Deliverables | Done: updated `Validation Results`, final roadmap status note, and handoff notes naming the deferred external-repo copy. |
| Validation Checkpoint | Passed: `git diff --check`, targeted reference search, implementation-scope diff review, and `./build.ps1 -FullBuild build`. |
| Commit Checkpoint | Final tracking commit records this plan/roadmap completion state. |

### Task 4: Rewrite as generated import document
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Make `docs/FRONTEND_AI_CONTRACT.md` useful only as an imported instruction document for frontend AI agents, and remove live non-exempt migration-guide mentions. |
| Owned Files Or Packages | `docs/FRONTEND_AI_CONTRACT.md`, `README.md`, `WORKING_WITH_AI.md`, `AGENTS.md`, `.agents/references/documentation.md`, `CHANGELOG.md`, `SETUP.md`, `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/technical-overview-controller.adoc` |
| Coordinator-Owned Shared Files | `ROADMAP.md`, `.agents/plans/PLAN_frontend_ai_contract.md` |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/documentation.md`, `.agents/references/references-rules.md`, `.agents/references/testing.md`, this plan, and the user follow-up request |
| Behavior To Preserve | No backend runtime behavior or approved OpenAPI change; frontend contract remains subordinate to executable specs, REST Docs, approved OpenAPI, and README. |
| Deliverables | Generated-document notice, import-only frontend AI contract wording, removal of the migration-guide pointer from the frontend contract, and removal of live non-exempt migration-guide references. |
| Validation Checkpoint | Passed: `git diff --check`, targeted reference searches, approved OpenAPI snapshot assertion, and `./build.ps1 build`. |
| Commit Checkpoint | Final follow-up commit records this generated import-document rewrite. |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| User provides a concrete frontend repository and asks to include the copy before execution starts | Replan to add an external-repo task, inspect the destination AI-instruction convention, and restore destination validation. | User/Agent | Open |
| External skill URLs change, disappear, or need destination-specific pinning | Revise the frontend contract's URL references and any destination-repository copy instructions. | Agent/User | Open |
| Backend published contract docs conflict with each other | Stop and resolve the contract source using `AGENTS.md` spec priority before writing frontend guidance. | Agent | Open |
| Implementation discovers the new file belongs under `.agents/` instead of repository root | Replan D2 and update affected documentation ownership before moving the file. | Agent/User | Open |
| The future frontend copy becomes urgent before this source artifact is validated | Finish or pause this plan explicitly, then create a separate destination-repo task with Q1/Q2 answered. | User/Agent | Open |

## Edge Cases And Failure Modes
- The generated instruction must not tell frontend agents to rely on CORS, JWTs, bearer tokens, `/login`, `/oauth2/authorization/github`, or provider callback paths outside the documented `/api/session/**` contract.
- The file should distinguish current cookie/header names from the safer integration rule: bootstrap from `GET /api/session` and use published CSRF metadata for unsafe writes.
- The frontend contract must keep security and design skill guidance as URL-only references instead of copying, summarizing, or adapting the external skill bodies.
- The frontend contract must not include copy-maintenance instructions, source-document narration, or other content that is not useful to the AI agent importing the document.
- Because this plan does not edit a frontend repository, it must include enough future-copy guidance for a later agent without pretending that destination-specific conventions have been inspected.

## Validation Plan
- Run `git diff --check` after documentation/AI-guidance edits.
- Run `./build.ps1 build` after local documentation/AI-guidance edits; this should classify as lightweight support-file work unless an unexpected non-lightweight file changes.
- Run targeted text checks for the new artifact references:
  - `rg -n "FRONTEND_AI_CONTRACT|Frontend AI contract|frontend AI contract" README.md WORKING_WITH_AI.md AGENTS.md .agents/references/documentation.md docs/FRONTEND_AI_CONTRACT.md`
- Run targeted text checks for removed migration-guide references outside exempt plan, roadmap, changelog, and documentation-owner files.
- Manually review `docs/FRONTEND_AI_CONTRACT.md` against `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/index.adoc`, and the approved OpenAPI session schema.
- Confirm `git diff --name-only` shows no backend source, generated contract, database migration, build-logic, or release-history changes unless the plan is revised.

## Testing Strategy
- Unit tests: not applicable unless implementation unexpectedly adds parsing or generation code.
- Integration tests: not applicable because no backend runtime behavior should change.
- Contract tests: no OpenAPI or REST Docs generation expected; run only if implementation changes contract artifacts, which should trigger replanning.
- Smoke/benchmark tests: not applicable for docs/AI-guidance-only work.
- Negative scenarios: manual review should check for forbidden frontend assumptions: CORS support, bearer-token auth, hard-coded provider paths, bypassing `GET /api/session`, and ignoring CSRF metadata.
- Import-only review should check that `docs/FRONTEND_AI_CONTRACT.md` no longer includes source-copy procedure, migration-guide pointers, or maintenance-only instructions.

## Better Engineering Notes
- Prefer a concise, explicit source instruction file over a broad prompt dump; frontend agents need contract-critical constraints and current external skill links, not copied prompt bodies.
- The frontend contract is now a generated import document; keep repository-maintenance notes in the plan, documentation ownership guide, roadmap, or changelog instead of the imported file.
- Keep roadmap detail small; this plan owns tasks and validation history.
- The deferred external copy is intentional. When the frontend repository is known, use this source file as input to a separate destination-aware task instead of expanding this plan retroactively after local validation is complete.
- If this source file proves useful beyond one frontend repo, consider a later small task to turn it into a reusable skill or task prompt rather than expanding this plan.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-05-09 | `git diff --check` | Original planning and roadmap whitespace check | Passed | No whitespace errors reported before this replan |
| 2026-05-09 | `./build.ps1 build` | Original local docs/AI-guidance validation | Passed | Lightweight-only path; Gradle build skipped because only `.agents/plans/PLAN_frontend_ai_contract.md` and `ROADMAP.md` changed |
| 2026-05-09 | `git diff --check` | Replan whitespace check | Passed | No whitespace errors reported |
| 2026-05-09 | `./build.ps1 build` | Replan validation | Passed | Lightweight-only path; Gradle build skipped because only `.agents/plans/PLAN_frontend_ai_contract.md` and `ROADMAP.md` changed |
| 2026-05-09 | `rg -n "CORS|JWT|bearer|/login|/oauth2/authorization/github|GET /api/session|authorizationPath|XSRF-TOKEN|X-XSRF-TOKEN|messageKey|FRONTEND_AI_CONTRACT" FRONTEND_AI_CONTRACT.md` | Task 1 contract-term check | Passed | Source artifact includes required session/CSRF/error guidance and names forbidden frontend assumptions; path later moved by D8 |
| 2026-05-09 | `rg -n "FRONTEND_AI_CONTRACT|Frontend AI contract|frontend AI contract" README.md WORKING_WITH_AI.md AGENTS.md .agents/references/documentation.md FRONTEND_AI_CONTRACT.md CHANGELOG.md` | Task 2 reference check | Passed | Source artifact is discoverable from human-facing and AI-facing indexes plus unreleased changelog; path later moved by D8 |
| 2026-05-09 | `git diff --check` | Task 1 and 2 whitespace check | Passed | No whitespace errors reported |
| 2026-05-09 | `./build.ps1 build` | Task 1 and 2 validation | Passed | Lightweight-only path; Gradle build skipped because only support/documentation files changed |
| 2026-05-09 | `git diff --check` | Final whitespace check | Passed | No whitespace errors reported |
| 2026-05-09 | `rg -n "FRONTEND_AI_CONTRACT|Frontend AI contract|frontend AI contract" README.md WORKING_WITH_AI.md AGENTS.md .agents/references/documentation.md FRONTEND_AI_CONTRACT.md CHANGELOG.md` | Final reference check | Passed | Source artifact remained discoverable from required indexes before the D8 path move |
| 2026-05-09 | `git diff --name-only HEAD~1..HEAD; git diff --name-only` | Final scope review | Passed | Implementation commit touched only planned docs/support files; remaining uncommitted diff was plan tracking only |
| 2026-05-09 | `./build.ps1 -FullBuild build` | Whole-plan final validation | Passed | Full Gradle build completed successfully; 270 tests passed |
| 2026-05-09 | `git diff --check` | Final tracking whitespace check | Passed | No whitespace errors reported after plan and roadmap completion updates |
| 2026-05-09 | `./build.ps1 build` | Final tracking validation | Passed | Lightweight-only path; Gradle build skipped because only `.agents/plans/PLAN_frontend_ai_contract.md` and `ROADMAP.md` changed |
| 2026-05-09 | `git diff --check` | D8 path relocation whitespace check | Passed | No whitespace errors reported |
| 2026-05-09 | `rg --hidden -n "docs/FRONTEND_AI_CONTRACT\.md\|FRONTEND_AI_CONTRACT\.md" README.md WORKING_WITH_AI.md AGENTS.md .agents/references/documentation.md CHANGELOG.md ROADMAP.md .agents/plans/PLAN_frontend_ai_contract.md docs/FRONTEND_AI_CONTRACT.md` | D8 path relocation reference check | Passed | Live ownership and index references point to `docs/FRONTEND_AI_CONTRACT.md`; older root-path mentions remain only as historical validation evidence with D8 notes |
| 2026-05-09 | `rg --hidden --pcre2 -n "(?<!docs/)FRONTEND_AI_CONTRACT\.md" README.md WORKING_WITH_AI.md AGENTS.md .agents/references/documentation.md CHANGELOG.md ROADMAP.md docs/FRONTEND_AI_CONTRACT.md` | D8 stale live-reference check | Passed | No stale live references to root `FRONTEND_AI_CONTRACT.md` found; command returned no matches |
| 2026-05-09 | `./build.ps1 build` | D8 path relocation validation | Passed | Lightweight-only path; Gradle build skipped because only support/documentation files changed |
| 2026-05-09 | `git diff --check` | D9 URL-only skill reference whitespace check | Passed | No whitespace errors reported |
| 2026-05-09 | `rg -n "External Skill References\|claude-code-owasp\|openai/skills\|anthropics/skills\|Security Defaults\|UI And Design\|Never put private API keys\|Avoid DOM XSS\|purple-gradient" docs/FRONTEND_AI_CONTRACT.md` | D9 URL-only skill reference content check | Passed | Only the external skill section and three URL references matched; removed inlined security and UI guidance did not match |
| 2026-05-09 | `PowerShell approved-openapi summary assertion` | D9 approved OpenAPI snapshot check | Failed then passed | Initial command used invalid property access for JSON key `in`; corrected explicit property access confirmed the contract summary matches `approved-openapi.json` |
| 2026-05-09 | `./build.ps1 build` | D9 URL-only skill reference validation | Passed | Lightweight-only path; Gradle build skipped because only support/documentation files changed |
| 2026-05-09 | `git diff --check` | D10 generated import-document whitespace check | Passed | No whitespace errors reported |
| 2026-05-09 | `rg` targeted generated notice, skill URL, removed-section, and migration-guide checks | D10 generated import-document content check | Passed | Generated notice and three skill URLs are present; removed inlined sections and frontend-contract migration pointer are absent; no non-exempt `upgrade-1x-to-2-0.adoc` references remain |
| 2026-05-09 | `PowerShell approved-openapi summary assertion` | D10 approved OpenAPI snapshot check | Passed | Confirmed the generated contract snapshot still matches `approved-openapi.json` |
| 2026-05-09 | `./build.ps1 build` | D10 generated import-document validation | Passed | Full Gradle build completed successfully; 270 tests passed |

## User Validation
- Review `docs/FRONTEND_AI_CONTRACT.md` and confirm it reflects how a separate frontend should consume this backend.
- Confirm later, in a separate task, the destination frontend repository path and target AI-instruction filename/location.
- After any future frontend-repo copy, review the copied instruction file and verify it preserves this backend source contract without overriding unrelated frontend repo rules.

## Required Content Checklist
- Behavior changing and why: create a frontend AI contract source file so frontend agents preserve this backend's public contract and design intent.
- Decision-complete status: ready; Q1 and Q2 are deferred by D7 and do not block this backend source-contract plan.
- Roadmap entry: `ROADMAP.md` `Frontend AI contract` tracks this plan.
- Out of scope: backend runtime/API changes, frontend implementation, generated UI assets, setup/release process changes, and external frontend-repo edits.
- Governing specs/contracts: `AGENTS.md` spec priority, `ROADMAP.md`, REST Docs, approved OpenAPI, README, and design guide.
- Likely files: `docs/FRONTEND_AI_CONTRACT.md`, `README.md`, `WORKING_WITH_AI.md`, `AGENTS.md`, `.agents/references/documentation.md`, `CHANGELOG.md`, and `ROADMAP.md`.
- Compatibility promises: stable `2.x`, same-site first-party UI, session-cookie auth, CSRF metadata, no CORS/JWT/bearer-token promise.
- Edge cases and risks: destination repo unknown, external skill URLs may change, accidental contract drift, excessive copying of external source text.
- Requirement gaps: Q1 and Q2 are explicit, deferred, and non-blocking.
- Locked decisions: D1-D10.
- Execution shape: one local branch in this repository; external copy is a future destination-aware task.
- Shared files: named in execution shape and tasks.
- Progress tracking: top-level tracker included.
- Replan triggers: blocker table included.
- Context per task: exact read sets named.
- Artifact movement: docs/AI guidance only; no backend contract artifact changes expected.
- Validation: diff check, wrapper build, text checks, manual contract review, no backend contract/source diff confirmation.
- Validation ledger: included.
- User verification: source instruction review and separate future destination confirmation.
