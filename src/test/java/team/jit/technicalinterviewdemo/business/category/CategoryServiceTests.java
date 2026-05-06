package team.jit.technicalinterviewdemo.business.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.util.ReflectionTestUtils;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserRole;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;
import team.jit.technicalinterviewdemo.testing.CacheTestSupport;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {

    private static final String CATEGORY_DIRECTORY_CACHE_KEY = "byNormalizedName";

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ApplicationMetrics applicationMetrics;

    @Mock
    private CurrentUserAccountService currentUserAccountService;

    @Mock
    private AuditLogService auditLogService;

    private CacheManager cacheManager;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        cacheManager = new ConcurrentMapCacheManager(CacheNames.CATEGORIES, CacheNames.CATEGORY_DIRECTORY);
        categoryService = new CategoryService(
                categoryRepository,
                bookRepository,
                cacheManager,
                applicationMetrics,
                currentUserAccountService,
                auditLogService);
    }

    @Test
    void resolveForAssignmentUsesNormalizedDirectoryCacheAndReturnsAlphabeticalCategories() {
        Category bestPractices = category(1L, "Best Practices");
        Category java = category(2L, "Java");
        when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(List.of(bestPractices, java));
        when(categoryRepository.findAllByNormalizedNames(Set.of("java", "best practices")))
                .thenReturn(List.of(bestPractices, java));
        when(categoryRepository.findAllByNormalizedNames(Set.of("java"))).thenReturn(List.of(java));

        Set<Category> resolvedCategories = categoryService.resolveForAssignment(List.of("  java  ", "BEST PRACTICES"));

        assertThat(resolvedCategories).extracting(Category::getName).containsExactly("Best Practices", "Java");
        assertThat(cachedCategoryDirectory())
                .containsEntry("best practices", 1L)
                .containsEntry("java", 2L);

        Set<Category> secondResolution = categoryService.resolveForAssignment(List.of("JAVA"));

        assertThat(secondResolution).extracting(Category::getName).containsExactly("Java");
        verify(categoryRepository, times(1)).findAllByOrderByNameAsc();
        verify(categoryRepository).findAllByNormalizedNames(Set.of("java", "best practices"));
        verify(categoryRepository).findAllByNormalizedNames(Set.of("java"));
        verify(applicationMetrics, times(2)).recordCategoryOperation("resolve");
        verify(applicationMetrics).recordCacheEvent(CacheNames.CATEGORY_DIRECTORY, "miss");
        verify(applicationMetrics).recordCacheEvent(CacheNames.CATEGORY_DIRECTORY, "put");
        verify(applicationMetrics).recordCacheEvent(CacheNames.CATEGORY_DIRECTORY, "hit");
    }

    @Test
    void resolveForAssignmentRejectsUnknownCategoriesAfterTrimmingInput() {
        when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(List.of(category(1L, "Java")));

        assertThatThrownBy(() -> categoryService.resolveForAssignment(List.of("  Missing Category  ")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Unknown categories: Missing Category.");

        verify(categoryRepository, never()).findAllById(any());
    }

    @Test
    void createRejectsDuplicateNameIgnoringCaseAfterTrimming() {
        when(categoryRepository.existsByNameIgnoreCase("java")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(new CategoryCreateRequest("  java  ")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Category 'java' already exists.");

        verify(currentUserAccountService).requireRole(UserRole.ADMIN, "Category management requires the ADMIN role.");
        verify(categoryRepository).existsByNameIgnoreCase("java");
        verify(categoryRepository, never()).saveAndFlush(any());
        verifyNoInteractions(auditLogService);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Long> cachedCategoryDirectory() {
        return CacheTestSupport.cache(cacheManager, CacheNames.CATEGORY_DIRECTORY)
                .get(CATEGORY_DIRECTORY_CACHE_KEY, Map.class);
    }

    private static Category category(Long id, String name) {
        Category category = new Category(name);
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }
}
