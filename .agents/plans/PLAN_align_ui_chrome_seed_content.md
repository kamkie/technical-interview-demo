# Plan: Align ui.* Chrome Seed Content With The Frontend Registry

## Provenance

| Field | Value |
| --- | --- |
| Created By | Claude Code (claude-fable-5) |
| Created On | 2026-06-12 |
| Source Request | User prompt "refine this tasks" targeting the two `ROADMAP.md` Conceptualization candidates: `M-COPY-001` seed copy alignment and `M-CATALOG-001` category chip search keys (both captured 2026-06-12) |
| Generation Context | Plan From Roadmap mode; loaded `AGENTS.md`, `.agents/references/planning.md`, `.agents/plans/PLAN_TEMPLATE.md`, archived `.agents/archive/PLAN_seed_ui_chrome_localizations.md`, the `ui-chrome` seed resources, and the live frontend registry `technical-interview-frontend/src/i18n/messages.ts`; branch `main`, single-agent `M0: direct` |

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Implementation |
| Status | In Progress |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | D2 (full registry snapshot), D5 (AI-authored translations) |
| Ready For Execution | Yes |
| Last Updated | 2026-06-12 |

## Linked Pre-Planning Artifacts
| Artifact | Path | Role | Status |
| --- | --- | --- | --- |
| ADR | None | Content-only seed alignment follows the established bootstrap-seed pattern | None |
| PRD | None | Scope is bounded demo-content alignment, fully defined here | None |
| Spec | None | Behavior is covered by `docs/FRONTEND_AI_CONTRACT.md` `## Localization And Errors` and the existing executable seed tests | None |

## Summary
- Align the backend `ui.*` chrome localization seed with the current frontend `UI_MESSAGES` registry: fix the broken English copy of `ui.admin-localization.rows-status-error`, add the 8 registry keys missing from the seed, and remove the 5 orphan keys the registry dropped in `M-WORKFLOW-002` (384 keys per language after the change, exact registry parity) across all 7 supported languages.
- The frontend (milestones `M-COPY-001` and `M-CATALOG-001`, plus newer common/session recovery strings) renders these keys from catalog rows with in-code English fallback; without seeded rows, demo environments render English-only for the new chrome and the admin coverage view shows gaps.
- Success: on a fresh demo-seeded database, all 7 languages serve 384 `ui.*` rows including the 8 new keys with native-script translations; the English `rows-status-error` text matches the corrected frontend copy; required validation is green.

## Scope
- In scope: the 7 seed resources `src/main/resources/localization/seed/ui-chrome/<lang>.json`, `CHANGELOG.md` `## [Unreleased]`, `ROADMAP.md` alignment, this plan's tracker.
- Out of scope: `error-messages` seed content, seed loader or initializer code changes, frontend repository changes, automated cross-repo key synchronization, production (non-demo) seeding, localization API behavior.

