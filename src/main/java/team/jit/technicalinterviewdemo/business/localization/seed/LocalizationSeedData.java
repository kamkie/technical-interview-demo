package team.jit.technicalinterviewdemo.business.localization.seed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.jit.technicalinterviewdemo.business.localization.Localization;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Loads the documented {@code error.*} seed translations from per-language UTF-8 classpath resources.
 * Every supported language must ship a resource covering exactly the documented keys.
 */
public final class LocalizationSeedData {

    private static final List<String> DOCUMENTED_KEYS = List.of(
            "error.category.in_use",
            "error.category.not_found",
            "error.book.isbn_duplicate",
            "error.book.not_found",
            "error.book.stale_version",
            "error.data.integrity_violation",
            "error.localization.duplicate",
            "error.localization.not_found",
            "error.request.constraint_violation",
            "error.request.csrf_invalid",
            "error.request.forbidden",
            "error.request.invalid",
            "error.request.invalid_parameter",
            "error.request.malformed_body",
            "error.request.method_not_allowed",
            "error.request.missing_header",
            "error.request.missing_parameter",
            "error.request.resource_not_found",
            "error.request.unauthorized",
            "error.request.unsupported_media_type",
            "error.request.validation_failed",
            "error.user.not_found",
            "error.server.internal");

    private static final Set<String> DOCUMENTED_KEY_SET = Set.copyOf(DOCUMENTED_KEYS);

    private static final List<String> SUPPORTED_LANGUAGES = List.of("en", "es", "de", "fr", "pl", "uk", "no");

    private static final String ERROR_KEY_PREFIX = "error.";
    private static final Pattern MESSAGE_KEY_PATTERN = Pattern.compile("^[a-z0-9._-]+$");
    private static final int MAX_MESSAGE_KEY_LENGTH = 150;
    private static final int MAX_MESSAGE_TEXT_LENGTH = 2000;

    private LocalizationSeedData() {}

    public static List<String> documentedKeys() {
        return DOCUMENTED_KEYS;
    }

    public static List<String> supportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    public static List<Localization> defaultMessages() {
        List<Localization> messages = new ArrayList<>();
        for (String language : SUPPORTED_LANGUAGES) {
            Map<String, String> translations = loadLanguage(language);
            translations.forEach((messageKey, messageText) -> messages.add(
                    new Localization(messageKey, language, messageText, description(messageKey, language))));
        }
        return List.copyOf(messages);
    }

    static Map<String, String> loadLanguage(String language) {
        return loadResource("localization/seed/error-messages/%s.json".formatted(language));
    }

    static Map<String, String> loadResource(String location) {
        Map<String, String> translations = parseResource(location);
        if (!translations.keySet().equals(DOCUMENTED_KEY_SET)) {
            throw new IllegalStateException(
                    "Error message seed resource '%s' must contain exactly the documented error keys."
                            .formatted(location));
        }
        return translations;
    }

    static Map<String, String> parseResource(String location) {
        try (InputStream resourceStream =
                LocalizationSeedData.class.getClassLoader().getResourceAsStream(location)) {
            if (resourceStream == null) {
                throw new IllegalStateException("Error message seed resource '%s' is missing.".formatted(location));
            }
            Map<String, String> translations =
                    new ObjectMapper().readValue(resourceStream, new TypeReference<LinkedHashMap<String, String>>() {});
            translations.forEach((messageKey, messageText) -> validateEntry(location, messageKey, messageText));
            return Collections.unmodifiableMap(translations);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error message seed resource '%s' cannot be parsed.".formatted(location), e);
        }
    }

    private static void validateEntry(String location, String messageKey, String messageText) {
        if (messageKey == null
                || !messageKey.startsWith(ERROR_KEY_PREFIX)
                || messageKey.length() > MAX_MESSAGE_KEY_LENGTH
                || !MESSAGE_KEY_PATTERN.matcher(messageKey).matches()) {
            throw new IllegalStateException("Error message seed resource '%s' contains invalid message key '%s'."
                    .formatted(location, messageKey));
        }
        if (messageText == null || messageText.isBlank() || messageText.length() > MAX_MESSAGE_TEXT_LENGTH) {
            throw new IllegalStateException(
                    "Error message seed resource '%s' contains invalid message text for key '%s'."
                            .formatted(location, messageKey));
        }
    }

    private static String description(String messageKey, String language) {
        return "Seed translation for %s in %s.".formatted(messageKey, language);
    }
}
