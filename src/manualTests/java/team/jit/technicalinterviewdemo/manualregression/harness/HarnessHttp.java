package team.jit.technicalinterviewdemo.manualregression.harness;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * HTTP client wrapper that records every exchange into the active {@link SuiteReport} and
 * consistently injects the session cookie plus the CSRF cookie/header pair for authenticated calls.
 *
 * <p>Redirect following is disabled so the harness can assert status codes (e.g., 302 from
 * CSRF-rejected POSTs) explicitly. Timeouts are generous enough to tolerate a cold app start during
 * the first request of a run.
 */
public final class HarnessHttp {

    /** Standard cookie name issued by the demo app. */
    public static final String SESSION_COOKIE_NAME = "technical-interview-demo-session";

    /** CSRF header expected by Spring Security on unsafe methods. */
    public static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    /** Readable CSRF cookie mirrored into {@link #CSRF_HEADER_NAME} on unsafe methods. */
    public static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private static final Set<String> SUPPORTED_METHODS =
            Set.of("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS");
    private static final Set<String> SECRET_HEADER_NAMES =
            Set.of("authorization", "cookie", "set-cookie", CSRF_HEADER_NAME.toLowerCase(Locale.ROOT), "x-csrf-token");
    private static final Pattern SENSITIVE_JSON_FIELD = Pattern.compile(
            "(?i)(\"(?:csrfToken|xsrfToken|token|secret|password|session|sessionCookie|adminSessionCookie|adminCsrfToken|regularSessionCookie|regularCsrfToken)\"\\s*:\\s*\")([^\"]*)(\")");

    private final RunConfig config;
    private final SuiteReport report;
    private final HttpClient client;

    public HarnessHttp(RunConfig config, SuiteReport report) {
        this.config = config;
        this.report = report;
        this.client = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    /** Builds an anonymous request: no cookie, no CSRF header. */
    public HarnessRequest anonymous() {
        return new HarnessRequest(config.baseUrl());
    }

    /** Builds an admin-authenticated request. Throws if no admin identity is configured. */
    public HarnessRequest asAdmin() {
        if (!config.hasAdminIdentity()) {
            throw new IllegalStateException("Admin identity is not configured");
        }
        return authenticated(
                config.adminSessionCookie().orElseThrow(),
                config.adminCsrfToken().orElseThrow());
    }

    /** Builds a regular-user-authenticated request. Throws if not configured. */
    public HarnessRequest asRegularUser() {
        if (!config.hasRegularIdentity()) {
            throw new IllegalStateException("Regular-user identity is not configured");
        }
        return authenticated(
                config.regularSessionCookie().orElseThrow(),
                config.regularCsrfToken().orElseThrow());
    }

    private HarnessRequest authenticated(String sessionCookie, String csrfToken) {
        return anonymous()
                .header("Cookie", SESSION_COOKIE_NAME + "=" + sessionCookie + "; " + CSRF_COOKIE_NAME + "=" + csrfToken)
                .header(CSRF_HEADER_NAME, csrfToken);
    }

    /**
     * Sends a request via the supplied {@link HarnessRequest}, records the exchange in the report,
     * and returns the response.
     *
     * @param expectedStatus the status code the caller expects, or {@code null} when no assertion
     *     should be enforced by the recorder
     */
    public HarnessResponse send(
            String method, String pathOrUrl, HarnessRequest request, Integer expectedStatus, Optional<String> note) {
        String normalizedMethod = method.toUpperCase(Locale.ROOT);
        if (!SUPPORTED_METHODS.contains(normalizedMethod)) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        Instant start = Instant.now();
        String correlationId = UUID.randomUUID().toString();
        String url = request.resolve(pathOrUrl).toString();
        Map<String, List<String>> sentRequestHeaders = redactHeaders(request.headers());
        String sentRequestBody = request.body().map(HarnessHttp::redactBody).orElse(null);
        try {
            HttpResponse<String> response = client.send(
                    request.toHttpRequest(normalizedMethod, pathOrUrl, REQUEST_TIMEOUT),
                    HttpResponse.BodyHandlers.ofString());
            long latency = Duration.between(start, Instant.now()).toMillis();
            HarnessResponse harnessResponse = new HarnessResponse(
                    response.statusCode(), response.headers().map(), response.body());
            report.recordRequest(new RequestRecord(
                    start,
                    report.suiteName(),
                    ManualRegressionExtension.currentTestName().orElse("(unknown test)"),
                    correlationId,
                    normalizedMethod,
                    url,
                    sentRequestHeaders,
                    sentRequestBody,
                    expectedStatus,
                    harnessResponse.statusCode(),
                    redactHeaders(harnessResponse.headers()),
                    redactBody(harnessResponse.asString()),
                    latency,
                    expectedStatus == null || expectedStatus == harnessResponse.statusCode() ? "matched" : "mismatched",
                    note));
            assertExpectedStatus(normalizedMethod, url, expectedStatus, harnessResponse);
            return harnessResponse;
        } catch (IOException ex) {
            throw recordException(
                    start,
                    correlationId,
                    normalizedMethod,
                    url,
                    sentRequestHeaders,
                    sentRequestBody,
                    expectedStatus,
                    note,
                    ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw recordException(
                    start,
                    correlationId,
                    normalizedMethod,
                    url,
                    sentRequestHeaders,
                    sentRequestBody,
                    expectedStatus,
                    note,
                    ex);
        } catch (RuntimeException ex) {
            throw recordException(
                    start,
                    correlationId,
                    normalizedMethod,
                    url,
                    sentRequestHeaders,
                    sentRequestBody,
                    expectedStatus,
                    note,
                    ex);
        }
    }

    private IllegalStateException recordException(
            Instant start,
            String correlationId,
            String method,
            String url,
            Map<String, List<String>> sentRequestHeaders,
            String sentRequestBody,
            Integer expectedStatus,
            Optional<String> note,
            Exception ex) {
        long latency = Duration.between(start, Instant.now()).toMillis();
        String failure = exceptionSummary(ex);
        report.recordRequest(new RequestRecord(
                start,
                report.suiteName(),
                ManualRegressionExtension.currentTestName().orElse("(unknown test)"),
                correlationId,
                method,
                url,
                sentRequestHeaders,
                sentRequestBody,
                expectedStatus,
                null,
                Map.of(),
                failure,
                latency,
                "exception",
                note));
        return new IllegalStateException(
                "HTTP " + method + " " + url + " failed before receiving a response: " + failure, ex);
    }

    private static void assertExpectedStatus(
            String method, String url, Integer expectedStatus, HarnessResponse response) {
        if (expectedStatus != null && expectedStatus != response.statusCode()) {
            throw new AssertionError("Expected HTTP " + method + " " + url + " to return " + expectedStatus
                    + " but got " + response.statusCode());
        }
    }

    private static Map<String, List<String>> redactHeaders(Map<String, List<String>> rawHeaders) {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        rawHeaders.forEach((name, values) -> {
            List<String> redactedValues = new ArrayList<>();
            for (String value : values) {
                redactedValues.add(redactHeader(name, value));
            }
            headers.put(name, redactedValues);
        });
        return headers;
    }

    private static String redactHeader(String name, String value) {
        if (name != null && SECRET_HEADER_NAMES.contains(name.toLowerCase(Locale.ROOT))) {
            return "***";
        }
        return value;
    }

    private static String exceptionSummary(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return ex.getClass().getSimpleName();
        }
        return ex.getClass().getSimpleName() + ": " + message;
    }

    private static String redactBody(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }
        return SENSITIVE_JSON_FIELD.matcher(body).replaceAll("$1***$3");
    }
}
