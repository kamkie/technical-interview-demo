# Code Style Guide For AI Agents

`.agents/references/code-style.md` owns how repository edits should be shaped.

Use this file when the task changes source code, tests, Gradle/build logic, workflow YAML, or other repo-owned implementation files.
Use `docs/ARCHITECTURE.md` for placement and ownership. This file is about edit shape and repo-local coding conventions, not behavior authority.

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
- keep code compatible with Error Prone and the curated PMD ruleset
- log successful operations that change database state

## Editing Discipline

- implement the smallest coherent change that satisfies the governing spec
- keep renames, moves, and cleanup narrow enough that the behavioral change stays obvious
- do not broaden a small fix into cross-repo cleanup unless the repetition is already real
- do not change public contract behavior accidentally while cleaning up internals
- when build or workflow files change, prefer existing Gradle tasks and workflows over parallel scripts
- treat Palantir Java Format as the Gradle-owned Java formatter; IntelliJ alignment comes from the Palantir formatter plugin plus committed project code-style settings for imports and non-Java options
- use `./build.ps1 format` for repository-wide formatting and `./build.ps1 checkFormat` for formatter verification instead of hand-formatting source files
- keep `.editorconfig` to portable editor defaults; put IntelliJ-specific project style in `.idea/codeStyles/`
- keep REST Docs AsciiDoc sources formatter-managed; write nested lists with explicit marker depth (`*`, `**`) because IntelliJ's AsciiDoc formatter may remove indentation and flatten same-marker nesting
- preserve intentional `.properties` blank-line separators with the checked-in IntelliJ EditorConfig key instead of excluding project properties files from IDE formatting
- keep YAML flow mappings and sequences compact as `{}` and `[]`; `.editorconfig` disables IntelliJ spaces within YAML braces and brackets
- **IntelliJ HTTP Client:** Include an empty line before response handler script blocks (`> {%`) and request bodies to ensure correct parsing.
- Flyway migration SQL under `src/main/resources/db/migration/` hand-formatted and out of IntelliJ reformatting; Spotless may still trim trailing whitespace and enforce final newlines

## Naming And Placement

- prefer names that describe business meaning or technical role directly
- keep new types close to the feature or technical slice that owns them
- avoid generic `helper`, `util`, `base`, or `shared` layers unless a repeated problem clearly justifies them
- if placement or boundary questions appear, resolve them through `docs/ARCHITECTURE.md` instead of encoding architecture rules here

## Cross-References

- use `.agents/references/testing.md` for validation scope
- use `.agents/references/reviews.md` for final bug-risk and security review
- use `.agents/references/documentation.md` when the change also moves docs, examples, AI guidance, roadmap entries, or release history
