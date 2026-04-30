package team.jit.technicalinterviewdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@TestcontainersTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiCompatibilityIntegrationTests {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @Test
    void openApiContractRemainsBackwardCompatibleWithApprovedBaseline() throws Exception {
        JsonNode approved = readApprovedBaseline();
        JsonNode current = fetchCurrentContract();

        List<String> issues = OpenApiContractCompatibilityChecker.findBreakingChanges(approved, current);
        assertTrue(issues.isEmpty(), String.join(System.lineSeparator(), issues));
    }

    private JsonNode readApprovedBaseline() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("openapi/approved-openapi.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("Approved OpenAPI baseline was not found on the test classpath.");
            }
            String baseline = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return OpenApiContractSupport.objectMapper().readTree(baseline);
        }
    }

    private JsonNode fetchCurrentContract() throws Exception {
        HttpResponse<String> response = HTTP_CLIENT.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/v3/api-docs"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());
        return OpenApiContractSupport.normalize(response.body());
    }
}
