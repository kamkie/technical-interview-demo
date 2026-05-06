package team.jit.technicalinterviewdemo.technical.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;
import team.jit.technicalinterviewdemo.technical.api.ApiProblemFactory;

@Component
@RequiredArgsConstructor
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().findAndAddModules().build();

    private final ApiProblemFactory apiProblemFactory;

    @Override
    public void handle(
                       HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        ProblemDetail problemDetail = accessDeniedException instanceof CsrfException ? apiProblemFactory.clientProblem(
                HttpStatus.FORBIDDEN, "Invalid CSRF Token", "A valid CSRF token is required to perform this operation.", "error.request.csrf_invalid", request, Map.of("exception", accessDeniedException.getClass().getSimpleName())
        ) : apiProblemFactory.clientProblem(
                HttpStatus.FORBIDDEN, "Forbidden", accessDeniedException.getMessage() == null || accessDeniedException.getMessage().isBlank() ? "Access is denied." : accessDeniedException.getMessage(), "error.request.forbidden", request, Map.of("exception", accessDeniedException.getClass().getSimpleName())
        );
        writeProblem(response, problemDetail, HttpStatus.FORBIDDEN);
    }

    private void writeProblem(HttpServletResponse response, ProblemDetail problemDetail, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        OBJECT_MAPPER.writeValue(response.getWriter(), toBody(problemDetail));
    }

    private Map<String, Object> toBody(ProblemDetail problemDetail) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", problemDetail.getTitle());
        body.put("status", problemDetail.getStatus());
        body.put("detail", problemDetail.getDetail());
        if (problemDetail.getProperties() != null) {
            body.putAll(problemDetail.getProperties());
        }
        return body;
    }
}
