# Release All Merged Work

Category: Releasing
Slug: `release-all-merged-work`
Placeholders: none

Verify and release all merged but unreleased work currently on `main`.

Use `.agents/references/releases.md`.
Include every merged PR and executed `.agents/plans/PLAN_*.md` file that belongs to the unreleased change set.
Stop on readiness blockers instead of publishing a partial release.
If the merged work is ready, prepare the release commit, archive every included executed plan under `.agents/archive/`, create the annotated tag, push the release commit and tag, and verify remote publication.
Summarize exactly which merged PRs and executed plan files were included.
