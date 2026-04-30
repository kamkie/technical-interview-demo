package team.jit.technicalinterviewdemo.business.localization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.jit.technicalinterviewdemo.business.localization.seed.LocalizationMessageSeedData;
import team.jit.technicalinterviewdemo.technical.localization.LocalizationContext;
import team.jit.technicalinterviewdemo.testing.IntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.LocalizationMessageTestData;

@IntegrationSpringBootTest
class LocalizationMessageServiceTests {

    @Autowired
    private LocalizationMessageRepository localizationMessageRepository;

    @Autowired
    private LocalizationMessageService localizationMessageService;

    @Autowired
    private LocalizationContext localizationContext;

    @BeforeEach
    void setUp() {
        LocalizationMessageTestData.reloadDefaultMessages(localizationMessageRepository);
        localizationContext.clear();
    }

    @Test
    void getMessageReturnsSeededMessageForRequestedLanguage() {
        String message = localizationMessageService.getMessage("error.book.not_found", "EN");
        LocalizationMessage storedMessage = localizationMessageRepository.findByMessageKeyAndLanguage("error.book.not_found", "en")
                .orElseThrow();

        assertThat(message).isEqualTo("The requested book was not found.");
        assertThat(storedMessage.getCreatedAt()).isNotNull();
        assertThat(storedMessage.getUpdatedAt()).isNotNull();
    }

    @Test
    void getMessageWithFallbackReturnsFallbackWhenRequestedLanguageIsMissing() {
        String message = localizationMessageService.getMessageWithFallback("error.request.invalid", "it", "en");

        assertThat(message).isEqualTo("The request is invalid.");
    }

    @Test
    void findMessageWithFallbackReturnsResolvedLanguage() {
        LocalizationMessage message = localizationMessageService.findByMessageKeyAndLanguageWithFallback("error.request.invalid", "it", "en");

        assertThat(message.getLanguage()).isEqualTo("en");
        assertThat(message.getMessageText()).isEqualTo("The request is invalid.");
    }

    @Test
    void findMessageForCurrentLanguageUsesLocalizationContext() {
        localizationContext.setCurrentLanguage("uk");

        LocalizationMessage message = localizationMessageService.findByMessageKeyForCurrentLanguageWithFallback("error.request.invalid");

        assertThat(message.getLanguage()).isEqualTo("uk");
        assertThat(message.getMessageText()).isEqualTo("Zapyt ye nevalidnym.");
    }

    @Test
    void getAllMessagesReturnsMessagesForRequestedLanguage() {
        Map<String, String> messages = localizationMessageService.getAllMessages("pl");

        assertThat(messages).containsEntry("error.book.not_found", "Nie znaleziono zadanej ksiazki.");
        assertThat(messages).containsEntry("error.request.invalid", "Zadanie jest nieprawidlowe.");
    }

    @Test
    void seedDataIncludesAllDocumentedKeysForAllSupportedLanguages() {
        for (String language : LocalizationMessageSeedData.supportedLanguages()) {
            Map<String, String> messages = localizationMessageService.getAllMessages(language);

            assertThat(messages.keySet()).containsAll(LocalizationMessageSeedData.documentedKeys());
            assertThat(messages).hasSize(LocalizationMessageSeedData.documentedKeys().size());
        }
    }

    @Test
    void getMessageWithFallbackThrowsWhenMessageIsMissingInAllLanguages() {
        assertThatThrownBy(() -> localizationMessageService.getMessageWithFallback("error.unknown", "fr", "en"))
                .isInstanceOf(LocalizationMessageNotFoundException.class)
                .hasMessage("Localization message with key 'error.unknown' was not found for language 'fr' or fallback language 'en'.");
    }

    @Test
    void getAllMessagesRejectsUnsupportedLanguage() {
        assertThatThrownBy(() -> localizationMessageService.getAllMessages("it"))
                .isInstanceOf(team.jit.technicalinterviewdemo.technical.api.InvalidRequestException.class)
                .hasMessage("language must be one of: en, es, de, fr, pl, uk, no.");
    }
}
