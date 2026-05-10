---
name: diff-review
description: Review repository diffs for bugs, spec drift, missing validation, documentation mismatch, and security risk. Use for Reviewer sidecars, Worker self-review before handoff, or final review of docs, skills, plans, and code changes.
---

# Diff Review

## Overview

Use this skill to review a repository diff with the repo's review priorities.
`.agents/references/reviews.md` remains the authority for review behavior.

## Read Set

- always: `AGENTS.md`, `.agents/references/reviews.md`, and the diff under review
- if docs, ADRs, plans, skills, or AI guidance changed: `.agents/references/documentation.md`
- if validation adequacy is in question: `.agents/references/testing.md`
- if code style or placement matters: `.agents/references/code-style.md` or `.agents/references/architecture.md`
- if security-sensitive behavior changed: the relevant security or workflow owner guide

## Inputs

- diff or commit range
- source request, plan task, or handoff packet
- validation evidence
- known non-goals and out-of-scope files

## Workflow

1. Identify the intended behavior or documentation change.
2. Compare the diff against governing specs, ADRs, plans, docs, and owner guides.
3. Prioritize findings in this order: bugs, spec drift, contract drift, missing validation, security risk, maintainability.
4. Check for hidden scope expansion, unrelated refactors, stale references, and contradictory guidance.
5. For security-sensitive changes, apply the security review triggers from `.agents/references/reviews.md`.
6. Return a gate decision: pass, pass with notes, blocked, or replan required.
7. If no findings exist, say so and name any remaining validation or environment risk.

## Stop Conditions

- the diff cannot be tied to a request, plan, or spec
- validation evidence is missing for a non-lightweight change
- public behavior changed without matching spec or contract artifacts
- security-sensitive behavior changed without an explicit security review pass

## Output

Report findings first with file references when possible, then open questions, validation gaps, gate decision, and residual risk.
