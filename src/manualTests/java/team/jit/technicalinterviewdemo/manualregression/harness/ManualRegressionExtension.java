package team.jit.technicalinterviewdemo.manualregression.harness;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.TestAbortedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JUnit 5 extension that gives every suite class:
 *
 * <ul>
 *   <li>a per-suite {@link SuiteReport}
 *   <li>a shared {@link RunConfig} resolved once per JVM
 *   <li>prerequisite gating against the {@link SuiteName#requires()} of previously executed suites
 *   <li>identity gating: suites declaring {@code requiresAdminIdentity}/{@code requiresRegularIdentity}
 *       are reported as {@link SuiteResult#BLOCKED} when the corresponding identity is missing,
 *       instead of being executed and failing
 *   <li>final report aggregation written by a JVM-shutdown hook
 * </ul>
 *
 * <p>The extension uses {@link TestAbortedException} to skip blocked suites — JUnit reports them as
 * {@code skipped} rather than {@code failed}, which matches the plan's "Blocked" semantics.
 */
public final class ManualRegressionExtension
        implements BeforeAllCallback, AfterAllCallback, TestExecutionExceptionHandler {

    private static final Namespace NAMESPACE = Namespace.create(ManualRegressionExtension.class);
    private static final String KEY_SUITE_REPORT = "suiteReport";
    private static final String KEY_HTTP = "http";
    private static final String KEY_BLOCKED = "blocked";
    private static final String KEY_FAILURE_REASON = "failureReason";

    private static final Object STATE_LOCK = new Object();
    private static volatile RunConfig runConfig;
    private static final Map<String, SuiteReport> COMPLETED = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final List<String> EXECUTION_ORDER = Collections.synchronizedList(new ArrayList<>());
    private static volatile boolean shutdownHookInstalled = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        RunConfig config = ensureConfig();
        Class<?> suiteClass = context.getRequiredTestClass();
        SuiteName suiteAnnotation = suiteClass.getAnnotation(SuiteName.class);
        if (suiteAnnotation == null) {
            throw new IllegalStateException(
                    "Suite " + suiteClass.getName() + " is missing @SuiteName");
        }
        String suiteName = suiteAnnotation.value();
        SuiteReport report = new SuiteReport(suiteName, Instant.now());
        Store store = context.getStore(NAMESPACE);
        store.put(KEY_SUITE_REPORT, report);
        store.put(KEY_HTTP, new HarnessHttp(config, report));

        // Selection check.
        if (!config.isSuiteSelected(suiteName)) {
            String reason = "Suite not in selected suites: " + String.join(",", config.selectedSuites());
            store.put(KEY_BLOCKED, reason);
            return;
        }

        // Identity gating.
        if (suiteAnnotation.requiresAdminIdentity() && !config.hasAdminIdentity()) {
            store.put(KEY_BLOCKED, "Admin identity not configured");
            return;
        }
        if (suiteAnnotation.requiresRegularIdentity() && !config.hasRegularIdentity()) {
            store.put(KEY_BLOCKED, "Regular-user identity not configured");
            return;
        }
        if (suiteAnnotation.requiresRegularUserId() && !config.hasRegularUserId()) {
            store.put(KEY_BLOCKED, "Regular-user id not configured");
            return;
        }

        // Prerequisite gating.
        for (String required : suiteAnnotation.requires()) {
            SuiteReport prior = COMPLETED.get(required);
            if (prior == null) {
                store.put(KEY_BLOCKED, "Required suite did not run: " + required);
                return;
            }
            if (prior.result() == SuiteResult.FAILED || prior.result() == SuiteResult.BLOCKED) {
                store.put(KEY_BLOCKED, "Required suite did not pass: " + required + " (" + prior.result() + ")");
                return;
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        Store store = context.getStore(NAMESPACE);
        SuiteReport report = store.get(KEY_SUITE_REPORT, SuiteReport.class);
        if (report == null) {
            return;
        }
        String blocked = store.get(KEY_BLOCKED, String.class);
        String failure = store.get(KEY_FAILURE_REASON, String.class);
        Instant now = Instant.now();
        if (blocked != null) {
            report.markBlocked(now, blocked);
        } else if (failure != null) {
            report.markFailed(now, failure);
        } else {
            report.markPassed(now);
        }
        COMPLETED.put(report.suiteName(), report);
        EXECUTION_ORDER.add(report.suiteName());
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        // TestAbortedException → JUnit "skipped"; do not mark as failed.
        if (throwable instanceof TestAbortedException) {
            throw throwable;
        }
        Store store = context.getStore(NAMESPACE);
        String existing = store.get(KEY_FAILURE_REASON, String.class);
        if (existing == null) {
            store.put(KEY_FAILURE_REASON, throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }
        throw throwable;
    }

    /** Helper used by {@link SuiteBase} to fetch the suite report attached to the test class. */
    public static SuiteReport suiteReport(ExtensionContext context) {
        return context.getStore(NAMESPACE).get(KEY_SUITE_REPORT, SuiteReport.class);
    }

    /** Helper used by {@link SuiteBase} to fetch the HTTP client attached to the test class. */
    public static HarnessHttp http(ExtensionContext context) {
        return context.getStore(NAMESPACE).get(KEY_HTTP, HarnessHttp.class);
    }

    /** Helper used by {@code SuiteBase} to skip remaining tests in a blocked suite. */
    public static String blockedReason(ExtensionContext context) {
        return context.getStore(NAMESPACE).get(KEY_BLOCKED, String.class);
    }

    public static RunConfig runConfig() {
        return ensureConfig();
    }

    /** Returns a generated identifier captured by an earlier suite, if available. */
    public static java.util.Optional<String> lookupIdentifier(String suiteName, String key) {
        SuiteReport prior = COMPLETED.get(suiteName);
        if (prior == null) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.ofNullable(prior.generatedIdentifiers().get(key));
    }

    private static RunConfig ensureConfig() {
        RunConfig snapshot = runConfig;
        if (snapshot != null) {
            return snapshot;
        }
        synchronized (STATE_LOCK) {
            if (runConfig == null) {
                runConfig = ConfigLoader.load();
                installShutdownHook();
            }
            return runConfig;
        }
    }

    private static void installShutdownHook() {
        if (shutdownHookInstalled) {
            return;
        }
        shutdownHookInstalled = true;
        Runtime.getRuntime().addShutdownHook(new Thread(ManualRegressionExtension::flushReport, "manual-regression-report-flush"));
    }

    private static void flushReport() {
        RunConfig config = runConfig;
        if (config == null) {
            return;
        }
        List<SuiteReport> ordered = new ArrayList<>();
        for (String suiteName : EXECUTION_ORDER) {
            SuiteReport report = COMPLETED.get(suiteName);
            if (report != null) {
                ordered.add(report);
            }
        }
        try {
            ReportWriter.write(config, ordered);
            System.out.println(
                    "[manual-regression] Report written to " + config.outputDirectory().toAbsolutePath());
        } catch (RuntimeException ex) {
            System.err.println("[manual-regression] Failed to write report: " + ex.getMessage());
        }
    }
}
