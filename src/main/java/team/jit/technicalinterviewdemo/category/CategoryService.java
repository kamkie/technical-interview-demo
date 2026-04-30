package team.jit.technicalinterviewdemo.category;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.api.InvalidRequestException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private static final int MAX_CATEGORY_NAME_LENGTH = 60;

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public Category create(CategoryCreateRequest request) {
        String normalizedName = normalizeName(request.name());
        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new InvalidRequestException("Category '%s' already exists.".formatted(normalizedName));
        }

        Category savedCategory = categoryRepository.saveAndFlush(new Category(normalizedName));
        log.info("Created category id={} name={}", savedCategory.getId(), savedCategory.getName());
        return savedCategory;
    }

    public Set<Category> resolveForAssignment(Collection<String> names) {
        Set<String> normalizedNames = normalizeNames(names);
        if (normalizedNames.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<Category> categories = categoryRepository.findAllByNormalizedNames(
                normalizedNames.stream()
                        .map(name -> name.toLowerCase(Locale.ROOT))
                        .toList()
        );

        Set<String> resolvedNames = categories.stream()
                .map(category -> category.getName().toLowerCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.toSet());
        List<String> missingNames = normalizedNames.stream()
                .filter(name -> !resolvedNames.contains(name.toLowerCase(Locale.ROOT)))
                .toList();
        if (!missingNames.isEmpty()) {
            throw new InvalidRequestException("Unknown categories: %s.".formatted(String.join(", ", missingNames)));
        }

        return new LinkedHashSet<>(categories);
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
}
