package team.jit.technicalinterviewdemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.oauthUser;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import team.jit.technicalinterviewdemo.business.book.Book;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.BookCatalogTestData;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class ApiDemoTests extends AbstractMockMvcIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CacheManager cacheManager;

    private Book cleanCode;
    private Book effectiveJava;

    @BeforeEach
    void setUp() {
        BookCatalogTestData.BookCatalog catalog =
                BookCatalogTestData.seedDefaultCatalog(bookRepository, categoryRepository, cacheManager);
        cleanCode = catalog.cleanCode();
        effectiveJava = catalog.effectiveJava();
    }

    @Test
    void helloEndpointReturnsHelloWorld() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }

    @Test
    void listBooksReturnsSeededBooks() throws Exception {
        mockMvc.perform(get("/api/books")
                        .queryParam("page", "0")
                        .queryParam("size", "1")
                        .queryParam("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"))
                .andExpect(jsonPath("$.content[0].categories.length()").value(2))
                .andExpect(jsonPath("$.content[0].version").isNumber())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void listBooksFiltersByTitleAndAuthorIgnoringCase() throws Exception {
        bookRepository.saveAndFlush(new Book("Clean Architecture", "Robert C. Martin", "9780134494166", 2017));
        bookRepository.saveAndFlush(new Book("Code Complete", "Steve McConnell", "9780735619678", 2004));

        mockMvc.perform(get("/api/books")
                        .queryParam("title", "CLEAN")
                        .queryParam("author", "martin")
                        .queryParam("sort", "year,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Clean Architecture"))
                .andExpect(jsonPath("$.content[0].publicationYear").value(2017))
                .andExpect(jsonPath("$.content[1].title").value("Clean Code"))
                .andExpect(jsonPath("$.content[1].publicationYear").value(2008))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void listBooksFiltersByExactPublicationYear() throws Exception {
        bookRepository.saveAndFlush(new Book("Domain-Driven Design", "Eric Evans", "9780321125217", 2003));

        mockMvc.perform(get("/api/books")
                        .queryParam("year", "2018"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.content[0].publicationYear").value(2018))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listBooksFiltersByCategoryIgnoringCase() throws Exception {
        mockMvc.perform(get("/api/books")
                        .queryParam("category", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.content[0].categories[0].name").value("Best Practices"))
                .andExpect(jsonPath("$.content[0].categories[1].name").value("Java"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listBooksFiltersByIsbnAndYearRangeAndSupportsMultipleSortFields() throws Exception {
        bookRepository.saveAndFlush(new Book("Refactoring", "Martin Fowler", "9780201485677", 1999));
        bookRepository.saveAndFlush(new Book("Refactoring", "Martin Fowler", "9780134757599", 2018));

        mockMvc.perform(get("/api/books")
                        .queryParam("isbn", "9780")
                        .queryParam("yearFrom", "1990")
                        .queryParam("yearTo", "2020")
                        .queryParam("author", "fowler")
                        .queryParam("sort", "title,asc")
                        .queryParam("sort", "year,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Refactoring"))
                .andExpect(jsonPath("$.content[0].publicationYear").value(2018))
                .andExpect(jsonPath("$.content[1].title").value("Refactoring"))
                .andExpect(jsonPath("$.content[1].publicationYear").value(1999))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void listBooksWithConflictingYearFiltersReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/books")
                        .queryParam("year", "2018")
                        .queryParam("yearFrom", "2000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value(
                        "Use either 'year' or the 'yearFrom'/'yearTo' range parameters, not both."
                ));
    }

    @Test
    void listBooksWithUnsupportedSortReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/books")
                        .queryParam("sort", "dropTable,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value(
                        "Sort field 'dropTable' is not supported. Use one of: id, title, author, isbn, year."
                ));
    }

    @Test
    void getBookByIdReturnsRequestedBook() throws Exception {
        mockMvc.perform(get("/api/books/{id}", effectiveJava.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(effectiveJava.getId()))
                .andExpect(jsonPath("$.version").value(effectiveJava.getVersion()))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"));
    }

    @Test
    void createBookReturnsCreatedBook() throws Exception {
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
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.version").isNumber())
                .andExpect(jsonPath("$.title").value("Spring in Action"))
                .andExpect(jsonPath("$.author").value("Craig Walls"))
                .andExpect(jsonPath("$.isbn").value("9781617297571"))
                .andExpect(jsonPath("$.publicationYear").value(2022))
                .andExpect(jsonPath("$.categories.length()").value(2))
                .andExpect(jsonPath("$.categories[0].name").value("Best Practices"))
                .andExpect(jsonPath("$.categories[1].name").value("Java"));
    }

    @Test
    void createBookWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring in Action",
                                  "author": "Craig Walls",
                                  "isbn": "9781617297571",
                                  "publicationYear": 2022
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createBookWithoutCsrfStillSucceedsWhenAuthenticated() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring in Action",
                                  "author": "Craig Walls",
                                  "isbn": "9781617297571",
                                  "publicationYear": 2022
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void createBookWithDuplicateIsbnReturnsConflict() throws Exception {
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
                .andExpect(jsonPath("$.title").value("Duplicate ISBN"))
                .andExpect(jsonPath("$.detail").value("Book with ISBN 9780132350884 already exists."))
                .andExpect(jsonPath("$.messageKey").value("error.book.isbn_duplicate"))
                .andExpect(jsonPath("$.message").value("A book with the same ISBN already exists."))
                .andExpect(jsonPath("$.language").value("en"))
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void createBookWithUnknownCategoryReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring in Action",
                                  "author": "Craig Walls",
                                  "isbn": "9781617297571",
                                  "publicationYear": 2022,
                                  "categories": ["Missing Category"]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("Unknown categories: Missing Category."))
                .andExpect(jsonPath("$.messageKey").value("error.request.invalid"))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void createBookWithInvalidPayloadReturnsBadRequest() throws Exception {
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
        mockMvc.perform(post("/api/books")
                        .with(oauthUser())
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
        mockMvc.perform(post("/api/books")
                        .with(oauthUser())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not-json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.title").value("Unsupported Media Type"));
    }

    @Test
    void requestLoggingRedactsSensitiveQueryParameters(CapturedOutput output) throws Exception {
        String secret = "secret-log-token-123";

        mockMvc.perform(get("/hello")
                        .queryParam("token", secret)
                        .queryParam("page", "1"))
                .andExpect(status().isOk());

        assertThat(output).contains("params={token=<redacted>, page=1}");
        assertThat(output).doesNotContain(secret);
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

    @Test
    void errorLoggingRedactsSensitiveQueryParameters(CapturedOutput output) throws Exception {
        String secret = "Bearer raw-secret-value";

        mockMvc.perform(get("/api/books/{id}", "abc")
                        .queryParam("authorization", secret))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Parameter"));

        assertThat(output).contains("params={authorization=<redacted>}");
        assertThat(output).doesNotContain(secret);
    }

    @Test
    void updateBookReturnsUpdatedBook() throws Exception {
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
                .andExpect(jsonPath("$.id").value(cleanCode.getId()))
                .andExpect(jsonPath("$.version").value(cleanCode.getVersion() + 1))
                .andExpect(jsonPath("$.title").value("Clean Code Second Edition"))
                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                .andExpect(jsonPath("$.publicationYear").value(2026))
                .andExpect(jsonPath("$.categories.length()").value(1))
                .andExpect(jsonPath("$.categories[0].name").value("Java"));
    }

    @Test
    void updateBookIgnoresProvidedIsbn() throws Exception {
        mockMvc.perform(put("/api/books/{id}", cleanCode.getId())
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code Second Edition",
                                  "author": "Robert C. Martin",
                                  "isbn": "9780134685991",
                                  "version": %d,
                                  "publicationYear": 2026
                                }
                                """.formatted(cleanCode.getVersion())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9780132350884"));

        mockMvc.perform(get("/api/books/{id}", cleanCode.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9780132350884"));
    }

    @Test
    void updateBookWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(put("/api/books/{id}", cleanCode.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code Second Edition",
                                  "author": "Robert C. Martin",
                                  "version": %d,
                                  "publicationYear": 2026
                                }
                                """.formatted(cleanCode.getVersion())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateBookWithStaleVersionReturnsConflict() throws Exception {
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
                .andExpect(jsonPath("$.title").value("Concurrent Modification"))
                .andExpect(jsonPath("$.detail").value(
                        "Book with id %d is at version %d. Retry the update with the latest version instead of %d."
                                .formatted(cleanCode.getId(), staleVersion + 1, staleVersion)
                ));
    }

    @Test
    void deleteBookRemovesBook() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", cleanCode.getId())
                        .with(oauthUser()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/{id}", cleanCode.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book Not Found"))
                .andExpect(jsonPath("$.detail").value("Book with id %d was not found.".formatted(cleanCode.getId())))
                .andExpect(jsonPath("$.messageKey").value("error.book.not_found"))
                .andExpect(jsonPath("$.message").value("The requested book was not found."))
                .andExpect(jsonPath("$.language").value("en"))
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void deleteBookWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", cleanCode.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptLanguageHeaderReturnsLocalizedErrorMessage() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 9999)
                        .header("Accept-Language", "es-ES,es;q=0.9,en;q=0.8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book Not Found"))
                .andExpect(jsonPath("$.detail").value("Book with id 9999 was not found."))
                .andExpect(jsonPath("$.messageKey").value("error.book.not_found"))
                .andExpect(jsonPath("$.message").value("No se encontro el libro solicitado."))
                .andExpect(jsonPath("$.language").value("es"));
    }

    @Test
    void langQueryParameterOverridesAcceptLanguageHeader() throws Exception {
        mockMvc.perform(get("/api/books")
                        .queryParam("year", "2018")
                        .queryParam("yearFrom", "2000")
                        .queryParam("lang", "uk-UA")
                        .header("Accept-Language", "es-ES,es;q=0.9"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value(
                        "Use either 'year' or the 'yearFrom'/'yearTo' range parameters, not both."
                ))
                .andExpect(jsonPath("$.messageKey").value("error.request.invalid"))
                .andExpect(jsonPath("$.message").value("Zapyt ye nevalidnym."))
                .andExpect(jsonPath("$.language").value("uk"));
    }

    @Test
    void invalidLangQueryParameterFallsBackToEnglish() throws Exception {
        mockMvc.perform(get("/api/books/{id}", "abc")
                        .queryParam("lang", "zz-ZZ")
                        .header("Accept-Language", "pl-PL,pl;q=0.9"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Parameter"))
                .andExpect(jsonPath("$.detail").value("Parameter 'id' value 'abc' is invalid."))
                .andExpect(jsonPath("$.messageKey").value("error.request.invalid_parameter"))
                .andExpect(jsonPath("$.message").value("A request parameter has an invalid value."))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void languageCookieIsUsedWhenAcceptLanguageIsUnsupportedAndQueryOverrideIsAbsent() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 9999)
                        .header("Accept-Language", "it-IT,it;q=0.9")
                        .cookie(new Cookie("language", "pl-PL")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book Not Found"))
                .andExpect(jsonPath("$.detail").value("Book with id 9999 was not found."))
                .andExpect(jsonPath("$.messageKey").value("error.book.not_found"))
                .andExpect(jsonPath("$.message").value("Nie znaleziono zadanej ksiazki."))
                .andExpect(jsonPath("$.language").value("pl"));
    }

    @Test
    void unsupportedAcceptLanguageFallsBackToLanguageCookie() throws Exception {
        mockMvc.perform(get("/api/books")
                        .queryParam("year", "2018")
                        .queryParam("yearFrom", "2000")
                        .header("Accept-Language", "it-IT,it;q=0.9")
                        .cookie(new Cookie("language", "no")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value(
                        "Use either 'year' or the 'yearFrom'/'yearTo' range parameters, not both."
                ))
                .andExpect(jsonPath("$.messageKey").value("error.request.invalid"))
                .andExpect(jsonPath("$.message").value("Foresporselen er ugyldig."))
                .andExpect(jsonPath("$.language").value("no"));
    }

}

