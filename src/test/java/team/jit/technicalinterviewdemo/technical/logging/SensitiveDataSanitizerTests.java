package team.jit.technicalinterviewdemo.technical.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SensitiveDataSanitizerTests {

    @Test
    void sanitizeForLogEscapesControlCharacters() {
        assertThat(SensitiveDataSanitizer.sanitizeForLog("line1\r\nline2\tvalue"))
                .isEqualTo("line1\\r\\nline2\\tvalue");
        assertThat(SensitiveDataSanitizer.containsUnsafeLogCharacters("line1\r\nline2")).isTrue();
        assertThat(SensitiveDataSanitizer.containsUnsafeLogCharacters("safe-value")).isFalse();
    }

    @Test
    void sanitizeParametersEscapesValuesAndRedactsSensitiveNames() {
        Map<String, Object> sanitized = SensitiveDataSanitizer.sanitizeParameters(Map.of(
                "query", new String[]{"line1\r\nline2"},
                "token", new String[]{"secret-value"},
                "multi", new String[]{"a", "b\r\nc"}
        ));

        assertThat(sanitized)
                .containsEntry("query", "line1\\r\\nline2")
                .containsEntry("token", SensitiveDataSanitizer.REDACTED)
                .containsEntry("multi", List.of("a", "b\\r\\nc"));
    }

    @Test
    void sanitizeContextForLogEscapesNestedValues() {
        Map<String, Object> sanitized = SensitiveDataSanitizer.sanitizeContextForLog(Map.of(
                "title", "Invalid\r\nTitle",
                "nested", Map.of("detail", "bad\r\nvalue")
        ));

        assertThat(sanitized)
                .containsEntry("title", "Invalid\\r\\nTitle")
                .containsEntry("nested", Map.of("detail", "bad\\r\\nvalue"));
    }
}
