package team.jit.technicalinterviewdemo.business.localization.seed;

import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.business.localization.Localization;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalizationSeedDataTests {

    @Test
    void defaultMessagesSatisfyCatalogConstraintsForSupportedLanguagesOnly() {
        List<Localization> messages = LocalizationSeedData.defaultMessages();

        assertThat(messages).isNotEmpty();
        assertThat(messages).allSatisfy(message -> {
            assertThat(message.getMessageKey()).startsWith("error.").matches("^[a-z0-9._-]+$");
            assertThat(message.getMessageKey().length()).isLessThanOrEqualTo(150);
            assertThat(message.getMessageText()).isNotBlank();
            assertThat(message.getMessageText().length()).isLessThanOrEqualTo(2000);
            assertThat(message.getDescription()).isNotBlank();
            assertThat(LocalizationSeedData.supportedLanguages()).contains(message.getLanguage());
        });
    }

    @Test
    void defaultMessagesCoverEveryDocumentedKeyInEverySupportedLanguage() {
        List<Localization> messages = LocalizationSeedData.defaultMessages();

        assertThat(messages)
                .hasSize(LocalizationSeedData.documentedKeys().size()
                        * LocalizationSeedData.supportedLanguages().size());
    }

    @Test
    void everySupportedLanguageResourceShipsExactlyTheDocumentedKeys() {
        Set<String> documentedKeys = Set.copyOf(LocalizationSeedData.documentedKeys());

        for (String language : LocalizationSeedData.supportedLanguages()) {
            Map<String, String> translations = LocalizationSeedData.loadLanguage(language);
            assertThat(translations.keySet())
                    .as("error message keys for language '%s'", language)
                    .isEqualTo(documentedKeys);
        }
    }

    @Test
    void missingResourceFailsFast() {
        assertThatThrownBy(() -> LocalizationSeedData.loadLanguage("xx"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("error-messages/xx.json");
    }

    @Test
    void malformedResourceFailsFast() {
        assertThatThrownBy(() ->
                        LocalizationSeedData.parseResource("localization/seed/error-messages-test/malformed.json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be parsed");
    }

    @Test
    void nonErrorKeyFailsFast() {
        assertThatThrownBy(() ->
                        LocalizationSeedData.parseResource("localization/seed/error-messages-test/non-error-key.json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("invalid message key 'ui.sneaky_override'");
    }

    @Test
    void blankMessageTextFailsFast() {
        assertThatThrownBy(() ->
                        LocalizationSeedData.parseResource("localization/seed/error-messages-test/blank-text.json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("invalid message text for key 'error.test.blank'");
    }

    @Test
    void incompleteDocumentedKeySetFailsFast() {
        assertThatThrownBy(() ->
                        LocalizationSeedData.loadResource("localization/seed/error-messages-test/missing-key.json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must contain exactly the documented error keys");
    }
}
