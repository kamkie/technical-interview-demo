package team.jit.technicalinterviewdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@TestcontainersTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpTracingIntegrationTests {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @Test
    void helloResponseIncludesGeneratedTraceparent() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/hello"))
                .GET()
                .build());

        assertEquals(200, response.statusCode());
        assertEquals("Hello World!", response.body());
        assertMatchesRequestId(response.headers().firstValue("X-Request-Id").orElse(null));
        assertMatchesTraceparent(response.headers().firstValue("traceparent").orElse(null));
    }

    @Test
    void incomingTraceparentPreservesTraceIdInResponse() throws IOException, InterruptedException {
        String incomingTraceparent = "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01";

        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/hello"))
                .header("traceparent", incomingTraceparent)
                .GET()
                .build());

        assertEquals(200, response.statusCode());
        String responseTraceparent = response.headers().firstValue("traceparent").orElse(null);
        assertNotNull(responseTraceparent);
        assertTrue(responseTraceparent.matches("00-4bf92f3577b34da6a3ce929d0e0e4736-[0-9a-f]{16}-01"));
    }

    @Test
    void incomingRequestIdIsReturnedInResponse() throws IOException, InterruptedException {
        String incomingRequestId = "request-12345";

        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/hello"))
                .header("X-Request-Id", incomingRequestId)
                .GET()
                .build());

        assertEquals(200, response.statusCode());
        assertEquals(incomingRequestId, response.headers().firstValue("X-Request-Id").orElse(null));
    }

    @Test
    void errorResponseIncludesTraceparent() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/api/missing"))
                .GET()
                .build());

        assertEquals(404, response.statusCode());
        assertMatchesRequestId(response.headers().firstValue("X-Request-Id").orElse(null));
        assertMatchesTraceparent(response.headers().firstValue("traceparent").orElse(null));
    }

    @Test
    void actuatorInfoEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/actuator/info"))
                .GET()
                .build());

        assertEquals(200, response.statusCode());
        assertMatchesRequestId(response.headers().firstValue("X-Request-Id").orElse(null));
        assertMatchesTraceparent(response.headers().firstValue("traceparent").orElse(null));
        assertTrue(response.body().startsWith("{"));
    }

    @Test
    void actuatorLivenessEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/actuator/health/liveness"))
                .GET()
                .build());

        assertEquals(200, response.statusCode());
        assertMatchesRequestId(response.headers().firstValue("X-Request-Id").orElse(null));
        assertMatchesTraceparent(response.headers().firstValue("traceparent").orElse(null));
        assertTrue(response.body().contains("\"status\":\"UP\""));
    }

    @Test
    void actuatorReadinessEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/actuator/health/readiness"))
                .GET()
                .build());

        assertEquals(200, response.statusCode());
        assertMatchesRequestId(response.headers().firstValue("X-Request-Id").orElse(null));
        assertMatchesTraceparent(response.headers().firstValue("traceparent").orElse(null));
        assertTrue(response.body().contains("\"status\":\"UP\""));
    }

    @Test
    void actuatorPrometheusEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/actuator/prometheus"))
                .GET()
                .build());

        assertEquals(200, response.statusCode());
        assertMatchesRequestId(response.headers().firstValue("X-Request-Id").orElse(null));
        assertMatchesTraceparent(response.headers().firstValue("traceparent").orElse(null));
        assertTrue(response.body().contains("# HELP"));
    }

    @Test
    void docsEndpointRedirectsToGeneratedDocumentation() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/docs"))
                .GET()
                .build());

        assertEquals(302, response.statusCode());
        String location = response.headers().firstValue("location").orElseThrow();
        assertTrue(location.endsWith("/docs/index.html"));
        assertMatchesRequestId(response.headers().firstValue("X-Request-Id").orElse(null));
        assertMatchesTraceparent(response.headers().firstValue("traceparent").orElse(null));
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private void assertMatchesTraceparent(String traceparent) {
        assertNotNull(traceparent);
        assertTrue(traceparent.matches("00-[0-9a-f]{32}-[0-9a-f]{16}-0[01]"));
    }

    private void assertMatchesRequestId(String requestId) {
        assertNotNull(requestId);
        assertTrue(requestId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
}
