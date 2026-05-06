package team.jit.technicalinterviewdemo.technical.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserAccount;

import java.io.IOException;
import java.util.Map;

public class AuditingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String DEFAULT_TARGET_URL = "/";

    private final CurrentUserAccountService currentUserAccountService;
    private final AuditLogService auditLogService;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public AuditingAuthenticationSuccessHandler(
                                                CurrentUserAccountService currentUserAccountService, AuditLogService auditLogService
    ) {
        this.currentUserAccountService = currentUserAccountService;
        this.auditLogService = auditLogService;
    }

    @Override
    public void onAuthenticationSuccess(
                                        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        SecurityContext existingContext = SecurityContextHolder.getContext();
        Authentication previousAuthentication = existingContext.getAuthentication();
        boolean injectedAuthentication = previousAuthentication == null;

        if (injectedAuthentication) {
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
        }

        try {
            UserAccount userAccount = currentUserAccountService.getCurrentUserOrSynchronize();
            auditLogService.recordWithActor(
                    AuditTargetType.AUTHENTICATION, userAccount.getId(), AuditAction.LOGIN_SUCCESS, userAccount, userAccount.getExternalLogin(), "Successful OAuth login for '%s'.".formatted(userAccount.getExternalLogin()), Map.of(
                            "provider", userAccount.getProvider(), "login", userAccount.getExternalLogin()
                    )
            );
            redirectStrategy.sendRedirect(request, response, DEFAULT_TARGET_URL);
        } finally {
            if (injectedAuthentication) {
                SecurityContextHolder.clearContext();
            } else {
                existingContext.setAuthentication(previousAuthentication);
                SecurityContextHolder.setContext(existingContext);
            }
        }
    }
}
