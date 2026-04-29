package team.jit.technicalinterviewdemo;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import team.jit.technicalinterviewdemo.book.Book;
import team.jit.technicalinterviewdemo.book.BookRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class ApiDocumentationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private Book cleanCode;
    private Book effectiveJava;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        cleanCode = bookRepository.saveAndFlush(new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008));
        effectiveJava = bookRepository.saveAndFlush(new Book("Effective Java", "Joshua Bloch", "9780134685991", 2018));
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
    void documentListBooksEndpoint() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(documentEndpoint(
                        "books/list-books",
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("[].id").description("Book identifier."),
                                fieldWithPath("[].title").description("Book title."),
                                fieldWithPath("[].author").description("Book author."),
                                fieldWithPath("[].isbn").description("Unique ISBN assigned when the book is created."),
                                fieldWithPath("[].publicationYear").description("Publication year.")
                        )
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
                                fieldWithPath("title").description("Book title."),
                                fieldWithPath("author").description("Book author."),
                                fieldWithPath("isbn").description("Unique ISBN assigned when the book was created."),
                                fieldWithPath("publicationYear").description("Publication year.")
                        )
                ));
    }

    @Test
    void documentCreateBookEndpoint() throws Exception {
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
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andDo(documentEndpoint(
                        "books/create-book",
                        requestBody(),
                        requestFields(
                                fieldWithPath("title").description("Book title."),
                                fieldWithPath("author").description("Book author."),
                                fieldWithPath("isbn").description("Unique ISBN for the new book."),
                                fieldWithPath("publicationYear").description("Publication year.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("id").description("Created book identifier."),
                                fieldWithPath("title").description("Book title."),
                                fieldWithPath("author").description("Book author."),
                                fieldWithPath("isbn").description("Unique ISBN assigned to the book."),
                                fieldWithPath("publicationYear").description("Publication year.")
                        )
                ));
    }

    @Test
    void documentUpdateBookEndpoint() throws Exception {
        mockMvc.perform(put("/api/books/{id}", cleanCode.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code Second Edition",
                                  "author": "Robert C. Martin",
                                  "publicationYear": 2026
                                }
                                """))
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
                                fieldWithPath("publicationYear").description("Updated publication year.")
                        ),
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("id").description("Book identifier."),
                                fieldWithPath("title").description("Updated book title."),
                                fieldWithPath("author").description("Updated book author."),
                                fieldWithPath("isbn").description("Original ISBN. ISBN is immutable after creation."),
                                fieldWithPath("publicationYear").description("Updated publication year.")
                        )
                ));
    }

    @Test
    void documentDeleteBookEndpoint() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", cleanCode.getId()))
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
}
