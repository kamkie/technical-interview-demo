package team.jit.technicalinterviewdemo.business.localization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import team.jit.technicalinterviewdemo.testdata.LocalizationTestData;
import team.jit.technicalinterviewdemo.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RestDocsIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminBrowserSession;

@RestDocsIntegrationSpringBootTest
class LocalizationApiDocumentationTests extends AbstractDocumentationIntegrationTest {

    @Autowired
    private LocalizationRepository localizationRepository;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    private Localization bookNotFoundEn;
    private Localization bookNotFoundEs;
    private BrowserSession adminSession;

    @BeforeEach
    void setUp() {
        LocalizationTestData.DefaultLocalizations messages =
                LocalizationTestData.reloadDefaultMessages(localizationRepository);
        bookNotFoundEn = messages.bookNotFoundEn();
        bookNotFoundEs = messages.bookNotFoundEs();
        adminSession = adminBrowserSession(sessionRepository);
    }

    @Test
    void documentListLocalizationsEndpoint() throws Exception {
        mockMvc.perform(get("/api/localizations")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sort", "messageKey,asc")
                        .queryParam("sort", "language,asc"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/list-localizations",
                        queryParameters(
                                parameterWithName("messageKey")
                                        .optional()
                                        .description("Exact localization key filter."),
                                parameterWithName("language")
                                        .optional()
                                        .description(
                                                "Supported two-letter ISO 639-1 language code filter. Current values:"
                                                        + " `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`."),
                                parameterWithName("page").optional().description("Zero-based page index."),
                                parameterWithName("size").optional().description("Page size capped by the server."),
                                parameterWithName("sort")
                                        .optional()
                                        .description("Sort expression in the form `property,direction`. Repeat the"
                                                + " parameter for multiple sort fields. Supported properties:"
                                                + " `id`, `messageKey`, `language`, `createdAt`, `updatedAt`.")),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("content[].id").description("Localization identifier."),
                                fieldWithPath("content[].messageKey").description("Stable localization key."),
                                fieldWithPath("content[].language").description("Two-letter ISO 639-1 language code."),
                                fieldWithPath("content[].messageText").description("Localized message text."),
                                fieldWithPath("content[].description")
                                        .description("Optional description for maintainers."),
                                fieldWithPath("content[].createdAt")
                                        .description("Creation timestamp as a UTC instant."),
                                fieldWithPath("content[].updatedAt")
                                        .description("Last update timestamp as a UTC instant."),
                                subsectionWithPath("pageable").description("Pagination request metadata."),
                                subsectionWithPath("sort").description("Applied sort metadata."),
                                fieldWithPath("totalPages").description("Total number of pages."),
                                fieldWithPath("totalElements").description("Total number of localizations."),
                                fieldWithPath("last").description("Whether this page is the last page."),
                                fieldWithPath("size").description("Requested page size."),
                                fieldWithPath("number").description("Current zero-based page index."),
                                fieldWithPath("numberOfElements")
                                        .description("Number of messages returned in the current page."),
                                fieldWithPath("first").description("Whether this page is the first page."),
                                fieldWithPath("empty").description("Whether the page content is empty."))));
    }

    @Test
    void documentGetLocalizationByIdEndpoint() throws Exception {
        mockMvc.perform(get("/api/localizations/{id}", bookNotFoundEs.getId()))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/get-localization",
                        pathParameters(parameterWithName("id").description("Localization identifier.")),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(responseFieldDescriptors())));
    }

    @Test
    void documentListLocalizationsWithFiltersEndpoint() throws Exception {
        mockMvc.perform(get("/api/localizations")
                        .queryParam("messageKey", "error.request.invalid")
                        .queryParam("language", "en"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/list-localizations-filtered",
                        queryParameters(
                                parameterWithName("messageKey").description("Exact localization key filter."),
                                parameterWithName("language")
                                        .description(
                                                "Supported two-letter ISO 639-1 language code filter. Current values:"
                                                        + " `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`."),
                                parameterWithName("page").optional().description("Zero-based page index."),
                                parameterWithName("size").optional().description("Page size capped by the server."),
                                parameterWithName("sort")
                                        .optional()
                                        .description("Sort expression in the form `property,direction`. Repeat the"
                                                + " parameter for multiple sort fields. Supported properties:"
                                                + " `id`, `messageKey`, `language`, `createdAt`, `updatedAt`.")),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("content[].id").description("Localization identifier."),
                                fieldWithPath("content[].messageKey").description("Stable localization key."),
                                fieldWithPath("content[].language").description("Two-letter ISO 639-1 language code."),
                                fieldWithPath("content[].messageText").description("Localized message text."),
                                fieldWithPath("content[].description")
                                        .description("Optional description for maintainers."),
                                fieldWithPath("content[].createdAt")
                                        .description("Creation timestamp as a UTC instant."),
                                fieldWithPath("content[].updatedAt")
                                        .description("Last update timestamp as a UTC instant."),
                                subsectionWithPath("pageable").description("Pagination request metadata."),
                                subsectionWithPath("sort").description("Applied sort metadata."),
                                fieldWithPath("totalPages").description("Total number of pages."),
                                fieldWithPath("totalElements").description("Total number of matching localizations."),
                                fieldWithPath("last").description("Whether this page is the last page."),
                                fieldWithPath("size").description("Requested page size."),
                                fieldWithPath("number").description("Current zero-based page index."),
                                fieldWithPath("numberOfElements")
                                        .description("Number of localizations returned in the current page."),
                                fieldWithPath("first").description("Whether this page is the first page."),
                                fieldWithPath("empty").description("Whether the page content is empty."))));
    }

    @Test
    void documentCreateLocalizationEndpoint() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .with(adminSession.unsafeWrite())
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
                        "localization/create-localization",
                        requestBody(),
                        requestFields(
                                fieldWithPath("messageKey").description("Stable localization key."),
                                fieldWithPath("language")
                                        .description(
                                                "Supported two-letter ISO 639-1 language code. Current values: `en`,"
                                                        + " `es`, `de`, `fr`, `pl`, `uk`, `no`."),
                                fieldWithPath("messageText").description("Localized message text."),
                                fieldWithPath("description").description("Optional maintainer-facing description.")),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(responseFieldDescriptors())));
    }

    @Test
    void documentUpdateLocalizationEndpoint() throws Exception {
        mockMvc.perform(put("/api/localizations/{id}", bookNotFoundEn.getId())
                        .with(adminSession.unsafeWrite())
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
                        "localization/update-localization",
                        pathParameters(parameterWithName("id").description("Localization identifier.")),
                        requestBody(),
                        requestFields(
                                fieldWithPath("messageKey").description("Stable localization key."),
                                fieldWithPath("language")
                                        .description(
                                                "Supported two-letter ISO 639-1 language code. Current values: `en`,"
                                                        + " `es`, `de`, `fr`, `pl`, `uk`, `no`."),
                                fieldWithPath("messageText").description("Updated localized message text."),
                                fieldWithPath("description")
                                        .description("Optional updated maintainer-facing description.")),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(responseFieldDescriptors())));
    }

    @Test
    void documentDeleteLocalizationEndpoint() throws Exception {
        mockMvc.perform(delete("/api/localizations/{id}", bookNotFoundEn.getId())
                        .with(adminSession.unsafeWrite()))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/delete-localization",
                        pathParameters(parameterWithName("id").description("Localization identifier.")),
                        responseHeaders(commonResponseHeaders())));
    }

    @Test
    void documentListLocalizationsByLanguageEndpoint() throws Exception {
        mockMvc.perform(get("/api/localizations").queryParam("language", "es").queryParam("sort", "messageKey,asc"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "localization/list-localizations-by-language",
                        queryParameters(
                                parameterWithName("messageKey")
                                        .optional()
                                        .description("Exact localization key filter."),
                                parameterWithName("language")
                                        .description(
                                                "Supported two-letter ISO 639-1 language code filter. Current values:"
                                                        + " `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`."),
                                parameterWithName("page").optional().description("Zero-based page index."),
                                parameterWithName("size").optional().description("Page size capped by the server."),
                                parameterWithName("sort")
                                        .optional()
                                        .description("Sort expression in the form `property,direction`. Repeat the"
                                                + " parameter for multiple sort fields. Supported properties:"
                                                + " `id`, `messageKey`, `language`, `createdAt`, `updatedAt`.")),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("content[].id").description("Localization identifier."),
                                fieldWithPath("content[].messageKey").description("Stable localization key."),
                                fieldWithPath("content[].language").description("Two-letter ISO 639-1 language code."),
                                fieldWithPath("content[].messageText").description("Localized message text."),
                                fieldWithPath("content[].description")
                                        .description("Optional description for maintainers."),
                                fieldWithPath("content[].createdAt")
                                        .description("Creation timestamp as a UTC instant."),
                                fieldWithPath("content[].updatedAt")
                                        .description("Last update timestamp as a UTC instant."),
                                subsectionWithPath("pageable").description("Pagination request metadata."),
                                subsectionWithPath("sort").description("Applied sort metadata."),
                                fieldWithPath("totalPages").description("Total number of pages."),
                                fieldWithPath("totalElements").description("Total number of matching localizations."),
                                fieldWithPath("last").description("Whether this page is the last page."),
                                fieldWithPath("size").description("Requested page size."),
                                fieldWithPath("number").description("Current zero-based page index."),
                                fieldWithPath("numberOfElements")
                                        .description("Number of localizations returned in the current page."),
                                fieldWithPath("first").description("Whether this page is the first page."),
                                fieldWithPath("empty").description("Whether the page content is empty."))));
    }

    @Test
    void documentCreateLocalizationValidationError() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .with(adminSession.unsafeWrite())
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
                        "errors/create-localization-validation-failed",
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFieldsWithFieldErrors())));
    }

    @Test
    void documentCreateLocalizationUnsupportedLanguageError() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .with(adminSession.unsafeWrite())
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
                        "errors/create-localization-unsupported-language",
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())));
    }

    @Test
    void documentCreateLocalizationDuplicateError() throws Exception {
        mockMvc.perform(post("/api/localizations")
                        .with(adminSession.unsafeWrite())
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
                        "errors/create-localization-duplicate",
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())));
    }

    @Test
    void documentGetLocalizationNotFoundError() throws Exception {
        mockMvc.perform(get("/api/localizations/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "errors/get-localization-not-found",
                        pathParameters(
                                parameterWithName("id").description("Localization identifier that does not exist.")),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())));
    }

    private org.springframework.restdocs.payload.FieldDescriptor[] responseFieldDescriptors() {
        return new org.springframework.restdocs.payload.FieldDescriptor[] {
            fieldWithPath("id").description("Localization identifier."),
            fieldWithPath("messageKey").description("Stable localization key."),
            fieldWithPath("language")
                    .description("Supported two-letter ISO 639-1 language code. Current values: `en`, `es`, `de`, `fr`,"
                            + " `pl`, `uk`, `no`."),
            fieldWithPath("messageText").description("Localized message text."),
            fieldWithPath("description").description("Optional description for maintainers."),
            fieldWithPath("createdAt").description("Creation timestamp as a UTC instant."),
            fieldWithPath("updatedAt").description("Last update timestamp as a UTC instant.")
        };
    }
}
