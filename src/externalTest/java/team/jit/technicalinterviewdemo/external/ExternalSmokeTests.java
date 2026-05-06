package team.jit.technicalinterviewdemo.external;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ExternalSmokeTests extends ExternalHttpTestSupport {

    private static final String SMOKE_LOGIN = "smoke-user";
    private static final String SMOKE_PREFERRED_LANGUAGE = "pl";

    @Test
    void rootEndpointReturnsOverviewPayload() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/", "application/json");
        JsonNode overview = jsonBody(response);

        assertEquals(200, response.statusCode());
        assertTrue(requiredHeader(response, "content-type").contains("application/json"));
        assertTrue(response.body().contains("\"build\""));
        assertTrue(response.body().contains("\"configuration\""));
        assertTrue(
                overview.path("configuration").path("security").path("csrfEnabled").asBoolean(false), "Expected the published overview to report CSRF enabled."
        );
        assertEquals("XSRF-TOKEN", textAt(overview, "configuration", "security", "csrfCookieName"));
        assertEquals("X-XSRF-TOKEN", textAt(overview, "configuration", "security", "csrfHeaderName"));
        assertEquals("edge-or-gateway", textAt(overview, "configuration", "security", "abuseProtection", "owner"));
        assertEquals(
                "/api/session/oauth2/authorization/{registrationId}", textAt(overview, "configuration", "security", "abuseProtection", "loginBootstrapPathTemplate")
        );
        assertEquals("/api/**", textAt(overview, "configuration", "security", "abuseProtection", "unsafeWritePathPattern"));
    }

    @Test
    void rootEndpointMatchesExpectedReleaseIdentityWhenConfigured() throws IOException, InterruptedException {
        String expectedBuildVersion = expectedValue(
                "external.expected.buildVersion", "EXTERNAL_CHECK_EXPECTED_BUILD_VERSION"
        );
        String expectedShortCommitId = expectedValue(
                "external.expected.shortCommitId", "EXTERNAL_CHECK_EXPECTED_SHORT_COMMIT_ID"
        );
        assumeTrue(
                expectedBuildVersion != null && expectedShortCommitId != null, "Skipping release identity assertions because expected build identity is not configured."
        );

        HttpResponse<String> response = get("/", "application/json");
        JsonNode overview = jsonBody(response);

        assertEquals(200, response.statusCode());
        assertEquals(expectedBuildVersion, textAt(overview, "build", "version"));
        assertEquals(expectedShortCommitId, textAt(overview, "git", "shortCommitId"));
    }

    @Test
    void rootEndpointMatchesExpectedRuntimePostureWhenConfigured() throws IOException, InterruptedException {
        String expectedActiveProfile = expectedValue(
                "external.expected.activeProfile", "EXTERNAL_CHECK_EXPECTED_ACTIVE_PROFILE"
        );
        String expectedSessionStoreType = expectedValue(
                "external.expected.sessionStoreType", "EXTERNAL_CHECK_EXPECTED_SESSION_STORE_TYPE"
        );
        String expectedSessionTimeout = expectedValue(
                "external.expected.sessionTimeout", "EXTERNAL_CHECK_EXPECTED_SESSION_TIMEOUT"
        );
        assumeTrue(
                expectedActiveProfile != null && expectedSessionStoreType != null && expectedSessionTimeout != null, "Skipping runtime posture assertions because expected deployment posture is not configured."
        );

        HttpResponse<String> response = get("/", "application/json");
        JsonNode overview = jsonBody(response);

        assertEquals(200, response.statusCode());
        assertTrue(
                StreamSupport.stream(overview.path("runtime").path("activeProfiles").spliterator(), false).map(JsonNode::asText).anyMatch(expectedActiveProfile::equals), "Expected active profile '%s' to be present.".formatted(expectedActiveProfile)
        );
        assertEquals(expectedSessionStoreType, textAt(overview, "configuration", "session", "storeType"));
        assertEquals(expectedSessionTimeout, textAt(overview, "configuration", "session", "timeout"));
        assertTrue(
                overview.path("configuration").path("security").path("csrfEnabled").asBoolean(false), "Expected CSRF to remain enabled for the documented prod posture."
        );
        assertEquals("XSRF-TOKEN", textAt(overview, "configuration", "security", "csrfCookieName"));
        assertEquals("X-XSRF-TOKEN", textAt(overview, "configuration", "security", "csrfHeaderName"));
        assertEquals("edge-or-gateway", textAt(overview, "configuration", "security", "abuseProtection", "owner"));
    }

    @Test
    void helloEndpointReturnsHelloWorld() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/hello", "text/plain");

        assertEquals(200, response.statusCode());
        assertTrue(requiredHeader(response, "content-type").contains("text/plain"));
        assertEquals("Hello World!", response.body());
    }

    @Test
    void docsEndpointRedirectsToIndex() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/docs", "text/html");

        assertTrue(response.statusCode() >= 300 && response.statusCode() < 400);
        assertTrue(requiredHeader(response, "location").endsWith("/docs/index.html"));
    }

    @Test
    void readinessEndpointReportsUp() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/actuator/health/readiness", "application/json");

        assertEquals(200, response.statusCode());
        assertTrue(requiredHeader(response, "content-type").contains("application/json"));
        assertTrue(bodyContainsRegex(response, "\\\"status\\\"\\s*:\\s*\\\"UP\\\""));
    }

    @Test
    void booksListEndpointIsPubliclyReadable() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/api/books?page=0&size=1&sort=id,asc", "application/json");

        assertEquals(200, response.statusCode());
        assertTrue(requiredHeader(response, "content-type").contains("application/json"));
        assertTrue(response.body().contains("\"content\""));
        assertTrue(response.body().contains("\"totalElements\""));
    }

    @Test
    void docsIndexEndpointServesGeneratedHtml() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/docs/index.html", "text/html");

        assertEquals(200, response.statusCode());
        assertTrue(requiredHeader(response, "content-type").contains("text/html"));
        assertTrue(response.body().contains("Technical Interview Demo API"));
    }

    @Test
    void openApiJsonEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/v3/api-docs", "application/json");

        assertEquals(200, response.statusCode());
        assertTrue(requiredHeader(response, "content-type").contains("application/json"));
        assertTrue(response.body().contains("\"openapi\":\"3."));
        assertTrue(response.body().contains("\"/api/books\""));
    }

    @Test
    void openApiYamlEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/v3/api-docs.yaml", "application/vnd.oai.openapi");

        assertEquals(200, response.statusCode());
        assertTrue(requiredHeader(response, "content-type").contains("openapi"));
        assertTrue(response.body().contains("openapi: 3."));
        assertTrue(response.body().contains("/api/books:"));
    }

    @Test
    void sessionEndpointBootstrapsCsrfCookieForJdbcBackedAuthenticatedSession() throws IOException, InterruptedException {
        assumeTrue(
                ExternalSessionSupport.isJdbcConfigured(), "Skipping JDBC-backed session smoke assertions because external JDBC configuration is not available."
        );
        try (ExternalSessionSupport sessionSupport = ExternalSessionSupport.create()) {
            String sessionId = sessionSupport.createAuthenticatedSession(SMOKE_LOGIN);

            HttpResponse<String> response = getWithSession("/api/session", "application/json", sessionId);
            JsonNode session = jsonBody(response);

            assertEquals(200, response.statusCode());
            assertTrue(requiredHeader(response, "content-type").contains("application/json"));
            assertTrue(session.path("authenticated").asBoolean(false));
            assertEquals("/api/account", textAt(session, "accountPath"));
            assertEquals("/api/session/logout", textAt(session, "logoutPath"));
            assertEquals(SESSION_COOKIE_NAME, textAt(session, "sessionCookie", "name"));
            assertTrue(session.path("csrf").path("enabled").asBoolean(false));
            assertEquals(CSRF_COOKIE_NAME, textAt(session, "csrf", "cookieName"));
            assertEquals("X-XSRF-TOKEN", textAt(session, "csrf", "headerName"));
            assertFalse(requiredCookie(response, CSRF_COOKIE_NAME).isBlank());
        }
    }

    @Test
    void accountLanguageWriteAcceptsJdbcBackedAuthenticatedSessionAndBootstrappedCsrfToken() throws IOException, InterruptedException {
        assumeTrue(
                ExternalSessionSupport.isJdbcConfigured(), "Skipping JDBC-backed session smoke assertions because external JDBC configuration is not available."
        );
        try (ExternalSessionSupport sessionSupport = ExternalSessionSupport.create()) {
            String sessionId = sessionSupport.createAuthenticatedSession(SMOKE_LOGIN);

            assertEquals(1, sessionSupport.sessionRowCount(sessionId));
            assertTrue(sessionSupport.sessionAttributeCount(sessionId) > 0);
            assertTrue(sessionSupport.hasStoredSecurityContext(sessionId));

            HttpResponse<String> sessionResponse = getWithSession("/api/session", "application/json", sessionId);
            String csrfToken = requiredCookie(sessionResponse, CSRF_COOKIE_NAME);

            HttpResponse<String> response = putJsonWithCookies(
                    "/api/account/language", "application/json", Map.of("X-XSRF-TOKEN", csrfToken), Map.of(
                            SESSION_COOKIE_NAME, encodedSessionCookieValue(sessionId), CSRF_COOKIE_NAME, csrfToken
                    ), """
                            {
                              "preferredLanguage": "pl"
                            }
                            """
            );
            JsonNode account = jsonBody(response);

            assertEquals(200, response.statusCode());
            assertTrue(requiredHeader(response, "content-type").contains("application/json"));
            assertEquals("github", textAt(account, "provider"));
            assertEquals(SMOKE_LOGIN, textAt(account, "login"));
            assertEquals(SMOKE_LOGIN + " display", textAt(account, "displayName"));
            assertEquals(SMOKE_PREFERRED_LANGUAGE, textAt(account, "preferredLanguage"));

            HttpResponse<String> accountResponse = getWithSession("/api/account", "application/json", sessionId);
            JsonNode persistedAccount = jsonBody(accountResponse);

            assertEquals(200, accountResponse.statusCode());
            assertTrue(requiredHeader(accountResponse, "content-type").contains("application/json"));
            assertEquals(SMOKE_PREFERRED_LANGUAGE, textAt(persistedAccount, "preferredLanguage"));
        }
    }

    @Test
    void flywayStateIsInspectableWhenJdbcAccessIsConfigured() {
        assumeTrue(
                ExternalSessionSupport.isJdbcConfigured(), "Skipping Flyway verification because external JDBC configuration is not available."
        );
        try (ExternalSessionSupport sessionSupport = ExternalSessionSupport.create()) {
            assertTrue(sessionSupport.hasFlywaySchemaHistoryTable());
            assertTrue(sessionSupport.successfulFlywayMigrationCount() > 0);
        }
    }

    private static String expectedValue(String propertyName, String environmentName) {
        return firstNonBlank(System.getProperty(propertyName), System.getenv(environmentName));
    }

    private static String textAt(JsonNode root, String... fieldPath) {
        JsonNode current = root;
        for (String fieldName : fieldPath) {
            current = current.path(fieldName);
        }
        assertNotNull(current);
        assertFalse(current.isMissingNode(), "Expected JSON path to exist: " + String.join(".", fieldPath));
        assertFalse(current.isNull(), "Expected JSON path to be non-null: " + String.join(".", fieldPath));
        return current.asText();
    }
}
