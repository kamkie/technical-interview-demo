package team.jit.technicalinterviewdemo.technical.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationService;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class ApiProblemFactoryLoggingTests {

    @Mock
    private LocalizationService localizationService;

    private ApiProblemFactory apiProblemFactory;

    @BeforeEach
    void setUp() {
        apiProblemFactory = new ApiProblemFactory(localizationService);
    }

    @Test
    void clientProblemEscapesControlCharactersInLogsWithoutChangingResponse(CapturedOutput output) {
        when(localizationService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.request.invalid_parameter")))
                .thenReturn(new Localization("error.request.invalid_parameter", "en", "Localized\r\nmessage", null));

        String rawTitle = "Invalid Parameter\r\nforged-title";
        String rawDetail = "Parameter 'id' value 'abc\r\nforged-value' is invalid.";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/books/abc");
        request.addParameter("query", "line1\r\nline2");

        ProblemDetail problemDetail = apiProblemFactory.clientProblem(
                HttpStatus.BAD_REQUEST,
                rawTitle,
                rawDetail,
                "error.request.invalid_parameter",
                request,
                Map.of("rejectedValue", "abc\r\nforged-value")
        );

        String logLine = output.getOut().trim();

        assertThat(problemDetail.getTitle()).isEqualTo(rawTitle);
        assertThat(problemDetail.getDetail()).isEqualTo(rawDetail);
        assertThat(logLine).contains("forged-title");
        assertThat(logLine).contains("forged-value");
        assertThat(logLine).contains("line2");
        assertThat(logLine).doesNotContain("\r");
        assertThat(logLine).doesNotContain("\n");
        assertThat(output).doesNotContain(rawTitle);
        assertThat(output).doesNotContain(rawDetail);
    }
}
