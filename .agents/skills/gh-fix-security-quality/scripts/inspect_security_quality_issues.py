#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import subprocess
import sys
from pathlib import Path
from typing import Any, Iterable, Sequence

SEVERITY_ORDER = {
    "critical": 5,
    "high": 4,
    "medium": 3,
    "warning": 2,
    "low": 2,
    "note": 1,
    "unknown": 0,
}


class GhResult:
    def __init__(self, returncode: int, stdout: str, stderr: str):
        self.returncode = returncode
        self.stdout = stdout
        self.stderr = stderr


def run_gh_command(args: Sequence[str], cwd: Path) -> GhResult:
    process = subprocess.run(
        ["gh", *args],
        cwd=cwd,
        text=True,
        capture_output=True,
    )
    return GhResult(process.returncode, process.stdout, process.stderr)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Inspect open GitHub Security and quality issues for the current repository "
            "by fetching code-scanning and Dependabot alerts."
        ),
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument("--repo", default=".", help="Path inside the target Git repository.")
    parser.add_argument(
        "--kind",
        choices=["all", "code-scanning", "dependabot"],
        default="all",
        help="Which alert families to inspect.",
    )
    parser.add_argument(
        "--severity",
        action="append",
        default=[],
        help="Filter by normalized severity. Repeat to include multiple severities.",
    )
    parser.add_argument(
        "--code-scanning-number",
        dest="code_scanning_numbers",
        action="append",
        default=[],
        type=int,
        help="Filter to one or more specific code-scanning alert numbers.",
    )
    parser.add_argument(
        "--dependabot-number",
        dest="dependabot_numbers",
        action="append",
        default=[],
        type=int,
        help="Filter to one or more specific Dependabot alert numbers.",
    )
    parser.add_argument(
        "--limit",
        type=int,
        default=0,
        help="Maximum alerts to emit per kind after filtering. Use 0 for no limit.",
    )
    parser.add_argument("--json", action="store_true", help="Emit JSON instead of text output.")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    repo_root = find_git_root(Path(args.repo))
    if repo_root is None:
        print("Error: not inside a Git repository.", file=sys.stderr)
        return 1

    if not ensure_gh_available(repo_root):
        return 1

    repo_slug = fetch_repo_slug(repo_root)
    if repo_slug is None:
        return 1

    severity_filter = {normalize_severity(severity) for severity in args.severity}

    code_scanning_alerts: list[dict[str, Any]] = []
    dependabot_alerts: list[dict[str, Any]] = []

    if args.kind in ("all", "code-scanning"):
        code_scanning_alerts = fetch_code_scanning_alerts(repo_slug, repo_root)
        if code_scanning_alerts is None:
            return 1
        code_scanning_alerts = filter_alerts(
            code_scanning_alerts,
            severity_filter,
            set(args.code_scanning_numbers),
            args.limit,
        )

    if args.kind in ("all", "dependabot"):
        dependabot_alerts = fetch_dependabot_alerts(repo_slug, repo_root)
        if dependabot_alerts is None:
            return 1
        dependabot_alerts = filter_alerts(
            dependabot_alerts,
            severity_filter,
            set(args.dependabot_numbers),
            args.limit,
        )

    summary = build_summary(repo_slug, code_scanning_alerts, dependabot_alerts)

    if args.json:
        print(json.dumps(summary, indent=2))
    else:
        render_summary(summary)

    return 1 if summary["totals"]["matchingAlerts"] > 0 else 0


def find_git_root(start: Path) -> Path | None:
    result = subprocess.run(
        ["git", "rev-parse", "--show-toplevel"],
        cwd=start,
        text=True,
        capture_output=True,
    )
    if result.returncode != 0:
        return None
    return Path(result.stdout.strip())


def ensure_gh_available(repo_root: Path) -> bool:
    result = run_gh_command(["auth", "status"], cwd=repo_root)
    if result.returncode == 0:
        return True
    message = (result.stderr or result.stdout or "").strip()
    print(message or "Error: gh not authenticated.", file=sys.stderr)
    return False


def fetch_repo_slug(repo_root: Path) -> str | None:
    result = run_gh_command(["repo", "view", "--json", "nameWithOwner"], cwd=repo_root)
    if result.returncode != 0:
        message = (result.stderr or result.stdout or "").strip()
        print(message or "Error: unable to resolve repository name.", file=sys.stderr)
        return None
    try:
        data = json.loads(result.stdout or "{}")
    except json.JSONDecodeError:
        print("Error: unable to parse repository JSON.", file=sys.stderr)
        return None
    name_with_owner = data.get("nameWithOwner")
    if not name_with_owner:
        print("Error: repository nameWithOwner missing from gh repo view output.", file=sys.stderr)
        return None
    return str(name_with_owner)


def fetch_code_scanning_alerts(repo_slug: str, repo_root: Path) -> list[dict[str, Any]] | None:
    raw_alerts = fetch_paginated_api(
        f"/repos/{repo_slug}/code-scanning/alerts?state=open",
        repo_root,
    )
    if raw_alerts is None:
        return None
    return [map_code_scanning_alert(alert) for alert in raw_alerts]


