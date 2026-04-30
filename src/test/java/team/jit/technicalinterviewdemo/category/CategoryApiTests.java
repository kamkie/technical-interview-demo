package team.jit.technicalinterviewdemo.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.SecurityTestSupport.csrfToken;
import static team.jit.technicalinterviewdemo.SecurityTestSupport.oauthUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import team.jit.technicalinterviewdemo.TestcontainersTest;
import team.jit.technicalinterviewdemo.book.BookRepository;
import team.jit.technicalinterviewdemo.cache.CacheNames;

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
    void listCategoriesReturnsOrderedCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Best Practices"))
                .andExpect(jsonPath("$[1].name").value("Java"));
    }

    @Test
    void createCategoryReturnsCreatedCategory() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(oauthUser())
                        .with(csrfToken())
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
    void createCategoryWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(csrfToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Architecture"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCategoryWithDuplicateNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(oauthUser())
                        .with(csrfToken())
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
