package team.jit.technicalinterviewdemo.business.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import team.jit.technicalinterviewdemo.testdata.BookCatalogTestData;
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
        BookCatalogTestData.seedDefaultCatalog(bookRepository, categoryRepository, cacheManager);
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
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.detail").value("Category management requires the ADMIN role."))
                .andExpect(jsonPath("$.messageKey").value("error.request.forbidden"))
                .andExpect(jsonPath("$.message").value("You do not have permission to perform this operation."))
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
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Authentication is required to access this resource."))
                .andExpect(jsonPath("$.messageKey").value("error.request.unauthorized"))
                .andExpect(jsonPath("$.message").value("You must authenticate before performing this operation."))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void createCategoryWithoutAuthenticationUsesRequestLanguageForUnauthorizedProblem() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .queryParam("lang", "pl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Architecture"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.messageKey").value("error.request.unauthorized"))
                .andExpect(jsonPath("$.message").value("Musisz sie uwierzytelnic przed wykonaniem tej operacji."))
                .andExpect(jsonPath("$.language").value("pl"));
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

    @Test
    void updateCategoryReturnsUpdatedCategory() throws Exception {
        Category javaCategory = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(category -> "Java".equals(category.getName()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(put("/api/categories/{id}", javaCategory.getId())
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "JVM"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(javaCategory.getId()))
                .andExpect(jsonPath("$.name").value("JVM"));
    }

    @Test
    void updateCategoryWithDuplicateNameReturnsBadRequest() throws Exception {
        Category javaCategory = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(category -> "Java".equals(category.getName()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(put("/api/categories/{id}", javaCategory.getId())
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Best Practices"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("Category 'Best Practices' already exists."))
                .andExpect(jsonPath("$.messageKey").value("error.request.invalid"));
    }

    @Test
    void updateCategoryMissingIdReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/categories/{id}", 9999)
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Architecture"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Category Not Found"))
                .andExpect(jsonPath("$.detail").value("Category with id 9999 was not found."))
                .andExpect(jsonPath("$.messageKey").value("error.category.not_found"))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void updateCategoryAsRegularUserReturnsForbidden() throws Exception {
        Category javaCategory = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(category -> "Java".equals(category.getName()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(put("/api/categories/{id}", javaCategory.getId())
                        .with(oauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "JVM"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Category management requires the ADMIN role."))
                .andExpect(jsonPath("$.messageKey").value("error.request.forbidden"));
    }

    @Test
    void deleteUnusedCategoryReturnsNoContent() throws Exception {
        Category architecture = categoryRepository.saveAndFlush(new Category("Architecture"));

        mockMvc.perform(delete("/api/categories/{id}", architecture.getId())
                        .with(adminOauthUser()))
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.findById(architecture.getId())).isEmpty();
    }

    @Test
    void deleteCategoryAssignedToBooksReturnsConflict() throws Exception {
        Category javaCategory = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(category -> "Java".equals(category.getName()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(delete("/api/categories/{id}", javaCategory.getId())
                        .with(adminOauthUser()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Category In Use"))
                .andExpect(jsonPath("$.detail").value(
                        "Category 'Java' with id %d cannot be deleted because it is still assigned to one or more books."
                                .formatted(javaCategory.getId())
                ))
                .andExpect(jsonPath("$.messageKey").value("error.category.in_use"))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void deleteMissingCategoryReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", 9999)
                        .with(adminOauthUser()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Category Not Found"))
                .andExpect(jsonPath("$.detail").value("Category with id 9999 was not found."))
                .andExpect(jsonPath("$.messageKey").value("error.category.not_found"));
    }

    @Test
    void deleteCategoryWithoutAuthenticationReturnsUnauthorized() throws Exception {
        Category javaCategory = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(category -> "Java".equals(category.getName()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(delete("/api/categories/{id}", javaCategory.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.messageKey").value("error.request.unauthorized"));
    }
}
