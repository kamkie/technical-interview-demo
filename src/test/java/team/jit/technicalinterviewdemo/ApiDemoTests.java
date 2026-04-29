package team.jit.technicalinterviewdemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import team.jit.technicalinterviewdemo.book.Book;
import team.jit.technicalinterviewdemo.book.BookRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        mockMvc = webAppContextSetup(webApplicationContext).build();
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
                .andExpect(status().reason("ISBN already exists"));
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
                                  "publicationYear": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isNotFound());
    }
}
