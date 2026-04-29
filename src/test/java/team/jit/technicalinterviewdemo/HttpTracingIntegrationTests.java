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
    void errorResponseIncludesTraceparent() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(uri("/api/missing"))
                .GET()
                .build());

        assertEquals(404, response.statusCode());
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
}
