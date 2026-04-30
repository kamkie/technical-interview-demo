package team.jit.technicalinterviewdemo.technical.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MissingRequestHeaderException;
import team.jit.technicalinterviewdemo.business.book.Book;
import team.jit.technicalinterviewdemo.business.localization.LocalizationMessage;
import team.jit.technicalinterviewdemo.business.localization.LocalizationMessageService;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTests {

    @Mock
    private LocalizationMessageService localizationMessageService;

    private ApiExceptionHandler apiExceptionHandler;

    @BeforeEach
    void setUp() {
        apiExceptionHandler = new ApiExceptionHandler(localizationMessageService);
    }

    @Test
    void handleMissingRequestHeaderIncludesHeaderNameAndLocalizedMetadata() {
        when(localizationMessageService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.request.missing_header")))
                .thenReturn(localizedMessage("error.request.missing_header", "Missing header"));

        ProblemDetail problemDetail = apiExceptionHandler.handleMissingRequestHeader(
                new MissingRequestHeaderException("X-Request-Id", null),
                request("/api/test")
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Required request header 'X-Request-Id' is missing.");
        assertThat(problemDetail.getProperties())
                .containsEntry("messageKey", "error.request.missing_header")
                .containsEntry("message", "Missing header")
                .containsEntry("language", "pl");
    }

    @Test
    void handleConcurrentModificationUsesRetryMessageForOptimisticLockFailures() {
        when(localizationMessageService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.book.stale_version")))
                .thenReturn(localizedMessage("error.book.stale_version", "Stale version"));

        ProblemDetail problemDetail = apiExceptionHandler.handleConcurrentModification(
                new ObjectOptimisticLockingFailureException(Book.class, 7L),
                request("/api/books/7")
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problemDetail.getDetail())
                .isEqualTo("Book state changed concurrently. Retry the request with the latest version.");
        assertThat(problemDetail.getProperties())
                .containsEntry("messageKey", "error.book.stale_version")
                .containsEntry("message", "Stale version")
                .containsEntry("language", "pl");
    }

    @Test
    void handleDataIntegrityViolationIncludesRootCauseMessage() {
        when(localizationMessageService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.data.integrity_violation")))
                .thenReturn(localizedMessage("error.data.integrity_violation", "Integrity violation"));

        ProblemDetail problemDetail = apiExceptionHandler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("outer", new IllegalStateException("duplicate key value violates constraint")),
                request("/api/books")
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Book data violates a database constraint.");
        assertThat(problemDetail.getProperties())
                .containsEntry("messageKey", "error.data.integrity_violation")
                .containsEntry("message", "Integrity violation")
                .containsEntry("language", "pl");
    }

    @Test
    void handleUnexpectedExceptionReturnsGenericServerProblem() {
        when(localizationMessageService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.server.internal")))
                .thenReturn(localizedMessage("error.server.internal", "Server error"));

        ProblemDetail problemDetail = apiExceptionHandler.handleUnexpectedException(
                new IllegalStateException("boom"),
                request("/api/test")
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problemDetail.getDetail()).isEqualTo("An unexpected error occurred.");
        assertThat(problemDetail.getProperties())
                .containsEntry("messageKey", "error.server.internal")
                .containsEntry("message", "Server error")
                .containsEntry("language", "pl");
    }

    private HttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.addParameter("secret", "value");
        return request;
    }

    private LocalizationMessage localizedMessage(String messageKey, String messageText) {
        return new LocalizationMessage(messageKey, "pl", messageText, null);
    }
}
