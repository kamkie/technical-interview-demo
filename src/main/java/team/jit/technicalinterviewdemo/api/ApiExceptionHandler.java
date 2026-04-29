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

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    ProblemDetail handleBookNotFound(BookNotFoundException exception) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "Book Not Found", exception.getMessage());
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    ProblemDetail handleDuplicateIsbn(DuplicateIsbnException exception) {
        return createProblemDetail(HttpStatus.CONFLICT, "Duplicate ISBN", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Request validation failed."
        );

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        problemDetail.setProperty("fieldErrors", fieldErrors);
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException exception) {
        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Constraint Violation",
                "Request validation failed."
        );

        Map<String, String> violations = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            violations.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage());
        }

        problemDetail.setProperty("violations", violations);
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String parameterName = exception.getName();
        String rejectedValue = String.valueOf(exception.getValue());
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid Parameter",
                "Parameter '%s' value '%s' is invalid.".formatted(parameterName, rejectedValue)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleHttpMessageNotReadable() {
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Malformed Request Body",
                "Request body is missing or malformed."
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ProblemDetail handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Missing Request Parameter",
                "Required request parameter '%s' is missing.".formatted(exception.getParameterName())
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    ProblemDetail handleMissingRequestHeader(MissingRequestHeaderException exception) {
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Missing Request Header",
                "Required request header '%s' is missing.".formatted(exception.getHeaderName())
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ProblemDetail handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        String contentType = exception.getContentType() == null ? "unknown" : exception.getContentType().toString();
        return createProblemDetail(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported Media Type",
                "Content type '%s' is not supported.".formatted(contentType)
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ProblemDetail handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        return createProblemDetail(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method Not Allowed",
                "HTTP method '%s' is not supported for this endpoint.".formatted(exception.getMethod())
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ProblemDetail handleNoResourceFound(NoResourceFoundException exception) {
        return createProblemDetail(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                "Resource '%s' was not found.".formatted(exception.getResourcePath())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrityViolation() {
        return createProblemDetail(
                HttpStatus.CONFLICT,
                "Data Integrity Violation",
                "Book data violates a database constraint."
        );
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpectedException(Exception exception) {
        log.error("Unhandled exception", exception);
        return createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred."
        );
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
