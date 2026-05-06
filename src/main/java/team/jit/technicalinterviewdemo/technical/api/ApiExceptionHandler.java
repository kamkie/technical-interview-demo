package team.jit.technicalinterviewdemo.technical.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
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
import team.jit.technicalinterviewdemo.business.book.BookNotFoundException;
import team.jit.technicalinterviewdemo.business.book.DuplicateIsbnException;
import team.jit.technicalinterviewdemo.business.book.StaleBookVersionException;
import team.jit.technicalinterviewdemo.business.category.CategoryInUseException;
import team.jit.technicalinterviewdemo.business.category.CategoryNotFoundException;
import team.jit.technicalinterviewdemo.business.localization.DuplicateLocalizationException;
import team.jit.technicalinterviewdemo.business.localization.LocalizationNotFoundException;
import team.jit.technicalinterviewdemo.business.user.UserAccountNotFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final ApiProblemFactory apiProblemFactory;

    @ExceptionHandler(BookNotFoundException.class)
    ProblemDetail handleBookNotFound(BookNotFoundException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.NOT_FOUND, "Book Not Found", exception.getMessage(), "error.book.not_found", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(LocalizationNotFoundException.class)
    ProblemDetail handleLocalizationNotFound(LocalizationNotFoundException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.NOT_FOUND, "Localization Not Found", exception.getMessage(), "error.localization.not_found", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    ProblemDetail handleCategoryNotFound(CategoryNotFoundException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.NOT_FOUND, "Category Not Found", exception.getMessage(), "error.category.not_found", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(UserAccountNotFoundException.class)
    ProblemDetail handleUserAccountNotFound(UserAccountNotFoundException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.NOT_FOUND, "User Account Not Found", exception.getMessage(), "error.user.not_found", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(CategoryInUseException.class)
    ProblemDetail handleCategoryInUse(CategoryInUseException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.CONFLICT, "Category In Use", exception.getMessage(), "error.category.in_use", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    ProblemDetail handleDuplicateIsbn(DuplicateIsbnException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.CONFLICT, "Duplicate ISBN", exception.getMessage(), "error.book.isbn_duplicate", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(DuplicateLocalizationException.class)
    ProblemDetail handleDuplicateLocalization(
                                              DuplicateLocalizationException exception, HttpServletRequest request
    ) {
        return apiProblemFactory.clientProblem(
                HttpStatus.CONFLICT, "Duplicate Localization", exception.getMessage(), "error.localization.duplicate", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(InvalidRequestException.class)
    ProblemDetail handleInvalidRequest(InvalidRequestException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.BAD_REQUEST, "Invalid Request", exception.getMessage(), "error.request.invalid", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler({StaleBookVersionException.class, ObjectOptimisticLockingFailureException.class})
    ProblemDetail handleConcurrentModification(Exception exception, HttpServletRequest request) {
        String detail = exception instanceof StaleBookVersionException ? exception.getMessage() : "Book state changed concurrently. Retry the request with the latest version.";
        return apiProblemFactory.clientProblem(
                HttpStatus.CONFLICT, "Concurrent Modification", detail, "error.book.stale_version", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ProblemDetail problemDetail = apiProblemFactory.clientProblem(
                HttpStatus.BAD_REQUEST, "Validation Failed", "Request validation failed.", "error.request.validation_failed", request, extractFieldErrors(exception)
        );
        problemDetail.setProperty("fieldErrors", extractFieldErrors(exception));
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        Map<String, String> violations = extractViolations(exception);
        ProblemDetail problemDetail = apiProblemFactory.clientProblem(
                HttpStatus.BAD_REQUEST, "Constraint Violation", "Request validation failed.", "error.request.constraint_violation", request, violations
        );
        problemDetail.setProperty("violations", violations);
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        String parameterName = exception.getName();
        String rejectedValue = String.valueOf(exception.getValue());
        return apiProblemFactory.clientProblem(
                HttpStatus.BAD_REQUEST, "Invalid Parameter", "Parameter '%s' value '%s' is invalid.".formatted(parameterName, rejectedValue), "error.request.invalid_parameter", request, Map.of(
                        "parameter", parameterName, "rejectedValue", rejectedValue, "expectedType", exception.getRequiredType() == null ? "unknown" : exception.getRequiredType().getSimpleName()
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.BAD_REQUEST, "Malformed Request Body", "Request body is missing or malformed.", "error.request.malformed_body", request, Map.of(
                        "exception", exception.getClass().getSimpleName(), "cause", rootCauseMessage(exception)
                )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ProblemDetail handleMissingServletRequestParameter(MissingServletRequestParameterException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.BAD_REQUEST, "Missing Request Parameter", "Required request parameter '%s' is missing.".formatted(exception.getParameterName()), "error.request.missing_parameter", request, Map.of("parameter", exception.getParameterName())
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    ProblemDetail handleMissingRequestHeader(MissingRequestHeaderException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.BAD_REQUEST, "Missing Request Header", "Required request header '%s' is missing.".formatted(exception.getHeaderName()), "error.request.missing_header", request, Map.of("header", exception.getHeaderName())
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ProblemDetail handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception, HttpServletRequest request) {
        String contentType = exception.getContentType() == null ? "unknown" : exception.getContentType().toString();
        return apiProblemFactory.clientProblem(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type", "Content type '%s' is not supported.".formatted(contentType), "error.request.unsupported_media_type", request, Map.of("contentType", contentType)
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ProblemDetail handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", "HTTP method '%s' is not supported for this endpoint.".formatted(exception.getMethod()), "error.request.method_not_allowed", request, Map.of("method", exception.getMethod())
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ProblemDetail handleNoResourceFound(NoResourceFoundException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.NOT_FOUND, "Resource Not Found", "Resource '%s' was not found.".formatted(exception.getResourcePath()), "error.request.resource_not_found", request, Map.of("resourcePath", exception.getResourcePath())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.CONFLICT, "Data Integrity Violation", "Book data violates a database constraint.", "error.data.integrity_violation", request, Map.of(
                        "exception", exception.getClass().getSimpleName(), "cause", rootCauseMessage(exception)
                )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail handleAccessDenied(AccessDeniedException exception, HttpServletRequest request) {
        return apiProblemFactory.clientProblem(
                HttpStatus.FORBIDDEN, "Forbidden", exception.getMessage(), "error.request.forbidden", request, Map.of("exception", exception.getClass().getSimpleName())
        );
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpectedException(Exception exception, HttpServletRequest request) {
        return apiProblemFactory.serverProblem(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred.", "error.server.internal", request, Map.of("exception", exception.getClass().getName()), exception
        );
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
}
