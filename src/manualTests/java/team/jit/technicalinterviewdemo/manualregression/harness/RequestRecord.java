package team.jit.technicalinterviewdemo.manualregression.harness;

import java.time.Instant;
import java.util.Optional;

/**
 * One recorded HTTP exchange. Stored in the report so reviewers can see exactly which method/URL
 * the harness called, what status it expected vs. observed, and how long the call took.
 */
public record RequestRecord(
        Instant startedAt,
        String method,
        String url,
        Integer expectedStatus,
        int actualStatus,
        long latencyMillis,
        Optional<String> note) {

    public boolean matchedExpectation() {
        return expectedStatus == null || expectedStatus == actualStatus;
    }
}