## Current State
- Re-verified 2026-06-12 (after frontend milestone `M-WORKFLOW-002`) against the live registry: the frontend has 384 `ui.*` keys; the backend seed has 381. The 8 keys missing from the seed are `ui.catalog.categories-no-match`, `ui.catalog.category-search-label`, `ui.catalog.category-search-placeholder`, `ui.catalog.empty-unfiltered-message`, `ui.catalog.empty-unfiltered-title`, `ui.common.backend-unavailable`, `ui.common.retry`, and `ui.session.bootstrap-failed`.
- `M-WORKFLOW-002` removed the admin list Refresh buttons, so 5 seeded keys are no longer in the registry (orphans): `ui.common.refresh`, `ui.admin-users.refresh-label`, `ui.admin-localization.refresh-label`, `ui.admin-catalog.refresh-books-label`, and `ui.admin-catalog.refresh-categories-label` (roadmap Conceptualization row, 2026-06-12; folded into this plan per D9).
- Exactly one English value drift exists between registry and seed: `ui.admin-localization.rows-status-error` is `Localization rows are needs attention.` (grammar bug) in `en.json` versus the corrected `Localization rows need attention.` in the registry. All six non-English seed values for that key already express the corrected meaning (e.g. pl `Wiersze lokalizacji wymagają uwagi.`, uk `Рядки локалізації потребують уваги.`), so only `en.json` changes for the reword.
- `UiChromeSeedDataTests` enforce key-set parity across all shipped languages, the `^[a-z0-9._-]+$` key pattern, column length limits, and message-format token parity dynamically — no test hardcodes the 381 count, so adding keys requires no test edits (they re-prove the invariants at the new size).
- Seeding is idempotent missing-row-only (`LocalizationDataInitializer`, gated by `app.bootstrap.seed.demo-data`); existing rows are never updated or overwritten.
- Seed JSON files keep keys in ordinal alphabetical order; format is LF line endings, UTF-8 without BOM, 2-space indent, trailing newline.
- Address register in existing translations: `de` formal (Sie), `es` formal (usted), `fr` `vous`, `uk` formal (Ви), `pl` informal (ty), `no` informal (du).
- None of the 8 new English strings contain message-format tokens.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | The roadmap rows name only 5 keys, but the registry has 8 keys missing from the seed — seed all 8 or only the named 5? | Determines whether the seed snapshot stays internally consistent with the registry | Agent | Answered | D2: seed all 8; the registry snapshot is the single source of truth per the archived seeding plan's D3 pattern | No |
| Q2 | Do the non-English `rows-status-error` values also need rewording? | Could expand content scope ×6 | Agent | Answered | No — verified all six already match the corrected meaning (see Current State); only `en.json` changes | No |
| Q3 | `M-WORKFLOW-002` orphaned 5 seeded refresh keys — keep them as harmless orphans or remove them with this slice? | Determines whether the seed snapshot keeps exact registry parity | Agent | Answered | D9: remove them; registry parity is the established principle, and removal only affects what fresh databases seed | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Batch both roadmap candidates (`M-COPY-001`, `M-CATALOG-001`) into one maintenance slice, as both rows requested | Roadmap rows | 2026-06-12 | None |
| D2 | Seed the full current registry snapshot (all 8 missing keys, 389 total per language), not just the 5 keys the roadmap rows named; the 3 extra keys (`ui.common.backend-unavailable`, `ui.common.retry`, `ui.session.bootstrap-failed`) are newer frontend recovery strings | Agent fallback; archived plan D3 precedent (registry is the key-list owner) | 2026-06-12 | Registry changes materially before Task 1 executes — re-diff and regenerate |
| D3 | Only `en.json` changes for `ui.admin-localization.rows-status-error`; existing translations already aligned (verified) | Code | 2026-06-12 | None |
| D4 | All new translations use native script (Spanish accents, German umlauts/eszett, French accents, Polish diacritics, Cyrillic Ukrainian, Norwegian å/æ/ø) | User decision recorded in archived plan D6 and `docs/LEARNINGS.md` | 2026-06-12 | None |
| D5 | AI-authored translation content is acceptable for demo seed rows; exact strings are locked in Task 1's translation block so the executor invents nothing | Precedent: archived plan Q3 | 2026-06-12 | User requests professional translation review |
| D6 | New translations follow the existing per-language address register (de Sie, es usted, fr vous, uk Ви, pl ty, no du) | Code (verified samples) | 2026-06-12 | None |
| D7 | No loader, initializer, or test code changes expected; resources are loaded dynamically and the parity tests adapt to the new key count | Code analysis | 2026-06-12 | A check or test unexpectedly hardcodes seed counts — update it deliberately, never weaken invariants |
| D8 | Databases seeded before this change keep the old English `rows-status-error` text because seeding never updates existing rows; the fix reaches fresh seeds only, and operators can correct live rows through the admin API. Accepted for demo scope | Code (missing-row-only insert semantics) | 2026-06-12 | User asks for an in-place seed-content correction mechanism |
| D9 | Fold the `M-WORKFLOW-002` orphan cleanup into Task 1: remove the 5 dropped refresh keys from all 7 seed resources. Removal only changes what fresh databases seed — the initializer never deletes existing rows, so already-seeded databases keep their refresh rows as operator-manageable content. Keeps exact key-set parity (384) with the registry per D2 | Agent fallback; roadmap Conceptualization row 2026-06-12 | 2026-06-12 | User wants the orphan rows kept seeded for fresh databases |

## Execution Shape And Shared Files
- Recommended shape: `M0: direct` — one agent on `main`, two commit-sized tasks.
- Content-only change to one resource directory plus tracker docs; nothing is parallelizable enough to justify delegation overhead.
- No coordinator-owned shared files.

