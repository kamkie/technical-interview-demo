package team.jit.technicalinterviewdemo.manualregression.harness;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestWatcher;
import org.opentest4j.TestAbortedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback, TestExecutionExceptionHandler, TestWatcher {

    private static final Namespace NAMESPACE = Namespace.create(ManualRegressionExtension.class);
    private static final String KEY_SUITE_REPORT = "suiteReport";
    private static final String KEY_HTTP = "http";
    private static final String KEY_BLOCKED = "blocked";
    private static final String KEY_FAILURE_REASON = "failureReason";

    private static final Object STATE_LOCK = new Object();
    private static volatile RunConfig runConfig;
    private static final Map<String, SuiteReport> COMPLETED = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final List<String> EXECUTION_ORDER = Collections.synchronizedList(new ArrayList<>());
    private static final Map<Class<?>, SuiteReport> ACTIVE_REPORTS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Class<?>, HarnessHttp> ACTIVE_HTTP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Class<?>, String> BLOCKED_REASONS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Class<?>, String> FAILURE_REASONS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final ThreadLocal<Class<?>> CURRENT_SUITE_CLASS = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_TEST_NAME = new ThreadLocal<>();
    private static volatile boolean shutdownHookInstalled = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        RunConfig config = ensureConfig();
        Class<?> suiteClass = context.getRequiredTestClass();
        SuiteName suiteAnnotation = suiteClass.getAnnotation(SuiteName.class);
        if (suiteAnnotation == null) {
            throw new IllegalStateException("Suite " + suiteClass.getName() + " is missing @SuiteName");
        }
        String suiteName = suiteAnnotation.value();
        SuiteReport report = new SuiteReport(suiteName, Instant.now());
        HarnessHttp http = new HarnessHttp(config, report);
        ACTIVE_REPORTS.put(suiteClass, report);
        ACTIVE_HTTP.put(suiteClass, http);
        Store store = context.getStore(NAMESPACE);
        store.put(KEY_SUITE_REPORT, report);
        store.put(KEY_HTTP, http);

        // Selection check.
        if (!config.isSuiteSelected(suiteName)) {
            blockSuite(suiteClass, store, "Suite not in selected suites: " + String.join(",", config.selectedSuites()));
            return;
        }

        // Identity gating.
        if (suiteAnnotation.requiresAdminIdentity() && !config.hasAdminIdentity()) {
            blockSuite(suiteClass, store, "Admin identity not configured");
            return;
        }
        if (suiteAnnotation.requiresRegularIdentity() && !config.hasRegularIdentity()) {
            blockSuite(suiteClass, store, "Regular-user identity not configured");
            return;
        }
        if (suiteAnnotation.requiresRegularUserId() && !config.hasRegularUserId()) {
            blockSuite(suiteClass, store, "Regular-user id not configured");
            return;
        }

        // Prerequisite gating.
        for (String required : suiteAnnotation.requires()) {
            SuiteReport prior = COMPLETED.get(required);
            if (prior == null) {
                blockSuite(suiteClass, store, "Required suite did not run: " + required);
                return;
            }
            if (prior.result() == SuiteResult.FAILED || prior.result() == SuiteResult.BLOCKED) {
                blockSuite(
                        suiteClass, store, "Required suite did not pass: " + required + " (" + prior.result() + ")");
                return;
            }
        }
    }

    private static void blockSuite(Class<?> suiteClass, Store store, String reason) {
        store.put(KEY_BLOCKED, reason);
        BLOCKED_REASONS.put(suiteClass, reason);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Class<?> suiteClass = context.getRequiredTestClass();
        CURRENT_SUITE_CLASS.set(suiteClass);
        CURRENT_TEST_NAME.set(context.getDisplayName());
        SuiteReport report = suiteReport(suiteClass);
        if (report != null && context.getTestMethod().isPresent()) {
            report.recordTestStarted(context.getUniqueId(), context.getDisplayName(), Instant.now());
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        Class<?> suiteClass = context.getRequiredTestClass();
        Store store = context.getStore(NAMESPACE);
        SuiteReport report = suiteReport(suiteClass);
        if (report == null) {
            return;
        }
        String blocked = BLOCKED_REASONS.getOrDefault(suiteClass, store.get(KEY_BLOCKED, String.class));
        String failure = FAILURE_REASONS.getOrDefault(suiteClass, store.get(KEY_FAILURE_REASON, String.class));
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
        ACTIVE_REPORTS.remove(suiteClass);
        ACTIVE_HTTP.remove(suiteClass);
        BLOCKED_REASONS.remove(suiteClass);
        FAILURE_REASONS.remove(suiteClass);
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
            String reason = throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            store.put(KEY_FAILURE_REASON, reason);
            FAILURE_REASONS.put(context.getRequiredTestClass(), reason);
        }
        throw throwable;
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        recordTestOutcome(context, "PASSED", Optional.empty());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        recordTestOutcome(context, "BLOCKED", Optional.ofNullable(cause.getMessage()));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        recordTestOutcome(
                context,
                "FAILED",
                Optional.of(cause.getClass().getSimpleName() + ": " + cause.getMessage()));
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        recordTestOutcome(context, "NOT_RUN", reason);
    }

    private void recordTestOutcome(ExtensionContext context, String outcome, Optional<String> reason) {
        SuiteReport report = suiteReport(context.getRequiredTestClass());
        if (report != null && context.getTestMethod().isPresent()) {
            report.recordTestFinished(context.getUniqueId(), context.getDisplayName(), Instant.now(), outcome, reason);
        }
    }

    /** Helper used by {@link SuiteBase} to fetch the suite report attached to the test class. */
    public static SuiteReport suiteReport() {
        Class<?> suiteClass = CURRENT_SUITE_CLASS.get();
        return suiteClass == null ? null : suiteReport(suiteClass);
    }

    private static SuiteReport suiteReport(Class<?> suiteClass) {
        return ACTIVE_REPORTS.get(suiteClass);
    }

    /** Helper used by {@link SuiteBase} to fetch the HTTP client attached to the test class. */
    public static HarnessHttp http() {
        Class<?> suiteClass = CURRENT_SUITE_CLASS.get();
        return suiteClass == null ? null : ACTIVE_HTTP.get(suiteClass);
    }

    /** Helper used by {@code SuiteBase} to skip remaining tests in a blocked suite. */
    public static String blockedReason() {
        Class<?> suiteClass = CURRENT_SUITE_CLASS.get();
        return suiteClass == null ? null : BLOCKED_REASONS.get(suiteClass);
    }

    public static void setCurrentSuiteClass(Class<?> suiteClass) {
        CURRENT_SUITE_CLASS.set(suiteClass);
    }

    public static RunConfig runConfig() {
        return ensureConfig();
    }

    public static java.util.Optional<String> currentTestName() {
        return java.util.Optional.ofNullable(CURRENT_TEST_NAME.get());
    }

    public static void setCurrentTestName(String displayName) {
        CURRENT_TEST_NAME.set(displayName);
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
        Runtime.getRuntime()
                .addShutdownHook(new Thread(ManualRegressionExtension::flushReport, "manual-regression-report-flush"));
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
            System.out.println("[manual-regression] Report written to "
                    + config.outputDirectory().toAbsolutePath());
        } catch (RuntimeException ex) {
            System.err.println("[manual-regression] Failed to write report: " + ex.getMessage());
        }
    }
}
