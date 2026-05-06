package team.jit.technicalinterviewdemo.technical.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationService;
import team.jit.technicalinterviewdemo.technical.logging.SensitiveDataSanitizer;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiProblemFactory {

    private final LocalizationService localizationService;

    public ProblemDetail clientProblem(
            HttpStatus status,
            String title,
            String detail,
            String messageKey,
            HttpServletRequest request,
            Map<String, ?> context) {
        LocalizedProblemMessage localizedMessage = resolveLocalizedProblemMessage(messageKey);
        Map<String, Object> enrichedContext = new LinkedHashMap<>(context);
        enrichedContext.put("messageKey", localizedMessage.messageKey());
        enrichedContext.put("language", localizedMessage.language());
        String requestMethod = SensitiveDataSanitizer.sanitizeForLog(request.getMethod());
        String requestPath = SensitiveDataSanitizer.sanitizeForLog(request.getRequestURI());
        log.warn(
                "Handled client error status={} method={} path={} params={} title='{}' detail='{}'"
                        + " localizedMessage='{}' context={}",
                status.value(),
                requestMethod,
                requestPath,
                SensitiveDataSanitizer.sanitizeParameters(request.getParameterMap()),
                SensitiveDataSanitizer.sanitizeForLog(title),
                SensitiveDataSanitizer.sanitizeForLog(detail),
                SensitiveDataSanitizer.sanitizeForLog(localizedMessage.message()),
                SensitiveDataSanitizer.sanitizeContextForLog(enrichedContext));
        return createProblemDetail(status, title, detail, localizedMessage);
    }

    public ProblemDetail serverProblem(
            HttpStatus status,
            String title,
            String detail,
            String messageKey,
            HttpServletRequest request,
            Map<String, ?> context,
            Exception exception) {
        LocalizedProblemMessage localizedMessage = resolveLocalizedProblemMessage(messageKey);
        Map<String, Object> enrichedContext = new LinkedHashMap<>(context);
        enrichedContext.put("messageKey", localizedMessage.messageKey());
        enrichedContext.put("language", localizedMessage.language());
        String requestMethod = SensitiveDataSanitizer.sanitizeForLog(request.getMethod());
        String requestPath = SensitiveDataSanitizer.sanitizeForLog(request.getRequestURI());
        log.error(
                "Handled server error status={} method={} path={} params={} title='{}' detail='{}'"
                        + " localizedMessage='{}' context={}",
                status.value(),
                requestMethod,
                requestPath,
                SensitiveDataSanitizer.sanitizeParameters(request.getParameterMap()),
                SensitiveDataSanitizer.sanitizeForLog(title),
                SensitiveDataSanitizer.sanitizeForLog(detail),
                SensitiveDataSanitizer.sanitizeForLog(localizedMessage.message()),
                SensitiveDataSanitizer.sanitizeContextForLog(enrichedContext),
                exception);
        return createProblemDetail(status, title, detail, localizedMessage);
    }

    private ProblemDetail createProblemDetail(
            HttpStatus status, String title, String detail, LocalizedProblemMessage localizedMessage) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("messageKey", localizedMessage.messageKey());
        problemDetail.setProperty("message", localizedMessage.message());
        problemDetail.setProperty("language", localizedMessage.language());
        return problemDetail;
    }

    private LocalizedProblemMessage resolveLocalizedProblemMessage(String messageKey) {
        Localization resolvedMessage = localizationService.findByMessageKeyForCurrentLanguageWithFallback(messageKey);
        return new LocalizedProblemMessage(messageKey, resolvedMessage.getMessageText(), resolvedMessage.getLanguage());
    }

    private record LocalizedProblemMessage(String messageKey, String message, String language) {}
}
