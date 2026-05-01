package team.jit.technicalinterviewdemo.business.localization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.jit.technicalinterviewdemo.business.localization.seed.LocalizationSeedData;
import team.jit.technicalinterviewdemo.technical.localization.LocalizationContext;
import team.jit.technicalinterviewdemo.testing.IntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.LocalizationTestData;

@IntegrationSpringBootTest
class LocalizationServiceTests {

    @Autowired
    private LocalizationRepository localizationRepository;

    @Autowired
    private LocalizationService localizationService;

    @Autowired
    private LocalizationContext localizationContext;

    @BeforeEach
    void setUp() {
        LocalizationTestData.reloadDefaultMessages(localizationRepository);
        localizationContext.clear();
    }

    @Test
    void getMessageReturnsSeededMessageForRequestedLanguage() {
        String message = localizationService.getMessage("error.book.not_found", "EN");
        Localization storedMessage = localizationRepository.findByMessageKeyAndLanguage("error.book.not_found", "en")
                .orElseThrow();

        assertThat(message).isEqualTo("The requested book was not found.");
        assertThat(storedMessage.getCreatedAt()).isNotNull();
        assertThat(storedMessage.getUpdatedAt()).isNotNull();
    }

    @Test
    void getMessageWithFallbackReturnsFallbackWhenRequestedLanguageIsMissing() {
        String message = localizationService.getMessageWithFallback("error.request.invalid", "it", "en");

        assertThat(message).isEqualTo("The request is invalid.");
    }

    @Test
    void findMessageWithFallbackReturnsResolvedLanguage() {
        Localization message = localizationService.findByMessageKeyAndLanguageWithFallback("error.request.invalid", "it", "en");

        assertThat(message.getLanguage()).isEqualTo("en");
        assertThat(message.getMessageText()).isEqualTo("The request is invalid.");
    }

    @Test
    void findMessageForCurrentLanguageUsesLocalizationContext() {
        localizationContext.setCurrentLanguage("uk");

        Localization message = localizationService.findByMessageKeyForCurrentLanguageWithFallback("error.request.invalid");

        assertThat(message.getLanguage()).isEqualTo("uk");
        assertThat(message.getMessageText()).isEqualTo("Zapyt ye nevalidnym.");
    }

    @Test
    void getAllMessagesReturnsMessagesForRequestedLanguage() {
        Map<String, String> messages = localizationService.getAllMessages("pl");

        assertThat(messages).containsEntry("error.book.not_found", "Nie znaleziono zadanej ksiazki.");
        assertThat(messages).containsEntry("error.request.invalid", "Zadanie jest nieprawidlowe.");
    }

    @Test
    void seedDataIncludesAllDocumentedKeysForAllSupportedLanguages() {
        for (String language : LocalizationSeedData.supportedLanguages()) {
            Map<String, String> messages = localizationService.getAllMessages(language);

            assertThat(messages.keySet()).containsAll(LocalizationSeedData.documentedKeys());
            assertThat(messages).hasSize(LocalizationSeedData.documentedKeys().size());
        }
    }

    @Test
    void getMessageWithFallbackThrowsWhenMessageIsMissingInAllLanguages() {
        assertThatThrownBy(() -> localizationService.getMessageWithFallback("error.unknown", "fr", "en"))
                .isInstanceOf(LocalizationNotFoundException.class)
                .hasMessage("Localization with key 'error.unknown' was not found for language 'fr' or fallback language 'en'.");
    }

    @Test
    void getAllMessagesRejectsUnsupportedLanguage() {
        assertThatThrownBy(() -> localizationService.getAllMessages("it"))
                .isInstanceOf(team.jit.technicalinterviewdemo.technical.api.InvalidRequestException.class)
                .hasMessage("language must be one of: en, es, de, fr, pl, uk, no.");
    }
}
