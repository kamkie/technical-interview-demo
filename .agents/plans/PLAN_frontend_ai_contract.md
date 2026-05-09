# Plan: Frontend AI Contract

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Needs Input |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | No |
| Blocking Open Questions | Q1, Q2 |
| Accepted Fallbacks | None |
| Ready For Execution | No |
| Last Updated | 2026-05-09 |

## Summary
- Create a repository-owned frontend AI contract document that gives agents in the separate first-party UI repository the backend contract, security, API, and design constraints they must preserve.
- Use Anthropic's `frontend-design` skill as source guidance for design quality, but synthesize it into this project's API and demo-product context instead of copying the skill verbatim.
- Copy or adapt the resulting instruction file into the frontend repository once the destination repository and target AI-instruction path are confirmed.
- Success is measured by a discoverable source file in this repository, aligned repository AI/documentation indexes, a destination copy in the frontend repository, and validation evidence showing no backend contract artifacts changed accidentally.

## Scope
- In scope:
  - create `FRONTEND_AI_CONTRACT.md` in this repository as the source instruction artifact
  - encode the supported `/api/**`, same-site browser, session, CSRF, authentication, authorization, error, localization, and deployment assumptions that frontend AI must respect
  - adapt the useful parts of Anthropic's frontend-design guidance to this app's operational, interview-demo UI context
  - register the new source artifact in the owning AI and human-facing documentation indexes
  - copy or adapt the file into the frontend repository after Q1 and Q2 are answered
- Out of scope:
  - changing backend runtime behavior, public API shape, OpenAPI, REST Docs snippets, database schema, or application code
  - creating a frontend implementation, UI components, generated assets, or a frontend design system
  - changing this repository's setup, release process, branch workflow, or validation policy
  - vendoring the complete Anthropic skill text

## Current State
- `ROADMAP.md` has selected `Frontend AI contract` as the next workstream and says it should generate a frontend-contract AI instruction file in this repository using Anthropic's `frontend-design` skill as source guidance, then copy it into the frontend repository.
- Before this file was created, there were no active `.agents/plans/PLAN_*.md` files; this is now the concrete active plan for that selected roadmap item.
- `docs/DESIGN.md` states this backend remains a compact technical interview demo, not a frontend-heavy product, and any first-party UI stays in a separate repository.
- `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/index.adoc`, and `src/test/resources/openapi/approved-openapi.json` publish the current same-site browser contract for the separate UI.
- The frontend contract must preserve the current 2.x stable-line policy: no breaking public behavior unless a later major-version plan explicitly changes that policy.
- The external source guidance is Anthropic's `frontend-design` skill in the official Claude Code plugin path, plus the current Anthropic plugin page. Implementation must re-check the current source before drafting because plugin content can change.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Which frontend repository path, branch, or remote should receive the copied instruction file? | The roadmap explicitly requires copying into the frontend repository, but this backend repository does not identify the destination. | User | Open | Pending user-provided repository path and branch/workflow. | Yes |
| Q2 | What filename and AI-instruction location should be used inside the frontend repository? | The destination may already have `AGENTS.md`, `.agents/`, Claude skills, or another AI instruction convention that should be extended rather than overwritten. | User/Agent after Q1 | Open | Inspect the destination repo after Q1; if no convention exists, propose `FRONTEND_AI_CONTRACT.md` or the repo's local equivalent before editing. | Yes |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Plan `.agents/plans/PLAN_frontend_ai_contract.md` owns execution detail for the selected roadmap item. | `ROADMAP.md` selected row and planning guide | 2026-05-09 | If roadmap priority changes |
| D2 | Use `FRONTEND_AI_CONTRACT.md` at this repository root as the source artifact. | Agent decision from documentation ownership: it is a copyable cross-repository source, not only a Codex-private reference | 2026-05-09 | If the user wants the file under `.agents/` or another frontend handoff location |
| D3 | Synthesize design guidance from Anthropic's `frontend-design` skill instead of vendoring the skill text. | External source review and copyright/license caution | 2026-05-09 | If the user explicitly wants a licensed vendored skill and license review supports that |
| D4 | The frontend AI contract must defer to backend executable specs, REST Docs, OpenAPI, and README when those artifacts define API behavior. | `AGENTS.md` spec priority and published contract docs | 2026-05-09 | If public API ownership changes |
| D5 | The instruction file should emphasize same-origin browser usage, `GET /api/session` bootstrap, provider `authorizationPath`, session-cookie auth, CSRF cookie/header mirroring, and no promised CORS/JWT/bearer-token contract. | `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/index.adoc`, approved OpenAPI | 2026-05-09 | If the backend session/auth contract changes |
| D6 | This is documentation and AI-guidance work; no backend code, OpenAPI baseline, REST Docs snippets, or benchmark fixtures should change. | Documentation and testing guides | 2026-05-09 | If implementation discovers a real contract mismatch |

