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

Each script is designed to build a markdown report during execution.

### Option A: Manual Export (IntelliJ)

1.  After running the suite, open the **Services** tool window.
2.  Find the last request named **"Finalize Report"**.
3.  The report content will be visible in the **Response** -> **Console** tab through `client.log`.
4.  Save exported report files under `temp/manual-regression/http-client/`.

### Option B: Command Line (ijhttp)

If you have the `ijhttp` CLI installed:

```powershell
New-Item -ItemType Directory -Force temp/manual-regression/http-client
ijhttp --env dev src/manualTests/http/suites/suite-01-public-overview-and-docs.http > temp/manual-regression/http-client/01-public-overview-and-docs_report_$(Get-Date -Format "yyyy-MM-dd").md
```

You may need to clean up the output if `ijhttp` adds extra logging.

## Suite Dependencies

The suites are designed to be run in order (01 through 12). Some suites rely on global variables captured by previous suites:

- Suite 07 relies on `firstCategoryId` from Suite 03.
- Suite 11 relies on `runTag` from earlier lifecycle suites.

If you run them out of order, some tests may be skipped or fail.
