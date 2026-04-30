package team.jit.technicalinterviewdemo.book;

import jakarta.persistence.criteria.JoinType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

public final class BookSpecifications {

    private BookSpecifications() {
    }

    public static Specification<Book> fromSearchRequest(BookSearchRequest request) {
        Specification<Book> specification = null;
        specification = and(specification, containsIgnoreCase("title", request.getTitle()));
        specification = and(specification, containsIgnoreCase("author", request.getAuthor()));
        specification = and(specification, containsIgnoreCase("isbn", request.getIsbn()));
        specification = and(specification, hasCategoryName(request.getCategory()));
        specification = and(specification, publicationYearMatches(request.getYear(), request.getYearFrom(), request.getYearTo()));
        return specification == null ? (root, query, criteriaBuilder) -> criteriaBuilder.conjunction() : specification;
    }

    private static Specification<Book> containsIgnoreCase(String property, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalizedValue = "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get(property)), normalizedValue);
    }

    private static Specification<Book> publicationYearMatches(Integer year, Integer yearFrom, Integer yearTo) {
        if (year != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("publicationYear"), year);
        }
        if (yearFrom == null && yearTo == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            if (yearFrom != null && yearTo != null) {
                return criteriaBuilder.between(root.get("publicationYear"), yearFrom, yearTo);
            }
            if (yearFrom != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("publicationYear"), yearFrom);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("publicationYear"), yearTo);
        };
    }

    private static Specification<Book> hasCategoryName(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        LinkedHashSet<String> normalizedCategories = new LinkedHashSet<>();
        for (String category : categories) {
            if (category != null && !category.isBlank()) {
                normalizedCategories.add(category.trim().toLowerCase(Locale.ROOT));
            }
        }
        if (normalizedCategories.isEmpty()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.lower(root.join("categories", JoinType.LEFT).get("name")).in(normalizedCategories);
        };
    }

    private static Specification<Book> and(Specification<Book> left, Specification<Book> right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return left.and(right);
    }
}
