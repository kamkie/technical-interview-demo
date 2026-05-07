package team.jit.technicalinterviewdemo.manualregression.harness;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Base class for every manual-regression suite.
 *
 * <ul>
 *   <li>wires {@link ManualRegressionExtension}
 *   <li>uses one shared instance per class so suite state (created identifiers) survives across tests
 *   <li>orders tests by {@code @Order} so the lifecycle (create → read → update → delete) is stable
 *   <li>captures the active {@link ExtensionContext} per test so suites can reach the report and HTTP client
 *   <li>aborts remaining tests with {@link Assumptions#abort} when the extension marked the suite blocked
 * </ul>
 */
@ExtendWith(ManualRegressionExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class SuiteBase {

    @BeforeAll
    void captureClassContext() {
        ManualRegressionExtension.setCurrentSuiteClass(getClass());
    }

    @BeforeEach
    void captureContext() {
        ManualRegressionExtension.setCurrentSuiteClass(getClass());
        String reason = ManualRegressionExtension.blockedReason();
        if (reason != null) {
            Assumptions.abort("Suite blocked: " + reason);
        }
    }

    protected RunConfig config() {
        return ManualRegressionExtension.runConfig();
    }

    protected SuiteReport report() {
        return ManualRegressionExtension.suiteReport();
    }

    protected HarnessHttp http() {
        return ManualRegressionExtension.http();
    }

    protected String runTag() {
        return config().runTag();
    }

    protected void recordIdentifier(String key, String value) {
        report().recordIdentifier(key, value);
    }

    protected void note(String note) {
        report().addNote(note);
    }

    protected void leftover(String identifier) {
        report().addLeftover(identifier);
    }
}
