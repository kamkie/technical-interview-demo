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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import team.jit.technicalinterviewdemo.technical.api.ApiProblemFactory;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().findAndAddModules().build();

    private final ApiProblemFactory apiProblemFactory;

    @Override
    public void commence(
                         HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException
    ) throws IOException, ServletException {
        var problemDetail = apiProblemFactory.clientProblem(
                HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication is required to access this resource.", "error.request.unauthorized", request, Map.of("exception", authenticationException.getClass().getSimpleName())
        );
        writeProblem(response, problemDetail, HttpStatus.UNAUTHORIZED);
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