## Affected Artifacts
- Resources: `src/main/resources/localization/seed/ui-chrome/{en,es,de,fr,pl,uk,no}.json` (1 reworded value in `en`; 8 new keys and 5 removed keys in each of the 7 files, alphabetical order preserved).
- Tests: none expected to change (D7); `UiChromeSeedDataTests` and the localization test suite re-prove parity, pattern, length, and token invariants at 384 keys.
- Docs: `CHANGELOG.md` `## [Unreleased]` (`### Added` for the new keys, `### Fixed` for the English copy fix, `### Removed` for the orphan keys); `ROADMAP.md` row transitions; this plan.
- OpenAPI / REST Docs / HTTP examples: none — no public contract change.
- Benchmarks: `gatlingBenchmark` not required; no behavior code changes (record the skip rationale in Validation Results).

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Seed content alignment | Done | Agent | `feat(localization): align ui chrome seed content with frontend registry` | Targeted seed tests (22) passed; `./build.ps1 build` green; post-change re-diff exact parity (384/384, drift 0) | Replan trigger fired 2026-06-12: registry re-diff showed `M-WORKFLOW-002` dropped 5 keys (389 → 384); orphan removal folded in per D9 |
| 2: Changelog, roadmap, final validation | Not Started | Agent | Pending | Pending | |

## Execution Tasks
### Task 1: Seed content alignment
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Bring all 7 `ui-chrome` seed resources to exact key-set parity with the 384-key frontend registry (add 8, remove 5) and correct the English `rows-status-error` copy |
| Owned Files Or Packages | `src/main/resources/localization/seed/ui-chrome/{en,es,de,fr,pl,uk,no}.json` |
| Coordinator-Owned Shared Files | None |
| Context Required | none beyond AGENTS.md, .agents/references/execution.md, and this plan |
| Behavior To Preserve | All existing seed values except the one reworded English string; alphabetical key order; key-set parity across languages; idempotent missing-row-only seeding semantics |
| Deliverables | In `en.json`: reword `ui.admin-localization.rows-status-error` to `Localization rows need attention.` and add the 8 registry English defaults. In each non-English file: add the 8 translations from the translation block below, verbatim. In all 7 files: remove the 5 orphan keys named in Current State (D9). Re-diff performed 2026-06-12 after `M-WORKFLOW-002`; key list and counts in this plan reflect that diff |
| Validation Checkpoint | `./build.ps1 test --tests "team.jit.technicalinterviewdemo.business.localization.seed.*"` then `./build.ps1 build` |
| Commit Checkpoint | `feat(localization): align ui chrome seed content with frontend registry` with plan-task footers |

Translation block (D4 native script, D6 register; merge into each file in alphabetical key order):

```jsonc
// en — must match the frontend registry defaults exactly
"ui.catalog.categories-no-match": "No categories match this search.",
"ui.catalog.category-search-label": "Search categories",
"ui.catalog.category-search-placeholder": "Type to find categories",
"ui.catalog.empty-unfiltered-message": "There are no books in the catalog yet.",
"ui.catalog.empty-unfiltered-title": "The catalog is empty",
"ui.common.backend-unavailable": "The service cannot be reached right now. Check your connection or try again shortly.",
"ui.common.retry": "Try again",
"ui.session.bootstrap-failed": "The session could not be checked."
```

```jsonc
// es
"ui.catalog.categories-no-match": "Ninguna categoría coincide con esta búsqueda.",
"ui.catalog.category-search-label": "Buscar categorías",
"ui.catalog.category-search-placeholder": "Escriba para encontrar categorías",
"ui.catalog.empty-unfiltered-message": "Todavía no hay libros en el catálogo.",
"ui.catalog.empty-unfiltered-title": "El catálogo está vacío",
"ui.common.backend-unavailable": "No se puede acceder al servicio en este momento. Compruebe su conexión o inténtelo de nuevo en breve.",
"ui.common.retry": "Intentar de nuevo",
"ui.session.bootstrap-failed": "No se pudo comprobar la sesión."
```

```jsonc
// de
"ui.catalog.categories-no-match": "Keine Kategorien entsprechen dieser Suche.",
"ui.catalog.category-search-label": "Kategorien durchsuchen",
"ui.catalog.category-search-placeholder": "Tippen Sie, um Kategorien zu finden",
"ui.catalog.empty-unfiltered-message": "Es gibt noch keine Bücher im Katalog.",
"ui.catalog.empty-unfiltered-title": "Der Katalog ist leer",
"ui.common.backend-unavailable": "Der Dienst ist derzeit nicht erreichbar. Prüfen Sie Ihre Verbindung oder versuchen Sie es in Kürze erneut.",
"ui.common.retry": "Erneut versuchen",
"ui.session.bootstrap-failed": "Die Sitzung konnte nicht überprüft werden."
```

