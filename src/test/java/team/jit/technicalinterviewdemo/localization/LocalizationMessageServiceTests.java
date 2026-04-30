package team.jit.technicalinterviewdemo.localization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.jit.technicalinterviewdemo.TestcontainersTest;

@TestcontainersTest
@SpringBootTest
class LocalizationMessageServiceTests {

    @Autowired
    private LocalizationMessageRepository localizationMessageRepository;

    @Autowired
    private LocalizationMessageService localizationMessageService;

    @BeforeEach
    void setUp() {
        localizationMessageRepository.deleteAll();
        localizationMessageRepository.saveAll(LocalizationMessageSeedData.defaultMessages());
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
    void getAllMessagesReturnsMessagesForRequestedLanguage() {
        Map<String, String> messages = localizationMessageService.getAllMessages("es");

        assertThat(messages).containsEntry("error.book.not_found", "No se encontro el libro solicitado.");
        assertThat(messages).containsEntry("error.request.invalid", "La solicitud no es valida.");
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
}
