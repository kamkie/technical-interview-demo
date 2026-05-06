package team.jit.technicalinterviewdemo.technical.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import team.jit.technicalinterviewdemo.testing.AbstractBookCatalogMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcIntegrationSpringBootTest
@ActiveProfiles(
        value = {"prod", "test"},
        inheritProfiles = false)
@TestPropertySource(properties = "server.servlet.session.cookie.secure=true")
@ExtendWith(OutputCaptureExtension.class)
class RequestLoggingIntegrationTests extends AbstractBookCatalogMockMvcIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void requestLoggingRedactsSensitiveQueryParameters(CapturedOutput output) throws Exception {
        String secret = "secret-log-token-123";

        mockMvc.perform(get("/hello").queryParam("token", secret).queryParam("page", "1"))
                .andExpect(status().isOk());

        JsonNode requestLog = parseJsonLog(findLogLine(output, "HTTP request started method=GET path=/hello"));
        assertThat(requestLog.path("@timestamp").asText()).isNotBlank();
        assertThat(requestLog.path("level").asText()).isEqualTo("INFO");
        assertThat(requestLog.path("logger_name").asText()).contains("HttpTracingLoggingFilter");
        assertThat(requestLog.path("thread_name").asText()).isNotBlank();
        assertThat(requestLog.path("message").asText()).contains("params={token=<redacted>, page=1}");
        assertThat(requestLog.path("rid").asText()).isNotBlank();
        assertThat(requestLog.path("traceId").asText()).matches("[0-9a-f]{32}");
        assertThat(requestLog.path("spanId").asText()).matches("[0-9a-f]{16}");
        assertThat(output).doesNotContain(secret);
    }

    @Test
    void errorLoggingRedactsSensitiveQueryParameters(CapturedOutput output) throws Exception {
        String secret = "Bearer raw-secret-value";

        mockMvc.perform(get("/api/books/{id}", "abc").queryParam("authorization", secret))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Parameter"));

        JsonNode requestLog = parseJsonLog(findLogLine(output, "HTTP request started method=GET path=/api/books/abc"));
        assertThat(requestLog.path("message").asText()).contains("params={authorization=<redacted>}");
        assertThat(requestLog.path("rid").asText()).isNotBlank();
        assertThat(requestLog.path("traceId").asText()).matches("[0-9a-f]{32}");
        assertThat(output).doesNotContain(secret);
    }

    private JsonNode parseJsonLog(String logLine) throws IOException {
        return OBJECT_MAPPER.readTree(logLine);
    }

    private String findLogLine(CapturedOutput output, String messageFragment) {
        return Arrays.stream(output.getOut().split("\\R"))
                .filter(line -> line.contains(messageFragment))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing log entry fragment: " + messageFragment));
    }
}
