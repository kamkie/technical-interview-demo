package team.jit.technicalinterviewdemo.business.localization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.clearAuthentication;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.setAdminAuthenticatedUser;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import team.jit.technicalinterviewdemo.business.localization.seed.LocalizationSeedData;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;
import team.jit.technicalinterviewdemo.technical.localization.LocalizationContext;
import team.jit.technicalinterviewdemo.testdata.LocalizationTestData;
import team.jit.technicalinterviewdemo.testing.CacheTestSupport;
import team.jit.technicalinterviewdemo.testing.IntegrationSpringBootTest;

@IntegrationSpringBootTest
class LocalizationServiceTests {

    @Autowired
    private LocalizationRepository localizationRepository;

    @Autowired
    private LocalizationService localizationService;

    @Autowired
    private LocalizationContext localizationContext;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        LocalizationTestData.reloadDefaultMessages(localizationRepository);
        localizationContext.clear();
        clearAuthentication();
        clearLocalizationCaches();
    }

    @AfterEach
    void tearDown() {
        clearAuthentication();
        clearLocalizationCaches();
    }

    @Test
    void getMessageReturnsSeededMessageForRequestedLanguage() {
        String message = localizationService.getMessage("error.book.not_found", "EN");
        Localization storedMessage = localizationRepository.findByMessageKeyAndLanguage("error.book.not_found", "en").orElseThrow();

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
    void findMessageWithFallbackCachesRequestedLanguageHitUsingNormalizedLookupKey() {
        Localization message = localizationService.findByMessageKeyAndLanguageWithFallback("error.book.not_found", "ES", "en");

        Localization cachedMessage = lookupCache().get(lookupCacheKey("error.book.not_found", "es", "en"), Localization.class);

        assertThat(message.getLanguage()).isEqualTo("es");
        assertThat(message.getMessageText()).isEqualTo("No se encontro el libro solicitado.");
        assertThat(cachedMessage).isNotNull();
        assertThat(cachedMessage.getLanguage()).isEqualTo("es");
        assertThat(cachedMessage.getMessageText()).isEqualTo("No se encontro el libro solicitado.");
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
    void getAllMessagesCachesMessagesUsingNormalizedLanguageKey() {
        Map<String, String> messages = localizationService.getAllMessages(" EN ");

        @SuppressWarnings("unchecked") Map<String, String> cachedMessages = (Map<String, String>) cache(CacheNames.LOCALIZATION_MESSAGE_MAPS).get("en").get();

        assertThat(cachedMessages).isEqualTo(messages);
    }

    @Test
    void findAllByLanguageCachesMessagesUsingNormalizedLanguageKey() {
        List<Localization> messages = localizationService.findAllByLanguage(" EN ");

        @SuppressWarnings("unchecked") List<Localization> cachedMessages = (List<Localization>) cache(CacheNames.LOCALIZATION_LISTS).get("en").get();

        assertThat(cachedMessages).isEqualTo(messages);
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
        assertThatThrownBy(() -> localizationService.getMessageWithFallback("error.unknown", "fr", "en")).isInstanceOf(LocalizationNotFoundException.class).hasMessage("Localization with key 'error.unknown' was not found for language 'fr' or fallback language 'en'.");
    }

    @Test
    void findMessageWithFallbackDoesNotCacheMissingLookup() {
        assertThatThrownBy(() -> localizationService.findByMessageKeyAndLanguageWithFallback("error.unknown", "fr", "en")).isInstanceOf(LocalizationNotFoundException.class).hasMessage("Localization with key 'error.unknown' was not found for language 'fr' or fallback language 'en'.");

        assertThat(lookupCache().get(lookupCacheKey("error.unknown", "fr", "en"))).isNull();
    }

    @Test
    void getAllMessagesRejectsUnsupportedLanguage() {
        assertThatThrownBy(() -> localizationService.getAllMessages("it")).isInstanceOf(team.jit.technicalinterviewdemo.technical.api.InvalidRequestException.class).hasMessage("language must be one of: en, es, de, fr, pl, uk, no.");
    }

    @Test
    void createEvictsLookupListAndMessageMapCaches() {
        primeLocalizationCaches();
        setAdminAuthenticatedUser();

        Localization createdMessage = localizationService.create(new LocalizationRequest(
                "info.book.created", "fr", "Le livre a ete cree.", "French success message for new books."
        ));

        assertLocalizationCachesCleared();
        assertThat(createdMessage.getMessageKey()).isEqualTo("info.book.created");
        assertThat(localizationService.getMessage("info.book.created", "fr")).isEqualTo("Le livre a ete cree.");
    }

    @Test
    void updateEvictsLookupListAndMessageMapCaches() {
        primeLocalizationCaches();
        setAdminAuthenticatedUser();
        Localization message = localizationService.findByMessageKeyAndLanguage("error.book.not_found", "es");

        localizationService.update(message.getId(), new LocalizationRequest(
                "error.book.not_found", "es", "No se encontro el libro solicitado. Actualizado.", "Updated Spanish missing-book message."
        ));

        assertLocalizationCachesCleared();
        assertThat(localizationService.getMessageWithFallback("error.book.not_found", "es", "en")).isEqualTo("No se encontro el libro solicitado. Actualizado.");
    }

    @Test
    void deleteEvictsLookupListAndMessageMapCaches() {
        primeLocalizationCaches();
        setAdminAuthenticatedUser();
        Localization message = localizationService.findByMessageKeyAndLanguage("error.book.not_found", "es");

        localizationService.delete(message.getId());

        assertLocalizationCachesCleared();
        assertThat(localizationRepository.findById(message.getId())).isEmpty();
    }

    private void primeLocalizationCaches() {
        assertThat(localizationService.getMessageWithFallback("error.book.not_found", "es", "en")).isEqualTo("No se encontro el libro solicitado.");
        assertThat(localizationService.findAllByLanguage("EN")).hasSize(LocalizationSeedData.documentedKeys().size());
        assertThat(localizationService.getAllMessages("EN")).containsEntry("error.request.invalid", "The request is invalid.");

        assertThat(lookupCache().get(lookupCacheKey("error.book.not_found", "es", "en"))).isNotNull();
        assertThat(cache(CacheNames.LOCALIZATION_LISTS).get("en")).isNotNull();
        assertThat(cache(CacheNames.LOCALIZATION_MESSAGE_MAPS).get("en")).isNotNull();
    }

    private void assertLocalizationCachesCleared() {
        assertThat(lookupCache().get(lookupCacheKey("error.book.not_found", "es", "en"))).isNull();
        assertThat(cache(CacheNames.LOCALIZATION_LISTS).get("en")).isNull();
        assertThat(cache(CacheNames.LOCALIZATION_MESSAGE_MAPS).get("en")).isNull();
    }

    private void clearLocalizationCaches() {
        CacheTestSupport.clearCaches(
                cacheManager, CacheNames.LOCALIZATION_LOOKUPS, CacheNames.LOCALIZATION_LISTS, CacheNames.LOCALIZATION_MESSAGE_MAPS
        );
    }

    private Cache lookupCache() {
        return cache(CacheNames.LOCALIZATION_LOOKUPS);
    }

    private Cache cache(String cacheName) {
        return CacheTestSupport.cache(cacheManager, cacheName);
    }

    private String lookupCacheKey(String messageKey, String language, String fallbackLanguage) {
        return "%s::%s::%s".formatted(messageKey, language, fallbackLanguage);
    }
}
