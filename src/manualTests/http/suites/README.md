# Semi-Automated Manual Regression Via IntelliJ HTTP Client

This directory contains `.http` scripts that cover the same requests as the Java manual regression suites found in `src/manualTests/java/.../suites/`.

## Prerequisites

1.  **IntelliJ IDEA** (with HTTP Client plugin enabled).
2.  **Environment Configuration**: Ensure `src/manualTests/http/suites/http-client.private.env.json` contains a valid `sessionCookie`.
    ```json
    {
      "dev": {
        "baseUrl": "http://localhost:8080",
        "sessionCookie": "your-session-cookie-here"
      }
    }
    ```
3.  **Active Application**: The application should be running, usually on `http://localhost:8080`.

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

If you have the `ijhttp` CLI installed, you can run a single suite or all of them at once. The tool automatically discovers environment files in the same directory.

**Run a single suite:**
```powershell
ijhttp -e dev src/manualTests/http/suites/suite-01-public-overview-and-docs.http
```

**Run all suites in order:**
```powershell
ijhttp -e dev src/manualTests/http/suites/
```

**Generate XML report (for CI):**
```powershell
ijhttp -e dev --report src/manualTests/http/suites/
```
The XML report will be saved to `reports/report.xml` relative to the execution directory.

### Option D: Docker (No Installation)

If you don't have `ijhttp` installed, use the official Docker image:

```powershell
docker run --rm -it -v ${PWD}:/workdir jetbrains/intellij-http-client -e dev src/manualTests/http/suites/
```

## Suite Dependencies

The suites are designed to be run in order (01 through 12). Some suites rely on global variables captured by previous suites:

- Suite 07 relies on `firstCategoryId` from Suite 03.
- Suite 11 relies on `runTag` from earlier lifecycle suites.

If you run them out of order, some tests may be skipped or fail.
