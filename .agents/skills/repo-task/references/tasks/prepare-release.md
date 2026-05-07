# Prepare Release

Category: Preparing Release
Slug: `prepare-release`
Placeholders: none

Prepare the release candidate for all merged but unreleased work currently on `main`.

Follow `.agents/references/releases.md` and `.agents/references/documentation.md`.
Only proceed if the approved implementation PR is already merged onto `main`.
Include every merged and executed `.agents/plans/PLAN_*.md` file that belongs to the unreleased change set.
If any included work is not release-ready, stop and list blockers first instead of preparing a partial release.
If the merged work is ready, prepare the release commit, archive every included executed plan under `.agents/archive/`, create the annotated tag locally, and summarize exactly which merged PRs and executed plan files were included.
Do not push unless I ask.
