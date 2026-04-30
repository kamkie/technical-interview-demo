package team.jit.technicalinterviewdemo.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import team.jit.technicalinterviewdemo.logging.SensitiveDataSanitizer;
import team.jit.technicalinterviewdemo.localization.DuplicateLocalizationMessageException;
import team.jit.technicalinterviewdemo.localization.LocalizationMessageNotFoundException;
import team.jit.technicalinterviewdemo.localization.LocalizationMessageService;
import team.jit.technicalinterviewdemo.localization.RequestLanguageResolver;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final LocalizationMessageService localizationMessageService;
    private final RequestLanguageResolver requestLanguageResolver;

    @ExceptionHandler(BookNotFoundException.class)
    ProblemDetail handleBookNotFound(BookNotFoundException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.NOT_FOUND,
                "Book Not Found",
                exception.getMessage(),
                "error.book.not_found",
                request,
                Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(LocalizationMessageNotFoundException.class)
    ProblemDetail handleLocalizationMessageNotFound(LocalizationMessageNotFoundException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.NOT_FOUND,
                "Localization Message Not Found",
                exception.getMessage(),
                "error.localization.not_found",
                request,
                Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    ProblemDetail handleDuplicateIsbn(DuplicateIsbnException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.CONFLICT,
                "Duplicate ISBN",
                exception.getMessage(),
                "error.book.isbn_duplicate",
                request,
                Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(DuplicateLocalizationMessageException.class)
    ProblemDetail handleDuplicateLocalizationMessage(
            DuplicateLocalizationMessageException exception,
            HttpServletRequest request
    ) {
        return logClientProblem(
                HttpStatus.CONFLICT,
                "Duplicate Localization Message",
                exception.getMessage(),
                "error.localization.duplicate",
                request,
                Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(InvalidRequestException.class)
    ProblemDetail handleInvalidRequest(InvalidRequestException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid Request",
                exception.getMessage(),
                "error.request.invalid",
                request,
                Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler({StaleBookVersionException.class, ObjectOptimisticLockingFailureException.class})
    ProblemDetail handleConcurrentModification(Exception exception, HttpServletRequest request) {
        String detail = exception instanceof StaleBookVersionException
                ? exception.getMessage()
                : "Book state changed concurrently. Retry the request with the latest version.";
        return logClientProblem(
                HttpStatus.CONFLICT,
                "Concurrent Modification",
                detail,
                "error.book.stale_version",
                request,
                Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ProblemDetail problemDetail = logClientProblem(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Request validation failed.",
                "error.request.validation_failed",
                request,
                extractFieldErrors(exception)
        );
        problemDetail.setProperty("fieldErrors", extractFieldErrors(exception));
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        Map<String, String> violations = extractViolations(exception);
        ProblemDetail problemDetail = logClientProblem(
                HttpStatus.BAD_REQUEST,
                "Constraint Violation",
                "Request validation failed.",
                "error.request.constraint_violation",
                request,
                violations
        );
        problemDetail.setProperty("violations", violations);
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        String parameterName = exception.getName();
        String rejectedValue = String.valueOf(exception.getValue());
        return logClientProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid Parameter",
                "Parameter '%s' value '%s' is invalid.".formatted(parameterName, rejectedValue),
                "error.request.invalid_parameter",
                request,
                Map.of(
                        "parameter", parameterName,
                        "rejectedValue", rejectedValue,
                        "expectedType", exception.getRequiredType() == null ? "unknown" : exception.getRequiredType().getSimpleName()
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.BAD_REQUEST,
                "Malformed Request Body",
                "Request body is missing or malformed.",
                "error.request.malformed_body",
                request,
                Map.of(
                        "exception", exception.getClass().getSimpleName(),
                        "cause", rootCauseMessage(exception)
                )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ProblemDetail handleMissingServletRequestParameter(MissingServletRequestParameterException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.BAD_REQUEST,
                "Missing Request Parameter",
                "Required request parameter '%s' is missing.".formatted(exception.getParameterName()),
                "error.request.missing_parameter",
                request,
                Map.of("parameter", exception.getParameterName())
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    ProblemDetail handleMissingRequestHeader(MissingRequestHeaderException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.BAD_REQUEST,
                "Missing Request Header",
                "Required request header '%s' is missing.".formatted(exception.getHeaderName()),
                "error.request.missing_header",
                request,
                Map.of("header", exception.getHeaderName())
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ProblemDetail handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception, HttpServletRequest request) {
        String contentType = exception.getContentType() == null ? "unknown" : exception.getContentType().toString();
        return logClientProblem(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported Media Type",
                "Content type '%s' is not supported.".formatted(contentType),
                "error.request.unsupported_media_type",
                request,
                Map.of("contentType", contentType)
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ProblemDetail handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method Not Allowed",
                "HTTP method '%s' is not supported for this endpoint.".formatted(exception.getMethod()),
                "error.request.method_not_allowed",
                request,
                Map.of("method", exception.getMethod())
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ProblemDetail handleNoResourceFound(NoResourceFoundException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                "Resource '%s' was not found.".formatted(exception.getResourcePath()),
                "error.request.resource_not_found",
                request,
                Map.of("resourcePath", exception.getResourcePath())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.CONFLICT,
                "Data Integrity Violation",
                "Book data violates a database constraint.",
                "error.data.integrity_violation",
                request,
                Map.of(
                        "exception", exception.getClass().getSimpleName(),
                        "cause", rootCauseMessage(exception)
                )
        );
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpectedException(Exception exception, HttpServletRequest request) {
        return logServerProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred.",
                "error.server.internal",
                request,
                Map.of("exception", exception.getClass().getName()),
                exception
        );
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail, LocalizedProblemMessage localizedMessage) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("messageKey", localizedMessage.messageKey());
        problemDetail.setProperty("message", localizedMessage.message());
        problemDetail.setProperty("language", localizedMessage.language());
        return problemDetail;
    }

    private ProblemDetail logClientProblem(
            HttpStatus status,
            String title,
            String detail,
            String messageKey,
            HttpServletRequest request,
            Map<String, ?> context
    ) {
        LocalizedProblemMessage localizedMessage = resolveLocalizedProblemMessage(messageKey, request);
        Map<String, Object> enrichedContext = new LinkedHashMap<>(context);
        enrichedContext.put("messageKey", localizedMessage.messageKey());
        enrichedContext.put("language", localizedMessage.language());
        log.warn(
                "Handled client error status={} method={} path={} params={} title='{}' detail='{}' localizedMessage='{}' context={}",
                status.value(),
                request.getMethod(),
                request.getRequestURI(),
                SensitiveDataSanitizer.sanitizeParameters(request.getParameterMap()),
                title,
                detail,
                localizedMessage.message(),
                enrichedContext
        );
        return createProblemDetail(status, title, detail, localizedMessage);
    }

    private ProblemDetail logServerProblem(
            HttpStatus status,
            String title,
            String detail,
            String messageKey,
            HttpServletRequest request,
            Map<String, ?> context,
            Exception exception
    ) {
        LocalizedProblemMessage localizedMessage = resolveLocalizedProblemMessage(messageKey, request);
        Map<String, Object> enrichedContext = new LinkedHashMap<>(context);
        enrichedContext.put("messageKey", localizedMessage.messageKey());
        enrichedContext.put("language", localizedMessage.language());
        log.error(
                "Handled server error status={} method={} path={} params={} title='{}' detail='{}' localizedMessage='{}' context={}",
                status.value(),
                request.getMethod(),
                request.getRequestURI(),
                SensitiveDataSanitizer.sanitizeParameters(request.getParameterMap()),
                title,
                detail,
                localizedMessage.message(),
                enrichedContext,
                exception
        );
        return createProblemDetail(status, title, detail, localizedMessage);
    }

    private Map<String, String> extractFieldErrors(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return fieldErrors;
    }

    private Map<String, String> extractViolations(ConstraintViolationException exception) {
        Map<String, String> violations = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            violations.putIfAbsent(sanitizePropertyPath(violation.getPropertyPath().toString()), violation.getMessage());
        }
        return violations;
    }

    private String rootCauseMessage(Exception exception) {
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage() == null ? "<no-message>" : rootCause.getMessage();
    }

    private String sanitizePropertyPath(String propertyPath) {
        if (propertyPath == null || propertyPath.isBlank()) {
            return "unknown";
        }

        int lastSeparatorIndex = propertyPath.lastIndexOf('.');
        return lastSeparatorIndex >= 0 ? propertyPath.substring(lastSeparatorIndex + 1) : propertyPath;
    }

    private LocalizedProblemMessage resolveLocalizedProblemMessage(String messageKey, HttpServletRequest request) {
        String preferredLanguage = requestLanguageResolver.resolvePreferredLanguage(request);
        team.jit.technicalinterviewdemo.localization.LocalizationMessage resolvedMessage =
                localizationMessageService.findByMessageKeyAndLanguageWithFallback(
                messageKey,
                preferredLanguage,
                RequestLanguageResolver.DEFAULT_LANGUAGE
        );
        return new LocalizedProblemMessage(messageKey, resolvedMessage.getMessageText(), resolvedMessage.getLanguage());
    }

    private record LocalizedProblemMessage(String messageKey, String message, String language) {
    }
}
