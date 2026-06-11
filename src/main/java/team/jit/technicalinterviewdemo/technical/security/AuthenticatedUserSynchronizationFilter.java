package team.jit.technicalinterviewdemo.technical.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserAccount;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthenticatedUserSynchronizationFilter extends OncePerRequestFilter {

    private static final String SESSION_ATTRIBUTE =
            AuthenticatedUserSynchronizationFilter.class.getName() + ".syncedUser";

    private final CurrentUserAccountService currentUserAccountService;
    private final AuditLogService auditLogService;
    private final CurrentApplicationSessionResolver currentApplicationSessionResolver;
    private final SessionRepository<? extends Session> sessionRepository;
    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional<String> authenticatedUserKey = currentUserAccountService.currentAuthenticatedUserKey();
        if (authenticatedUserKey.isPresent()) {
            // The blocked check intentionally runs before the per-session sync shortcut so a block
            // takes effect on the blocked account's next authenticated request.
            UserAccount currentUser =
                    currentUserAccountService.findCurrentUser().orElse(null);
            if (currentUser != null && currentUser.isBlocked()) {
                rejectBlockedSession(request, response, currentUser);
                return;
            }
            synchronizeUser(request, authenticatedUserKey.get());
        }
        filterChain.doFilter(request, response);
    }

    private void synchronizeUser(HttpServletRequest request, String authenticatedUserKey) {
        HttpSession session = request.getSession(true);
        Object syncedUser = session.getAttribute(SESSION_ATTRIBUTE);
        if (authenticatedUserKey.equals(syncedUser)) {
            return;
        }

        currentUserAccountService.synchronizeCurrentAuthenticatedUser();
        session.setAttribute(SESSION_ATTRIBUTE, authenticatedUserKey);
    }

    private void rejectBlockedSession(HttpServletRequest request, HttpServletResponse response, UserAccount userAccount)
            throws ServletException, IOException {
        auditLogService.recordWithActor(
                AuditTargetType.AUTHENTICATION,
                userAccount.getId(),
                AuditAction.SESSION_REJECTION,
                userAccount,
                userAccount.getExternalLogin(),
                "Rejected active session for blocked account '%s'.".formatted(userAccount.getExternalLogin()),
                Map.of(
                        "failureReason",
                        "account_blocked",
                        "provider",
                        userAccount.getProvider(),
                        "login",
                        userAccount.getExternalLogin()));
        Optional<String> sessionId = currentApplicationSessionResolver.currentSessionId(request);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sessionId.ifPresent(sessionRepository::deleteById);
        SecurityContextHolder.clearContext();
        apiAuthenticationEntryPoint.commence(request, response, new LockedException("Account is blocked."));
    }
}
