package team.jit.technicalinterviewdemo.business.category;

import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.SecurityTestSupport.adminOauthUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.technical.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.technical.testing.BookCatalogTestData;
import team.jit.technicalinterviewdemo.technical.testing.RestDocsIntegrationSpringBootTest;

@RestDocsIntegrationSpringBootTest
class CategoryApiDocumentationTests extends AbstractDocumentationIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        BookCatalogTestData.seedDefaultCategories(categoryRepository, bookRepository, cacheManager);
    }

    @Test
    void documentListCategoriesEndpoint() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "categories/list-categories",
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("[].id").description("Category identifier."),
                                fieldWithPath("[].name").description("Category name.")
                        )
                ));
    }

    @Test
    void documentCreateCategoryEndpoint() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Architecture"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "categories/create-category",
                        requestBody(),
                        requestFields(
                                fieldWithPath("name").description("Unique category name.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("id").description("Created category identifier."),
                                fieldWithPath("name").description("Created category name.")
                        )
                ));
    }

    @Test
    void documentCreateCategoryDuplicateError() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "java"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andDo(documentEndpoint(
                        "errors/create-category-duplicate",
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())
                ));
    }
}

