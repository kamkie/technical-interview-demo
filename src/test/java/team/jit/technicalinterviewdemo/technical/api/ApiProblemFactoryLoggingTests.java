package team.jit.technicalinterviewdemo.technical.api;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiProblemFactoryLoggingTests {

    @Mock
    private LocalizationService localizationService;

    private ApiProblemFactory apiProblemFactory;
    private Logger apiProblemFactoryLogger;
    private Level previousLoggerLevel;
    private ListAppender<ILoggingEvent> logEvents;

    @BeforeEach
    void setUp() {
        apiProblemFactory = new ApiProblemFactory(localizationService);
        apiProblemFactoryLogger = (Logger) LoggerFactory.getLogger(ApiProblemFactory.class);
        previousLoggerLevel = apiProblemFactoryLogger.getLevel();
        apiProblemFactoryLogger.setLevel(Level.WARN);
        logEvents = new ListAppender<>();
        logEvents.start();
        apiProblemFactoryLogger.addAppender(logEvents);
    }

    @AfterEach
    void tearDown() {
        apiProblemFactoryLogger.detachAppender(logEvents);
        apiProblemFactoryLogger.setLevel(previousLoggerLevel);
        logEvents.stop();
    }

    @Test
    void clientProblemEscapesControlCharactersInLogsWithoutChangingResponse() {
        when(localizationService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.request.invalid_parameter"))).thenReturn(new Localization("error.request.invalid_parameter", "en", "Localized\r\nmessage", null));

        String rawTitle = "Invalid Parameter\r\nforged-title";
        String rawDetail = "Parameter 'id' value 'abc\r\nforged-value' is invalid.";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/books/abc");
        request.addParameter("query", "line1\r\nline2");

        ProblemDetail problemDetail = apiProblemFactory.clientProblem(
                HttpStatus.BAD_REQUEST, rawTitle, rawDetail, "error.request.invalid_parameter", request, Map.of("rejectedValue", "abc\r\nforged-value")
        );

        ILoggingEvent logEvent = logEvents.list.getFirst();
        String logLine = logEvent.getFormattedMessage();

        assertThat(problemDetail.getTitle()).isEqualTo(rawTitle);
        assertThat(problemDetail.getDetail()).isEqualTo(rawDetail);
        assertThat(logEvents.list).hasSize(1);
        assertThat(logLine).contains("forged-title");
        assertThat(logLine).contains("forged-value");
        assertThat(logLine).contains("line2");
        assertThat(logLine).doesNotContain("\r");
        assertThat(logLine).doesNotContain("\n");
        assertThat(logLine).doesNotContain(rawTitle);
        assertThat(logLine).doesNotContain(rawDetail);
    }
}
