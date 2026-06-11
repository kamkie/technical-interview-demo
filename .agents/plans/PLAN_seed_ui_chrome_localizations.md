# Plan: Seed ui.* Frontend Chrome Localizations

## Provenance

| Field | Value |
| --- | --- |
| Created By | Claude Code (Claude Fable 5) |
| Created On | 2026-06-11 |
| Source Request | User asked to move the `ROADMAP.md` Conceptualization candidate "Seed `ui.*` frontend chrome translations in the localization catalog" through Triage into its next artifact |
| Generation Context | `Plan From Roadmap` mode on `main`; loaded `AGENTS.md`, `.agents/references/planning.md`, `.agents/references/testing.md`, `.agents/plans/PLAN_TEMPLATE.md`, `docs/FRONTEND_AI_CONTRACT.md`, backend localization seed sources, and the frontend registry `technical-interview-frontend/src/i18n/messages.ts` (milestone `M-I18N-001`) |

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | Q1 (demo-only seeding), Q2 (ASCII transliteration convention), Q3 (AI-authored translations) |
| Ready For Execution | Yes |
| Last Updated | 2026-06-11 |

## Linked Pre-Planning Artifacts
| Artifact | Path | Role | Status |
| --- | --- | --- | --- |
| ADR | None | No durable architecture choice; the work follows the existing bootstrap-seed pattern | None |
| PRD | None | Scope is bounded demo-content seeding, fully defined here | None |
| Spec | None | Behavior is covered by the published contract statement in `docs/FRONTEND_AI_CONTRACT.md` `## Localization And Errors` plus the executable seed and localization tests this plan updates | None |

## Summary
- Seed the localization catalog with `ui.*` chrome rows for all 7 supported languages in demo-data environments by extending the existing bootstrap seeder with resource-backed content snapshotted from the frontend `UI_MESSAGES` registry (364 keys, 2,548 rows).
- The first-party frontend (frontend milestone `M-I18N-001`) renders UI chrome from public catalog rows with `ui.`-prefixed keys and in-code English fallback; without seeded rows, demo environments render English-only chrome and the admin localization coverage view has no `ui.*` content to manage.
- Success: on a fresh database with `app.bootstrap.seed.demo-data=true`, the public localization endpoints serve `ui.*` rows for `en`, `es`, `de`, `fr`, `pl`, `uk`, and `no`; existing operator rows are never overwritten; all required validation is green.

## Scope
- In scope: `ui.*` seed content for the 7 supported languages, a resource-backed seed loader, wiring into the existing idempotent initializer, test updates, a demo-seed note in `docs/FRONTEND_AI_CONTRACT.md`, `CHANGELOG.md` `## [Unreleased]`, and `ROADMAP.md` alignment.
- Out of scope: production seeding (Flyway or any always-on mechanism), localization API behavior changes, native-script content revision, frontend repository changes, automated cross-repo key synchronization.

