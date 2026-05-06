package team.jit.technicalinterviewdemo.business.book;

import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

record BookSearchCriteria(
        String title,
        String author,
        String isbn,
        Integer year,
        Integer yearFrom,
        Integer yearTo,
        List<String> categories
) {

    private static final int MAX_TEXT_FILTER_LENGTH = 100;
    private static final int MAX_ISBN_FILTER_LENGTH = 32;
    private static final int MAX_CATEGORY_FILTER_COUNT = 10;
    private static final int MIN_YEAR = 0;
    private static final int MAX_YEAR = 3000;
    private static final Pattern ISBN_FILTER_PATTERN = Pattern.compile("[0-9Xx-]+");

    BookSearchCriteria {
        categories = categories == null ? List.of() : List.copyOf(categories);
    }

    static BookSearchCriteria fromRequest(BookSearchRequest request) {
        String normalizedTitle = normalizeTextFilter("title", request.getTitle(), MAX_TEXT_FILTER_LENGTH);
        String normalizedAuthor = normalizeTextFilter("author", request.getAuthor(), MAX_TEXT_FILTER_LENGTH);
        String normalizedIsbn = normalizeIsbnFilter(request.getIsbn());
        List<String> normalizedCategories = normalizeCategoryFilters(request.getCategory());
        validateYearFilter("year", request.getYear());
        validateYearFilter("yearFrom", request.getYearFrom());
        validateYearFilter("yearTo", request.getYearTo());

        if (request.getYear() != null && (request.getYearFrom() != null || request.getYearTo() != null)) {
            throw new InvalidRequestException("Use either 'year' or the 'yearFrom'/'yearTo' range parameters, not both.");
        }
        if (request.getYearFrom() != null
                && request.getYearTo() != null
                && request.getYearFrom() > request.getYearTo()) {
            throw new InvalidRequestException("'yearFrom' must be less than or equal to 'yearTo'.");
        }

        return new BookSearchCriteria(
                normalizedTitle,
                normalizedAuthor,
                normalizedIsbn,
                request.getYear(),
                request.getYearFrom(),
                request.getYearTo(),
                normalizedCategories
        );
    }

    private static String normalizeTextFilter(String fieldName, String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalizedValue = value.trim();
        if (normalizedValue.length() > maxLength) {
            throw new InvalidRequestException(
                    "Filter '%s' must be at most %d characters.".formatted(fieldName, maxLength)
            );
        }
        return normalizedValue.toLowerCase(Locale.ROOT);
    }

    private static String normalizeIsbnFilter(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return null;
        }

        String normalizedIsbn = isbn.trim();
        if (normalizedIsbn.length() > MAX_ISBN_FILTER_LENGTH) {
            throw new InvalidRequestException(
                    "Filter 'isbn' must be at most %d characters.".formatted(MAX_ISBN_FILTER_LENGTH)
            );
        }
        if (!ISBN_FILTER_PATTERN.matcher(normalizedIsbn).matches()) {
            throw new InvalidRequestException("Filter 'isbn' may contain only digits, hyphens, and X.");
        }
        return normalizedIsbn.toLowerCase(Locale.ROOT);
    }

    private static void validateYearFilter(String fieldName, Integer value) {
        if (value == null) {
            return;
        }
        if (value < MIN_YEAR || value > MAX_YEAR) {
            throw new InvalidRequestException(
                    "Filter '%s' must be between %d and %d.".formatted(fieldName, MIN_YEAR, MAX_YEAR)
            );
        }
    }

    private static List<String> normalizeCategoryFilters(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        if (categories.size() > MAX_CATEGORY_FILTER_COUNT) {
            throw new InvalidRequestException(
                    "At most %d category filters are supported.".formatted(MAX_CATEGORY_FILTER_COUNT)
            );
        }

        LinkedHashSet<String> normalizedCategories = new LinkedHashSet<>();
        for (String category : categories) {
            String normalizedCategory = normalizeTextFilter("category", category, MAX_TEXT_FILTER_LENGTH);
            if (normalizedCategory != null) {
                normalizedCategories.add(normalizedCategory);
            }
        }
        return List.copyOf(normalizedCategories);
    }
}
