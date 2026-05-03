package team.jit.technicalinterviewdemo.external;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

class ExternalSmokeTests extends ExternalHttpTestSupport {

    @Test
    void rootEndpointReturnsOverviewPayload() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/", "application/json");

        assertEquals(200, response.statusCode());
        assertTrue(requiredHeader(response, "content-type").contains("application/json"));
        assertTrue(response.body().contains("\"build\""));
        assertTrue(response.body().contains("\"configuration\""));
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
    void accountEndpointAcceptsJdbcBackedAuthenticatedSession() throws IOException, InterruptedException {
        assumeTrue(
                ExternalSessionSupport.isJdbcConfigured(),
                "Skipping JDBC-backed session smoke assertions because external JDBC configuration is not available."
        );
        try (ExternalSessionSupport sessionSupport = ExternalSessionSupport.create()) {
            String sessionId = sessionSupport.createAuthenticatedSession("smoke-user");

            assertEquals(1, sessionSupport.sessionRowCount(sessionId));
            assertTrue(sessionSupport.sessionAttributeCount(sessionId) > 0);
            assertTrue(sessionSupport.hasStoredSecurityContext(sessionId));

            HttpResponse<String> response = getWithSession("/api/account", "application/json", sessionId);

            assertEquals(200, response.statusCode());
            assertTrue(requiredHeader(response, "content-type").contains("application/json"));
            assertTrue(response.body().contains("\"provider\":\"github\""));
            assertTrue(response.body().contains("\"login\":\"smoke-user\""));
            assertTrue(response.body().contains("\"displayName\":\"smoke-user display\""));
        }
    }

    @Test
    void flywayStateIsInspectableWhenJdbcAccessIsConfigured() {
        assumeTrue(
                ExternalSessionSupport.isJdbcConfigured(),
                "Skipping Flyway verification because external JDBC configuration is not available."
        );
        try (ExternalSessionSupport sessionSupport = ExternalSessionSupport.create()) {
            assertTrue(sessionSupport.hasFlywaySchemaHistoryTable());
            assertTrue(sessionSupport.successfulFlywayMigrationCount() > 0);
        }
    }
}
