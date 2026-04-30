package team.jit.technicalinterviewdemo.technical.localization;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class RequestLanguageResolver {

    public static final String DEFAULT_LANGUAGE = "en";

    private static final String LANGUAGE_OVERRIDE_PARAMETER = "lang";
    private static final String LANGUAGE_COOKIE_NAME = "language";
    private static final Pattern SIMPLE_LANGUAGE_PATTERN = Pattern.compile("^[a-zA-Z]{2}$");

    public String resolvePreferredLanguage(HttpServletRequest request) {
        String languageOverride = request.getParameter(LANGUAGE_OVERRIDE_PARAMETER);
        if (languageOverride != null) {
            return normalizeRequestedLanguage(languageOverride)
                    .filter(SupportedLanguages::isSupported)
                    .orElse(DEFAULT_LANGUAGE);
        }

        String acceptLanguageHeader = request.getHeader("Accept-Language");
        if (acceptLanguageHeader != null && !acceptLanguageHeader.isBlank()) {
            Enumeration<Locale> locales = request.getLocales();
            while (locales.hasMoreElements()) {
                String candidateLanguage = locales.nextElement().getLanguage();
                if (SupportedLanguages.isSupported(candidateLanguage)) {
                    return candidateLanguage;
                }
            }
        }

        for (Cookie cookie : request.getCookies() == null ? new Cookie[0] : request.getCookies()) {
            if (LANGUAGE_COOKIE_NAME.equals(cookie.getName())) {
                return normalizeRequestedLanguage(cookie.getValue())
                        .filter(SupportedLanguages::isSupported)
                        .orElse(DEFAULT_LANGUAGE);
            }
        }

        return null;
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
