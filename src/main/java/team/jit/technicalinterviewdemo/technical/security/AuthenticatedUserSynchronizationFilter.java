package team.jit.technicalinterviewdemo.technical.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class AuthenticatedUserSynchronizationFilter extends OncePerRequestFilter {

    private static final String SESSION_ATTRIBUTE = AuthenticatedUserSynchronizationFilter.class.getName() + ".syncedUser";

    private final AuthenticatedUserSecurityService authenticatedUserSecurityService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        authenticatedUserSecurityService.currentAuthenticatedUserKey()
                .ifPresent(authenticatedUserKey -> synchronizeUser(request, authenticatedUserKey));
        filterChain.doFilter(request, response);
    }

    private void synchronizeUser(HttpServletRequest request, String authenticatedUserKey) {
        HttpSession session = request.getSession(true);
        Object syncedUser = session.getAttribute(SESSION_ATTRIBUTE);
        if (authenticatedUserKey.equals(syncedUser)) {
            return;
        }

        authenticatedUserSecurityService.synchronizeCurrentAuthenticatedUser();
        session.setAttribute(SESSION_ATTRIBUTE, authenticatedUserKey);
    }
}