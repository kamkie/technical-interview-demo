package team.jit.technicalinterviewdemo.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    ProblemDetail handleBookNotFound(BookNotFoundException exception, HttpServletRequest request) {
        return logClientProblem(
                HttpStatus.NOT_FOUND,
                "Book Not Found",
                exception.getMessage(),
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
                request,
                Map.of("exception", exception.getClass().getName()),
                exception
        );
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }

    private ProblemDetail logClientProblem(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request,
            Map<String, ?> context
    ) {
        log.warn(
                "Handled client error status={} method={} path={} query={} title='{}' detail='{}' context={}",
                status.value(),
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                title,
                detail,
                context
        );
        return createProblemDetail(status, title, detail);
    }

    private ProblemDetail logServerProblem(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request,
            Map<String, ?> context,
            Exception exception
    ) {
        log.error(
                "Handled server error status={} method={} path={} query={} title='{}' detail='{}' context={}",
                status.value(),
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                title,
                detail,
                context,
                exception
        );
        return createProblemDetail(status, title, detail);
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

        String[] segments = propertyPath.split("\\.");
        return segments[segments.length - 1];
    }
}
