package team.jit.technicalinterviewdemo.manualregression.harness;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Resolved configuration for a single manual-regression run.
 *
 * <p>This record captures every value the harness needs to execute the configured suites and to
 * write the report. It is built once per JVM by {@link ConfigLoader} and exposed to suites through
 * {@link ManualRegressionContext}.
 */
public record RunConfig(
        String baseUrl,
        Optional<String> adminSessionCookie,
        Optional<String> adminCsrfToken,
        Optional<String> regularSessionCookie,
        Optional<String> regularCsrfToken,
        Optional<String> regularUserId,
        String runTag,
        List<String> selectedSuites,
        Path outputDirectory,
        List<String> allowedHosts,
        Optional<String> activeProfileHint) {

    public boolean hasAdminIdentity() {
        return adminSessionCookie.isPresent() && adminCsrfToken.isPresent();
    }

    public boolean hasRegularIdentity() {
        return regularSessionCookie.isPresent() && regularCsrfToken.isPresent();
    }

    public boolean hasRegularUserId() {
        return regularUserId.isPresent();
    }

    public boolean isSuiteSelected(String suiteName) {
        return selectedSuites.isEmpty() || selectedSuites.contains(suiteName);
    }
}
