package team.jit.technicalinterviewdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class OpenApiIntegrationTests {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @Test
    void openApiJsonEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = send("/v3/api-docs");

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("application/json"));
        assertTrue(response.body().contains("\"openapi\":\"3."));
        assertTrue(response.body().contains("\"/api/books\""));
    }

    @Test
    void openApiYamlEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = send("/v3/api-docs.yaml");

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").contains("openapi"));
        assertTrue(response.body().contains("openapi: 3."));
        assertTrue(response.body().contains("/api/books:"));
    }

    private HttpResponse<String> send(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
