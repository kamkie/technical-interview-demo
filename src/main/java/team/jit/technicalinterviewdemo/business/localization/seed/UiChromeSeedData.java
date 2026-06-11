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
import java.util.regex.Pattern;

/**
 * Loads the {@code ui.*} frontend chrome seed translations from per-language classpath resources.
 * The English resource mirrors the first-party frontend's in-code defaults and must always ship;
 * other supported languages are seeded only when their resource exists.
 */
public final class UiChromeSeedData {

    private static final String CHROME_KEY_PREFIX = "ui.";
    private static final String REQUIRED_LANGUAGE = "en";
    private static final Pattern MESSAGE_KEY_PATTERN = Pattern.compile("^[a-z0-9._-]+$");
    private static final int MAX_MESSAGE_KEY_LENGTH = 150;
    private static final int MAX_MESSAGE_TEXT_LENGTH = 2000;

    private UiChromeSeedData() {}

    public static List<Localization> chromeMessages() {
        List<Localization> messages = new ArrayList<>();
        for (String language : LocalizationSeedData.supportedLanguages()) {
            Map<String, String> translations = loadLanguage(language, REQUIRED_LANGUAGE.equals(language));
            translations.forEach((messageKey, messageText) -> messages.add(
                    new Localization(messageKey, language, messageText, description(messageKey, language))));
        }
        return List.copyOf(messages);
    }

    static Map<String, String> loadLanguage(String language, boolean required) {
        String location = "localization/seed/ui-chrome/%s.json".formatted(language);
        if (UiChromeSeedData.class.getClassLoader().getResource(location) == null) {
            if (required) {
                throw new IllegalStateException(
                        "Required ui chrome seed resource '%s' is missing.".formatted(location));
            }
            return Map.of();
        }
        return parseResource(location);
    }

    static Map<String, String> parseResource(String location) {
        try (InputStream resourceStream =
                UiChromeSeedData.class.getClassLoader().getResourceAsStream(location)) {
            if (resourceStream == null) {
                throw new IllegalStateException("Ui chrome seed resource '%s' is missing.".formatted(location));
            }
            Map<String, String> translations =
                    new ObjectMapper().readValue(resourceStream, new TypeReference<LinkedHashMap<String, String>>() {});
            translations.forEach((messageKey, messageText) -> validateEntry(location, messageKey, messageText));
            return Collections.unmodifiableMap(translations);
        } catch (IOException e) {
            throw new IllegalStateException("Ui chrome seed resource '%s' cannot be parsed.".formatted(location), e);
        }
    }

    private static void validateEntry(String location, String messageKey, String messageText) {
        if (messageKey == null
                || !messageKey.startsWith(CHROME_KEY_PREFIX)
                || messageKey.length() > MAX_MESSAGE_KEY_LENGTH
                || !MESSAGE_KEY_PATTERN.matcher(messageKey).matches()) {
            throw new IllegalStateException(
                    "Ui chrome seed resource '%s' contains invalid message key '%s'.".formatted(location, messageKey));
        }
        if (messageText == null || messageText.isBlank() || messageText.length() > MAX_MESSAGE_TEXT_LENGTH) {
            throw new IllegalStateException("Ui chrome seed resource '%s' contains invalid message text for key '%s'."
                    .formatted(location, messageKey));
        }
    }

    private static String description(String messageKey, String language) {
        return "Seed ui chrome translation for %s in %s.".formatted(messageKey, language);
    }
}
