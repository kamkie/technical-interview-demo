package team.jit.technicalinterviewdemo.technical.localization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RequestLanguageContextFilter extends OncePerRequestFilter {

    private final RequestLanguageResolver requestLanguageResolver;
    private final LocalizationContext localizationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String resolvedLanguage = requestLanguageResolver.resolvePreferredLanguage(request);
        if (resolvedLanguage != null) {
            localizationContext.setCurrentLanguage(resolvedLanguage);
        } else {
            localizationContext.clear();
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            localizationContext.clear();
        }
    }
}
