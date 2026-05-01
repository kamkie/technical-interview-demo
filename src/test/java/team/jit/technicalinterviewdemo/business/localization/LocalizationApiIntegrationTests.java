package team.jit.technicalinterviewdemo.business.localization;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminOauthUser;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.oauthUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import team.jit.technicalinterviewdemo.business.localization.seed.LocalizationSeedData;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testdata.LocalizationTestData;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
class LocalizationApiIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Autowired
    private LocalizationRepository localizationRepository;

    private Localization bookNotFoundEn;
    private Localization bookNotFoundEs;
    private Localization invalidRequestEn;

    @BeforeEach
    void setUp() {
        LocalizationTestData.DefaultLocalizations messages =
                LocalizationTestData.reloadDefaultMessages(localizationRepository);
        bookNotFoundEn = messages.bookNotFoundEn();
        bookNotFoundEs = messages.bookNotFoundEs();
        invalidRequestEn = messages.invalidRequestEn();
    }

    @Test
    void listLocalizationsReturnsPaginatedResponse() throws Exception {
        mockMvc.perform(get("/api/localizations")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sort", "messageKey,asc")
                        .queryParam("sort", "language,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].messageKey").value("error.book.isbn_duplicate"))
                .andExpect(jsonPath("$.content[0].language").value("de"))
                .andExpect(jsonPath("$.content[1].language").value("en"))
                .andExpect(jsonPath("$.totalElements").value(totalSeededLocalizations()))
                .andExpect(jsonPath("$.totalPages").value(totalPagesForPageSize(2)));
    }

    @Test
    void listLocalizationsWithUnsupportedSortReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/localizations")
                        .queryParam("sort", "dropTable,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value(
                        "Sort field 'dropTable' is not supported. Use one of: id, messageKey, language, createdAt, updatedAt."
                ));
    }

    @Test
    void getLocalizationByIdReturnsRequestedMessage() throws Exception {
        mockMvc.perform(get("/api/localizations/{id}", bookNotFoundEs.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookNotFoundEs.getId()))
                .andExpect(jsonPath("$.messageKey").value("error.book.not_found"))
                .andExpect(jsonPath("$.language").value("es"))
                .andExpect(jsonPath("$.messageText").value("No se encontro el libro solicitado."));
    }

    @Test
    void getLocalizationByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/localizations/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Localization Not Found"))
                .andExpect(jsonPath("$.detail").value("Localization with id 9999 was not found."))
                .andExpect(jsonPath("$.messageKey").value("error.localization.not_found"))
                .andExpect(jsonPath("$.message").value("The requested localization message was not found."))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void listLocalizationsCanFilterByMessageKeyAndLanguage() throws Exception {
        mockMvc.perform(get("/api/localizations")
                        .queryParam("messageKey", "error.request.invalid")
                        .queryParam("language", "EN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(invalidRequestEn.getId()))
                .andExpect(jsonPath("$.content[0].messageKey").value("error.request.invalid"))
                .andExpect(jsonPath("$.content[0].language").value("en"))
                .andExpect(jsonPath("$.content[0].messageText").value("The request is invalid."))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void createLocalizationReturnsCreatedMessage() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "info.book.created",
                                  "language": "fr",
                                  "messageText": "Le livre a ete cree.",
                                  "description": "French success message for new books."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.messageKey").value("info.book.created"))
                .andExpect(jsonPath("$.language").value("fr"))
                .andExpect(jsonPath("$.messageText").value("Le livre a ete cree."))
                .andExpect(jsonPath("$.description").value("French success message for new books."))
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.updatedAt").isString());
    }

    @Test
    void createLocalizationWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "info.book.created",
                                  "language": "fr",
                                  "messageText": "Le livre a ete cree.",
                                  "description": "French success message for new books."
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createLocalizationWithDuplicateKeyAndLanguageReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "error.book.not_found",
                                  "language": "es",
                                  "messageText": "Duplicated message.",
                                  "description": "Should fail."
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Duplicate Localization"))
                .andExpect(jsonPath("$.detail").value("Localization with key 'error.book.not_found' and language 'es' already exists."))
                .andExpect(jsonPath("$.messageKey").value("error.localization.duplicate"))
                .andExpect(jsonPath("$.message").value("A localization message with the same key and language already exists."))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void createLocalizationWithInvalidPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "Invalid Key",
                                  "language": "english",
                                  "messageText": " ",
                                  "description": "x"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.messageKey").value("error.request.validation_failed"))
                .andExpect(jsonPath("$.message").value("Request body validation failed."))
                .andExpect(jsonPath("$.language").value("en"))
                .andExpect(jsonPath("$.fieldErrors.messageKey").value("messageKey must match ^[a-z0-9._-]+$"))
                .andExpect(jsonPath("$.fieldErrors.language").value("language must be a two-letter ISO 639-1 code"))
                .andExpect(jsonPath("$.fieldErrors.messageText").value("messageText is required"));
    }

    @Test
    void createLocalizationWithUnsupportedLanguageReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "info.book.created",
                                  "language": "it",
                                  "messageText": "Libro creato.",
                                  "description": "Unsupported language."
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("language must be one of: en, es, de, fr, pl, uk, no."))
                .andExpect(jsonPath("$.messageKey").value("error.request.invalid"))
                .andExpect(jsonPath("$.message").value("The request is invalid."))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void updateLocalizationReturnsUpdatedMessage() throws Exception {
        mockMvc.perform(put("/api/localizations/{id}", bookNotFoundEn.getId())
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "error.book.not_found_custom",
                                  "language": "fr",
                                  "messageText": "Le livre demande est introuvable.",
                                  "description": "French message for missing book errors."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookNotFoundEn.getId()))
                .andExpect(jsonPath("$.messageKey").value("error.book.not_found_custom"))
                .andExpect(jsonPath("$.language").value("fr"))
                .andExpect(jsonPath("$.messageText").value("Le livre demande est introuvable."))
                .andExpect(jsonPath("$.description").value("French message for missing book errors."));
    }

    @Test
    void deleteLocalizationWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/localizations/{id}", bookNotFoundEn.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteLocalizationRemovesMessage() throws Exception {
        mockMvc.perform(delete("/api/localizations/{id}", bookNotFoundEn.getId())
                        .with(adminOauthUser()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/localizations/{id}", bookNotFoundEn.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Localization Not Found"));
    }

    @Test
    void createLocalizationAsRegularUserReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "info.book.created",
                                  "language": "fr",
                                  "messageText": "Le livre a ete cree.",
                                  "description": "French success message for new books."
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.detail").value("Localization management requires the ADMIN role."))
                .andExpect(jsonPath("$.messageKey").value("error.request.forbidden"))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void listLocalizationsCanFilterByLanguage() throws Exception {
        mockMvc.perform(get("/api/localizations")
                        .queryParam("language", "es")
                        .queryParam("sort", "messageKey,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(documentedKeyCount()))
                .andExpect(jsonPath("$.content[0].language").value("es"))
                .andExpect(jsonPath("$.content[0].messageKey").value("error.book.isbn_duplicate"))
                .andExpect(jsonPath("$.content[%d].messageKey".formatted(documentedKeyCount() - 1)).value("error.server.internal"))
                .andExpect(jsonPath("$.totalElements").value(documentedKeyCount()))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    private int documentedKeyCount() {
        return LocalizationSeedData.documentedKeys().size();
    }

    private int totalSeededLocalizations() {
        return LocalizationSeedData.documentedKeys().size() * LocalizationSeedData.supportedLanguages().size();
    }

    private int totalPagesForPageSize(int pageSize) {
        return (totalSeededLocalizations() + pageSize - 1) / pageSize;
    }
}
