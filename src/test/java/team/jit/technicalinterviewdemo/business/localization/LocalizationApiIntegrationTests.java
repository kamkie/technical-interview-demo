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
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.LocalizationMessageTestData;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
class LocalizationApiIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Autowired
    private LocalizationMessageRepository localizationMessageRepository;

    private LocalizationMessage bookNotFoundEn;
    private LocalizationMessage bookNotFoundEs;
    private LocalizationMessage invalidRequestEn;

    @BeforeEach
    void setUp() {
        LocalizationMessageTestData.DefaultLocalizationMessages messages =
                LocalizationMessageTestData.reloadDefaultMessages(localizationMessageRepository);
        bookNotFoundEn = messages.bookNotFoundEn();
        bookNotFoundEs = messages.bookNotFoundEs();
        invalidRequestEn = messages.invalidRequestEn();
    }

    @Test
    void listLocalizationMessagesReturnsPaginatedResponse() throws Exception {
        mockMvc.perform(get("/api/localization-messages")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sort", "messageKey,asc")
                        .queryParam("sort", "language,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].messageKey").value("error.book.isbn_duplicate"))
                .andExpect(jsonPath("$.content[0].language").value("de"))
                .andExpect(jsonPath("$.content[1].language").value("en"))
                .andExpect(jsonPath("$.totalElements").value(126))
                .andExpect(jsonPath("$.totalPages").value(63));
    }

    @Test
    void listLocalizationMessagesWithUnsupportedSortReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/localization-messages")
                        .queryParam("sort", "dropTable,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value(
                        "Sort field 'dropTable' is not supported. Use one of: id, messageKey, language, createdAt, updatedAt."
                ));
    }

    @Test
    void getLocalizationMessageByIdReturnsRequestedMessage() throws Exception {
        mockMvc.perform(get("/api/localization-messages/{id}", bookNotFoundEs.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookNotFoundEs.getId()))
                .andExpect(jsonPath("$.messageKey").value("error.book.not_found"))
                .andExpect(jsonPath("$.language").value("es"))
                .andExpect(jsonPath("$.messageText").value("No se encontro el libro solicitado."));
    }

    @Test
    void getLocalizationMessageByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/localization-messages/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Localization Message Not Found"))
                .andExpect(jsonPath("$.detail").value("Localization message with id 9999 was not found."))
                .andExpect(jsonPath("$.messageKey").value("error.localization.not_found"))
                .andExpect(jsonPath("$.message").value("The requested localization message was not found."))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void getLocalizationMessageByKeyAndLanguageReturnsRequestedMessage() throws Exception {
        mockMvc.perform(get("/api/localization-messages/key/{messageKey}/lang/{language}", "error.request.invalid", "EN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invalidRequestEn.getId()))
                .andExpect(jsonPath("$.messageKey").value("error.request.invalid"))
                .andExpect(jsonPath("$.language").value("en"))
                .andExpect(jsonPath("$.messageText").value("The request is invalid."));
    }

    @Test
    void createLocalizationMessageReturnsCreatedMessage() throws Exception {
        mockMvc.perform(post("/api/localization-messages")
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
    void createLocalizationMessageWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/localization-messages")
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
    void createLocalizationMessageWithDuplicateKeyAndLanguageReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/localization-messages")
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
                .andExpect(jsonPath("$.title").value("Duplicate Localization Message"))
                .andExpect(jsonPath("$.detail").value(
                        "Localization message with key 'error.book.not_found' and language 'es' already exists."
                ))
                .andExpect(jsonPath("$.messageKey").value("error.localization.duplicate"))
                .andExpect(jsonPath("$.message").value("A localization message with the same key and language already exists."))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void createLocalizationMessageWithInvalidPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/localization-messages")
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
    void createLocalizationMessageWithUnsupportedLanguageReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/localization-messages")
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
    void updateLocalizationMessageReturnsUpdatedMessage() throws Exception {
        mockMvc.perform(put("/api/localization-messages/{id}", bookNotFoundEn.getId())
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
    void deleteLocalizationMessageWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/localization-messages/{id}", bookNotFoundEn.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteLocalizationMessageRemovesMessage() throws Exception {
        mockMvc.perform(delete("/api/localization-messages/{id}", bookNotFoundEn.getId())
                        .with(adminOauthUser()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/localization-messages/{id}", bookNotFoundEn.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Localization Message Not Found"));
    }

    @Test
    void createLocalizationMessageAsRegularUserReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/localization-messages")
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
    void listLocalizationMessagesByLanguageReturnsMessagesForLanguage() throws Exception {
        mockMvc.perform(get("/api/localization-messages/language/{language}", "es"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(18))
                .andExpect(jsonPath("$[0].language").value("es"))
                .andExpect(jsonPath("$[0].messageKey").value("error.book.isbn_duplicate"))
                .andExpect(jsonPath("$[17].messageKey").value("error.server.internal"));
    }
}