## Execution Shape And Shared Files
- Recommended shape: one local branch in this repository, plus a separate destination-repo edit only after Q1 and Q2 are resolved.
- This work is small and documentation-heavy, so delegation is not useful unless the destination frontend repository is large enough that its AI-instruction conventions need separate inspection.
- Coordinator-owned shared files if delegation later becomes necessary: `ROADMAP.md`, `.agents/plans/PLAN_frontend_ai_contract.md`, `FRONTEND_AI_CONTRACT.md`, `AGENTS.md`, `README.md`, `WORKING_WITH_AI.md`, and `.agents/references/documentation.md`.
- Candidate worker boundary if later delegation becomes necessary: one worker may inspect the destination frontend repository after Q1 while the coordinator keeps this repository's source artifact and roadmap alignment.

## Affected Artifacts
- Source AI contract: `FRONTEND_AI_CONTRACT.md` (new)
- Human/AI indexes likely needing alignment: `README.md`, `WORKING_WITH_AI.md`, `AGENTS.md`, `.agents/references/documentation.md`
- Roadmap tracking: `ROADMAP.md`
- External destination: target frontend repository file from Q1 and Q2
- Backend contract references to read, not edit unless a contradiction is found: `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/upgrade-1x-to-2-0.adoc`, `src/test/resources/openapi/approved-openapi.json`, `README.md`, `docs/DESIGN.md`
- Not affected unless replan is required: `src/main/`, `src/test/java/`, OpenAPI baseline, REST Docs snippets, database migrations, build logic, release notes

## Progress Tracker
| Milestone | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Draft source frontend AI contract | Not Started | Agent | Pending | Pending | Blocked by execution approval, not by Q1/Q2 |
| 2: Register source artifact in repo docs | Not Started | Agent | Pending | Pending | Requires `.agents/references/references-rules.md` before editing `.agents/references/documentation.md` |
| 3: Copy/adapt into frontend repository | Blocked | Agent/User | Pending | Pending | Requires Q1 and Q2 |
| 4: Validate and hand off | Not Started | Agent | Pending | Pending | Validation scope depends on whether external repo is edited |

## Execution Milestones
### Milestone 1: Draft source frontend AI contract
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Create the source instruction file that tells frontend AI agents how to design and build against this backend without violating the supported contract. |
| Owned Files Or Packages | `FRONTEND_AI_CONTRACT.md` |
| Coordinator-Owned Shared Files | `ROADMAP.md`, `.agents/plans/PLAN_frontend_ai_contract.md` |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, `ROADMAP.md`, `docs/DESIGN.md`, `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/upgrade-1x-to-2-0.adoc`, `README.md`, `src/test/resources/openapi/approved-openapi.json`, and the current Anthropic `frontend-design` source |
| Behavior To Preserve | No backend behavior or public contract changes; frontend guidance must defer to backend specs and published docs. |
| Deliverables | `FRONTEND_AI_CONTRACT.md` with purpose, source-of-truth hierarchy, API/session/CSRF rules, UI-state expectations, design-quality rules adapted from Anthropic guidance, anti-patterns, and copy instructions for the frontend repo. |
| Validation Checkpoint | Manual consistency review against the session docs, API overview, and OpenAPI session schema; confirm no source or generated contract files changed. |
| Commit Checkpoint | Commit after Milestone 2 unless the user asks for milestone-by-milestone commits. |

