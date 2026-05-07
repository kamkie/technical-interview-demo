package team.jit.technicalinterviewdemo.manualregression.harness;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders the run-level report in two formats:
 *
 * <ul>
 *   <li>{@code report.md} — human-readable summary with environment block, per-suite status table,
 *       generated identifiers, leftover identifiers (for manual cleanup), and a release-blocker
 *       section the executor pastes into the manual result log.
 *   <li>{@code report.json} — machine-readable structure of every recorded request and outcome.
 *   <li>{@code checklist.md} — Markdown suite/test checklist for manual execution signoff.
 * </ul>
 */
public final class ReportWriter {

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private static final ObjectMapper NDJSON = JSON.copy().disable(SerializationFeature.INDENT_OUTPUT);

    private ReportWriter() {}

    public static void write(RunConfig config, List<SuiteReport> reports) {
        try {
            Files.createDirectories(config.outputDirectory());
            Path markdown = config.outputDirectory().resolve("report.md");
            Path json = config.outputDirectory().resolve("report.json");
            Path executionLog = config.outputDirectory().resolve("execution-log.ndjson");
            Path checklist = config.outputDirectory().resolve("checklist.md");
            Files.writeString(markdown, renderMarkdown(config, reports), StandardCharsets.UTF_8);
            Files.writeString(json, renderJson(config, reports, executionLog, checklist), StandardCharsets.UTF_8);
            Files.writeString(executionLog, renderExecutionLog(reports), StandardCharsets.UTF_8);
            Files.writeString(checklist, renderChecklist(config, reports), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to write manual-regression report", ex);
        }
    }

    public static Path writeExample(Path rootDirectory) {
        String timestamp = DateTimeFormatterHolder.timestamp();
        Path outputDirectory =
                rootDirectory.resolve("manual-regression").resolve("example").resolve("run-" + timestamp);
        RunConfig config = new RunConfig(
                "http://localhost:8080",
                java.util.Optional.empty(),
                java.util.Optional.empty(),
                java.util.Optional.empty(),
                "example",
                List.of(),
                outputDirectory,
                List.of(),
                java.util.Optional.of("example"));
        SuiteReport report = new SuiteReport("01-public-overview-and-docs", Instant.now());
        Instant testStart = Instant.now();
        report.recordTestStarted("example-failure", "example failure", testStart);
        report.recordRequest(new RequestRecord(
                testStart,
                "01-public-overview-and-docs",
                "example failure",
                "example-correlation-id",
                "GET",
                "http://localhost:8080/",
                Map.of("Accept", List.of("application/json"), "Cookie", List.of("***"), "X-XSRF-TOKEN", List.of("***")),
                "{}",
                200,
                500,
                Map.of("Content-Type", List.of("application/problem+json"), "Set-Cookie", List.of("***")),
                "{\"status\":500,\"message\":\"synthetic example failure\"}",
                42,
                "mismatched",
                java.util.Optional.of("synthetic example report entry")));
        report.recordTestFinished(
                "example-failure",
                "example failure",
                Instant.now(),
                "FAILED",
                java.util.Optional.of("Synthetic all-failure example report"));
        report.markFailed(Instant.now(), "Synthetic all-failure example report");
        write(config, List.of(report));
        return outputDirectory;
    }

    private static String renderJson(RunConfig config, List<SuiteReport> reports, Path executionLog, Path checklist)
            throws IOException {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("runTag", config.runTag());
        root.put("baseUrl", config.baseUrl());
        root.put(
                "executionLog",
                config.outputDirectory().relativize(executionLog).toString().replace('\\', '/'));
        root.put(
                "checklist",
                config.outputDirectory().relativize(checklist).toString().replace('\\', '/'));
        root.put(
                "startedAt",
                reports.stream()
                        .map(SuiteReport::startedAt)
                        .min(Instant::compareTo)
                        .orElse(Instant.now()));
        root.put("hasAdminIdentity", config.hasAdminIdentity());
        root.put("hasRegularIdentity", config.hasRegularIdentity());
        root.put("selectedSuites", config.selectedSuites());
        root.put("suites", reports.stream().map(ReportWriter::toJsonNode).toList());
        return JSON.writeValueAsString(root);
    }

    private static Map<String, Object> toJsonNode(SuiteReport r) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("name", r.suiteName());
        node.put("result", r.result().name());
        node.put("startedAt", r.startedAt());
        r.finishedAt().ifPresent(f -> node.put("finishedAt", f));
        r.reason().ifPresent(reason -> node.put("reason", reason));
        node.put("generatedIdentifiers", r.generatedIdentifiers());
        node.put("notes", r.notes());
        node.put("leftoverIdentifiers", r.leftoverIdentifiers());
        node.put("tests", r.tests().stream().map(ReportWriter::toTestNode).toList());
        node.put(
                "requests",
                r.requests().stream()
                        .map(req -> {
                            Map<String, Object> n = new LinkedHashMap<>();
                            n.put("startedAt", req.startedAt());
                            n.put("correlationId", req.correlationId());
                            n.put("method", req.method());
                            n.put("url", req.url());
                            n.put("expectedStatus", req.expectedStatus());
                            n.put("actualStatus", req.actualStatus());
                            n.put("latencyMillis", req.latencyMillis());
                            req.note().ifPresent(note -> n.put("note", note));
                            n.put("matched", req.matchedExpectation());
                            return n;
                        })
                        .toList());
        return node;
    }

