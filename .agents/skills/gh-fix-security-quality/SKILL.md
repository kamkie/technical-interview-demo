---
name: gh-fix-security-quality
description: Inspect open GitHub Security and quality issues for this repository, summarize actionable code-scanning and Dependabot alerts, then either draft a repo-local fix plan or implement after approval. Use when a user asks to review or fix issues surfaced on https://github.com/kamkie/technical-interview-demo/security.
metadata:
  short-description: Fix GitHub security and quality issues
---

# Gh Fix Security And Quality

## Overview

Use `gh` to inspect the repository's open GitHub Security tab issues, summarize the actionable alerts, and move toward a fix without skipping the repository's normal planning and validation flow.
Keep the skill narrow: it is for GitHub code-scanning and Dependabot alert triage and follow-up, not for replacing the standing rules in `AGENTS.md`, `.agents/references/planning.md`, `.agents/references/testing.md`, or `.agents/references/reviews.md`.

By default this skill targets the current repository and the issues visible from `https://github.com/kamkie/technical-interview-demo/security`.

Prereq: ensure `gh` is installed and authenticated for the target repo host, then run `gh auth status` in the repository before trying to inspect alerts. If the security endpoints return permission errors, refresh the token so it can read security events for the repo.

## Inputs

- `repo`: path inside the repo (default `.`)
- optional filters such as alert kind, severity, or specific alert numbers
- `gh` authentication for the repo host with access to security alerts

## Read Set

- always: `AGENTS.md`, `.agents/references/testing.md`, and `.agents/references/reviews.md`
- if the fix is non-trivial or the user asks for a formal plan: `.agents/references/planning.md`, `WORKING_WITH_AI.md`, and `.agents/skills/repo-plan-author/SKILL.md`
- if the alert implies contract or documentation drift: `.agents/references/documentation.md` plus the affected contract artifacts

## Quick Start

- `python "<path-to-skill>/scripts/inspect_security_quality_issues.py" --repo "."`
- Add `--kind code-scanning` or `--kind dependabot` to narrow the result set.
- Add `--severity high --severity medium` to focus on higher-severity alerts.
- Add `--json` if you want machine-friendly output for summarization.

## Workflow

1. Verify `gh` authentication.
   - Run `gh auth status` in the repo after `gh auth login`.
   - If unauthenticated or under-scoped, ask the user to fix authentication before proceeding.
2. Inspect open GitHub Security and quality issues.
   - Preferred: run the bundled script:
     - `python "<path-to-skill>/scripts/inspect_security_quality_issues.py" --repo "."`
     - Add filters such as `--kind`, `--severity`, `--code-scanning-number`, or `--dependabot-number` when the user already chose a target alert.
   - Manual fallback:
     - `gh api "/repos/<owner>/<repo>/code-scanning/alerts?state=open&per_page=100"`
     - `gh api "/repos/<owner>/<repo>/dependabot/alerts?state=open&per_page=100"`
3. Summarize alerts for the user.
   - For code-scanning alerts, report the alert number, rule id, severity, file and line, message, and URL.
   - For Dependabot alerts, report the alert number, package, severity, vulnerable version range, first patched version, manifest path, and URL.
   - Call out repeated alert families, such as multiple `actions/unpinned-tag` findings, so the user can approve a batch fix consciously.
4. Decide planning depth.
   - For a small isolated fix, provide a concise implementation plan in the conversation and request approval.
   - For a multi-step or higher-risk fix, use `repo-plan-author` to create or revise an `.agents/plans/PLAN_*.md` plan before implementation.
5. Implement after approval.
   - Apply the approved fix or approved plan.
   - Do not dismiss alerts through GitHub APIs unless the user explicitly asks for that action.
   - When fixing workflow action pinning alerts, pin actions to verified full commit SHAs rather than swapping one floating tag for another.
   - Use `.agents/references/testing.md` to choose the minimum required validation.
   - Summarize the diff, validation, and any remaining risk.
6. Recheck status.
   - After changes, rerun the bundled script or the relevant `gh api` calls to confirm the targeted alerts have cleared.

## Bundled Resources

### scripts/inspect_security_quality_issues.py

Fetch open code-scanning and Dependabot alerts, summarize the security and quality issues currently reported by GitHub for this repository, and exit non-zero when matching alerts remain so it can be used in automation.

Usage examples:
- `python "<path-to-skill>/scripts/inspect_security_quality_issues.py" --repo "."`
- `python "<path-to-skill>/scripts/inspect_security_quality_issues.py" --repo "." --kind code-scanning --severity medium`
- `python "<path-to-skill>/scripts/inspect_security_quality_issues.py" --repo "." --dependabot-number 4 --json`