```jsonc
// fr
"ui.catalog.categories-no-match": "Aucune catégorie ne correspond à cette recherche.",
"ui.catalog.category-search-label": "Rechercher des catégories",
"ui.catalog.category-search-placeholder": "Saisissez du texte pour trouver des catégories",
"ui.catalog.empty-unfiltered-message": "Il n'y a pas encore de livres dans le catalogue.",
"ui.catalog.empty-unfiltered-title": "Le catalogue est vide",
"ui.common.backend-unavailable": "Le service est momentanément inaccessible. Vérifiez votre connexion ou réessayez dans un instant.",
"ui.common.retry": "Réessayer",
"ui.session.bootstrap-failed": "La session n'a pas pu être vérifiée."
```

```jsonc
// pl
"ui.catalog.categories-no-match": "Żadna kategoria nie pasuje do tego wyszukiwania.",
"ui.catalog.category-search-label": "Szukaj kategorii",
"ui.catalog.category-search-placeholder": "Wpisz, aby znaleźć kategorie",
"ui.catalog.empty-unfiltered-message": "W katalogu nie ma jeszcze książek.",
"ui.catalog.empty-unfiltered-title": "Katalog jest pusty",
"ui.common.backend-unavailable": "Usługa jest teraz niedostępna. Sprawdź połączenie lub spróbuj ponownie za chwilę.",
"ui.common.retry": "Spróbuj ponownie",
"ui.session.bootstrap-failed": "Nie udało się sprawdzić sesji."
```

```jsonc
// uk
"ui.catalog.categories-no-match": "Жодна категорія не відповідає цьому пошуку.",
"ui.catalog.category-search-label": "Пошук категорій",
"ui.catalog.category-search-placeholder": "Введіть текст, щоб знайти категорії",
"ui.catalog.empty-unfiltered-message": "У каталозі ще немає книг.",
"ui.catalog.empty-unfiltered-title": "Каталог порожній",
"ui.common.backend-unavailable": "Сервіс наразі недоступний. Перевірте з'єднання або спробуйте ще раз трохи пізніше.",
"ui.common.retry": "Спробувати ще раз",
"ui.session.bootstrap-failed": "Не вдалося перевірити сесію."
```

```jsonc
// no
"ui.catalog.categories-no-match": "Ingen kategorier samsvarer med dette søket.",
"ui.catalog.category-search-label": "Søk i kategorier",
"ui.catalog.category-search-placeholder": "Skriv for å finne kategorier",
"ui.catalog.empty-unfiltered-message": "Det er ingen bøker i katalogen ennå.",
"ui.catalog.empty-unfiltered-title": "Katalogen er tom",
"ui.common.backend-unavailable": "Tjenesten kan ikke nås akkurat nå. Sjekk tilkoblingen eller prøv igjen om litt.",
"ui.common.retry": "Prøv igjen",
"ui.session.bootstrap-failed": "Økten kunne ikke kontrolleres."
```

### Task 2: Changelog, roadmap, final validation
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Record the unreleased change, close the roadmap loop, and prove the cumulative diff |
| Owned Files Or Packages | `CHANGELOG.md`, `ROADMAP.md`, this plan |
| Coordinator-Owned Shared Files | None |
| Context Required | none beyond AGENTS.md, .agents/references/execution.md, and this plan |
| Behavior To Preserve | n/a (docs and validation only) |
| Deliverables | `CHANGELOG.md` `## [Unreleased]`: `### Added` entry for the 8 newly seeded keys (384 keys per language), `### Fixed` entry for the English `rows-status-error` copy fix noting it reaches fresh seeds only (D8), and `### Removed` entry for the 5 orphan refresh keys no longer seeded (D9, existing rows untouched); `ROADMAP.md` row moved to `Integrated` with updated `Immediate Next Action`; plan lifecycle moved to `Phase=Integration`, `Status=Implemented`; validation ledger updated |
| Validation Checkpoint | `pwsh ./scripts/docs/audit-docs.ps1` and final signoff `./build.ps1 -FullBuild build` (cumulative proof across both committed tasks) |
| Commit Checkpoint | `docs(localization): record ui chrome seed alignment` with plan-task footers |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| Frontend `UI_MESSAGES` registry changed materially since the 2026-06-12 snapshot (key count drift or new value drift) | Re-diff registry vs `en.json` before authoring, regenerate the key list and translation block, update this plan's Current State | Agent | Resolved — trigger fired 2026-06-12 (`M-WORKFLOW-002` dropped 5 refresh keys, 389 → 384); re-diff done, D9 folded the orphan removal into Task 1, missing-key list and drift unchanged |
| A test or check unexpectedly asserts exact seed counts | Update the assertion deliberately to the new size; never weaken parity, pattern, length, or token invariants (D7) | Agent | Open |
| Static analysis or resource validation rejects the new content | Fix the finding; if the content itself violates a constraint (key pattern, column length), correct the content, not the check | Agent | Open |

