package team.jit.technicalinterviewdemo.business.category;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.SecurityTestSupport.adminOauthUser;
import static team.jit.technicalinterviewdemo.SecurityTestSupport.csrfToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import team.jit.technicalinterviewdemo.TestcontainersTest;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;

@TestcontainersTest
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class CategoryApiDocumentationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        categoryRepository.saveAndFlush(new Category("Best Practices"));
        categoryRepository.saveAndFlush(new Category("Java"));
        cacheManager.getCache(CacheNames.CATEGORIES).clear();
        cacheManager.getCache(CacheNames.CATEGORY_DIRECTORY).clear();
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
                        .with(csrfToken())
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
                        .with(csrfToken())
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

    private RestDocumentationResultHandler documentEndpoint(String identifier, org.springframework.restdocs.snippet.Snippet... snippets) {
        return document(
                identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                snippets
        );
    }

    private HeaderDescriptor[] commonResponseHeaders() {
        return new HeaderDescriptor[]{
                headerWithName("X-Request-Id").description("Request identifier returned on every public endpoint."),
                headerWithName("traceparent").description("Trace context header returned when tracing is active.")
        };
    }

    private org.springframework.restdocs.payload.FieldDescriptor[] problemResponseFields() {
        return new org.springframework.restdocs.payload.FieldDescriptor[]{
                fieldWithPath("title").description("Problem title."),
                fieldWithPath("status").description("HTTP status code."),
                fieldWithPath("detail").description("Technical problem detail kept stable for debugging and logs."),
                fieldWithPath("messageKey").description("Stable localization key for the error type."),
                fieldWithPath("message").description("Localized end-user message resolved from the request language."),
                fieldWithPath("language").description("Two-letter ISO 639-1 language code actually used for the localized message.")
        };
    }
}
