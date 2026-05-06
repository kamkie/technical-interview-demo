package team.jit.technicalinterviewdemo.business.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.testdata.BookCatalogTestData;
import team.jit.technicalinterviewdemo.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RestDocsIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminBrowserSession;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.authenticatedBrowserSession;

@RestDocsIntegrationSpringBootTest
class CategoryApiDocumentationTests extends AbstractDocumentationIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    private BrowserSession adminSession;
    private BrowserSession userSession;

    @BeforeEach
    void setUp() {
        BookCatalogTestData.seedDefaultCatalog(bookRepository, categoryRepository, cacheManager);
        adminSession = adminBrowserSession(sessionRepository);
        userSession = authenticatedBrowserSession(sessionRepository, "reader-user");
    }

    @Test
    void documentListCategoriesEndpoint() throws Exception {
        mockMvc.perform(get("/api/categories")).andExpect(status().isOk()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andDo(documentEndpoint(
            "categories/list-categories", responseHeaders(commonResponseHeaders()), responseFields(
                fieldWithPath("[].id").description("Category identifier."), fieldWithPath("[].name").description("Category name.")
            )
        ));
    }

    @Test
    void documentCreateCategoryEndpoint() throws Exception {
        mockMvc.perform(post("/api/categories").with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "name": "Architecture"
            }
            """)).andExpect(status().isCreated()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andDo(documentEndpoint(
            "categories/create-category", requestBody(), requestFields(
                fieldWithPath("name").description("Unique category name.")
            ), responseHeaders(commonResponseHeaders()), responseFields(
                fieldWithPath("id").description("Created category identifier."), fieldWithPath("name").description("Created category name.")
            )
        ));
    }

    @Test
    void documentCreateCategoryDuplicateError() throws Exception {
        mockMvc.perform(post("/api/categories").with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "name": "java"
            }
            """)).andExpect(status().isBadRequest()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andExpect(jsonPath("$.title").value("Invalid Request")).andDo(documentEndpoint(
            "errors/create-category-duplicate", requestBody(), responseHeaders(commonResponseHeaders()), relaxedResponseFields(problemResponseFields())
        ));
    }

    @Test
    void documentCreateCategoryUnauthorizedError() throws Exception {
        mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content("""
            {
              "name": "Architecture"
            }
            """)).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.title").value("Unauthorized")).andDo(documentEndpoint(
            "errors/create-category-unauthorized", requestBody(), relaxedResponseFields(problemResponseFields())
        ));
    }

    @Test
    void documentCreateCategoryForbiddenError() throws Exception {
        mockMvc.perform(post("/api/categories").with(userSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "name": "Architecture"
            }
            """)).andExpect(status().isForbidden()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andExpect(jsonPath("$.title").value("Forbidden")).andDo(documentEndpoint(
            "errors/create-category-forbidden", requestBody(), responseHeaders(commonResponseHeaders()), relaxedResponseFields(problemResponseFields())
        ));
    }

    @Test
    void documentUpdateCategoryEndpoint() throws Exception {
        Category javaCategory = categoryRepository.findAllByOrderByNameAsc().stream().filter(category -> "Java".equals(category.getName())).findFirst().orElseThrow();

        mockMvc.perform(put("/api/categories/{id}", javaCategory.getId()).with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "name": "JVM"
            }
            """)).andExpect(status().isOk()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andDo(documentEndpoint(
            "categories/update-category", pathParameters(
                parameterWithName("id").description("Category identifier.")
            ), requestBody(), requestFields(
                fieldWithPath("name").description("Updated unique category name.")
            ), responseHeaders(commonResponseHeaders()), responseFields(
                fieldWithPath("id").description("Updated category identifier."), fieldWithPath("name").description("Updated category name.")
            )
        ));
    }

    @Test
    void documentDeleteCategoryEndpoint() throws Exception {
        Category architecture = categoryRepository.saveAndFlush(new Category("Architecture"));

        mockMvc.perform(delete("/api/categories/{id}", architecture.getId()).with(adminSession.unsafeWrite())).andExpect(status().isNoContent()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andDo(documentEndpoint(
            "categories/delete-category", pathParameters(
                parameterWithName("id").description("Category identifier.")
            ), responseHeaders(commonResponseHeaders())
        ));
    }

    @Test
    void documentDeleteCategoryNotFoundError() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", 9999).with(adminSession.unsafeWrite())).andExpect(status().isNotFound()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andExpect(jsonPath("$.title").value("Category Not Found")).andDo(documentEndpoint(
            "errors/delete-category-not-found", pathParameters(
                parameterWithName("id").description("Category identifier that does not exist.")
            ), responseHeaders(commonResponseHeaders()), relaxedResponseFields(problemResponseFields())
        ));
    }

    @Test
    void documentDeleteCategoryInUseError() throws Exception {
        Category javaCategory = categoryRepository.findAllByOrderByNameAsc().stream().filter(category -> "Java".equals(category.getName())).findFirst().orElseThrow();

        mockMvc.perform(delete("/api/categories/{id}", javaCategory.getId()).with(adminSession.unsafeWrite())).andExpect(status().isConflict()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andExpect(jsonPath("$.title").value("Category In Use")).andDo(documentEndpoint(
            "errors/delete-category-in-use", pathParameters(
                parameterWithName("id").description("Category identifier that is still assigned to one or more books.")
            ), responseHeaders(commonResponseHeaders()), relaxedResponseFields(problemResponseFields())
        ));
    }
}
