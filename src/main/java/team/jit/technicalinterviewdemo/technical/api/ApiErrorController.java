package team.jit.technicalinterviewdemo.technical.api;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.localization.LocalizationContext;
import team.jit.technicalinterviewdemo.technical.localization.RequestLanguageResolver;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Renders failures that escape the MVC layer, such as filter-chain errors, with the same localized
 * problem-details shape that {@link ApiExceptionHandler} produces for handler exceptions. Replaces
 * the Spring Boot default error body and stays out of the published OpenAPI contract because the
 * error dispatch is not a public endpoint.
 */
@Hidden
@RestController
@RequiredArgsConstructor
public class ApiErrorController implements ErrorController {

    private final ApiProblemFactory apiProblemFactory;
    private final RequestLanguageResolver requestLanguageResolver;
    private final LocalizationContext localizationContext;

    @RequestMapping("${server.error.path:${error.path:/error}}")
    ResponseEntity<ProblemDetail> renderErrorDispatch(HttpServletRequest request) {
        // The language filter only runs on the REQUEST dispatch and clears its thread-local while
        // the failure unwinds, so the error dispatch must re-resolve the request language itself.
        String resolvedLanguage = requestLanguageResolver.resolvePreferredLanguage(request);
        if (resolvedLanguage != null) {
            localizationContext.setCurrentLanguage(resolvedLanguage);
        }
        try {
            return ResponseEntity.status(resolveStatus(request))
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(createProblemDetail(request));
        } finally {
            localizationContext.clear();
        }
    }

    private ProblemDetail createProblemDetail(HttpServletRequest request) {
        HttpStatus status = resolveStatus(request);
        ErrorDescriptor descriptor = describe(status);
        Map<String, Object> context = new LinkedHashMap<>();
        Object errorPath = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (errorPath != null) {
            context.put("errorPath", errorPath.toString());
        }

        if (status.is5xxServerError()) {
            Exception failure = resolveFailure(request);
            context.put("exception", failure.getClass().getName());
            return apiProblemFactory.serverProblem(
                    status,
                    descriptor.title(),
                    descriptor.detail(),
                    descriptor.messageKey(),
                    request,
                    context,
                    failure);
        }
        return apiProblemFactory.clientProblem(
                status, descriptor.title(), descriptor.detail(), descriptor.messageKey(), request, context);
    }

    private HttpStatus resolveStatus(HttpServletRequest request) {
        HttpStatus status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) instanceof Integer statusCode
                ? HttpStatus.resolve(statusCode)
                : null;
        return status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
    }

    private Exception resolveFailure(HttpServletRequest request) {
        return request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) instanceof Exception failure
                ? failure
                : new IllegalStateException("Error dispatch carried no exception attribute.");
    }

    private ErrorDescriptor describe(HttpStatus status) {
        return switch (status) {
            case UNAUTHORIZED ->
                new ErrorDescriptor(
                        "Unauthorized",
                        "Authentication is required to access this resource.",
                        "error.request.unauthorized");
            case FORBIDDEN ->
                new ErrorDescriptor("Forbidden", "Access to this resource is denied.", "error.request.forbidden");
            case NOT_FOUND ->
                new ErrorDescriptor(
                        "Resource Not Found",
                        "The requested resource was not found.",
                        "error.request.resource_not_found");
            default ->
                status.is4xxClientError()
                        ? new ErrorDescriptor(
                                "Invalid Request", "The request could not be processed.", "error.request.invalid")
                        : new ErrorDescriptor(
                                "Internal Server Error", "An unexpected error occurred.", "error.server.internal");
        };
    }

    private record ErrorDescriptor(String title, String detail, String messageKey) {}
}
