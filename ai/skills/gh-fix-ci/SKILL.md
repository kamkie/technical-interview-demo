---
name: gh-fix-ci
description: Inspect GitHub PR checks with gh, pull failing GitHub Actions logs, summarize failure context, then either draft a repo-local fix plan or implement after approval. Use when a user asks to debug or fix failing PR CI/CD checks on GitHub Actions for this repository; for external checks (e.g., Buildkite), only report the details URL and mark them out of scope.
metadata:
  short-description: Fix failing GitHub CI actions
---

# Gh Fix CI

## Overview

Use `gh` to locate failing PR checks, fetch GitHub Actions logs for actionable failures, summarize the failure snippet, and move toward a fix without skipping the repository's normal planning and validation flow.
Keep the skill narrow: it is for GitHub Actions PR-check triage and follow-up, not for replacing the standing rules in `AGENTS.md`, `ai/PLANNING.md`, `ai/TESTING.md`, or `ai/REVIEWS.md`.

Prereq: ensure `gh` is installed and authenticated for the target repo host, then run `gh auth status` in the repository before trying to inspect PR checks.

## Inputs

- `repo`: path inside the repo (default `.`)
- `pr`: PR number or URL (optional; defaults to current branch PR)
- `gh` authentication for the repo host

## Read Set

- always: `AGENTS.md`, `ai/TESTING.md`, and `ai/REVIEWS.md`
- if the fix is non-trivial or the user asks for a formal plan: `ai/PLANNING.md`, `WORKING_WITH_AI.md`, and `ai/skills/repo-plan-author/SKILL.md`
- if the failure implies contract or documentation drift: `ai/DOCUMENTATION.md` plus the affected contract artifacts

## Quick start

- `python "<path-to-skill>/scripts/inspect_pr_checks.py" --repo "." --pr "<number-or-url>"`
- Add `--json` if you want machine-friendly output for summarization.

## Workflow

1. Verify gh authentication.
   - Run `gh auth status` in the repo after `gh auth login`.
   - If unauthenticated, ask the user to log in before proceeding.
2. Resolve the PR.
   - Prefer the current branch PR: `gh pr view --json number,url`.
   - If the user provides a PR number or URL, use that directly.
3. Inspect failing checks (GitHub Actions only).
   - Preferred: run the bundled script (handles gh field drift and job-log fallbacks):
     - `python "<path-to-skill>/scripts/inspect_pr_checks.py" --repo "." --pr "<number-or-url>"`
     - Add `--json` for machine-friendly output.
   - Manual fallback:
     - `gh pr checks <pr> --json name,state,bucket,link,startedAt,completedAt,workflow`
       - If a field is rejected, rerun with the available fields reported by `gh`.
     - For each failing check, extract the run id from `detailsUrl` and run:
       - `gh run view <run_id> --json name,workflowName,conclusion,status,url,event,headBranch,headSha`
       - `gh run view <run_id> --log`
     - If the run log says it is still in progress, fetch job logs directly:
       - `gh api "/repos/<owner>/<repo>/actions/jobs/<job_id>/logs" > "<path>"`
4. Scope non-GitHub Actions checks.
   - If `detailsUrl` is not a GitHub Actions run, label it as external and only report the URL.
   - Do not attempt Buildkite or other providers; keep the workflow lean.
5. Summarize failures for the user.
   - Provide the failing check name, run URL (if any), and a concise log snippet.
   - Call out missing logs explicitly.
6. Decide planning depth.
   - For a small isolated fix, provide a concise implementation plan in the conversation and request approval.
   - For a multi-step or higher-risk fix, use `repo-plan-author` to create or revise an `ai/plans/active/PLAN_*.md` plan before implementation.
7. Implement after approval.
   - Apply the approved fix or approved plan.
   - Use `ai/TESTING.md` to choose the minimum required validation.
   - Summarize the diff, validation, and any remaining risk.
8. Recheck status.
   - After changes, suggest re-running the relevant tests and `gh pr checks` to confirm.

## Bundled Resources

### scripts/inspect_pr_checks.py

Fetch failing PR checks, pull GitHub Actions logs, and extract a failure snippet. Exits non-zero when failures remain so it can be used in automation.

Usage examples:
- `python "<path-to-skill>/scripts/inspect_pr_checks.py" --repo "." --pr "123"`
- `python "<path-to-skill>/scripts/inspect_pr_checks.py" --repo "." --pr "https://github.com/org/repo/pull/123" --json`
- `python "<path-to-skill>/scripts/inspect_pr_checks.py" --repo "." --max-lines 200 --context 40`
