package team.jit.technicalinterviewdemo.manualregression.harness;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * One recorded HTTP exchange. Stored in the execution log so reviewers can inspect the request,
 * response, expected status, actual status, and timing without relying on console history.
 */
public record RequestRecord(
        Instant startedAt,
        String suiteName,
        String testName,
        String correlationId,
        String method,
        String url,
        Map<String, List<String>> requestHeaders,
        String requestBody,
        Integer expectedStatus,
        int actualStatus,
        Map<String, List<String>> responseHeaders,
        String responseBody,
        long latencyMillis,
        String outcome,
        Optional<String> note) {

    public boolean matchedExpectation() {
        return expectedStatus == null || expectedStatus == actualStatus;
    }
}
