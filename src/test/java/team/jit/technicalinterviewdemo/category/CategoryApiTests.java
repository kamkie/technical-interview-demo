package team.jit.technicalinterviewdemo.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import team.jit.technicalinterviewdemo.TestcontainersTest;
import team.jit.technicalinterviewdemo.book.BookRepository;

@TestcontainersTest
@SpringBootTest
@AutoConfigureMockMvc
class CategoryApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        categoryRepository.saveAndFlush(new Category("Best Practices"));
        categoryRepository.saveAndFlush(new Category("Java"));
    }

    @Test
    void listCategoriesReturnsOrderedCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Best Practices"))
                .andExpect(jsonPath("$[1].name").value("Java"));
    }

    @Test
    void createCategoryReturnsCreatedCategory() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Architecture"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Architecture"));
    }

    @Test
    void createCategoryWithDuplicateNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "java"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("Category 'java' already exists."))
                .andExpect(jsonPath("$.messageKey").value("error.request.invalid"))
                .andExpect(jsonPath("$.language").value("en"));
    }
}
