package team.jit.technicalinterviewdemo.manualregression.harness;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

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
                        .setParam("http.connection.timeout", (int) Duration.ofSeconds(15).toMillis())
                        .setParam("http.socket.timeout", (int) Duration.ofSeconds(30).toMillis()));
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
        return authenticated(config.adminSessionCookie().orElseThrow(), config.adminCsrfToken().orElseThrow());
    }

    /** Builds a regular-user-authenticated request specification. Throws if not configured. */
    public RequestSpecification asRegularUser() {
        if (!config.hasRegularIdentity()) {
            throw new IllegalStateException("Regular-user identity is not configured");
        }
        return authenticated(
                config.regularSessionCookie().orElseThrow(), config.regularCsrfToken().orElseThrow());
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
            String method,
            String pathOrUrl,
            RequestSpecification spec,
            Integer expectedStatus,
            Optional<String> note) {
        Instant start = Instant.now();
        Response response =
                switch (method.toUpperCase(java.util.Locale.ROOT)) {
                    case "GET" -> spec.get(pathOrUrl);
                    case "POST" -> spec.post(pathOrUrl);
                    case "PUT" -> spec.put(pathOrUrl);
                    case "PATCH" -> spec.patch(pathOrUrl);
                    case "DELETE" -> spec.delete(pathOrUrl);
                    case "HEAD" -> spec.head(pathOrUrl);
                    case "OPTIONS" -> spec.options(pathOrUrl);
                    default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
                };
        long latency = java.time.Duration.between(start, Instant.now()).toMillis();
        report.recordRequest(new RequestRecord(
                start, method.toUpperCase(java.util.Locale.ROOT), pathOrUrl, expectedStatus, response.statusCode(), latency, note));
        return response;
    }
}
