package team.jit.technicalinterviewdemo;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import team.jit.technicalinterviewdemo.book.Book;
import team.jit.technicalinterviewdemo.book.BookRepository;

import java.util.Comparator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class ApiDemoTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BookRepository bookRepository;

    private MockMvc mockMvc;
    private Book cleanCode;
    private Book effectiveJava;

    @BeforeEach
    void setUp() {
        Filter[] filters = webApplicationContext.getBeansOfType(Filter.class).values().stream()
                .sorted(Comparator.comparing(filter -> filter.getClass().getName()))
                .toArray(Filter[]::new);

        mockMvc = webAppContextSetup(webApplicationContext)
                .addFilters(filters)
                .build();
        bookRepository.deleteAll();
        cleanCode = bookRepository.saveAndFlush(new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008));
        effectiveJava = bookRepository.saveAndFlush(new Book("Effective Java", "Joshua Bloch", "9780134685991", 2018));
    }

    @Test
    void helloEndpointReturnsHelloWorld() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }

    @Test
    void listBooksReturnsSeededBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[1].title").value("Effective Java"));
    }

    @Test
    void getBookByIdReturnsRequestedBook() throws Exception {
        mockMvc.perform(get("/api/books/{id}", effectiveJava.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(effectiveJava.getId()))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"));
    }

    @Test
    void createBookReturnsCreatedBook() throws Exception {
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Spring in Action"))
                .andExpect(jsonPath("$.author").value("Craig Walls"))
                .andExpect(jsonPath("$.isbn").value("9781617297571"))
                .andExpect(jsonPath("$.publicationYear").value(2022));
    }

    @Test
    void createBookWithDuplicateIsbnReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/books")
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
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void createBookWithInvalidPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/books")
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
                .andExpect(jsonPath("$.detail").value("Resource 'api/missing' was not found."));
    }

    @Test
    void updateBookReturnsUpdatedBook() throws Exception {
        mockMvc.perform(put("/api/books/{id}", cleanCode.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code Second Edition",
                                  "author": "Robert C. Martin",
                                  "isbn": "9780132350884",
                                  "publicationYear": 2026
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cleanCode.getId()))
                .andExpect(jsonPath("$.title").value("Clean Code Second Edition"))
                .andExpect(jsonPath("$.publicationYear").value(2026));
    }

    @Test
    void deleteBookRemovesBook() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", cleanCode.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/{id}", cleanCode.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book Not Found"))
                .andExpect(jsonPath("$.detail").value("Book with id %d was not found.".formatted(cleanCode.getId())))
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.trace").doesNotExist());
    }
}
