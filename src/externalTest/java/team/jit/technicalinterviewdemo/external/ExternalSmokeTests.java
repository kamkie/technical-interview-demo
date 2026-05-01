package team.jit.technicalinterviewdemo.external;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
