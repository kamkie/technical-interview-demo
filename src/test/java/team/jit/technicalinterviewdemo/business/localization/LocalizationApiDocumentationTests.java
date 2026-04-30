package team.jit.technicalinterviewdemo.business.localization;

import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.SecurityTestSupport.adminOauthUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import team.jit.technicalinterviewdemo.technical.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.technical.testing.LocalizationMessageTestData;
import team.jit.technicalinterviewdemo.technical.testing.RestDocsIntegrationSpringBootTest;

@RestDocsIntegrationSpringBootTest
class LocalizationApiDocumentationTests extends AbstractDocumentationIntegrationTest {

    @Autowired
    private LocalizationMessageRepository localizationMessageRepository;

    private LocalizationMessage bookNotFoundEn;
    private LocalizationMessage bookNotFoundEs;

    @BeforeEach
    void setUp() {
        LocalizationMessageTestData.DefaultLocalizationMessages messages =
                LocalizationMessageTestData.reloadDefaultMessages(localizationMessageRepository);
        bookNotFoundEn = messages.bookNotFoundEn();
        bookNotFoundEs = messages.bookNotFoundEs();
    }

    @Test
    void documentListLocalizationMessagesEndpoint() throws Exception {
        mockMvc.perform(get("/api/localization-messages")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sort", "messageKey,asc")
                        .queryParam("sort", "language,asc"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/list-localization-messages",
                        queryParameters(
                                parameterWithName("page").optional().description("Zero-based page index."),
                                parameterWithName("size").optional().description("Page size capped by the server."),
                                parameterWithName("sort").optional().description(
                                        "Sort expression in the form `property,direction`. Repeat the parameter for multiple sort fields. Supported properties: `id`, `messageKey`, `language`, `createdAt`, `updatedAt`."
                                )
                        ),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("content[].id").description("Localization message identifier."),
                                fieldWithPath("content[].messageKey").description("Stable localization message key."),
                                fieldWithPath("content[].language").description("Two-letter ISO 639-1 language code."),
                                fieldWithPath("content[].messageText").description("Localized message text."),
                                fieldWithPath("content[].description").description("Optional description for maintainers."),
                                fieldWithPath("content[].createdAt").description("Creation timestamp in UTC."),
                                fieldWithPath("content[].updatedAt").description("Last update timestamp in UTC."),
                                subsectionWithPath("pageable").description("Pagination request metadata."),
                                subsectionWithPath("sort").description("Applied sort metadata."),
                                fieldWithPath("totalPages").description("Total number of pages."),
                                fieldWithPath("totalElements").description("Total number of localization messages."),
                                fieldWithPath("last").description("Whether this page is the last page."),
                                fieldWithPath("size").description("Requested page size."),
                                fieldWithPath("number").description("Current zero-based page index."),
                                fieldWithPath("numberOfElements").description("Number of messages returned in the current page."),
                                fieldWithPath("first").description("Whether this page is the first page."),
                                fieldWithPath("empty").description("Whether the page content is empty.")
                        )
                ));
    }

    @Test
    void documentGetLocalizationMessageByIdEndpoint() throws Exception {
        mockMvc.perform(get("/api/localization-messages/{id}", bookNotFoundEs.getId()))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/get-localization-message",
                        pathParameters(
                                parameterWithName("id").description("Localization message identifier.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(responseFieldDescriptors())
                ));
    }

    @Test
    void documentGetLocalizationMessageByKeyAndLanguageEndpoint() throws Exception {
        mockMvc.perform(get("/api/localization-messages/key/{messageKey}/lang/{language}", "error.request.invalid", "en"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/get-localization-message-by-key-and-language",
                        pathParameters(
                                parameterWithName("messageKey").description("Stable localization message key."),
                                parameterWithName("language").description("Supported two-letter ISO 639-1 language code. Current values: `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(responseFieldDescriptors())
                ));
    }

    @Test
    void documentCreateLocalizationMessageEndpoint() throws Exception {
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
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/create-localization-message",
                        requestBody(),
                        requestFields(
                                fieldWithPath("messageKey").description("Stable localization message key."),
                                fieldWithPath("language").description("Supported two-letter ISO 639-1 language code. Current values: `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`."),
                                fieldWithPath("messageText").description("Localized message text."),
                                fieldWithPath("description").description("Optional maintainer-facing description.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(responseFieldDescriptors())
                ));
    }

    @Test
    void documentUpdateLocalizationMessageEndpoint() throws Exception {
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
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/update-localization-message",
                        pathParameters(
                                parameterWithName("id").description("Localization message identifier.")
                        ),
                        requestBody(),
                        requestFields(
                                fieldWithPath("messageKey").description("Stable localization message key."),
                                fieldWithPath("language").description("Supported two-letter ISO 639-1 language code. Current values: `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`."),
                                fieldWithPath("messageText").description("Updated localized message text."),
                                fieldWithPath("description").description("Optional updated maintainer-facing description.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(responseFieldDescriptors())
                ));
    }

    @Test
    void documentDeleteLocalizationMessageEndpoint() throws Exception {
        mockMvc.perform(delete("/api/localization-messages/{id}", bookNotFoundEn.getId())
                        .with(adminOauthUser()))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/delete-localization-message",
                        pathParameters(
                                parameterWithName("id").description("Localization message identifier.")
                        ),
                        responseHeaders(commonResponseHeaders())
                ));
    }

    @Test
    void documentListLocalizationMessagesByLanguageEndpoint() throws Exception {
        mockMvc.perform(get("/api/localization-messages/language/{language}", "es"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/list-localization-messages-by-language",
                        pathParameters(
                                parameterWithName("language").description("Supported two-letter ISO 639-1 language code. Current values: `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("[].id").description("Localization message identifier."),
                                fieldWithPath("[].messageKey").description("Stable localization message key."),
                                fieldWithPath("[].language").description("Two-letter ISO 639-1 language code."),
                                fieldWithPath("[].messageText").description("Localized message text."),
                                fieldWithPath("[].description").description("Optional description for maintainers."),
                                fieldWithPath("[].createdAt").description("Creation timestamp in UTC."),
                                fieldWithPath("[].updatedAt").description("Last update timestamp in UTC.")
                        )
                ));
    }

    @Test
    void documentCreateLocalizationMessageValidationError() throws Exception {
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
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "errors/create-localization-message-validation-failed",
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFieldsWithFieldErrors())
                ));
    }

    @Test
    void documentCreateLocalizationMessageUnsupportedLanguageError() throws Exception {
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
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "errors/create-localization-message-unsupported-language",
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())
                ));
    }

    @Test
    void documentCreateLocalizationMessageDuplicateError() throws Exception {
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
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "errors/create-localization-message-duplicate",
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())
                ));
    }

    @Test
    void documentGetLocalizationMessageNotFoundError() throws Exception {
        mockMvc.perform(get("/api/localization-messages/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "errors/get-localization-message-not-found",
                        pathParameters(
                                parameterWithName("id").description("Localization message identifier that does not exist.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())
                ));
    }
    private org.springframework.restdocs.payload.FieldDescriptor[] responseFieldDescriptors() {
        return new org.springframework.restdocs.payload.FieldDescriptor[]{
                fieldWithPath("id").description("Localization message identifier."),
                fieldWithPath("messageKey").description("Stable localization message key."),
                fieldWithPath("language").description("Supported two-letter ISO 639-1 language code. Current values: `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`."),
                fieldWithPath("messageText").description("Localized message text."),
                fieldWithPath("description").description("Optional description for maintainers."),
                fieldWithPath("createdAt").description("Creation timestamp in UTC."),
                fieldWithPath("updatedAt").description("Last update timestamp in UTC.")
        };
    }

}

