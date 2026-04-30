package team.jit.technicalinterviewdemo.business.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryService;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private static final int MAX_TEXT_FILTER_LENGTH = 100;
    private static final int MAX_ISBN_FILTER_LENGTH = 32;
    private static final int MAX_CATEGORY_FILTER_COUNT = 10;
    private static final int MIN_YEAR = 0;
    private static final int MAX_YEAR = 3000;
    private static final Pattern ISBN_FILTER_PATTERN = Pattern.compile("[0-9Xx-]+");
    private static final Map<String, String> SORT_PROPERTY_ALIASES = Map.of(
            "id", "id",
            "title", "title",
            "author", "author",
            "isbn", "isbn",
            "year", "publicationYear",
            "publicationYear", "publicationYear"
    );

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final ApplicationMetrics applicationMetrics;
    private final AuditLogService auditLogService;

    public Page<Book> findAll(BookSearchRequest request, Pageable pageable) {
        applicationMetrics.recordBookOperation("list");
        validateSearchRequest(request);
        Pageable effectivePageable = createEffectivePageable(pageable);
        Specification<Book> searchSpecification = BookSearchSpecifications.fromSearchRequest(request);
        return bookRepository.findAll(searchSpecification, effectivePageable);
    }

    public Book findById(Long id) {
        applicationMetrics.recordBookOperation("get");
        return requireBook(id);
    }

    @Transactional
    public Book create(BookCreateRequest request) {
        validateUniqueIsbn(request.isbn());
        Set<Category> categories = categoryService.resolveForAssignment(request.categories());
        Book book = new Book(request.title(), request.author(), request.isbn(), request.publicationYear(), categories);
        Book savedBook = bookRepository.saveAndFlush(book);
        applicationMetrics.recordBookOperation("create");
        auditLogService.record(
                AuditTargetType.BOOK,
                savedBook.getId(),
                AuditAction.CREATE,
                "Created book '%s' with ISBN %s.".formatted(savedBook.getTitle(), savedBook.getIsbn())
        );
        log.info("Created book id={} isbn={} title={}", savedBook.getId(), savedBook.getIsbn(), savedBook.getTitle());
        return savedBook;
    }

    @Transactional
    public Book update(Long id, BookUpdateRequest request) {
        Book book = requireBook(id);
        if (!book.getVersion().equals(request.version())) {
            throw new StaleBookVersionException(id, request.version(), book.getVersion());
        }

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublicationYear(request.publicationYear());
        book.setCategories(categoryService.resolveForAssignment(request.categories()));

        Book updatedBook;
        try {
            updatedBook = bookRepository.saveAndFlush(book);
        } catch (ObjectOptimisticLockingFailureException exception) {
            throw new StaleBookVersionException(id, request.version(), book.getVersion(), exception);
        }
        applicationMetrics.recordBookOperation("update");
        auditLogService.record(
                AuditTargetType.BOOK,
                updatedBook.getId(),
                AuditAction.UPDATE,
                "Updated book '%s' with ISBN %s.".formatted(updatedBook.getTitle(), updatedBook.getIsbn())
        );
        log.info("Updated book id={} isbn={} title={}", updatedBook.getId(), updatedBook.getIsbn(), updatedBook.getTitle());
        return updatedBook;
    }

    @Transactional
    public void delete(Long id) {
        Book book = requireBook(id);
        bookRepository.delete(book);
        applicationMetrics.recordBookOperation("delete");
        auditLogService.record(
                AuditTargetType.BOOK,
                id,
                AuditAction.DELETE,
                "Deleted book '%s' with ISBN %s.".formatted(book.getTitle(), book.getIsbn())
        );
        log.info("Deleted book id={}", id);
    }

    private void validateUniqueIsbn(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateIsbnException(isbn);
        }
    }

    private Book requireBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    private void validateSearchRequest(BookSearchRequest request) {
        validateTextFilter("title", request.getTitle(), MAX_TEXT_FILTER_LENGTH);
        validateTextFilter("author", request.getAuthor(), MAX_TEXT_FILTER_LENGTH);
        validateIsbnFilter(request.getIsbn());
        validateCategoryFilters(request.getCategory());
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
    }

    private Pageable createEffectivePageable(Pageable pageable) {
        Sort effectiveSort = pageable.getSort().isSorted() ? normalizeSort(pageable.getSort()) : Sort.by(Sort.Order.asc("id"));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), effectiveSort);
    }

    private Sort normalizeSort(Sort sort) {
        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : sort) {
            String property = SORT_PROPERTY_ALIASES.get(order.getProperty());
            if (property == null) {
                throw new InvalidRequestException(
                        "Sort field '%s' is not supported. Use one of: id, title, author, isbn, year."
                                .formatted(order.getProperty())
                );
            }
            orders.add(new Sort.Order(order.getDirection(), property));
        }
        return Sort.by(orders);
    }

    private void validateTextFilter(String fieldName, String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (value.trim().length() > maxLength) {
            throw new InvalidRequestException(
                    "Filter '%s' must be at most %d characters.".formatted(fieldName, maxLength)
            );
        }
    }

    private void validateIsbnFilter(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return;
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
    }

    private void validateYearFilter(String fieldName, Integer value) {
        if (value == null) {
            return;
        }
        if (value < MIN_YEAR || value > MAX_YEAR) {
            throw new InvalidRequestException(
                    "Filter '%s' must be between %d and %d.".formatted(fieldName, MIN_YEAR, MAX_YEAR)
            );
        }
    }

    private void validateCategoryFilters(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return;
        }
        if (categories.size() > MAX_CATEGORY_FILTER_COUNT) {
            throw new InvalidRequestException(
                    "At most %d category filters are supported.".formatted(MAX_CATEGORY_FILTER_COUNT)
            );
        }

        for (String category : categories) {
            validateTextFilter("category", category, MAX_TEXT_FILTER_LENGTH);
        }
    }
}