### Milestone 2: Register source artifact in repo docs
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Make the new source artifact discoverable and owned by the repository's documentation map. |
| Owned Files Or Packages | `README.md`, `WORKING_WITH_AI.md`, `AGENTS.md`, `.agents/references/documentation.md` |
| Coordinator-Owned Shared Files | `FRONTEND_AI_CONTRACT.md`, `ROADMAP.md`, `.agents/plans/PLAN_frontend_ai_contract.md` |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, `.agents/references/documentation.md`, `.agents/references/references-rules.md`, `README.md`, and `WORKING_WITH_AI.md` |
| Behavior To Preserve | Keep setup details out of AI docs, keep active-work detail in the plan, and avoid making the new frontend file a higher-priority source than executable specs or published backend contracts. |
| Deliverables | Short references in the appropriate AI/human-facing indexes and documentation ownership guidance for `FRONTEND_AI_CONTRACT.md`. |
| Validation Checkpoint | `rg -n "FRONTEND_AI_CONTRACT|Frontend AI contract|frontend AI contract" README.md WORKING_WITH_AI.md AGENTS.md .agents/references/documentation.md FRONTEND_AI_CONTRACT.md`; manual check for duplicated policy. |
| Commit Checkpoint | Commit local source-artifact and index updates together. |

### Milestone 3: Copy/adapt into frontend repository
| Field | Value |
| --- | --- |
| Status | Blocked |
| Goal | Place the instruction source into the separate frontend repository using that repository's AI-instruction conventions. |
| Owned Files Or Packages | Destination frontend repository file from Q1 and Q2 |
| Coordinator-Owned Shared Files | `FRONTEND_AI_CONTRACT.md`, `.agents/plans/PLAN_frontend_ai_contract.md` |
| Context Required | Q1 and Q2 answers, destination repository AI instructions, destination repository README or setup guide, and `FRONTEND_AI_CONTRACT.md` from this repository |
| Behavior To Preserve | Do not overwrite unrelated frontend AI guidance; keep this backend contract as source guidance, not as frontend implementation code. |
| Deliverables | Copied or adapted frontend-repo instruction file with local paths adjusted and this backend's contract constraints preserved. |
| Validation Checkpoint | Destination repo `git diff --check`; run the smallest available docs/lint check if the destination repo exposes one, otherwise record why no executable validation was available. |
| Commit Checkpoint | Commit or hand off destination repo changes according to the user's repository workflow after Q1 and Q2. |

### Milestone 4: Validate and hand off
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Prove the completed docs/AI-guidance work is internally consistent and did not alter backend behavior. |
| Owned Files Or Packages | Validation records in this plan; any destination repo validation notes if Q1/Q2 are answered |
| Coordinator-Owned Shared Files | `.agents/plans/PLAN_frontend_ai_contract.md`, `ROADMAP.md` |
| Context Required | `AGENTS.md`, `.agents/references/testing.md`, `.agents/references/execution.md`, this plan, and destination repo validation instructions if applicable |
| Behavior To Preserve | No backend code or contract drift; roadmap and plan lifecycle stay aligned. |
| Deliverables | Updated `Validation Results`, final roadmap status note, and concise handoff summary naming any remaining external-repo gap. |
| Validation Checkpoint | `./build.ps1 build`; manual diff review for docs-only scope; destination repo validation from Milestone 3 if applicable. |
| Commit Checkpoint | Commit plan/roadmap validation updates if tracked files changed after prior commits. |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| Q1 or Q2 remains unanswered when execution starts | Keep lifecycle `Needs Input`; ask for the frontend repository path and target AI-instruction convention before Milestone 3. | User/Agent | Open |
| Anthropic's `frontend-design` source changes materially before implementation | Re-read the source and revise D3 or Milestone 1 before drafting the contract. | Agent | Open |
| Backend published contract docs conflict with each other | Stop and resolve the contract source using `AGENTS.md` spec priority before writing frontend guidance. | Agent | Open |
| Destination frontend repo already has conflicting AI instructions | Preserve that repo's conventions, adapt the copied file, and record the conflict before editing. | Agent/User | Open |
| Implementation discovers the new file belongs under `.agents/` instead of repository root | Replan D2 and update affected documentation ownership before moving the file. | Agent/User | Open |

## Edge Cases And Failure Modes
- The generated instruction must not tell frontend agents to rely on CORS, JWTs, bearer tokens, `/login`, `/oauth2/authorization/github`, or provider callback paths outside the documented `/api/session/**` contract.
- The file should distinguish current cookie/header names from the safer integration rule: bootstrap from `GET /api/session` and use published CSRF metadata for unsafe writes.
- The design guidance should avoid generic AI aesthetics, but it must still fit an operational interview-demo UI rather than turning the app into a marketing site.
- The instruction file must not paste large portions of Anthropic's skill text; it should cite or name the source guidance and apply it to this repository's frontend contract.
- If only this backend repository is available, copying to the frontend repository remains blocked rather than simulated in an unrelated sibling directory.

