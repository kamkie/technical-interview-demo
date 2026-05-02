# Code Style Guide For AI Agents

`ai/CODE_STYLE.md` owns standing AI guidance for shaping code and build-file edits in this repository.

Use this file when the task changes source code, tests, Gradle/build logic, workflow YAML, or other repo-owned implementation files.
Do not treat this file as a behavior spec. Contract truth still lives in the executable and published spec artifacts named in `AGENTS.md`.

## Change-Shaping Goals

Keep code changes:

- small enough to review quickly
- direct enough to preserve the demo nature of the project
- aligned with the governing spec instead of inventing extra behavior
- easy to reason about for a future maintainer or interview reviewer

## Preferred Style In This Repo

- preserve the demo nature of the project; prefer direct code over reusable frameworks built inside the repo
- keep package names under `team.jit.technicalinterviewdemo`
- keep non-trivial business rules in `@Service` beans
- keep Spring MVC controllers thin and focused on HTTP translation
- prefer Spring Data repositories for persistence access instead of adding extra repository abstraction layers
- keep REST responses JSON-friendly
- when returning `ResponseEntity`, assign the payload to a local variable first
- use Lombok when it clearly removes routine boilerplate without obscuring behavior
- keep code compatible with Error Prone and the curated PMD ruleset
- log successful operations that change database state

## Editing Discipline

- implement the smallest coherent change that satisfies the updated spec
- keep related renames, moves, and cleanup narrow enough that the behavioral change stays obvious
- prefer feature-local changes over cross-repo abstraction unless the repetition is already real
- do not change public contract behavior accidentally while cleaning up internals
- avoid opportunistic infrastructure churn when a direct code change is enough

## Naming And Structure

- prefer names that describe business meaning or technical role directly
- keep new types close to the feature or technical slice that owns them
- avoid generic helper, util, base, or shared layers unless a repeated problem clearly justifies them
- prefer explicit flow over hidden indirection or lifecycle magic

## When Build Or Workflow Files Change

- keep the automation path aligned with the repository docs and spec expectations
- avoid adding parallel scripts when an existing Gradle task or workflow already owns the behavior
- keep CI or release changes narrow and easy to audit

## Cross-References

- use `ai/TESTING.md` for test-layer and validation guidance
- use `ai/REVIEWS.md` for bug-risk and security-review lenses before finalizing a change
- use `ai/DOCUMENTATION.md` when the task also changes README, contributing guidance, setup guidance, AI docs, REST Docs, HTTP examples, or release history
