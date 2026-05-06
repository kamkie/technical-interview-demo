package team.jit.technicalinterviewdemo.technical.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;

public class AuditingAuthenticationFailureHandler implements AuthenticationFailureHandler {

    static final String LOGIN_FAILED_TARGET_URL = "/?login=failed";

    private final AuditLogService auditLogService;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public AuditingAuthenticationFailureHandler(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        AuditAction action = exception instanceof SessionAuthenticationException
                ? AuditAction.SESSION_REJECTION
                : AuditAction.LOGIN_FAILURE;
        String summary = action == AuditAction.SESSION_REJECTION
                ? "Rejected OAuth login because the concurrent session limit was reached."
                : "OAuth login failed.";
        auditLogService.recordWithActor(
                AuditTargetType.AUTHENTICATION,
                null,
                action,
                null,
                null,
                summary,
                failureDetails(request, exception, action)
        );
        redirectStrategy.sendRedirect(request, response, LOGIN_FAILED_TARGET_URL);
    }

    private Map<String, Object> failureDetails(
            HttpServletRequest request,
            AuthenticationException exception,
            AuditAction action
    ) {
        Map<String, Object> details = new LinkedHashMap<>();
        String provider = resolveProvider(request);
        if (provider != null) {
            details.put("provider", provider);
        }
        details.put(
                "failureType",
                action == AuditAction.SESSION_REJECTION ? "maximum_sessions_exceeded" : "oauth_authentication_failure"
        );
        if (exception instanceof OAuth2AuthenticationException oauth2AuthenticationException) {
            details.put("errorCode", oauth2AuthenticationException.getError().getErrorCode());
        }
        return details;
    }

    private String resolveProvider(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String callbackPrefix = SecuritySettingsProperties.OAuth.CALLBACK_BASE_URI + "/";
        if (!requestUri.startsWith(callbackPrefix)) {
            return null;
        }
        String registrationId = requestUri.substring(callbackPrefix.length()).trim();
        return registrationId.isEmpty() ? null : registrationId;
    }
}
