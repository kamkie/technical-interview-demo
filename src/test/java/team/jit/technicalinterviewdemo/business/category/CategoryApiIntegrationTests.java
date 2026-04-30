package team.jit.technicalinterviewdemo.business.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminOauthUser;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.oauthUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.BookCatalogTestData;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
class CategoryApiIntegrationTests extends AbstractMockMvcIntegrationTest {

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
    void listCategoriesReturnsOrderedCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Best Practices"))
                .andExpect(jsonPath("$[1].name").value("Java"));
    }

    @Test
    void createCategoryReturnsCreatedCategory() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(adminOauthUser())
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
    void createCategoryAsRegularUserReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Architecture"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.detail").value("Category management requires the ADMIN role."))
                .andExpect(jsonPath("$.messageKey").value("error.request.forbidden"))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void createCategoryWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/categories")
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
                        .with(adminOauthUser())
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
