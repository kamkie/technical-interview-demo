package team.jit.technicalinterviewdemo.business.localization.seed;

import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.business.localization.Localization;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UiChromeSeedDataTests {

    private static final Pattern MESSAGE_FORMAT_TOKEN_PATTERN =
            Pattern.compile("\\{([a-z0-9_]+)\\}", Pattern.CASE_INSENSITIVE);

    @Test
    void chromeMessagesSatisfyCatalogConstraintsForSupportedLanguagesOnly() {
        List<Localization> messages = UiChromeSeedData.chromeMessages();

        assertThat(messages).isNotEmpty();
        assertThat(messages).allSatisfy(message -> {
            assertThat(message.getMessageKey()).startsWith("ui.").matches("^[a-z0-9._-]+$");
            assertThat(message.getMessageKey().length()).isLessThanOrEqualTo(150);
            assertThat(message.getMessageText()).isNotBlank();
            assertThat(message.getMessageText().length()).isLessThanOrEqualTo(2000);
            assertThat(message.getDescription()).isNotBlank();
            assertThat(LocalizationSeedData.supportedLanguages()).contains(message.getLanguage());
        });
    }

    @Test
    void chromeMessagesDoNotOverlapDefaultSeedIdentities() {
        Set<String> chromeIdentities = UiChromeSeedData.chromeMessages().stream()
                .map(UiChromeSeedDataTests::messageIdentity)
                .collect(Collectors.toUnmodifiableSet());
        Set<String> defaultIdentities = LocalizationSeedData.defaultMessages().stream()
                .map(UiChromeSeedDataTests::messageIdentity)
                .collect(Collectors.toUnmodifiableSet());

        assertThat(chromeIdentities).doesNotContainAnyElementsOf(defaultIdentities);
    }

    @Test
    void englishChromeResourceIsShippedAndNonEmpty() {
        assertThat(UiChromeSeedData.loadLanguage("en", true)).isNotEmpty();
    }

    @Test
    void everySupportedLanguageShipsTheEnglishKeySet() {
        Map<String, String> englishMessages = UiChromeSeedData.loadLanguage("en", true);

        for (String language : LocalizationSeedData.supportedLanguages()) {
            Map<String, String> translations = UiChromeSeedData.loadLanguage(language, true);
            assertThat(translations.keySet())
                    .as("ui chrome keys for language '%s'", language)
                    .isEqualTo(englishMessages.keySet());
        }
    }

    @Test
    void everySupportedLanguagePreservesMessageFormatTokens() {
        Map<String, String> englishMessages = UiChromeSeedData.loadLanguage("en", true);

        for (String language : LocalizationSeedData.supportedLanguages()) {
            Map<String, String> translations = UiChromeSeedData.loadLanguage(language, true);
            translations.forEach((messageKey, messageText) -> assertThat(messageFormatTokens(messageText))
                    .as("message-format tokens for key '%s' in language '%s'", messageKey, language)
                    .isEqualTo(messageFormatTokens(englishMessages.get(messageKey))));
        }
    }

    @Test
    void missingRequiredResourceFailsFast() {
        assertThatThrownBy(() -> UiChromeSeedData.loadLanguage("xx", true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ui-chrome/xx.json");
    }

    @Test
    void missingOptionalResourceReturnsNoTranslations() {
        assertThat(UiChromeSeedData.loadLanguage("xx", false)).isEmpty();
    }

    @Test
    void malformedResourceFailsFast() {
        assertThatThrownBy(() -> UiChromeSeedData.parseResource("localization/seed/ui-chrome-test/malformed.json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be parsed");
    }

    @Test
    void nonChromeKeyFailsFast() {
        assertThatThrownBy(() -> UiChromeSeedData.parseResource("localization/seed/ui-chrome-test/non-chrome-key.json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("invalid message key 'error.sneaky_override'");
    }

    @Test
    void blankMessageTextFailsFast() {
        assertThatThrownBy(() -> UiChromeSeedData.parseResource("localization/seed/ui-chrome-test/blank-text.json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("invalid message text for key 'ui.test.blank'");
    }

    private static Set<String> messageFormatTokens(String messageText) {
        Set<String> tokens = new TreeSet<>();
        Matcher matcher = MESSAGE_FORMAT_TOKEN_PATTERN.matcher(messageText);
        while (matcher.find()) {
            tokens.add(matcher.group(1));
        }
        return tokens;
    }

    private static String messageIdentity(Localization message) {
        return message.getMessageKey() + "::" + message.getLanguage();
    }
}