## Edge Cases And Failure Modes
- Already-seeded demo databases: the reworded English text does not reach existing rows (D8), and removed orphan keys stay in existing databases as operator-manageable rows (D9); only fresh seeds reflect the new snapshot — intentionally accepted, documented in the changelog entries.
- Operator-edited rows: the missing-row filter keeps skipping any existing `(message_key, language)` pair, including hand-entered `ui.*` rows.
- Constraint safety: all 8 keys are ≤150 chars and match `^[a-z0-9._-]+$`; all texts are far below 2,000 chars; `UiChromeSeedDataTests` enforce both.
- No message-format tokens exist in the new strings, so token-parity checks are trivially satisfied.
- No schema, API, OpenAPI, or rollout impact: flag-gated demo content only.

## Validation Plan
- Task 1: `./build.ps1 test --tests "team.jit.technicalinterviewdemo.business.localization.seed.*"`, then `./build.ps1 build`.
- Task 2: `pwsh ./scripts/docs/audit-docs.ps1`; final signoff `./build.ps1 -FullBuild build`.
- `gatlingBenchmark`: skipped — no behavior code changes; seed volume grows by 21 rows net (2,667 → 2,688), far below any startup-cost concern (archived plan measured no issue at 2,667).

## Verification Strategy
- Unit: `UiChromeSeedDataTests` re-prove resource loading, all-language key parity, constraint limits, and fail-fast behavior at 384 keys.
- Integration: existing localization API and initializer tests prove seeded rows are served and seeding stays idempotent.
- Contract: OpenAPI compatibility check guards against accidental drift (none expected).
- Negative: malformed-resource fail-fast and disabled-flag paths remain covered by existing tests.

## Better Engineering Notes
- Deferred, not hidden (carried from the archived seeding plan): automated cross-repo key synchronization between the frontend registry and backend seed resources; production seeding strategy.
- No prerequisite cleanup identified.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-06-12 | Registry-vs-seed key/value diff (PowerShell over `messages.ts` and `en.json`) | Planning evidence | Passed | 389 registry keys vs 381 seeded; 8 missing keys identified; exactly one English value drift (`rows-status-error`) |
| 2026-06-12 | Registry-vs-seed re-diff after `M-WORKFLOW-002` | Task 1 replan-trigger check | Passed | Registry now 384 keys; same 8 missing keys and same single drift; 5 seeded orphan keys identified for removal (D9) |
| 2026-06-12 | Scripted alignment with byte-exact round-trip fidelity check, then registry-vs-seed re-diff | Task 1 content verification | Passed | All 7 resources at 384 keys; missing=0, orphans=0, drift=0 against the live registry |
| 2026-06-12 | `./build.ps1 test --tests "team.jit.technicalinterviewdemo.business.localization.seed.*"` | Task 1 targeted seed tests | Passed | 22 tests green in 3.3s, including all-language key parity, token preservation, and constraint checks at 384 keys |
| 2026-06-12 | `./build.ps1 build` | Task 1 full build with checks | Passed | BUILD SUCCESSFUL in 4m 6s |

## User Validation
- Start the app locally with the `local` profile on a fresh database, open the frontend, and use the catalog category search: the search label, placeholder, and no-match message should render localized after switching the language preference (e.g. Polish).
- As an admin, open the localization administration view and filter by `ui.catalog.category-search`: all 7 languages should show seeded rows; filter `ui.admin-localization.rows-status-error` and confirm the English text reads `Localization rows need attention.`
