package team.jit.technicalinterviewdemo.business.book;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public final class BookSearchSpecifications {

    private BookSearchSpecifications() {
    }

    public static Specification<Book> fromSearchRequest(BookSearchRequest request) {
        return fromCriteria(BookSearchCriteria.fromRequest(request));
    }

    static Specification<Book> fromCriteria(BookSearchCriteria criteria) {
        Specification<Book> specification = null;
        specification = and(specification, containsIgnoreCase("title", criteria.title()));
        specification = and(specification, containsIgnoreCase("author", criteria.author()));
        specification = and(specification, containsIgnoreCase("isbn", criteria.isbn()));
        specification = and(specification, hasCategoryName(criteria.categories()));
        specification = and(specification, publicationYearMatches(criteria.year(), criteria.yearFrom(), criteria.yearTo()));
        return specification == null ? (root, query, criteriaBuilder) -> criteriaBuilder.conjunction() : specification;
    }

    private static Specification<Book> containsIgnoreCase(String property, String normalizedValue) {
        if (normalizedValue == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(property)), "%" + normalizedValue + "%");
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
        if (categories.isEmpty()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.lower(root.join("categories", JoinType.LEFT).get("name")).in(categories);
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
