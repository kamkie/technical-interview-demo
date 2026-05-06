package team.jit.technicalinterviewdemo.technical.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import team.jit.technicalinterviewdemo.testing.AbstractBookCatalogMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.authenticatedBrowserSession;

@MockMvcIntegrationSpringBootTest
class ApiErrorHandlingIntegrationTests extends AbstractBookCatalogMockMvcIntegrationTest {

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    @Test
    void createBookWithInvalidPayloadReturnsBadRequest() throws Exception {
        BrowserSession browserSession = readerSession();

        mockMvc.perform(post("/api/books")
                        .with(browserSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "author": " ",
                                  "isbn": "",
                                  "publicationYear": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.title").value("title is required"))
                .andExpect(jsonPath("$.fieldErrors.author").value("author is required"))
                .andExpect(jsonPath("$.fieldErrors.isbn").value("isbn is required"))
                .andExpect(jsonPath("$.fieldErrors.publicationYear").value("publicationYear is required"))
                .andExpect(jsonPath("$.fieldErrors['create.arg0.title']").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void createBookWithMalformedJsonReturnsBadRequest() throws Exception {
        BrowserSession browserSession = readerSession();

        mockMvc.perform(post("/api/books")
                        .with(browserSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Broken JSON",
                                  "author": "Craig Walls",
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Malformed Request Body"))
                .andExpect(jsonPath("$.detail").value("Request body is missing or malformed."));
    }

    @Test
    void createBookWithUnsupportedMediaTypeReturnsUnsupportedMediaType() throws Exception {
        BrowserSession browserSession = readerSession();

        mockMvc.perform(post("/api/books")
                        .with(browserSession.unsafeWrite())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not-json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.title").value("Unsupported Media Type"));
    }

    @Test
    void getBookByInvalidIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/books/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Parameter"))
                .andExpect(jsonPath("$.detail").value("Parameter 'id' value 'abc' is invalid."));
    }

    @Test
    void unsupportedMethodReturnsMethodNotAllowed() throws Exception {
        mockMvc.perform(patch("/api/books"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.title").value("Method Not Allowed"))
                .andExpect(jsonPath("$.detail").value("HTTP method 'PATCH' is not supported for this endpoint."));
    }

    @Test
    void missingResourceReturnsNotFoundProblemDetail() throws Exception {
        mockMvc.perform(get("/api/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Resource 'api/missing' was not found."))
                .andExpect(jsonPath("$.messageKey").value("error.request.resource_not_found"))
                .andExpect(jsonPath("$.message").value("The requested resource was not found."))
                .andExpect(jsonPath("$.language").value("en"));
    }

    private BrowserSession readerSession() {
        return authenticatedBrowserSession(sessionRepository, "reader-user");
    }
}