def fetch_dependabot_alerts(repo_slug: str, repo_root: Path) -> list[dict[str, Any]] | None:
    raw_alerts = fetch_paginated_api(
        f"/repos/{repo_slug}/dependabot/alerts?state=open",
        repo_root,
    )
    if raw_alerts is None:
        return None
    return [map_dependabot_alert(alert) for alert in raw_alerts]


def fetch_paginated_api(endpoint: str, repo_root: Path) -> list[dict[str, Any]] | None:
    items: list[dict[str, Any]] = []
    page = 1
    while True:
        separator = "&" if "?" in endpoint else "?"
        result = run_gh_command(
            ["api", f"{endpoint}{separator}per_page=100&page={page}"],
            cwd=repo_root,
        )
        if result.returncode != 0:
            message = (result.stderr or result.stdout or "").strip()
            if page == 1 and "Pagination using the `page` parameter is not supported." in message:
                return fetch_single_page_api(endpoint, repo_root)
            print(message or f"Error: gh api failed for {endpoint}.", file=sys.stderr)
            return None
        try:
            data = json.loads(result.stdout or "[]")
        except json.JSONDecodeError:
            print(f"Error: unable to parse JSON for {endpoint}.", file=sys.stderr)
            return None
        if not isinstance(data, list):
            print(f"Error: unexpected JSON shape for {endpoint}.", file=sys.stderr)
            return None
        items.extend(item for item in data if isinstance(item, dict))
        if len(data) < 100:
            break
        page += 1
    return items


def fetch_single_page_api(endpoint: str, repo_root: Path) -> list[dict[str, Any]] | None:
    result = run_gh_command(["api", endpoint], cwd=repo_root)
    if result.returncode != 0:
        message = (result.stderr or result.stdout or "").strip()
        print(message or f"Error: gh api failed for {endpoint}.", file=sys.stderr)
        return None
    try:
        data = json.loads(result.stdout or "[]")
    except json.JSONDecodeError:
        print(f"Error: unable to parse JSON for {endpoint}.", file=sys.stderr)
        return None
    if not isinstance(data, list):
        print(f"Error: unexpected JSON shape for {endpoint}.", file=sys.stderr)
        return None
    return [item for item in data if isinstance(item, dict)]


def map_code_scanning_alert(alert: dict[str, Any]) -> dict[str, Any]:
    rule = alert.get("rule") or {}
    tool = alert.get("tool") or {}
    instance = alert.get("most_recent_instance") or {}
    location = instance.get("location") or {}
    severity = first_non_blank(
        rule.get("security_severity_level"),
        rule.get("severity"),
        "unknown",
    )
    return {
        "kind": "code-scanning",
        "number": alert.get("number"),
        "severity": normalize_severity(severity),
        "displaySeverity": severity,
        "ruleId": rule.get("id") or "",
        "ruleName": rule.get("name") or "",
        "tool": tool.get("name") or "",
        "message": ((instance.get("message") or {}).get("text") or "").strip(),
        "path": location.get("path") or "",
        "startLine": location.get("start_line"),
        "endLine": location.get("end_line"),
        "htmlUrl": alert.get("html_url") or "",
        "category": instance.get("category") or "",
        "ref": instance.get("ref") or "",
        "tags": rule.get("tags") or [],
        "recommendation": first_help_line(rule.get("help") or ""),
    }


def map_dependabot_alert(alert: dict[str, Any]) -> dict[str, Any]:
    dependency = alert.get("dependency") or {}
    package = dependency.get("package") or {}
    advisory = alert.get("security_advisory") or {}
    vulnerability = alert.get("security_vulnerability") or {}
    severity = first_non_blank(
        vulnerability.get("severity"),
        advisory.get("severity"),
        "unknown",
    )
    first_patched_version = (vulnerability.get("first_patched_version") or {}).get("identifier") or ""
    return {
        "kind": "dependabot",
        "number": alert.get("number"),
        "severity": normalize_severity(severity),
        "displaySeverity": severity,
        "packageName": package.get("name") or "",
        "ecosystem": package.get("ecosystem") or "",
        "manifestPath": dependency.get("manifest_path") or "",
        "relationship": dependency.get("relationship") or "",
        "scope": dependency.get("scope") or "",
        "summary": advisory.get("summary") or "",
        "ghsaId": advisory.get("ghsa_id") or "",
        "cveId": advisory.get("cve_id") or "",
        "vulnerableVersionRange": vulnerability.get("vulnerable_version_range") or "",
        "firstPatchedVersion": first_patched_version,
        "htmlUrl": alert.get("html_url") or "",
    }


def filter_alerts(
    alerts: list[dict[str, Any]],
    severities: set[str],
    numbers: set[int],
    limit: int,
) -> list[dict[str, Any]]:
    filtered = alerts
    if severities:
        filtered = [alert for alert in filtered if alert.get("severity") in severities]
    if numbers:
        filtered = [alert for alert in filtered if alert.get("number") in numbers]
    filtered.sort(key=alert_sort_key)
    if limit > 0:
        filtered = filtered[:limit]
    return filtered


