# Review Guide For AI Agents

`ai/REVIEWS.md` owns standing AI guidance for self-review, code review, and security review in this repository.

Use this file before finalizing a change, when the user asks for a review, or when a change touches security-sensitive behavior that needs an explicit review activity.

## Review Priorities

Default review order:

1. bugs or behavioral regressions
2. **Spec-Driven Development Verification**: Confirm that spec/contract artifacts (OpenAPI, tests, docs) were updated *before* or *alongside* the implementation, and that they define the intended behavior accurately.
3. contract drift or missing spec-artifact updates
4. missing validation or weak validation scope
5. security regressions
6. maintainability issues that materially affect the change

Do not lead with style-only comments when there are correctness, contract, or security risks still open.

## Code Review Activity

Look for:

- mismatches between the requested behavior and the governing specs
- regressions in existing endpoint behavior, docs, or examples
- missing or mis-scoped tests
- hidden scope expansion beyond the plan or user request
- changes that make the demo harder to understand without solving a real problem

## Security Review Activity

Apply an explicit security pass when the change touches:

- authentication or authorization behavior
- session handling, secrets, or externally supplied credentials
- logging of sensitive values
- CI, release, workflow permissions, or container publication behavior
- deployment-facing configuration or externally exposed endpoints

Check for:

- missing auth or role checks
- accidental leakage of secrets or sensitive request data
- overly broad workflow permissions or publish paths
- changes that weaken documented runtime posture without corresponding spec or doc updates

## Documentation And Process Review

For documentation-heavy changes, review for:

- contradictory instructions across README, CONTRIBUTING, AGENTS, and `ai/` docs
- task sections growing into policy dumps instead of linking to owning guidance
- release, PR, and validation instructions drifting away from the checked-in automation

## Reporting Expectations

When the user asked for a review:

- list findings first, ordered by severity
- include exact file references when possible
- keep summaries brief and secondary to the findings
- say explicitly when no findings were discovered

## Cross-References

- use `ai/TESTING.md` to confirm whether validation scope is sufficient
- use `ai/DOCUMENTATION.md` when the review question is mainly about doc ownership, cross-reference cleanup, or maintainer guidance alignment
