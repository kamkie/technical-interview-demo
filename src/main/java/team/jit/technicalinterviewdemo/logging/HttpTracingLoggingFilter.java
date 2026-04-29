package team.jit.technicalinterviewdemo.logging;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class HttpTracingLoggingFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String TRACEPARENT_HEADER = "traceparent";
    private static final String REQUEST_ID_MDC_KEY = "rid";
    private static final String ACTUATOR_HEALTH_PATH = "/actuator/health";

    private final Tracer tracer;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTimeNanos = System.nanoTime();
        boolean shouldLog = shouldLogRequest(request);
        String requestId = resolveRequestId(request);

        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        try {
            response.setHeader(REQUEST_ID_HEADER, requestId);
            setTraceparentHeaderIfTraceActive(response);
            if (shouldLog) {
                log.info(
                        "HTTP request started method={} path={} query={} params={} traceparent={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        request.getQueryString(),
                        sanitizeParameters(request.getParameterMap()),
                        response.getHeader(TRACEPARENT_HEADER)
                );
            }
            filterChain.doFilter(request, response);
        } finally {
            setTraceparentHeaderIfTraceActive(response);

            if (shouldLog) {
                log.info(
                        "HTTP response completed method={} path={} status={} durationMs={} traceparent={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        toDurationMillis(startTimeNanos),
                        response.getHeader(TRACEPARENT_HEADER)
                );
            }
            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return requestId;
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
                SensitiveDataSanitizer.isSensitiveName(name) ? SensitiveDataSanitizer.REDACTED : sanitizeValues(values)
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

    private boolean shouldLogRequest(HttpServletRequest request) {
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String requestUri = request.getRequestURI();
        String pathWithinApplication = requestUri.startsWith(contextPath)
                ? requestUri.substring(contextPath.length())
                : requestUri;

        return !pathWithinApplication.startsWith(ACTUATOR_HEALTH_PATH);
    }
}