## Validation Plan
- Run `./build.ps1 build` after local documentation/AI-guidance edits; this should classify as lightweight support-file work unless an unexpected non-lightweight file changes.
- Run targeted text checks for the new artifact references:
  - `rg -n "FRONTEND_AI_CONTRACT|Frontend AI contract|frontend AI contract" README.md WORKING_WITH_AI.md AGENTS.md .agents/references/documentation.md FRONTEND_AI_CONTRACT.md`
  - `git diff --check`
- Manually review `FRONTEND_AI_CONTRACT.md` against `src/docs/asciidoc/session-controller.adoc`, `src/docs/asciidoc/index.adoc`, and the approved OpenAPI session schema.
- If the destination frontend repo is edited, run its smallest discoverable docs/lint validation command; if none is available, record the skipped command and why.

## Testing Strategy
- Unit tests: not applicable unless implementation unexpectedly adds parsing or generation code.
- Integration tests: not applicable because no backend runtime behavior should change.
- Contract tests: no OpenAPI or REST Docs generation expected; run only if implementation changes contract artifacts, which should trigger replanning.
- Smoke/benchmark tests: not applicable for docs/AI-guidance-only work.
- Negative scenarios: manual review should check for forbidden frontend assumptions: CORS support, bearer-token auth, hard-coded provider paths, bypassing `GET /api/session`, and ignoring CSRF metadata.

## Better Engineering Notes
- Prefer a concise, explicit source instruction file over a broad prompt dump; frontend agents need contract-critical constraints first and aesthetic guidance second.
- Keep roadmap detail small; this plan owns milestones and validation history.
- If this source file proves useful beyond one frontend repo, consider a later small task to turn it into a reusable skill or task starter rather than expanding this plan.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-05-09 | `git diff --check` | Planning and roadmap whitespace check | Passed | No whitespace errors reported |
| 2026-05-09 | `./build.ps1 build` | Local docs/AI-guidance validation | Passed | Lightweight-only path; Gradle build skipped because only `.agents/plans/PLAN_frontend_ai_contract.md` and `ROADMAP.md` changed |
| 2026-05-09 | Pending | Destination frontend repository validation | Pending | Blocked by Q1 and Q2 |

## User Validation
- Review `FRONTEND_AI_CONTRACT.md` and confirm it reflects how the separate frontend should consume this backend.
- Confirm the destination frontend repository path and target AI-instruction filename/location.
- After execution, review the copied frontend-repo instruction file and verify it preserves this backend source contract without overriding unrelated frontend repo rules.

## Required Content Checklist
- Behavior changing and why: create a frontend AI contract source file so frontend agents preserve this backend's public contract and design intent.
- Decision-complete status: not ready; Q1 and Q2 block the destination-copy milestone.
- Roadmap entry: `ROADMAP.md` `Frontend AI contract` tracks this plan.
- Out of scope: backend runtime/API changes, frontend implementation, generated UI assets, setup/release process changes.
- Governing specs/contracts: `AGENTS.md` spec priority, `ROADMAP.md`, REST Docs, approved OpenAPI, README, and design guide.
- Likely files: `FRONTEND_AI_CONTRACT.md`, `README.md`, `WORKING_WITH_AI.md`, `AGENTS.md`, `.agents/references/documentation.md`, `ROADMAP.md`, and a destination frontend repo AI-instruction file.
- Compatibility promises: stable `2.x`, same-site first-party UI, session-cookie auth, CSRF metadata, no CORS/JWT/bearer-token promise.
- Edge cases and risks: destination repo unknown, external skill source may change, accidental contract drift, excessive copying of external source text.
- Requirement gaps: Q1 and Q2 are explicit and blocking.
- Locked decisions: D1-D6.
- Execution shape: one local branch plus destination repo copy after input.
- Shared files: named in execution shape and milestones.
- Progress tracking: top-level tracker included.
- Replan triggers: blocker table included.
- Context per milestone: exact read sets named.
- Artifact movement: docs/AI guidance only; no backend contract artifact changes expected.
- Validation: wrapper build, text checks, diff check, manual contract review, destination repo validation if applicable.
- Validation ledger: included.
- User verification: source and destination instruction review.
