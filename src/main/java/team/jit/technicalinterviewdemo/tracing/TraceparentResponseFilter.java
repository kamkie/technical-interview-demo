package team.jit.technicalinterviewdemo.tracing;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class TraceparentResponseFilter extends OncePerRequestFilter {

    public static final String TRACEPARENT_HEADER = "traceparent";
    private static final String REDACTED = "<redacted>";
    private static final Set<String> SENSITIVE_TOKENS = Set.of(
            "password",
            "passwd",
            "pwd",
            "secret",
            "token",
            "authorization",
            "credential",
            "cookie",
            "session",
            "apikey",
            "accesskey",
            "privatekey",
            "clientsecret",
            "bearer",
            "refreshtoken"
    );

    private final Tracer tracer;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTimeNanos = System.nanoTime();

        try {
            setTraceparentHeaderIfTraceActive(response);
            log.info(
                    "HTTP request started method={} path={} query={} params={} traceparent={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    sanitizeParameters(request.getParameterMap()),
                    response.getHeader(TRACEPARENT_HEADER)
            );
            filterChain.doFilter(request, response);
        } finally {
            setTraceparentHeaderIfTraceActive(response);

            log.info(
                    "HTTP response completed method={} path={} status={} durationMs={} traceparent={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    toDurationMillis(startTimeNanos),
                    response.getHeader(TRACEPARENT_HEADER)
            );
        }
    }

    private String toTraceparent(TraceContext traceContext) {
        String traceFlags = Boolean.TRUE.equals(traceContext.sampled()) ? "01" : "00";
        return "00-%s-%s-%s".formatted(traceContext.traceId(), traceContext.spanId(), traceFlags);
    }

    private void setTraceparentHeaderIfTraceActive(HttpServletResponse response) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan == null) {
            return;
        }

        TraceContext traceContext = currentSpan.context();
        response.setHeader(TRACEPARENT_HEADER, toTraceparent(traceContext));
    }

    private long toDurationMillis(long startTimeNanos) {
        return (System.nanoTime() - startTimeNanos) / 1_000_000;
    }

    private Map<String, Object> sanitizeParameters(Map<String, String[]> parameterMap) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        parameterMap.forEach((name, values) -> sanitized.put(
                name,
                isSensitive(name) ? REDACTED : sanitizeValues(values)
        ));
        return sanitized;
    }

    private Object sanitizeValues(String[] values) {
        if (values == null) {
            return null;
        }
        if (values.length == 1) {
            return values[0];
        }
        return Arrays.asList(values);
    }

    private boolean isSensitive(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        String normalized = normalize(name);
        for (String token : SENSITIVE_TOKENS) {
            if (normalized.contains(normalize(token))) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }
}
