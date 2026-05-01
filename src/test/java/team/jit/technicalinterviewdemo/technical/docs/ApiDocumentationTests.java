package team.jit.technicalinterviewdemo.technical.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.oauthUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import team.jit.technicalinterviewdemo.business.book.Book;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.testdata.BookCatalogTestData;
import team.jit.technicalinterviewdemo.testing.RestDocsIntegrationSpringBootTest;

@RestDocsIntegrationSpringBootTest
class ApiDocumentationTests extends AbstractDocumentationIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CacheManager cacheManager;

    private Book cleanCode;
    private Book effectiveJava;
    private Category bestPractices;

    @BeforeEach
    void setUp() {
        BookCatalogTestData.BookCatalog catalog =
                BookCatalogTestData.seedDefaultCatalog(bookRepository, categoryRepository, cacheManager);
        cleanCode = catalog.cleanCode();
        effectiveJava = catalog.effectiveJava();
        bestPractices = catalog.bestPractices();
    }

    @Test
    void documentHelloEndpoint() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(content().string("Hello World!"))
                .andDo(documentEndpoint(
                        "hello/get-hello",
                        responseHeaders(commonResponseHeaders()),
                        responseBody()
                ));
    }

    @Test
    void documentRootOverviewEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.build").exists())
                .andExpect(jsonPath("$.git").exists())
                .andExpect(jsonPath("$.runtime").exists())
                .andExpect(jsonPath("$.dependencies").exists())
                .andExpect(jsonPath("$.configuration").exists())
                .andDo(documentEndpoint(
                        "technical/get-root",
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("build.name").description("Build name from generated build metadata."),
                                fieldWithPath("build.group").description("Build group from generated build metadata."),
                                fieldWithPath("build.artifact").description("Build artifact from generated build metadata."),
                                fieldWithPath("build.version").description("Application version derived from the nearest reachable git tag."),
                                fieldWithPath("build.time").description("Build timestamp."),
                                fieldWithPath("git.branch").description("Current git branch captured during the build."),
                                fieldWithPath("git.commitId").description("Full git commit id captured during the build."),
                                fieldWithPath("git.shortCommitId").description("Short git commit id captured during the build."),
                                fieldWithPath("git.commitTime").description("Commit timestamp for the captured git revision."),
                                fieldWithPath("runtime.applicationName").description("Spring application name."),
                                fieldWithPath("runtime.javaVersion").description("Running JVM version."),
                                fieldWithPath("runtime.javaVendor").description("Running JVM vendor."),
                                fieldWithPath("runtime.activeProfiles").description("Currently active Spring profiles."),
                                subsectionWithPath("dependencies").description("Selected runtime dependency versions for the main technical building blocks."),
                                fieldWithPath("configuration.pagination.defaultPageSize").description("Default pageable page size."),
                                fieldWithPath("configuration.pagination.maxPageSize").description("Maximum pageable page size."),
                                fieldWithPath("configuration.session.storeType").description("Configured Spring Session store type."),
                                fieldWithPath("configuration.session.timeout").description("HTTP session timeout."),
                                fieldWithPath("configuration.session.cookieName").description("Session cookie name."),
                                fieldWithPath("configuration.session.cookieHttpOnly").description("Whether the session cookie is HTTP-only."),
                                fieldWithPath("configuration.session.cookieSameSite").description("Session cookie SameSite mode."),
                                fieldWithPath("configuration.observability.exposedEndpoints").description("Actuator web endpoints exposed by configuration."),
                                fieldWithPath("configuration.observability.healthProbesEnabled").description("Whether readiness/liveness probe groups are enabled."),
                                fieldWithPath("configuration.observability.tracingSamplingProbability").description("Tracing sampling probability."),
                                fieldWithPath("configuration.documentation.html").description("Bundled HTML documentation entry point."),
                                fieldWithPath("configuration.documentation.openApiJson").description("OpenAPI JSON endpoint path."),
                                fieldWithPath("configuration.documentation.openApiYaml").description("OpenAPI YAML endpoint path."),
                                fieldWithPath("configuration.documentation.openApiVersion").description("Configured OpenAPI document dialect."),
                                fieldWithPath("configuration.security.csrfEnabled").description("Whether CSRF protection is enabled for the application."),
                                fieldWithPath("configuration.security.oauthProfileActive").description("Whether the optional oauth profile is currently active."),
                                fieldWithPath("configuration.security.oauthLoginPath").description("Interactive GitHub OAuth login path when the oauth profile is active."),
                                fieldWithPath("configuration.shutdown.serverShutdown").description("Server shutdown mode."),
                                fieldWithPath("configuration.shutdown.timeoutPerShutdownPhase").description("Per-phase graceful shutdown timeout.")
                        )
                ));
    }

    @Test
    void documentDocsEndpoint() throws Exception {
        mockMvc.perform(get("/docs"))
                .andExpect(status().isFound())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(header().string("Location", "/docs/index.html"))
                .andDo(documentEndpoint(
                        "docs/get-docs",
                        responseHeaders(
                                headerWithName("Location").description("Redirect target for the generated API documentation."),
                                headerWithName("X-Request-Id").description("Request identifier returned on every public endpoint."),
                                headerWithName("traceparent").description("Trace context header returned when tracing is active.")
                        )
                ));
    }

    @Test
    void documentListBooksEndpoint() throws Exception {
        Category architecture = categoryRepository.saveAndFlush(new Category("Architecture"));
        bookRepository.saveAndFlush(new Book(
                "Clean Architecture",
                "Robert C. Martin",
                "9780134494166",
                2017,
                new java.util.LinkedHashSet<>(java.util.List.of(architecture, bestPractices))
        ));

        mockMvc.perform(get("/api/books")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("title", "clean")
                        .queryParam("author", "martin")
                        .queryParam("category", "Best Practices")
                        .queryParam("yearFrom", "2000")
                        .queryParam("yearTo", "2020")
                        .queryParam("sort", "title,asc")
                        .queryParam("sort", "year,desc"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andDo(documentEndpoint(
                        "books/list-books",
                        queryParameters(
                                parameterWithName("page").optional().description("Zero-based page index."),
                                parameterWithName("size").optional().description("Page size capped by the server."),
                                parameterWithName("sort").optional().description(
                                        "Sort expression in the form `property,direction`. Repeat the parameter for multiple sort fields. Supported properties: `id`, `title`, `author`, `isbn`, `year`."
                                ),
                                parameterWithName("title").optional().description("Case-insensitive title substring filter."),
                                parameterWithName("author").optional().description("Case-insensitive author substring filter."),
                                parameterWithName("isbn").optional().description("Exact or partial ISBN filter."),
                                parameterWithName("category").optional().description(
                                        "Repeatable case-insensitive category-name filter. When multiple values are provided, books that match any requested category are returned."
                                ),
                                parameterWithName("year").optional().description("Exact publication year filter. Cannot be combined with `yearFrom` or `yearTo`."),
                                parameterWithName("yearFrom").optional().description("Inclusive publication year lower bound."),
                                parameterWithName("yearTo").optional().description("Inclusive publication year upper bound.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("content[].id").description("Book identifier."),
                                fieldWithPath("content[].version").description("Optimistic-locking version for the book."),
                                fieldWithPath("content[].title").description("Book title."),
                                fieldWithPath("content[].author").description("Book author."),
                                fieldWithPath("content[].isbn").description("Unique ISBN assigned when the book is created."),
                                fieldWithPath("content[].publicationYear").description("Publication year."),
                                fieldWithPath("content[].categories[].id").description("Category identifier assigned to the book."),
                                fieldWithPath("content[].categories[].name").description("Category name assigned to the book."),
                                subsectionWithPath("pageable").description("Pagination request metadata."),
                                subsectionWithPath("sort").description("Applied sort metadata."),
                                fieldWithPath("totalPages").description("Total number of pages."),
                                fieldWithPath("totalElements").description("Total number of books."),
                                fieldWithPath("last").description("Whether this page is the last page."),
                                fieldWithPath("size").description("Requested page size."),
                                fieldWithPath("number").description("Current zero-based page index."),
                                fieldWithPath("numberOfElements").description("Number of books returned in the current page."),
                                fieldWithPath("first").description("Whether this page is the first page."),
                                fieldWithPath("empty").description("Whether the page content is empty.")
                        )
                ));
    }

    @Test
    void documentListBooksInvalidRequestError() throws Exception {
        mockMvc.perform(get("/api/books")
                        .queryParam("year", "2018")
                        .queryParam("yearFrom", "2000"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andDo(documentEndpoint(
                        "errors/list-books-invalid-request",
                        queryParameters(
                                parameterWithName("year").description("Exact publication year filter used in the invalid request."),
                                parameterWithName("yearFrom").description("Range filter that conflicts with `year` in this example.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())
                ));
    }

    @Test
    void documentGetBookEndpoint() throws Exception {
        mockMvc.perform(get("/api/books/{id}", effectiveJava.getId()))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "books/get-book",
                        pathParameters(
                                parameterWithName("id").description("Book identifier.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("id").description("Book identifier."),
                                fieldWithPath("version").description("Optimistic-locking version for the book."),
                                fieldWithPath("title").description("Book title."),
                                fieldWithPath("author").description("Book author."),
                                fieldWithPath("isbn").description("Unique ISBN assigned when the book was created."),
                                fieldWithPath("publicationYear").description("Publication year."),
                                fieldWithPath("categories[].id").description("Category identifier assigned to the book."),
                                fieldWithPath("categories[].name").description("Category name assigned to the book.")
                        )
                ));
    }

    @Test
    void documentCreateBookEndpoint() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring in Action",
                                  "author": "Craig Walls",
                                  "isbn": "9781617297571",
                                  "publicationYear": 2022,
                                  "categories": ["Java", "Best Practices"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "books/create-book",
                        requestBody(),
                        requestFields(
                                fieldWithPath("title").description("Book title."),
                                fieldWithPath("author").description("Book author."),
                                fieldWithPath("isbn").description("Unique ISBN for the new book."),
                                fieldWithPath("publicationYear").description("Publication year."),
                                fieldWithPath("categories").optional().description("Optional list of existing category names to assign to the book."),
                                fieldWithPath("categories[]").optional().description("Existing category name.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("id").description("Created book identifier."),
                                fieldWithPath("version").description("Initial optimistic-locking version for the book."),
                                fieldWithPath("title").description("Book title."),
                                fieldWithPath("author").description("Book author."),
                                fieldWithPath("isbn").description("Unique ISBN assigned to the book."),
                                fieldWithPath("publicationYear").description("Publication year."),
                                fieldWithPath("categories[].id").description("Assigned category identifier."),
                                fieldWithPath("categories[].name").description("Assigned category name.")
                        )
                ));
    }

    @Test
    void documentCreateBookValidationError() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(oauthUser())
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
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andDo(documentEndpoint(
                        "errors/create-book-validation-failed",
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFieldsWithFieldErrors())
                ));
    }

    @Test
    void documentCreateBookDuplicateIsbnError() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Another Clean Code",
                                  "author": "Robert C. Martin",
                                  "isbn": "9780132350884",
                                  "publicationYear": 2009
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.title").value("Duplicate ISBN"))
                .andDo(documentEndpoint(
                        "errors/create-book-duplicate-isbn",
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())
                ));
    }

    @Test
    void documentUpdateBookEndpoint() throws Exception {
        mockMvc.perform(put("/api/books/{id}", cleanCode.getId())
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code Second Edition",
                                  "author": "Robert C. Martin",
                                  "version": %d,
                                  "publicationYear": 2026,
                                  "categories": ["Java"]
                                }
                                """.formatted(cleanCode.getVersion())))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "books/update-book",
                        pathParameters(
                                parameterWithName("id").description("Book identifier.")
                        ),
                        requestBody(),
                        requestFields(
                                fieldWithPath("title").description("Updated book title."),
                                fieldWithPath("author").description("Updated book author."),
                                fieldWithPath("version").description("Current optimistic-locking version required for the update."),
                                fieldWithPath("publicationYear").description("Updated publication year."),
                                fieldWithPath("categories").optional().description("Optional replacement list of existing category names."),
                                fieldWithPath("categories[]").optional().description("Existing category name.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("id").description("Book identifier."),
                                fieldWithPath("version").description("Incremented optimistic-locking version after the update."),
                                fieldWithPath("title").description("Updated book title."),
                                fieldWithPath("author").description("Updated book author."),
                                fieldWithPath("isbn").description("Original ISBN. ISBN is immutable after creation."),
                                fieldWithPath("publicationYear").description("Updated publication year."),
                                fieldWithPath("categories[].id").description("Assigned category identifier."),
                                fieldWithPath("categories[].name").description("Assigned category name.")
                        )
                ));
    }

    @Test
    void documentUpdateBookStaleVersionError() throws Exception {
        long staleVersion = cleanCode.getVersion();

        mockMvc.perform(put("/api/books/{id}", cleanCode.getId())
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code Second Edition",
                                  "author": "Robert C. Martin",
                                  "version": %d,
                                  "publicationYear": 2026
                                }
                                """.formatted(staleVersion)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/books/{id}", cleanCode.getId())
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code Third Edition",
                                  "author": "Robert C. Martin",
                                  "version": %d,
                                  "publicationYear": 2027
                                }
                                """.formatted(staleVersion)))
                .andExpect(status().isConflict())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.title").value("Concurrent Modification"))
                .andDo(documentEndpoint(
                        "errors/update-book-stale-version",
                        pathParameters(
                                parameterWithName("id").description("Book identifier being updated with an outdated version.")
                        ),
                        requestBody(),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())
                ));
    }

    @Test
    void documentGetBookInvalidIdError() throws Exception {
        mockMvc.perform(get("/api/books/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.title").value("Invalid Parameter"))
                .andDo(documentEndpoint(
                        "errors/get-book-invalid-id",
                        pathParameters(
                                parameterWithName("id").description("Invalid book identifier value used in this example.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())
                ));
    }

    @Test
    void documentGetBookNotFoundError() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 9999)
                        .queryParam("lang", "es"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.title").value("Book Not Found"))
                .andExpect(jsonPath("$.message").value("No se encontro el libro solicitado."))
                .andDo(documentEndpoint(
                        "errors/get-book-not-found",
                        pathParameters(
                                parameterWithName("id").description("Book identifier that does not exist.")
                        ),
                        queryParameters(
                                parameterWithName("lang").optional().description("Optional language override for localized error messages. Browser-style values such as `es` or `es-ES` are accepted.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(problemResponseFields())
                ));
    }

    @Test
    void documentDeleteBookEndpoint() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", cleanCode.getId())
                        .with(oauthUser()))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "books/delete-book",
                        pathParameters(
                                parameterWithName("id").description("Book identifier.")
                        ),
                        responseHeaders(commonResponseHeaders())
                ));
    }

    @Test
    void documentActuatorInfoEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.build").exists())
                .andExpect(jsonPath("$.git").exists())
                .andDo(documentEndpoint(
                        "actuator/get-info",
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("build").description("Build metadata generated by Spring Boot."),
                                fieldWithPath("git").description("Git metadata generated during the build.")
                        )
                ));
    }

    @Test
    void documentActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andDo(documentEndpoint(
                        "actuator/get-health",
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("status").description("Overall application health status."),
                                fieldWithPath("groups").description("Health groups exposed by the actuator endpoint.")
                        )
                ));
    }

    @Test
    void documentActuatorLivenessEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andDo(documentEndpoint(
                        "actuator/get-health-liveness",
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("status").description("Liveness status for the running application.")
                        )
                ));
    }

    @Test
    void documentActuatorReadinessEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andDo(documentEndpoint(
                        "actuator/get-health-readiness",
                        responseHeaders(commonResponseHeaders()),
                        relaxedResponseFields(
                                fieldWithPath("status").description("Readiness status for serving traffic.")
                        )
                ));
    }

    @Test
    void documentActuatorPrometheusEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "actuator/get-prometheus",
                        responseHeaders(commonResponseHeaders()),
                        responseBody()
                ));
    }
}
