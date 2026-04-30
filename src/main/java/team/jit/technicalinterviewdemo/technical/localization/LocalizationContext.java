package team.jit.technicalinterviewdemo.technical.localization;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.jit.technicalinterviewdemo.technical.security.AuthenticatedUserSecurityService;

@Component
@RequiredArgsConstructor
public class LocalizationContext {

    private static final ThreadLocal<String> CURRENT_LANGUAGE = new ThreadLocal<>();

    private final AuthenticatedUserSecurityService authenticatedUserSecurityService;

    public void setCurrentLanguage(String language) {
        CURRENT_LANGUAGE.set(language);
    }

    public Optional<String> getCurrentLanguage() {
        return Optional.ofNullable(CURRENT_LANGUAGE.get());
    }

    public String resolveCurrentLanguageOrDefault() {
        return getCurrentLanguage()
                .or(() -> authenticatedUserSecurityService.findCurrentUserPreferredLanguage())
                .orElse(RequestLanguageResolver.DEFAULT_LANGUAGE);
    }

    public String getCurrentLanguageOrDefault() {
        return resolveCurrentLanguageOrDefault();
    }

    public void clear() {
        CURRENT_LANGUAGE.remove();
    }
}
