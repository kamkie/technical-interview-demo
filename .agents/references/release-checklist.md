# Release Checklist Reference

`.agents/references/release-checklist.md` owns detailed mechanics for preparing a release metadata commit and annotated tag.
Load it only after `.agents/references/releases.md` release preconditions are satisfied and the task is preparing a release from local `main`.

From local `main`:

1. Review any new or changed Flyway migrations under `src/main/resources/db/migration/` and confirm each has an intentional JSON sidecar under `src/main/resources/db/migration/metadata/`.
2. Run `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag <previous-tag> -CurrentRef HEAD` and record whether the candidate is `none`, `rolling-compatible`, or `restore-sensitive`.
3. If the candidate is `restore-sensitive`, confirm restore-drill evidence exists for the exact candidate image using `pwsh ./scripts/release/invoke-restore-drill.ps1 ...`.
4. Confirm the required validation from `.agents/references/testing.md` passed for the exact candidate, including `./build.ps1 gatlingBenchmark` when that guide requires it.
5. Confirm OpenAPI compatibility still passes as part of the standard build, the latest default-branch CodeQL run is healthy or intentionally reviewed, and no unreviewed OpenAPI baseline refresh slipped in.
6. Merge any accepted `CHANGELOG_<topic>.md` files into `CHANGELOG.md`, then delete the temporary files.
7. Move the release-relevant `CHANGELOG.md` entries from `## [Unreleased]` into a new version section dated `YYYY-MM-DD`, and leave a fresh `## [Unreleased]` at the top.
8. Update `ROADMAP.md` to remove work completed by the release.
9. Move the executed concrete `.agents/plans/PLAN_*.md` file to `.agents/archive/` and update moved-path references in the same change; do not move `.agents/plans/PLAN_TEMPLATE.md`.
10. Update the target plan `Lifecycle` section to the closing lifecycle phase, normally `Phase=Release`, and `Status=Closed`; `Closed` is a terminal status, not a phase.
11. Use `.agents/references/learning-rules.md` to decide whether any execution or release lesson should be recorded, then update `docs/LEARNINGS.md` only for accepted durable repo-wide lessons.
12. Re-run validation that does not depend on tag presence if the release-metadata edits made earlier validation stale.
13. Commit with `Prepare vMAJOR.MINOR.PATCH[-PRERELEASE] release`.
14. Create an annotated tag `vMAJOR.MINOR.PATCH[-PRERELEASE]` with a concise annotation such as `Release vMAJOR.MINOR.PATCH[-PRERELEASE]`.
15. Run `pwsh ./scripts/docs/audit-docs.ps1` after the local annotated tag exists when `CHANGELOG.md` or `ROADMAP.md` names the new stable release, because the stable-version check uses local semantic-version tags.
16. Re-run `./build.ps1 -FullBuild build` after the local annotated tag exists if the exact release candidate has not already passed full validation or the checked behavior depends on tag-derived version identity.
17. Verify locally that the tag points at the release commit and that `git branch --show-current`, `git status --short`, `git log --first-parent --decorate --oneline -n 5`, `CHANGELOG.md`, `ROADMAP.md`, and the archived plan all reflect the release correctly.
