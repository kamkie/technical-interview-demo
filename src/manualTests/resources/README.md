# Manual Regression Harness

This source set hosts the manual-regression harness referenced by
`.agents/plans/PLAN_manual_regression_execution.md`. It runs through the documented public and
admin REST contract against a locally running app, captures every request/response into a per-run
report and execution log, and is **not** wired into the default `./build.ps1 build` pipeline. Invoke
it explicitly when you want to validate a release candidate end-to-end.

## What it covers

Twelve ordered suites, named with a numeric prefix that doubles as their execution order:

| #  | Suite                              | Auth required          |
|----|------------------------------------|------------------------|
| 01 | `public-overview-and-docs`         | none                   |
| 02 | `actuator-and-observability`       | none                   |
| 03 | `public-book-and-category-reads`   | none                   |
| 04 | `public-localization-reads`        | none                   |
| 05 | `anonymous-negative-access`        | none                   |
| 06 | `session-and-account`              | admin                  |
| 07 | `book-lifecycle`                   | admin                  |
| 08 | `category-lifecycle-admin`         | admin                  |
| 09 | `localization-lifecycle-admin`     | admin                  |
| 10 | `admin-user-management`            | admin (regular optional) |
| 11 | `audit-log-review`                 | admin                  |
| 12 | `operator-surface`                 | admin                  |

Suites that require an admin (or regular-user) identity report `BLOCKED` rather than `FAILED`
when the corresponding inputs are missing, so an anonymous-only run remains useful.

## How to run

1. Start the app locally (`./build.ps1 bootRun` or `docker-compose up`).
2. Sign in once via the browser, capture the `technical-interview-demo-session` cookie value and
   the CSRF token returned by `GET /api/session`.
3. Either copy `run.properties.example` to `run.properties` and fill in `adminSessionCookie` /
   `adminCsrfToken`, or pass them as Gradle properties:
   ```powershell
   ./build.ps1 manualTests `
     "-PmanualTests.adminSessionCookie=<cookie>" `
     "-PmanualTests.adminCsrfToken=<csrf>"
   ```
4. The harness prints the report path on completion. Reports default to
   `temp/manual-regression/<runTag>/run-<UTC-timestamp>/`.

Each run directory contains:

- `checklist.md`
- `report.md`
- `report.json`
- `execution-log.ndjson`

`checklist.md` is the suite/test execution checklist. It contains Markdown checkboxes for each
observed suite and JUnit test, with the generated outcome next to each item so the executor can
review or fill in completion while the run evidence stays beside the report.

`execution-log.ndjson` is the authoritative per-exchange debug artifact. It records request and
response headers and bodies, expected and actual status, latency, suite/test context, and redacted
secret-bearing values.

Generate a deterministic synthetic report without a running app:

```powershell
./build.ps1 manualRegressionExampleReport
```

The example output is written under `temp/manual-regression/example/run-<UTC-timestamp>/`.

## Configuration precedence

1. Explicit Gradle property: `-PmanualTests.<key>=<value>`
2. Environment variable: `MANUAL_TESTS_<KEY>` (camelCase → `SCREAMING_SNAKE_CASE`)
3. `src/manualTests/resources/run.properties` (gitignored counterpart of `run.properties.example`)
4. Interactive prompt — only when `System.console()` is non-null and the harness was not launched
   from Gradle. Secrets are read with `Console.readPassword` and never echoed.

## Selecting suites

Pass `-PmanualTests.suites=01-public-overview-and-docs,03-public-book-and-category-reads` to run a
subset; declared prerequisites are still enforced and missing prerequisites surface as `BLOCKED`.

## Safety rails

The harness refuses to run when the `BASE_URL` host is not localhost, a private IP range, or
explicitly listed via `MANUAL_TESTS_ALLOWED_HOSTS`. It also refuses to run when
`SPRING_PROFILES_ACTIVE` contains `prod`.

## Files in this directory

- `run.properties.example` — template for a local `run.properties`.
- `junit-platform.properties` — orders suite classes alphabetically (matches the numeric prefix).

Related IntelliJ HTTP Client material lives outside the runtime resource classpath:

- `src/manualTests/http/examples/` — reviewer-facing runnable request examples.
- `src/manualTests/http/suites/` — semi-automated HTTP Client scripts aligned with the Java
  manual-regression suites.

The Java harness does not parse these files at runtime; they are kept beside the harness so the
manual reviewer flow and executable suites stay aligned.
