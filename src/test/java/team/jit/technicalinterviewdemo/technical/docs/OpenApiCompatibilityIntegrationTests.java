package team.jit.technicalinterviewdemo.technical.docs;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.testing.AbstractRandomPortIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RandomPortIntegrationSpringBootTest;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RandomPortIntegrationSpringBootTest
class OpenApiCompatibilityIntegrationTests extends AbstractRandomPortIntegrationTest {

    @Test
    void openApiContractRemainsBackwardCompatibleWithApprovedBaseline() throws Exception {
        JsonNode approved = readApprovedBaseline();
        JsonNode current = fetchCurrentContract();

        List<String> issues = OpenApiContractCompatibilityChecker.findBreakingChanges(approved, current);
        assertTrue(issues.isEmpty(), String.join(System.lineSeparator(), issues));
    }

    private JsonNode readApprovedBaseline() throws Exception {
        try (InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream("openapi/approved-openapi.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("Approved OpenAPI baseline was not found on the test classpath.");
            }
            String baseline = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return OpenApiContractSupport.objectMapper().readTree(baseline);
        }
    }

    private JsonNode fetchCurrentContract() throws Exception {
        var response = get("/v3/api-docs");
        assertEquals(200, response.statusCode());
        return OpenApiContractSupport.normalize(response.body());
    }
}
