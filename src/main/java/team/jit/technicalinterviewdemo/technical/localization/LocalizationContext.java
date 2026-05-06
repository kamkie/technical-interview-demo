package team.jit.technicalinterviewdemo.technical.localization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LocalizationContext {

    private static final ThreadLocal<String> CURRENT_LANGUAGE = new ThreadLocal<>();

    private final CurrentUserAccountService currentUserAccountService;

    public void setCurrentLanguage(String language) {
        CURRENT_LANGUAGE.set(language);
    }

    public Optional<String> getCurrentLanguage() {
        return Optional.ofNullable(CURRENT_LANGUAGE.get());
    }

    public String resolveCurrentLanguageOrDefault() {
        return getCurrentLanguage()
                .or(() -> currentUserAccountService.findCurrentUserPreferredLanguage())
                .orElse(RequestLanguageResolver.DEFAULT_LANGUAGE);
    }

    public void clear() {
        CURRENT_LANGUAGE.remove();
    }
}