def alert_sort_key(alert: dict[str, Any]) -> tuple[int, int, str]:
    severity = normalize_severity(alert.get("severity"))
    return (
        -(SEVERITY_ORDER.get(severity, 0)),
        -(int(alert.get("number") or 0)),
        str(alert.get("kind") or ""),
    )


def build_summary(
    repo_slug: str,
    code_scanning_alerts: list[dict[str, Any]],
    dependabot_alerts: list[dict[str, Any]],
) -> dict[str, Any]:
    return {
        "repo": repo_slug,
        "totals": {
            "codeScanning": len(code_scanning_alerts),
            "dependabot": len(dependabot_alerts),
            "matchingAlerts": len(code_scanning_alerts) + len(dependabot_alerts),
        },
        "codeScanning": {
            "byRule": group_counts(code_scanning_alerts, "ruleId"),
            "alerts": code_scanning_alerts,
        },
        "dependabot": {
            "byPackage": group_counts(dependabot_alerts, "packageName"),
            "alerts": dependabot_alerts,
        },
    }


def group_counts(alerts: list[dict[str, Any]], field_name: str) -> list[dict[str, Any]]:
    counts: dict[str, int] = {}
    for alert in alerts:
        value = str(alert.get(field_name) or "unknown")
        counts[value] = counts.get(value, 0) + 1
    return [
        {"value": value, "count": count}
        for value, count in sorted(counts.items(), key=lambda item: (-item[1], item[0]))
    ]


def render_summary(summary: dict[str, Any]) -> None:
    print(f"Repo: {summary['repo']}")
    print(f"Matching security and quality issues: {summary['totals']['matchingAlerts']}")
    print(
        "Code scanning: "
        f"{summary['totals']['codeScanning']} open, Dependabot: {summary['totals']['dependabot']} open"
    )

    code_scanning_rules = summary["codeScanning"]["byRule"]
    if code_scanning_rules:
        print()
        print("Code scanning rules:")
        for item in code_scanning_rules:
            print(f"- {item['value']}: {item['count']}")

    dependabot_packages = summary["dependabot"]["byPackage"]
    if dependabot_packages:
        print()
        print("Dependabot packages:")
        for item in dependabot_packages:
            print(f"- {item['value']}: {item['count']}")

    if summary["codeScanning"]["alerts"]:
        print()
        print("Code scanning alerts:")
        render_code_scanning_alerts(summary["codeScanning"]["alerts"])

    if summary["dependabot"]["alerts"]:
        print()
        print("Dependabot alerts:")
        render_dependabot_alerts(summary["dependabot"]["alerts"])

    if summary["totals"]["matchingAlerts"] == 0:
        print()
        print("No matching security or quality issues detected.")


def render_code_scanning_alerts(alerts: Iterable[dict[str, Any]]) -> None:
    for alert in alerts:
        print("-" * 60)
        line_suffix = f":{alert['startLine']}" if alert.get("startLine") else ""
        print(
            f"#{alert.get('number')} {alert.get('ruleId')} "
            f"[{alert.get('displaySeverity')}] {alert.get('path')}{line_suffix}"
        )
        if alert.get("message"):
            print(f"Message: {alert['message']}")
        if alert.get("tool"):
            print(f"Tool: {alert['tool']}")
        if alert.get("recommendation"):
            print(f"Recommendation: {alert['recommendation']}")
        if alert.get("htmlUrl"):
            print(f"URL: {alert['htmlUrl']}")


def render_dependabot_alerts(alerts: Iterable[dict[str, Any]]) -> None:
    for alert in alerts:
        print("-" * 60)
        print(
            f"#{alert.get('number')} {alert.get('packageName')} "
            f"[{alert.get('displaySeverity')}]"
        )
        if alert.get("summary"):
            print(f"Summary: {alert['summary']}")
        manifest_bits = [bit for bit in [alert.get("manifestPath"), alert.get("relationship")] if bit]
        if manifest_bits:
            print(f"Manifest: {' | '.join(manifest_bits)}")
        if alert.get("vulnerableVersionRange"):
            print(f"Vulnerable: {alert['vulnerableVersionRange']}")
        if alert.get("firstPatchedVersion"):
            print(f"Patched: {alert['firstPatchedVersion']}")
        advisory_bits = [bit for bit in [alert.get("ghsaId"), alert.get("cveId")] if bit]
        if advisory_bits:
            print(f"Advisories: {', '.join(advisory_bits)}")
        if alert.get("htmlUrl"):
            print(f"URL: {alert['htmlUrl']}")


def first_non_blank(*values: Any) -> str:
    for value in values:
        if value is None:
            continue
        text = str(value).strip()
        if text:
            return text
    return ""


def normalize_severity(value: Any) -> str:
    return first_non_blank(value, "unknown").strip().lower()


def first_help_line(help_text: str) -> str:
    for line in help_text.splitlines():
        stripped = line.strip()
        if not stripped or stripped.startswith("#"):
            continue
        return stripped
    return ""


if __name__ == "__main__":
    raise SystemExit(main())