    private static Map<String, Object> toTestNode(TestRecord test) {
        Map<String, Object> n = new LinkedHashMap<>();
        n.put("displayName", test.displayName());
        n.put("startedAt", test.startedAt());
        test.finishedAt().ifPresent(finishedAt -> n.put("finishedAt", finishedAt));
        n.put("outcome", test.outcome());
        test.reason().ifPresent(reason -> n.put("reason", reason));
        return n;
    }

    private static String renderExecutionLog(List<SuiteReport> reports) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<RequestRecord> records = reports.stream()
                .flatMap(r -> r.requests().stream())
                .sorted(java.util.Comparator.comparing(RequestRecord::startedAt))
                .toList();
        for (RequestRecord record : records) {
            sb.append(NDJSON.writeValueAsString(toExecutionLogNode(record))).append('\n');
        }
        return sb.toString();
    }

    private static Map<String, Object> toExecutionLogNode(RequestRecord req) {
        Map<String, Object> n = new LinkedHashMap<>();
        n.put("timestamp", req.startedAt());
        n.put("suite", req.suiteName());
        n.put("test", req.testName());
        n.put("correlationId", req.correlationId());
        n.put("method", req.method());
        n.put("url", req.url());
        n.put("requestHeaders", req.requestHeaders());
        n.put("requestBody", req.requestBody());
        n.put("expectedStatus", req.expectedStatus());
        n.put("actualStatus", req.actualStatus());
        n.put("responseHeaders", req.responseHeaders());
        n.put("responseBody", req.responseBody());
        n.put("latencyMillis", req.latencyMillis());
        n.put("matched", req.matchedExpectation());
        n.put("outcome", req.outcome());
        req.note().ifPresent(note -> n.put("note", note));
        return n;
    }

    private static String renderMarkdown(RunConfig config, List<SuiteReport> reports) {
        StringBuilder sb = new StringBuilder(4096);
        sb.append("# Manual Regression Report\n\n");
        sb.append("- Run tag: `").append(config.runTag()).append("`\n");
        sb.append("- Base URL: `").append(config.baseUrl()).append("`\n");
        sb.append("- Admin identity supplied: ")
                .append(config.hasAdminIdentity() ? "yes" : "no")
                .append('\n');
        sb.append("- Regular-user identity supplied: ")
                .append(config.hasRegularIdentity() ? "yes" : "no")
                .append('\n');
        sb.append("- Selected suites: ")
                .append(
                        config.selectedSuites().isEmpty()
                                ? "(default ordered profile)"
                                : String.join(", ", config.selectedSuites()))
                .append('\n');
        sb.append("- Execution log: `execution-log.ndjson`\n");
        sb.append("- Execution checklist: `checklist.md`\n");
        config.activeProfileHint()
                .ifPresent(
                        p -> sb.append("- SPRING_PROFILES_ACTIVE: `").append(p).append("`\n"));
        sb.append('\n');

        sb.append("## Suite Results\n\n");
        sb.append("| # | Suite | Result | Duration | Reason / Notes |\n");
        sb.append("|---|-------|--------|----------|----------------|\n");
        int idx = 1;
        for (SuiteReport r : reports) {
            String duration = r.finishedAt()
                    .map(f -> Duration.between(r.startedAt(), f).toMillis() + " ms")
                    .orElse("—");
            String detail = r.reason().orElseGet(() -> r.notes().isEmpty() ? "" : String.join("; ", r.notes()));
            sb.append("| ")
                    .append(idx++)
                    .append(" | `")
                    .append(r.suiteName())
                    .append("` | ")
                    .append(r.result().name())
                    .append(" | ")
                    .append(duration)
                    .append(" | ")
                    .append(escapeTable(detail))
                    .append(" |\n");
        }
        sb.append('\n');

        sb.append("## Generated Identifiers\n\n");
        boolean anyIds =
                reports.stream().anyMatch(r -> !r.generatedIdentifiers().isEmpty());
        if (!anyIds) {
            sb.append("None.\n\n");
        } else {
            for (SuiteReport r : reports) {
                if (r.generatedIdentifiers().isEmpty()) {
                    continue;
                }
                sb.append("### `").append(r.suiteName()).append("`\n\n");
                r.generatedIdentifiers()
                        .forEach((k, v) -> sb.append("- ")
                                .append(k)
                                .append(": `")
                                .append(v)
                                .append("`\n"));
                sb.append('\n');
            }
        }

        sb.append("## Leftover Identifiers (manual cleanup required)\n\n");
        boolean anyLeftover =
                reports.stream().anyMatch(r -> !r.leftoverIdentifiers().isEmpty());
        if (!anyLeftover) {
            sb.append("None.\n\n");
        } else {
            for (SuiteReport r : reports) {
                if (r.leftoverIdentifiers().isEmpty()) {
                    continue;
                }
                sb.append("### `").append(r.suiteName()).append("`\n\n");
                r.leftoverIdentifiers()
                        .forEach(id -> sb.append("- `").append(id).append("`\n"));
                sb.append('\n');
            }
        }

        sb.append("## Release Blocker Summary\n\n");
        List<SuiteReport> failed =
                reports.stream().filter(r -> r.result() == SuiteResult.FAILED).toList();
        if (failed.isEmpty()) {
            sb.append("No release blockers observed in this run.\n");
        } else {
            sb.append("The following suites failed and must be triaged before release:\n\n");
            for (SuiteReport r : failed) {
                sb.append("- `")
                        .append(r.suiteName())
                        .append("`: ")
                        .append(r.reason().orElse("(no reason recorded)"))
                        .append('\n');
            }
        }

        return sb.toString();
    }

    private static String renderChecklist(RunConfig config, List<SuiteReport> reports) {
        StringBuilder sb = new StringBuilder(4096);
        sb.append("# Manual Regression Checklist\n\n");
        sb.append("- Run tag: `").append(config.runTag()).append("`\n");
        sb.append("- Base URL: `").append(config.baseUrl()).append("`\n");
        sb.append("- Report: `report.md`\n");
        sb.append("- Execution log: `execution-log.ndjson`\n\n");
        sb.append("Use this file to review or fill in completion as each suite and test is executed.\n");
        sb.append("Generated checkboxes are checked when the harness observed a terminal outcome.\n\n");

        for (SuiteReport report : reports) {
            sb.append("- ")
                    .append(checkbox(report.result() != SuiteResult.NOT_RUN))
                    .append(" `")
                    .append(escapeInline(report.suiteName()))
                    .append("` - ")
                    .append(report.result().name());
            report.reason().ifPresent(reason -> sb.append(" - ").append(escapeInline(reason)));
            sb.append('\n');
            if (report.tests().isEmpty()) {
                sb.append("  - [ ] No test outcome was observed for this suite.\n");
            } else {
                for (TestRecord test : report.tests()) {
                    sb.append("  - ")
                            .append(checkbox(test.finishedAt().isPresent()))
                            .append(" `")
                            .append(escapeInline(test.displayName()))
                            .append("` - ")
                            .append(test.outcome());
                    test.reason().ifPresent(reason -> sb.append(" - ").append(escapeInline(reason)));
                    sb.append('\n');
                }
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    private static String checkbox(boolean checked) {
        return checked ? "[x]" : "[ ]";
    }

    private static String escapeTable(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "\\|").replace("\n", " ");
    }

    private static String escapeInline(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("`", "'").replace("\r", " ").replace("\n", " ");
    }

    private static final class DateTimeFormatterHolder {

        private static String timestamp() {
            return java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                    .withZone(java.time.ZoneOffset.UTC)
                    .format(Instant.now());
        }
    }
}
