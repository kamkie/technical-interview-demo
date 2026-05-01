package team.jit.technicalinterviewdemo.technical.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRepository;
import team.jit.technicalinterviewdemo.business.user.UserAccountRepository;
import team.jit.technicalinterviewdemo.business.user.UserRole;

@Slf4j
@Component
public class ApplicationMetrics {

    private static final String BOOK_OPERATIONS = "technical.interview.demo.books.operations";
    private static final String BOOK_TOTAL = "technical.interview.demo.books.total";
    private static final String CATEGORY_OPERATIONS = "technical.interview.demo.categories.operations";
    private static final String CATEGORY_TOTAL = "technical.interview.demo.categories.total";
    private static final String LOCALIZATION_OPERATIONS = "technical.interview.demo.localization.operations";
    private static final String LOCALIZATION_TOTAL = "technical.interview.demo.localization.total";
    private static final String USER_OPERATIONS = "technical.interview.demo.users.operations";
    private static final String USER_TOTAL = "technical.interview.demo.users.total";
    private static final String ADMIN_TOTAL = "technical.interview.demo.users.admin.total";
    private static final String CACHE_EVENTS = "technical.interview.demo.cache.events";

    private final MeterRegistry meterRegistry;

    public ApplicationMetrics(
            MeterRegistry meterRegistry,
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            LocalizationRepository localizationRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.meterRegistry = meterRegistry;
        Gauge.builder(BOOK_TOTAL, bookRepository, BookRepository::count)
                .description("Current number of books.")
                .register(meterRegistry);
        Gauge.builder(CATEGORY_TOTAL, categoryRepository, CategoryRepository::count)
                .description("Current number of categories.")
                .register(meterRegistry);
        Gauge.builder(LOCALIZATION_TOTAL, localizationRepository, LocalizationRepository::count)
                .description("Current number of localization messages.")
                .register(meterRegistry);
        Gauge.builder(USER_TOTAL, userAccountRepository, UserAccountRepository::count)
                .description("Current number of persisted application users.")
                .register(meterRegistry);
        Gauge.builder(ADMIN_TOTAL, userAccountRepository, repository -> repository.countByRole(UserRole.ADMIN))
                .description("Current number of persisted admin users.")
                .register(meterRegistry);
        log.debug("Registered application gauges for books, categories, localization messages, and users.");
    }

    public void recordBookOperation(String operation) {
        meterRegistry.counter(BOOK_OPERATIONS, "operation", operation).increment();
    }

    public void recordCategoryOperation(String operation) {
        meterRegistry.counter(CATEGORY_OPERATIONS, "operation", operation).increment();
    }

    public void recordLocalizationOperation(String operation) {
        meterRegistry.counter(LOCALIZATION_OPERATIONS, "operation", operation).increment();
    }

    public void recordUserOperation(String operation) {
        meterRegistry.counter(USER_OPERATIONS, "operation", operation).increment();
    }

    public void recordCacheEvent(String cacheName, String event) {
        meterRegistry.counter(CACHE_EVENTS, "cache", cacheName, "event", event).increment();
    }
}