## Current State
- `error.*` messages are seeded by `LocalizationSeedData` + `LocalizationDataInitializer` (a `CommandLineRunner` doing an idempotent missing-row bulk insert), gated by `app.bootstrap.seed.demo-data` — `false` by default and in `application-prod.properties`, `true` only in `application-local.properties`. Production catalog content is operator-managed via the localization admin API.
- `docs/FRONTEND_AI_CONTRACT.md` `## Localization And Errors` (lines 111–112) already publishes the chrome-key contract: `ui.*` rows are operator-managed content; languages without seeded rows fall back to in-code English defaults without breaking the page.
- The frontend registry `technical-interview-frontend/src/i18n/messages.ts` is the single owner of the key list: 364 `ui.*` keys with English default text, keys following the backend `^[a-z0-9._-]+$` pattern.
- `localization_messages` constraints: unique `(message_key, language)`, `message_key` varchar(150), `message_text` varchar(2000) (`V2__create_localization_messages_table.sql`).
- `SupportedLanguages` anchors the language list to `LocalizationSeedData.supportedLanguages()` (7 languages).
- Count-sensitive executable specs: `LocalizationApiIntegrationTests` derives expected totals from `documentedKeys() × supportedLanguages()`; `LocalizationServiceTests` asserts exact seed-sized message maps; `LocalizationDataInitializerTests` covers flag gating and missing-row-only writes.
- Existing seed translations use ASCII-only transliteration (no diacritics; Ukrainian in Latin script) with description text `"Seed translation for <key> in <language>."`.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Should production environments receive `ui.*` rows out of the box (e.g. Flyway seed) instead of staying operator-managed? | Determines mechanism and whether production stops rendering English-only chrome without operator action | User | Answered | No — demo-only seeding; the published contract states chrome rows are operator-managed in production, matching the existing `error.*` posture (D1, D2). Revisit if the user asks for production seeding. | No |
| Q2 | Native-script translations (diacritics, Cyrillic Ukrainian) or the existing ASCII transliteration convention? | Content quality vs consistency with every existing seed row | User | Answered | Follow the existing ASCII transliteration convention for consistency (D6). Switching to native script is a separate content revision. | No |
| Q3 | Is AI-authored translation content acceptable for the 6 non-English languages (~2,184 strings)? | Content ownership and quality bar | User | Answered | Yes for demo seed data, matching how existing `error.*` translations were authored; operators can override any row through the admin API (D2). | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Mechanism: extend the existing `app.bootstrap.seed.demo-data` bootstrap seeder with an idempotent missing-row insert; do not add Flyway seed data. The roadmap's "Flyway vs admin API" framing missed this existing third mechanism, which is the established repo pattern and never overwrites operator content. | Code (`LocalizationDataInitializer`, profile properties) | 2026-06-11 | User requests production out-of-the-box seeding |
| D2 | Content ownership: repo-maintained demo seed defaults (same ownership as `error.*` seeds), operator-overridable through the localization admin API; production content remains operator-entered per the published contract. | `docs/FRONTEND_AI_CONTRACT.md` `## Localization And Errors` | 2026-06-11 | Contract statement changes |
| D3 | Key list source: a snapshot of the frontend `UI_MESSAGES` registry (364 keys); seeded `en` text matches the frontend in-code defaults exactly. Later frontend drift is benign because missing rows fall back to in-code defaults by contract. | Frontend `src/i18n/messages.ts` + contract fallback rule | 2026-06-11 | Frontend registry changes materially before execution |
| D4 | Seed all 7 languages including `en`, consistent with `error.*` seeds; complete `en` rows keep the admin coverage view meaningful and let operators edit English chrome without a frontend release. | Code convention | 2026-06-11 | None |
| D5 | Content format: per-language UTF-8 JSON classpath resources (flat key-to-text maps) under `src/main/resources/localization/seed/ui-chrome/<lang>.json`, loaded by a new `UiChromeSeedData` loader; ~2,548 Java literal builder rows would be unmaintainable and churn-heavy. | Agent fallback | 2026-06-11 | Static-analysis or packaging constraints make resources unworkable |
| D6 | Translations follow the existing ASCII transliteration convention used by all current seed rows. | Code convention; accepted fallback for Q2 | 2026-06-11 | User requests native-script content |
| D7 | `LocalizationSeedData.documentedKeys()` keeps its current `error.*` semantics; `ui.*` seed counts are exposed separately, and count-sensitive tests are updated deliberately rather than weakened. | Test analysis | 2026-06-11 | None |
| D8 | A malformed or missing seed resource fails startup loudly (fail-fast) when demo seeding is enabled; silent partial seeding is worse than a visible failure in demo environments. | Agent fallback | 2026-06-11 | None |

## Execution Shape And Shared Files
- Recommended shape: `M0: direct`.
- One coherent feature with mechanical content generation; no slice is independent enough to justify delegation overhead, and all tasks touch the same seed package and tests.
- No coordinator-owned shared files; single-agent execution on `main` or a short-lived branch per `.agents/references/workflow.md`.

