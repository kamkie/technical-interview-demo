package team.jit.technicalinterviewdemo.manualregression.harness;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

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
 * Thin wrapper over REST Assured that records every exchange into the active {@link SuiteReport}
 * and consistently injects the session cookie + CSRF header for authenticated calls.
 *
 * <p>Configuration intentionally disables automatic redirect following so the harness can assert
 * status codes (e.g., 302 from CSRF-rejected POSTs) explicitly, and uses generous timeouts to
 * tolerate a cold app start during the first request of a run.
 */
public final class HarnessHttp {

    /** Standard cookie name issued by the demo app. */
    public static final String SESSION_COOKIE_NAME = "technical-interview-demo-session";

    /** CSRF header expected by Spring Security on unsafe methods. */
    public static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    private final RunConfig config;
    private final SuiteReport report;
    private final RestAssuredConfig restAssuredConfig;

    public HarnessHttp(RunConfig config, SuiteReport report) {
        this.config = config;
        this.report = report;
        this.restAssuredConfig = RestAssuredConfig.config()
                .redirect(RedirectConfig.redirectConfig().followRedirects(false))
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", (int)
                                Duration.ofSeconds(15).toMillis())
                        .setParam("http.socket.timeout", (int)
                                Duration.ofSeconds(30).toMillis()));
    }

    /** Builds an anonymous request specification: no cookie, no CSRF header. */
    public RequestSpecification anonymous() {
        return RestAssured.given()
                .config(restAssuredConfig)
                .baseUri(config.baseUrl())
                .accept(ContentType.JSON);
    }

    /** Builds an admin-authenticated request specification. Throws if no admin identity is configured. */
    public RequestSpecification asAdmin() {
        if (!config.hasAdminIdentity()) {
            throw new IllegalStateException("Admin identity is not configured");
        }
        return authenticated(
                config.adminSessionCookie().orElseThrow(),
                config.adminCsrfToken().orElseThrow());
    }

    /** Builds a regular-user-authenticated request specification. Throws if not configured. */
    public RequestSpecification asRegularUser() {
        if (!config.hasRegularIdentity()) {
            throw new IllegalStateException("Regular-user identity is not configured");
        }
        return authenticated(
                config.regularSessionCookie().orElseThrow(),
                config.regularCsrfToken().orElseThrow());
    }

    private RequestSpecification authenticated(String sessionCookie, String csrfToken) {
        return RestAssured.given()
                .config(restAssuredConfig)
                .baseUri(config.baseUrl())
                .accept(ContentType.JSON)
                .cookie(SESSION_COOKIE_NAME, sessionCookie)
                .header(CSRF_HEADER_NAME, csrfToken);
    }

    /**
     * Sends a request via the supplied {@link RequestSpecification}, records the exchange in the
     * report, and returns the response.
     *
     * @param expectedStatus the status code the caller expects, or {@code null} when no assertion
     *     should be enforced by the recorder
     */
    public Response send(
            String method, String pathOrUrl, RequestSpecification spec, Integer expectedStatus, Optional<String> note) {
        RequestSpecification recordingSpec =
                spec.filter(new ExchangeRecorder(method.toUpperCase(Locale.ROOT), pathOrUrl, expectedStatus, note));
        return switch (method.toUpperCase(Locale.ROOT)) {
            case "GET" -> recordingSpec.get(pathOrUrl);
            case "POST" -> recordingSpec.post(pathOrUrl);
            case "PUT" -> recordingSpec.put(pathOrUrl);
            case "PATCH" -> recordingSpec.patch(pathOrUrl);
            case "DELETE" -> recordingSpec.delete(pathOrUrl);
            case "HEAD" -> recordingSpec.head(pathOrUrl);
            case "OPTIONS" -> recordingSpec.options(pathOrUrl);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    private final class ExchangeRecorder implements Filter {

        private static final Set<String> SECRET_HEADER_NAMES = Set.of(
                "authorization", "cookie", "set-cookie", CSRF_HEADER_NAME.toLowerCase(Locale.ROOT), "x-csrf-token");
        private static final Pattern SENSITIVE_JSON_FIELD = Pattern.compile(
                "(?i)(\"(?:csrfToken|xsrfToken|token|secret|password|session|sessionCookie|adminSessionCookie|adminCsrfToken|regularSessionCookie|regularCsrfToken)\"\\s*:\\s*\")([^\"]*)(\")");

        private final String method;
        private final String pathOrUrl;
        private final Integer expectedStatus;
        private final Optional<String> note;

        private ExchangeRecorder(String method, String pathOrUrl, Integer expectedStatus, Optional<String> note) {
            this.method = method;
            this.pathOrUrl = pathOrUrl;
            this.expectedStatus = expectedStatus;
            this.note = note;
        }

        @Override
        public Response filter(
                FilterableRequestSpecification requestSpec,
                FilterableResponseSpecification responseSpec,
                FilterContext context) {
            Instant start = Instant.now();
            String correlationId = UUID.randomUUID().toString();
            String url = resolveUrl(requestSpec);
            Map<String, List<String>> sentRequestHeaders = requestHeaders(requestSpec);
            String sentRequestBody = bodyToString(requestSpec.getBody());
            try {
                Response response = context.next(requestSpec, responseSpec);
                long latency = Duration.between(start, Instant.now()).toMillis();
                RequestRecord record = new RequestRecord(
                        start,
                        report.suiteName(),
                        ManualRegressionExtension.currentTestName().orElse("(unknown test)"),
                        correlationId,
                        method,
                        url,
                        sentRequestHeaders,
                        sentRequestBody,
                        expectedStatus,
                        response.statusCode(),
                        responseHeaders(response),
                        redactBody(response.getBody().asString()),
                        latency,
                        expectedStatus == null || expectedStatus == response.statusCode() ? "matched" : "mismatched",
                        note);
                report.recordRequest(record);
                return response;
            } catch (RuntimeException ex) {
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
                throw new IllegalStateException(
                        "HTTP " + method + " " + url + " failed before receiving a response: " + failure, ex);
            }
        }

        private String resolveUrl(FilterableRequestSpecification requestSpec) {
            String uri = requestSpec.getURI();
            return uri == null || uri.isBlank() ? pathOrUrl : uri;
        }

        private Map<String, List<String>> requestHeaders(FilterableRequestSpecification requestSpec) {
            Map<String, List<String>> headers = new LinkedHashMap<>();
            for (Header header : requestSpec.getHeaders()) {
                headers.computeIfAbsent(header.getName(), ignored -> new ArrayList<>())
                        .add(redactHeader(header.getName(), header.getValue()));
            }
            for (Cookie cookie : requestSpec.getCookies().asList()) {
                headers.computeIfAbsent("Cookie", ignored -> new ArrayList<>())
                        .add(cookie.getName() + "=" + redactHeader("Cookie", cookie.getValue()));
            }
            return headers;
        }

        private Map<String, List<String>> responseHeaders(Response response) {
            Map<String, List<String>> headers = new LinkedHashMap<>();
            for (Header header : response.getHeaders()) {
                headers.computeIfAbsent(header.getName(), ignored -> new ArrayList<>())
                        .add(redactHeader(header.getName(), header.getValue()));
            }
            return headers;
        }

        private String redactHeader(String name, String value) {
            if (name != null && SECRET_HEADER_NAMES.contains(name.toLowerCase(Locale.ROOT))) {
                return "***";
            }
            return value;
        }

        private String bodyToString(Object body) {
            if (body == null) {
                return null;
            }
            return redactBody(String.valueOf(body));
        }

        private String exceptionSummary(RuntimeException ex) {
            String message = ex.getMessage();
            if (message == null || message.isBlank()) {
                return ex.getClass().getSimpleName();
            }
            return ex.getClass().getSimpleName() + ": " + message;
        }

        private String redactBody(String body) {
            if (body == null || body.isBlank()) {
                return body;
            }
            return SENSITIVE_JSON_FIELD.matcher(body).replaceAll("$1***$3");
        }
    }
}
