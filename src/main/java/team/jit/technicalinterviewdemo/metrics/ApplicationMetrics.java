package team.jit.technicalinterviewdemo.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team.jit.technicalinterviewdemo.book.BookRepository;
import team.jit.technicalinterviewdemo.category.CategoryRepository;
import team.jit.technicalinterviewdemo.localization.LocalizationMessageRepository;

@Slf4j
@Component
public class ApplicationMetrics {

    private static final String BOOK_OPERATIONS = "technical.interview.demo.books.operations";
    private static final String BOOK_TOTAL = "technical.interview.demo.books.total";
    private static final String CATEGORY_OPERATIONS = "technical.interview.demo.categories.operations";
    private static final String CATEGORY_TOTAL = "technical.interview.demo.categories.total";
    private static final String LOCALIZATION_OPERATIONS = "technical.interview.demo.localization.operations";
    private static final String LOCALIZATION_TOTAL = "technical.interview.demo.localization.total";
    private static final String CACHE_EVENTS = "technical.interview.demo.cache.events";

    private final MeterRegistry meterRegistry;

    public ApplicationMetrics(
            MeterRegistry meterRegistry,
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            LocalizationMessageRepository localizationMessageRepository
    ) {
        this.meterRegistry = meterRegistry;
        Gauge.builder(BOOK_TOTAL, bookRepository, BookRepository::count)
                .description("Current number of books.")
                .register(meterRegistry);
        Gauge.builder(CATEGORY_TOTAL, categoryRepository, CategoryRepository::count)
                .description("Current number of categories.")
                .register(meterRegistry);
        Gauge.builder(LOCALIZATION_TOTAL, localizationMessageRepository, LocalizationMessageRepository::count)
                .description("Current number of localization messages.")
                .register(meterRegistry);
        log.debug("Registered application gauges for books, categories, and localization messages.");
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

    public void recordCacheEvent(String cacheName, String event) {
        meterRegistry.counter(CACHE_EVENTS, "cache", cacheName, "event", event).increment();
    }
}
