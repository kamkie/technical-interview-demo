package team.jit.technicalinterviewdemo.technical.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import team.jit.technicalinterviewdemo.testing.AbstractBookCatalogMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class RequestLoggingIntegrationTests extends AbstractBookCatalogMockMvcIntegrationTest {

    @Test
    void requestLoggingRedactsSensitiveQueryParameters(CapturedOutput output) throws Exception {
        String secret = "secret-log-token-123";

        mockMvc.perform(get("/hello")
                        .queryParam("token", secret)
                        .queryParam("page", "1"))
                .andExpect(status().isOk());

        assertThat(output).contains("params={token=<redacted>, page=1}");
        assertThat(output).doesNotContain(secret);
    }

    @Test
    void errorLoggingRedactsSensitiveQueryParameters(CapturedOutput output) throws Exception {
        String secret = "Bearer raw-secret-value";

        mockMvc.perform(get("/api/books/{id}", "abc")
                        .queryParam("authorization", secret))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Parameter"));

        assertThat(output).contains("params={authorization=<redacted>}");
        assertThat(output).doesNotContain(secret);
    }
}
