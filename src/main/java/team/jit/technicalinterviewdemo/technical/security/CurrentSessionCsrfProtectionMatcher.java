package team.jit.technicalinterviewdemo.technical.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@RequiredArgsConstructor
public class CurrentSessionCsrfProtectionMatcher implements RequestMatcher {

    private final CurrentApplicationSessionResolver currentApplicationSessionResolver;

    @Override
    public boolean matches(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/") && CsrfFilter.DEFAULT_CSRF_MATCHER.matches(request) && currentApplicationSessionResolver.hasAuthenticatedSession(request);
    }
}
