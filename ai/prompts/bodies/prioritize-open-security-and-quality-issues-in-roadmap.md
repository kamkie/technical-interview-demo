# Prioritize Open Security And Quality Issues In Roadmap

Category: Discovery And Roadmap

## Prompt Body

```markdown
Use the repo-local skill `gh-fix-security-quality` to inspect all open GitHub Security and quality issues for this repository, then update `ROADMAP.md` to capture and prioritize them.

Read `AGENTS.md`, `ROADMAP.md`, `ai/PLAN.md`, and `ai/skills/gh-fix-security-quality/SKILL.md` first.
Use the skill to inspect every open code-scanning and Dependabot alert, summarize the actionable issue families, and turn them into concrete roadmap entries.
Prioritize the roadmap by severity, exploitability, release risk, and batching efficiency.
Group repeated alert families into one roadmap batch when that is the better execution unit than one item per alert.
Say explicitly which alerts were grouped together, which stayed separate, and why.
Do not create an execution plan or implement fixes unless I ask.
```
