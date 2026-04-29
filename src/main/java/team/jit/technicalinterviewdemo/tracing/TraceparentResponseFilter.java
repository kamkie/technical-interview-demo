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

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class TraceparentResponseFilter extends OncePerRequestFilter {

    public static final String TRACEPARENT_HEADER = "traceparent";

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
            filterChain.doFilter(request, response);
        } finally {
            setTraceparentHeaderIfTraceActive(response);

            log.info(
                    "HTTP request completed method={} path={} query={} status={} durationMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    response.getStatus(),
                    toDurationMillis(startTimeNanos)
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
}
