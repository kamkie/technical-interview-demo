package team.jit.technicalinterviewdemo.localization;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class RequestLanguageResolver {

    public static final String DEFAULT_LANGUAGE = "en";

    private static final String LANGUAGE_OVERRIDE_PARAMETER = "lang";
    private static final Pattern SIMPLE_LANGUAGE_PATTERN = Pattern.compile("^[a-zA-Z]{2}$");
    private static final Set<String> SUPPORTED_LANGUAGES = Set.copyOf(LocalizationMessageSeedData.supportedLanguages());

    public String resolvePreferredLanguage(HttpServletRequest request) {
        String languageOverride = request.getParameter(LANGUAGE_OVERRIDE_PARAMETER);
        if (languageOverride != null) {
            return normalizeRequestedLanguage(languageOverride)
                    .filter(SUPPORTED_LANGUAGES::contains)
                    .orElse(DEFAULT_LANGUAGE);
        }

        Enumeration<Locale> locales = request.getLocales();
        while (locales.hasMoreElements()) {
            String candidateLanguage = locales.nextElement().getLanguage();
            if (SUPPORTED_LANGUAGES.contains(candidateLanguage)) {
                return candidateLanguage;
            }
        }

        return DEFAULT_LANGUAGE;
    }

    private Optional<String> normalizeRequestedLanguage(String requestedLanguage) {
        String normalizedLanguage = requestedLanguage == null ? "" : requestedLanguage.trim();
        if (normalizedLanguage.isBlank()) {
            return Optional.empty();
        }

        if (SIMPLE_LANGUAGE_PATTERN.matcher(normalizedLanguage).matches()) {
            return Optional.of(normalizedLanguage.toLowerCase(Locale.ROOT));
        }

        Locale locale = Locale.forLanguageTag(normalizedLanguage.replace('_', '-'));
        if (locale.getLanguage().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(locale.getLanguage().toLowerCase(Locale.ROOT));
    }
}
