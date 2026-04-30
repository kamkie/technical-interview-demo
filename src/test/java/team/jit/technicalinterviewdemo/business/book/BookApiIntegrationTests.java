package team.jit.technicalinterviewdemo.business.book;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.oauthUser;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import team.jit.technicalinterviewdemo.testing.AbstractBookCatalogMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
class BookApiIntegrationTests extends AbstractBookCatalogMockMvcIntegrationTest {

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
}
