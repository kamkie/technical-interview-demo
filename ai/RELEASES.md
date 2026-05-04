# Release Guide For AI Agents

`ai/RELEASES.md` owns intentional release preparation after implementation is already complete on `main`.

Use this file when the task is to prepare a release commit, tag it, push it, or verify a published release.
Stay in `ai/EXECUTION.md` or `ai/WORKFLOW.md` while work is still local, on a side branch, or in an open PR.

When `ai/WORKFLOW.md` `Parallel Plans` mode was used, root-level `CHANGELOG_<topic>.md` files are release inputs. Merge accepted entries into `CHANGELOG.md` and delete the temporary files in the same release-preparation change.

## Release Preconditions

Do not start release work until all of these are true:

- the target plan is fully executed and its `Lifecycle` and `Validation Results` are current
- the approved implementation PR is already merged onto `main`, or the user explicitly chose a no-PR flow and the reviewed change set is already on `main`
- any worktree or side-branch work is already integrated onto `main`
- local `main` is synced to the release-candidate state
- required specs, contract docs, and maintainer docs are aligned through `ai/DOCUMENTATION.md`
- required validation from `ai/TESTING.md` passed for the exact release candidate

## Release Checklist

From local `main`:

1. Review any new or changed Flyway migrations under `src/main/resources/db/migration/` and confirm each has an intentional JSON sidecar under `src/main/resources/db/migration/metadata/`.
2. Run `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag <previous-tag> -CurrentRef HEAD` and record whether the candidate is `none`, `rolling-compatible`, or `restore-sensitive`.
3. If the candidate is `restore-sensitive`, confirm restore-drill evidence exists for the exact candidate image using `pwsh ./scripts/release/invoke-restore-drill.ps1 ...`.
4. Confirm the required validation from `ai/TESTING.md` passed for the exact candidate, including `./gradlew gatlingBenchmark` when that guide requires it.
5. Confirm OpenAPI compatibility still passes as part of the standard build, the latest default-branch CodeQL run is healthy or intentionally reviewed, and no unreviewed OpenAPI baseline refresh slipped in.
6. Merge any accepted `CHANGELOG_<topic>.md` files into `CHANGELOG.md`, then delete the temporary files.
7. Move the release-relevant `CHANGELOG.md` entries from `## [Unreleased]` into a new version section dated `YYYY-MM-DD`, and leave a fresh `## [Unreleased]` at the top.
8. Update `ROADMAP.md` to remove work completed by the release.
9. Move the executed `ai/PLAN_*.md` file to `ai/archive/` and update moved-path references in the same change.
10. Update the target plan `Lifecycle` section to `Phase=Closed` and `Status=Released`.
11. Re-run `.\gradlew.bat build` only if the release-metadata edits made the earlier validation stale.
12. Commit with `Prepare vMAJOR.MINOR.PATCH[-PRERELEASE] release`.
13. Create an annotated tag `vMAJOR.MINOR.PATCH[-PRERELEASE]` with a concise annotation such as `Release vMAJOR.MINOR.PATCH[-PRERELEASE]`.
14. Verify locally that the tag points at the release commit and that `git branch --show-current`, `git status --short`, `git log --first-parent --decorate --oneline -n 5`, `CHANGELOG.md`, `ROADMAP.md`, and the archived plan all reflect the release correctly.

## Choosing The Version

Use semantic tags `vMAJOR.MINOR.PATCH` or prerelease tags `vMAJOR.MINOR.PATCH-PRERELEASE`.

Choose deliberately:

- increment `PATCH` for backward-compatible fixes, cleanup, or doc-aligned contract corrections
- increment `MINOR` for backward-compatible feature additions or meaningful contract expansion
- increment `MAJOR` only for intentional breaking changes
- apply prerelease suffixes such as `-M1`, `-ALFA1`, `-BETA2`, or `-RC1` only after choosing the underlying base version

Before choosing the version:

- inspect tags with `git tag --sort=v:refname`
- inspect first-parent history with `git log --first-parent --decorate --oneline`
- keep version numbers increasing in first-parent history order

## Push And Remote Verification

Push only when the user asked or the task explicitly includes remote publication.

When pushing:

1. push `main`
2. push the annotated tag
3. verify the remote accepted both updates
4. monitor the tag-driven `Release` workflow until `./gradlew externalSmokeTest` passes for the tagged image, the GitHub Release is created, and GitHub code scanning still reflects the expected CodeQL posture
5. confirm GHCR published both `ghcr.io/<owner>/<repo>:vMAJOR.MINOR.PATCH[-PRERELEASE]` and `ghcr.io/<owner>/<repo>:sha-<12-char-commit>`
6. confirm the immutable published digest is signed and has provenance attestation
7. confirm the GitHub Release body includes every `CHANGELOG.md` section from the new tag back to, but not including, the previous published GitHub Release tag section, plus the tag image reference, short-SHA image reference, and package link
8. run the manual `Post-Deploy Smoke` workflow against the deployed environment with the release-summary inputs `expected_build_version`, `expected_short_commit_id`, `expected_active_profile=prod`, `expected_session_store_type=jdbc`, and `expected_session_timeout=15m`
9. when the JDBC secret set exists, confirm the same smoke run also proves `GET /api/session`, readable `XSRF-TOKEN` bootstrap, authenticated `PUT /api/account/language`, and persisted authenticated account access; environments without that JDBC access remain HTTP-only by design
10. remove temporary worktrees and branches used only to execute the released plan after confirming their changes are already integrated onto `main`

The `Release` workflow is expected to validate the exact tagged image, derive cumulative release notes from `CHANGELOG.md`, fail closed when the previous published release boundary or changelog section cannot be resolved, and treat the immutable digest rather than mutable tags as the authenticity anchor.

## What Not To Do

- do not record unreleased work as released
- do not cut a release from any branch other than `main`
- do not tag a commit that has not passed the required validation
- do not start release work from an unmerged PR or side branch
- do not refresh the OpenAPI baseline unless the contract change was intentional and reviewed
- do not rewrite release history unless the user explicitly asks for recovery work
