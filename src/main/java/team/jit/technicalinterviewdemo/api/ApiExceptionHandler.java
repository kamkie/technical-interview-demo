package team.jit.technicalinterviewdemo.api;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class  ApiExceptionHandler {

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrityViolation() {
        return createProblemDetail(
                HttpStatus.CONFLICT,
                "Data Integrity Violation",
                "Book data violates a database constraint."
        );
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
