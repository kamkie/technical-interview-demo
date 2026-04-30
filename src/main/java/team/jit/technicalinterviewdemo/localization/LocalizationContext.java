package team.jit.technicalinterviewdemo.localization;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class LocalizationContext {

    private static final ThreadLocal<String> CURRENT_LANGUAGE = new ThreadLocal<>();

    public void setCurrentLanguage(String language) {
        CURRENT_LANGUAGE.set(language);
    }

    public Optional<String> getCurrentLanguage() {
        return Optional.ofNullable(CURRENT_LANGUAGE.get());
    }

    public String getCurrentLanguageOrDefault() {
        return getCurrentLanguage().orElse(RequestLanguageResolver.DEFAULT_LANGUAGE);
    }

    public void clear() {
        CURRENT_LANGUAGE.remove();
    }
}
