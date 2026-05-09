# References Rules For AI Agents

`.agents/references/references-rules.md` owns the current standing rules for creating, editing, compacting, moving, and retiring markdown instruction documents under `.agents/references/`.
Use this file as the rule set that other `.agents/references/*.md` files are measured against; do not use it as a changelog, incident report, or record of edits already made.

This file makes the implicit and explicit rules for `.agents/references/*.md` documents explicit.
It does not own repository artifact routing; use `.agents/references/documentation.md` to decide which artifact owns a topic before editing multiple docs.
It does not replace file-specific ownership; each reference document still owns its named domain.

Entry points:

- changing any `.agents/references/*.md` file: read `## Core Rules`
- adding, moving, renaming, or retiring a reference document: also read `## Reference Lifecycle`
- resolving overlap between reference documents: also read `## Overlap And Compaction`

## Core Rules

- keep the role of each reference document distinct; do not collapse architecture, code style, design, documentation ownership, execution, planning, release workflow, review guidance, testing guidance, workflow guidance, and learnings into one document
- give each reference document a clear opening ownership statement that names the file and the guidance it owns
- keep this file expressed as the current reference-document rule set; replace outdated expectations with current expectations instead of appending notes about past changes
- write AI-guidance changes as current-state rules, not incident history or narrative reports
- update the relevant `.agents/references/` file in the same change when architecture, code-style expectations, design intent, documentation ownership, durable engineering guidance, release workflow, review/security review guidance, testing/validation guidance, workflow guidance, planning guidance, or plan-execution guidance materially changes
- keep standing rules in their focused owning files instead of redistributing them across task starters, plans, templates, skills, worker logs, or final responses
- keep setup walkthroughs and troubleshooting details out of reference documents unless the reference file specifically owns AI-facing setup or troubleshooting guidance
- treat reference documents as AI planning and execution aids, not higher-priority truth than executable specs, published contract docs, or the human-facing artifact that owns the topic
- treat cross-references in owner guides as conditional pointers, not recursive load requirements; a loaded guide is terminal unless the current task matches another guide's explicit entry condition
- use `.agents/references/documentation.md` first when ownership or artifact routing is unclear

## Placement And Naming

- keep AI instruction markdown files under `.agents/references/` by default; `AGENTS.md` is the only standing top-level exception
- prefer lower-kebab-case names for new reference files unless preserving a deliberate existing name such as `.agents/references/LEARNINGS.md`
- keep current detailed examples and templates in `.agents/templates/` or focused reference files instead of expanding standing top-level AI files
- keep retired report-like AI analysis artifacts under `.agents/archive/`, not `.agents/references/`
- keep active execution plans under `.agents/plans/`; reference documents must not become task-specific progress logs
- put durable repo-wide lessons in `.agents/references/LEARNINGS.md` only when they should survive refactors

## Editing Rules

- load only the reference document, owner guide, and source artifacts needed for the current change
- avoid bulk-loading `.agents/references/` as a standing pre-flight step
- when a request introduces or changes requirements for AI documents, update this guide in the same change by changing the standing rule future reference documents must satisfy, not by recording that the request happened or listing files changed; keep domain-specific rules in their owning guides
- update cross-references in the same change when moving, renaming, splitting, or consolidating reference guidance
- update `AGENTS.md` only when repo-level AI rules, owner-guide entry points, or document ownership changes
- when a repo-local skill wraps a workflow owned by a reference document, update the skill and the owning reference document together if that workflow changes
- if an interrupted tool or IDE run leaves a reference document incomplete, finish it or clearly mark the gaps before handoff
- validate documentation-only reference changes with manual consistency review, `git diff --check`, and the standard validation expected by `.agents/references/testing.md`

## Overlap And Compaction

- if reference documents accumulate overlap, move duplicated guidance into the single best owning file and update cross-references in the same change
- use links or brief pointers instead of copying standing rules into multiple reference documents
- keep `.agents/references/documentation.md` focused on artifact routing and cross-file alignment, not detailed rules for how reference documents are maintained
- keep this file focused on rules that govern `.agents/references/*.md`; do not move domain-specific execution, planning, testing, review, release, workflow, architecture, design, code-style, or learning rules here
- when a rule seems to fit multiple reference documents, choose the file that future agents are already required to load for that decision

## Reference Lifecycle

- add a new reference document only when durable guidance needs a distinct owner or an existing owner would become confusing
- before adding a new reference document, check `.agents/references/documentation.md` for artifact ownership and search existing reference files for an appropriate owner
- when adding a new reference document, update `.agents/references/documentation.md` artifact ownership and `AGENTS.md` owner-guide entry points when the new file changes repo-level AI document ownership
- when retiring a reference document, move any still-current rules to the best owning reference first, then move historical or report-like residue to `.agents/archive/`
- when renaming or moving a reference document, update `AGENTS.md` and other `.agents/` cross-references in the same change