## Affected Artifacts
- Source: `business/localization/seed` (new `UiChromeSeedData` loader; `LocalizationDataInitializer` wiring so one idempotent pass covers `error.*` and `ui.*` rows); new resources `src/main/resources/localization/seed/ui-chrome/<lang>.json` (7 files).
- Tests: `LocalizationDataInitializerTests` (combined seed source), new `UiChromeSeedDataTests` (resource loading, key-set parity across shipped languages, `^[a-z0-9._-]+$` and length constraints, fail-fast on malformed resources), count updates in `LocalizationApiIntegrationTests` and `LocalizationServiceTests`.
- Docs: `docs/FRONTEND_AI_CONTRACT.md` (one sentence noting demo-data environments pre-seed `ui.*` rows), `CHANGELOG.md` `## [Unreleased]`, `ROADMAP.md` row updates.
- OpenAPI / REST Docs / HTTP examples: none — no public contract change.
- Benchmarks: `gatlingBenchmark` not required; localization lookup behavior code is unchanged (record the skip rationale in Validation Results).

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Seed loader and English chrome resource | Not Started | Agent | Pending | Pending | |
| 2: Non-English chrome seed content | Not Started | Agent | Pending | Pending | |
| 3: Docs, changelog, and roadmap alignment | Not Started | Agent | Pending | Pending | |

## Execution Tasks
### Task 1: Seed loader and English chrome resource
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Load `ui.*` chrome seed rows from classpath resources and seed them through the existing idempotent initializer, starting with `en` |
| Owned Files Or Packages | `business/localization/seed` (main + test), `src/main/resources/localization/seed/ui-chrome/en.json`, count-sensitive localization tests |
| Coordinator-Owned Shared Files | None |
| Context Required | This plan, `.agents/references/execution.md`, `.agents/references/code-style.md`, `business/localization` sources and tests, frontend `technical-interview-frontend/src/i18n/messages.ts` for the `en` snapshot |
| Behavior To Preserve | `error.*` seeding semantics, `documentedKeys()` meaning, flag gating (`app.bootstrap.seed.demo-data`), never overwriting existing rows, localization API behavior |
| Deliverables | `UiChromeSeedData` loader (fail-fast per D8, validates key pattern and column lengths); `en.json` generated to exactly match the 364 frontend `UI_MESSAGES` defaults; initializer wiring with one combined idempotent pass; `UiChromeSeedDataTests`; updated `LocalizationDataInitializerTests`; deliberate count updates in `LocalizationApiIntegrationTests` and `LocalizationServiceTests` |
| Validation Checkpoint | Targeted localization test classes via the wrapper (`.agents/references/command-wrapper.md` syntax), then `./build.ps1 build` |
| Commit Checkpoint | `feat(localization): seed ui chrome rows from classpath resources` with plan-task footers |

### Task 2: Non-English chrome seed content
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Ship full `ui.*` coverage for `es`, `de`, `fr`, `pl`, `uk`, and `no` |
| Owned Files Or Packages | `src/main/resources/localization/seed/ui-chrome/{es,de,fr,pl,uk,no}.json` |
| Coordinator-Owned Shared Files | None |
| Context Required | This plan, `.agents/references/execution.md`, the `en.json` snapshot, existing `LocalizationSeedData` translations as the tone and transliteration reference |
| Behavior To Preserve | Key-set parity with `en.json` (enforced by `UiChromeSeedDataTests`); ASCII transliteration convention (D6); message-format tokens such as `{count}` preserved verbatim in every translation |
| Deliverables | Six translation resources, 364 keys each (2,184 strings), AI-authored per Q3 |
| Validation Checkpoint | Targeted seed and localization tests via the wrapper, then `./build.ps1 build` |
| Commit Checkpoint | `feat(localization): add non-english ui chrome seed translations` with plan-task footers |

