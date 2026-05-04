package team.jit.technicalinterviewdemo.business.category;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserRole;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private static final int MAX_CATEGORY_NAME_LENGTH = 60;
    private static final String ALL_CATEGORIES_CACHE_KEY = "all";
    private static final String CATEGORY_DIRECTORY_CACHE_KEY = "byNormalizedName";

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CacheManager cacheManager;
    private final ApplicationMetrics applicationMetrics;
    private final CurrentUserAccountService currentUserAccountService;
    private final AuditLogService auditLogService;

    public List<Category> findAll() {
        applicationMetrics.recordCategoryOperation("list");
        Cache categoriesCache = requireCache(CacheNames.CATEGORIES);
        @SuppressWarnings("unchecked")
        List<Category> cachedCategories = categoriesCache.get(ALL_CATEGORIES_CACHE_KEY, List.class);
        if (cachedCategories != null) {
            applicationMetrics.recordCacheEvent(CacheNames.CATEGORIES, "hit");
            return cachedCategories;
        }

        applicationMetrics.recordCacheEvent(CacheNames.CATEGORIES, "miss");
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        categoriesCache.put(ALL_CATEGORIES_CACHE_KEY, categories);
        applicationMetrics.recordCacheEvent(CacheNames.CATEGORIES, "put");
        return categories;
    }

    @Transactional
    public Category create(CategoryCreateRequest request) {
        currentUserAccountService.requireRole(UserRole.ADMIN, "Category management requires the ADMIN role.");
        String normalizedName = normalizeName(request.name());
        validateUniqueName(normalizedName, null);

        Category savedCategory = categoryRepository.saveAndFlush(new Category(normalizedName));
        evictCategoryCaches();
        applicationMetrics.recordCategoryOperation("create");
        auditLogService.record(
                AuditTargetType.CATEGORY,
                savedCategory.getId(),
                AuditAction.CREATE,
                "Created category '%s'.".formatted(savedCategory.getName()),
                auditDetails(savedCategory.getName())
        );
        log.info("Created category id={} name={}", savedCategory.getId(), savedCategory.getName());
        return savedCategory;
    }

    @Transactional
    public Category update(Long id, CategoryUpdateRequest request) {
        currentUserAccountService.requireRole(UserRole.ADMIN, "Category management requires the ADMIN role.");
        Category category = requireCategory(id);
        String previousName = category.getName();
        String normalizedName = normalizeName(request.name());
        validateUniqueName(normalizedName, id);

        category.setName(normalizedName);
        Category updatedCategory = categoryRepository.saveAndFlush(category);
        evictCategoryCaches();
        applicationMetrics.recordCategoryOperation("update");
        auditLogService.record(
                AuditTargetType.CATEGORY,
                updatedCategory.getId(),
                AuditAction.UPDATE,
                "Updated category '%s'.".formatted(updatedCategory.getName()),
                Map.of(
                        "previousName", previousName,
                        "name", updatedCategory.getName()
                )
        );
        log.info("Updated category id={} name={}", updatedCategory.getId(), updatedCategory.getName());
        return updatedCategory;
    }

    @Transactional
    public void delete(Long id) {
        currentUserAccountService.requireRole(UserRole.ADMIN, "Category management requires the ADMIN role.");
        Category category = requireCategory(id);
        if (bookRepository.existsByCategories_Id(id)) {
            throw new CategoryInUseException(id, category.getName());
        }

        categoryRepository.delete(category);
        evictCategoryCaches();
        applicationMetrics.recordCategoryOperation("delete");
        auditLogService.record(
                AuditTargetType.CATEGORY,
                id,
                AuditAction.DELETE,
                "Deleted category '%s'.".formatted(category.getName()),
                auditDetails(category.getName())
        );
        log.info("Deleted category id={} name={}", id, category.getName());
    }

    public Set<Category> resolveForAssignment(Collection<String> names) {
        applicationMetrics.recordCategoryOperation("resolve");
        Set<String> normalizedNames = normalizeNames(names);
        if (normalizedNames.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Map<String, Long> categoryDirectory = getCategoryDirectory();
        List<String> missingNames = normalizedNames.stream()
                .filter(name -> !categoryDirectory.containsKey(name.toLowerCase(Locale.ROOT)))
                .toList();
        if (!missingNames.isEmpty()) {
            throw new InvalidRequestException("Unknown categories: %s.".formatted(String.join(", ", missingNames)));
        }

        List<Long> categoryIds = normalizedNames.stream()
                .map(name -> categoryDirectory.get(name.toLowerCase(Locale.ROOT)))
                .toList();
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        categories.sort(Comparator.comparing(Category::getName));
        return new LinkedHashSet<>(categories);
    }

    private Category requireCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Long> getCategoryDirectory() {
        Cache categoryDirectoryCache = requireCache(CacheNames.CATEGORY_DIRECTORY);
        Map<String, Long> cachedDirectory = categoryDirectoryCache.get(CATEGORY_DIRECTORY_CACHE_KEY, Map.class);
        if (cachedDirectory != null) {
            applicationMetrics.recordCacheEvent(CacheNames.CATEGORY_DIRECTORY, "hit");
            return cachedDirectory;
        }

        applicationMetrics.recordCacheEvent(CacheNames.CATEGORY_DIRECTORY, "miss");
        Map<String, Long> categoryDirectory = new LinkedHashMap<>();
        for (Category category : categoryRepository.findAllByOrderByNameAsc()) {
            categoryDirectory.put(category.getName().toLowerCase(Locale.ROOT), category.getId());
        }
        categoryDirectoryCache.put(CATEGORY_DIRECTORY_CACHE_KEY, categoryDirectory);
        applicationMetrics.recordCacheEvent(CacheNames.CATEGORY_DIRECTORY, "put");
        return categoryDirectory;
    }

    private Set<String> normalizeNames(Collection<String> names) {
        Set<String> normalizedNames = new LinkedHashSet<>();
        if (names == null) {
            return normalizedNames;
        }

        for (String name : names) {
            normalizedNames.add(normalizeName(name));
        }
        return normalizedNames;
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("category name is required.");
        }

        String normalizedName = name.trim();
        if (normalizedName.length() > MAX_CATEGORY_NAME_LENGTH) {
            throw new InvalidRequestException(
                    "category name must be at most %d characters.".formatted(MAX_CATEGORY_NAME_LENGTH)
            );
        }
        return normalizedName;
    }

    private void validateUniqueName(String normalizedName, Long id) {
        boolean exists = id == null
                ? categoryRepository.existsByNameIgnoreCase(normalizedName)
                : categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id);
        if (exists) {
            throw new InvalidRequestException("Category '%s' already exists.".formatted(normalizedName));
        }
    }

    private void evictCategoryCaches() {
        requireCache(CacheNames.CATEGORIES).clear();
        applicationMetrics.recordCacheEvent(CacheNames.CATEGORIES, "evict");
        requireCache(CacheNames.CATEGORY_DIRECTORY).clear();
        applicationMetrics.recordCacheEvent(CacheNames.CATEGORY_DIRECTORY, "evict");
    }

    private Cache requireCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache '%s' is not configured.".formatted(cacheName));
        }
        return cache;
    }

    private Map<String, Object> auditDetails(String name) {
        return Map.of("name", name);
    }
}
