package team.jit.technicalinterviewdemo.manualregression.harness;

/** Per-suite outcome. {@link #BLOCKED} is distinct from {@link #FAILED}: skipped, not failed. */
public enum SuiteResult {
    PASSED,
    PASSED_WITH_NOTE,
    FAILED,
    BLOCKED,
    NOT_RUN
}