### Task 3: Docs, changelog, and roadmap alignment
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Align published docs and trackers with the delivered seeding behavior |
| Owned Files Or Packages | `docs/FRONTEND_AI_CONTRACT.md`, `CHANGELOG.md`, `ROADMAP.md`, this plan's tracker and validation ledger |
| Coordinator-Owned Shared Files | None |
| Context Required | This plan, `.agents/references/execution.md`, `.agents/references/documentation.md` if artifact routing becomes unclear |
| Behavior To Preserve | Existing contract statements; `CHANGELOG.md` format; roadmap rules |
| Deliverables | One-sentence demo-seed note in the contract doc's `## Localization And Errors`; `## [Unreleased]` entry; roadmap row moved to reflect integration state |
| Validation Checkpoint | `pwsh ./scripts/docs/audit-docs.ps1`; final signoff `./build.ps1 -FullBuild build` because Tasks 1–2 were committed earlier in the same plan |
| Commit Checkpoint | `docs(localization): record ui chrome seed coverage` with plan-task footers |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| Frontend `UI_MESSAGES` registry changed materially since the 2026-06-11 snapshot | Regenerate `en.json` from the current registry before Task 2 content is authored | Agent | Open |
| Count-sensitive integration tests assert page contents beyond totals and break in unexpected ways | Adjust assertions deliberately to the combined seed volume; never weaken contract checks or filter rows to hide drift | Agent | Open |
| Static analysis (PMD/SpotBugs/Error Prone/coverage) rejects the loader or resource layout | Fix the finding; if the resource approach itself is unworkable, replan D5 before coding around checks | Agent | Open |
| Seeding 2,548 rows visibly slows demo startup or test runs | Measure first; batch inserts already go through `saveAll`, so replan only with evidence | Agent | Open |

## Edge Cases And Failure Modes
- Malformed or missing seed resource: fail startup loudly when demo seeding is enabled (D8); never seed a partial language silently.
- Existing operator-edited rows: the missing-row filter must keep skipping any `(message_key, language)` that already exists, including hand-entered `ui.*` rows.
- Constraint safety: every key must satisfy `^[a-z0-9._-]+$` and ≤150 chars; every text ≤2000 chars — enforced by `UiChromeSeedDataTests` so a bad snapshot cannot reach the database.
- Message-format tokens (`{count}`, `{label}`, …) must survive translation verbatim; the frontend leaves unknown tokens in place, so a mistranslated token degrades visibly but safely.
- No rollout or migration risk: no schema change, no public API change, seeding is flag-gated demo content.

## Validation Plan
- Per `.agents/references/testing.md`: targeted localization/seed test classes during Tasks 1–2; `./build.ps1 build` at each commit checkpoint.
- `pwsh ./scripts/docs/audit-docs.ps1` for Task 3 (user-facing Markdown changes).
- Final signoff: `./build.ps1 -FullBuild build` (cumulative plan proof across committed tasks).
- `gatlingBenchmark`: skipped — localization lookup behavior code is unchanged; record the rationale in Validation Results.

## Verification Strategy
- Unit: `UiChromeSeedDataTests` (loading, parity, constraints, fail-fast), `LocalizationDataInitializerTests` (combined sources, flag gating, missing-row-only writes).
- Integration: existing localization API tests with updated totals prove seeded rows are served through the public endpoints.
- Contract: no OpenAPI/REST Docs change expected; the OpenAPI compatibility check guards against accidental drift.
- Negative: malformed resource fails startup; disabled flag seeds nothing; existing rows are not overwritten.

## Better Engineering Notes
- Deferred, not hidden: automated cross-repo key-sync between the frontend registry and backend seed resources; native-script content revision (Q2); production seeding strategy (Q1).
- No prerequisite cleanup identified; the seed package is small and current.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-06-11 | Pending | — | Pending | Plan created; no execution yet |

## User Validation
- Start the app locally with the `local` profile (demo seeding enabled) on a fresh database.
- Open the frontend, switch the language preference to Spanish: navigation, catalog chrome, and dialogs should render Spanish instead of English defaults.
- As an admin, open the localization administration view and filter by message key `ui.`: coverage should show the 7 supported languages complete for `ui.*` keys.
