package team.jit.technicalinterviewdemo.tracing;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpTracingFilter extends OncePerRequestFilter {

    public static final String TRACEPARENT_HEADER = "traceparent";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        HttpTraceContext traceContext = HttpTraceContext.create(request.getHeader(TRACEPARENT_HEADER));
        long startTimeNanos = System.nanoTime();

        request.setAttribute("traceId", traceContext.traceId());
        request.setAttribute("spanId", traceContext.spanId());
        request.setAttribute(TRACEPARENT_HEADER, traceContext.traceparent());
        response.setHeader(TRACEPARENT_HEADER, traceContext.traceparent());

        try (MDC.MDCCloseable traceId = MDC.putCloseable("traceId", traceContext.traceId());
             MDC.MDCCloseable spanId = MDC.putCloseable("spanId", traceContext.spanId())) {
            filterChain.doFilter(request, response);
        } finally {
            log.info(
                    "HTTP request completed method={} path={} query={} status={} durationMs={} traceparent={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    response.getStatus(),
                    toDurationMillis(startTimeNanos),
                    traceContext.traceparent()
            );
        }
    }

    private long toDurationMillis(long startTimeNanos) {
        return (System.nanoTime() - startTimeNanos) / 1_000_000;
    }
}
