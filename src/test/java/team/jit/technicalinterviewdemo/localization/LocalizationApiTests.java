package team.jit.technicalinterviewdemo.localization;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import team.jit.technicalinterviewdemo.TestcontainersTest;

@TestcontainersTest
@SpringBootTest
@AutoConfigureMockMvc
class LocalizationApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocalizationMessageRepository localizationMessageRepository;

    private LocalizationMessage bookNotFoundEn;
    private LocalizationMessage bookNotFoundEs;
    private LocalizationMessage invalidRequestEn;

    @BeforeEach
    void setUp() {
        localizationMessageRepository.deleteAll();
        List<LocalizationMessage> savedMessages = localizationMessageRepository.saveAll(List.of(
                new LocalizationMessage(
                        "error.book.not_found",
                        "en",
                        "The requested book was not found.",
                        "English message for missing book errors."
                ),
                new LocalizationMessage(
                        "error.book.not_found",
                        "es",
                        "No se encontro el libro solicitado.",
                        "Spanish message for missing book errors."
                ),
                new LocalizationMessage(
                        "error.book.not_found",
                        "de",
                        "Das angeforderte Buch wurde nicht gefunden.",
                        "German message for missing book errors."
                ),
                new LocalizationMessage(
                        "error.request.invalid",
                        "en",
                        "The request is invalid.",
                        "English message for invalid request errors."
                ),
                new LocalizationMessage(
                        "error.request.invalid",
                        "es",
                        "La solicitud no es valida.",
                        "Spanish message for invalid request errors."
                ),
                new LocalizationMessage(
                        "error.request.invalid",
                        "de",
                        "Die Anfrage ist ungueltig.",
                        "German message for invalid request errors."
                )
        ));
        bookNotFoundEn = savedMessages.get(0);
        bookNotFoundEs = savedMessages.get(1);
        invalidRequestEn = savedMessages.get(3);
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
                .andExpect(jsonPath("$.content[0].messageKey").value("error.book.not_found"))
                .andExpect(jsonPath("$.content[0].language").value("de"))
                .andExpect(jsonPath("$.content[1].language").value("en"))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(3));
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
                .andExpect(jsonPath("$.detail").value("Localization message with id 9999 was not found."));
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
    void createLocalizationMessageWithDuplicateKeyAndLanguageReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/localization-messages")
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
                ));
    }

    @Test
    void createLocalizationMessageWithInvalidPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/localization-messages")
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
                .andExpect(jsonPath("$.fieldErrors.messageKey").value("messageKey must match ^[a-z0-9._-]+$"))
                .andExpect(jsonPath("$.fieldErrors.language").value("language must be a two-letter ISO 639-1 code"))
                .andExpect(jsonPath("$.fieldErrors.messageText").value("messageText is required"));
    }

    @Test
    void updateLocalizationMessageReturnsUpdatedMessage() throws Exception {
        mockMvc.perform(put("/api/localization-messages/{id}", bookNotFoundEn.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "error.book.not_found",
                                  "language": "fr",
                                  "messageText": "Le livre demande est introuvable.",
                                  "description": "French message for missing book errors."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookNotFoundEn.getId()))
                .andExpect(jsonPath("$.language").value("fr"))
                .andExpect(jsonPath("$.messageText").value("Le livre demande est introuvable."))
                .andExpect(jsonPath("$.description").value("French message for missing book errors."));
    }

    @Test
    void deleteLocalizationMessageRemovesMessage() throws Exception {
        mockMvc.perform(delete("/api/localization-messages/{id}", bookNotFoundEn.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/localization-messages/{id}", bookNotFoundEn.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Localization Message Not Found"));
    }

    @Test
    void listLocalizationMessagesByLanguageReturnsMessagesForLanguage() throws Exception {
        mockMvc.perform(get("/api/localization-messages/language/{language}", "es"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].language").value("es"))
                .andExpect(jsonPath("$[0].messageKey").value("error.book.not_found"))
                .andExpect(jsonPath("$[1].messageKey").value("error.request.invalid"));
    }
}
