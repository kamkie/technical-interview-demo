# Code Style Guide For AI Agents

`ai/CODE_STYLE.md` owns how repository edits should be shaped.

Use this file when the task changes source code, tests, Gradle/build logic, workflow YAML, or other repo-owned implementation files.
Use `ai/ARCHITECTURE.md` and `ai/BUSINESS_MODULES.md` for placement and ownership. This file is about edit shape and repo-local coding conventions, not behavior authority.

## Change-Shaping Goals

Keep changes:

- small enough to review quickly
- direct enough to preserve the demo character of the project
- aligned with the governing spec instead of inventing extra behavior
- easy to trace for a maintainer or interview reviewer

## Repo Conventions

- keep code under `team.jit.technicalinterviewdemo`
- prefer existing feature-local types and helpers over new shared abstraction layers
- keep REST responses JSON-friendly and repo-owned
- when returning `ResponseEntity`, assign the payload to a local variable first
- use Lombok only when it removes routine boilerplate without hiding behavior
- use Palantir Java Format through Spotless for Java source formatting
- keep code compatible with Error Prone and the curated PMD ruleset
- log successful operations that change database state

## Editing Discipline

- implement the smallest coherent change that satisfies the governing spec
- keep renames, moves, and cleanup narrow enough that the behavioral change stays obvious
- do not broaden a small fix into cross-repo cleanup unless the repetition is already real
- do not change public contract behavior accidentally while cleaning up internals
- when build or workflow files change, prefer existing Gradle tasks and workflows over parallel scripts
- use `./build.ps1 spotlessApply` for repository formatting instead of IDE-specific formatter commands

## Naming And Placement

- prefer names that describe business meaning or technical role directly
- keep new types close to the feature or technical slice that owns them
- avoid generic `helper`, `util`, `base`, or `shared` layers unless a repeated problem clearly justifies them
- if placement or boundary questions appear, resolve them through `ai/ARCHITECTURE.md` and `ai/BUSINESS_MODULES.md` instead of encoding architecture rules here

## Cross-References

- use `ai/TESTING.md` for validation scope
- use `ai/REVIEWS.md` for final bug-risk and security review
- use `ai/DOCUMENTATION.md` when the change also moves docs, examples, AI guidance, roadmap entries, or release history
