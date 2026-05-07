# Semi-Automated Manual Regression Via IntelliJ HTTP Client

This directory contains `.http` scripts that cover the same requests as the Java manual regression suites found in `src/manualTests/java/.../suites/`.

## Prerequisites

1.  **IntelliJ IDEA** (with HTTP Client plugin enabled).
2.  **Environment Configuration**: Ensure `src/manualTests/http/suites/http-client.private.env.json` contains a valid `baseUrl` and `sessionCookie`.
    ```json
    {
      "dev": {
        "baseUrl": "http://localhost:8080",
        "sessionCookie": "your-session-cookie-here"
      }
    }
    ```
    Do not provide a `csrfToken`; authenticated suites capture the `XSRF-TOKEN` cookie during setup.
3.  **Active Application**: The application should be running, usually on `http://localhost:8080`.
4.  **CLI Java Runtime**: `ijhttp 2025.3` needs a Java 21 runtime on `PATH` or through `JAVA_HOME`.

## Running the Suites

1.  Open any `src/manualTests/http/suites/suite-XX-*.http` file.
2.  Select the `dev` environment in the top-right corner of the editor.
3.  Click **"Run all requests in file"** (the double green arrow).
4.  The script will execute all steps, capturing identifiers like `firstBookId` into global variables for downstream use.

## Producing Reports

Each script builds a markdown report.

### Option A: Automatic Redirection (Suite 01+)

`suite-01` (and other updated suites) automatically redirect their report to a file using the `>>!` operator and a `/debug/echo` reflecting endpoint.

1.  Run the suite in the IDE or via CLI.
2.  Check `src/manualTests/http/reports/` for the generated `.md` file.

### Option B: Manual Export (IntelliJ)

1.  After running the suite, open the **Services** tool window.
2.  Find the last request named **"Finalize Report"**.
3.  The report content will be visible in the **Response** -> **Console** tab through `client.log`.

### Option C: Command Line (ijhttp)

If you have the `ijhttp` CLI installed, you can run a single suite or all of them at once. Pass the private environment file explicitly when running from the repository root.

**Run a single suite:**
```powershell
ijhttp -e dev -p src/manualTests/http/suites/http-client.private.env.json src/manualTests/http/suites/suite-01-public-overview-and-docs.http
```

**Run all suites in order:**
```powershell
$files = Get-ChildItem src/manualTests/http/suites/suite-*.http | Sort-Object Name | ForEach-Object { $_.FullName }
ijhttp -e dev -p src/manualTests/http/suites/http-client.private.env.json @files
```

**Generate XML report (for CI):**
```powershell
$files = Get-ChildItem src/manualTests/http/suites/suite-*.http | Sort-Object Name | ForEach-Object { $_.FullName }
ijhttp -e dev -p src/manualTests/http/suites/http-client.private.env.json --report @files
```
The XML report will be saved to `reports/report.xml` relative to the execution directory.

### Option D: Docker (No Installation)

If you don't have `ijhttp` installed, use the official Docker image:

```powershell
$files = Get-ChildItem src/manualTests/http/suites/suite-*.http | Sort-Object Name | ForEach-Object {
    $_.FullName.Replace((Get-Location).Path + "\", "").Replace("\", "/")
}
docker run --rm -it -v ${PWD}:/workdir -w /workdir jetbrains/intellij-http-client -e dev -p src/manualTests/http/suites/http-client.private.env.json $files
```

## Script Authoring Notes

- File-level `@...` values are fallback defaults for partial runs. Response handlers should read them through `client.variables.file`.
- Values captured in response handlers should be stored with `client.global.set(...)`.
- Before using a captured value in a request URL, header, body, or `>>!` redirection, copy it into a request variable in a pre-request handler and reference that unique name, such as `{{requestCsrfToken}}` or `{{reportFile}}`.
- Use `client.variables.environment.get("baseUrl") || client.global.get("baseUrl") || client.variables.file.get("baseUrl")` when writing report metadata.
- For CSRF, setup requests capture `XSRF-TOKEN` from `Set-Cookie`; unsafe requests should use the captured token via `requestCsrfToken`.

## Suite Dependencies

The suites are designed to be run in order (01 through 12). Some suites rely on global variables captured by previous suites:

- Suite 11 uses `runTag` from earlier lifecycle suites when it is available.

If you run them out of order, some tests may be skipped or fail.
