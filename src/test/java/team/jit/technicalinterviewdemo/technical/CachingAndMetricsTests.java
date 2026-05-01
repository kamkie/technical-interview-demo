package team.jit.technicalinterviewdemo.technical;

import static org.assertj.core.api.Assertions.assertThat;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.clearAuthentication;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.setAdminAuthenticatedUser;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import team.jit.technicalinterviewdemo.business.book.BookCreateRequest;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.book.BookSearchRequest;
import team.jit.technicalinterviewdemo.business.book.BookService;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryCreateRequest;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.business.category.CategoryService;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRepository;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRequest;
import team.jit.technicalinterviewdemo.business.localization.LocalizationService;
import team.jit.technicalinterviewdemo.business.user.UserAccountRepository;
import team.jit.technicalinterviewdemo.testing.BookCatalogTestData;
import team.jit.technicalinterviewdemo.testing.CacheTestSupport;
import team.jit.technicalinterviewdemo.testing.IntegrationSpringBootTest;

@IntegrationSpringBootTest
class CachingAndMetricsTests {

    private static final String BOOK_OPERATIONS = "technical.interview.demo.books.operations";
    private static final String CATEGORY_OPERATIONS = "technical.interview.demo.categories.operations";
    private static final String LOCALIZATION_OPERATIONS = "technical.interview.demo.localization.operations";
    private static final String CACHE_EVENTS = "technical.interview.demo.cache.events";
    private static final String BOOK_TOTAL = "technical.interview.demo.books.total";
    private static final String CATEGORY_TOTAL = "technical.interview.demo.categories.total";
    private static final String LOCALIZATION_TOTAL = "technical.interview.demo.localization.total";
    private static final String CACHE_TEST_KEY = "cache.test.message";

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private LocalizationService localizationMessageService;

    @Autowired
    private LocalizationRepository localizationMessageRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        clearAuthentication();
        auditLogRepository.deleteAll();
        userAccountRepository.deleteAll();
        setAdminAuthenticatedUser();
        BookCatalogTestData.seedDefaultCatalog(bookRepository, categoryRepository, cacheManager);

        localizationMessageRepository.findByMessageKeyAndLanguage(CACHE_TEST_KEY, "en")
                .ifPresent(localizationMessageRepository::delete);
        localizationMessageRepository.findByMessageKeyAndLanguage(CACHE_TEST_KEY, "es")
                .ifPresent(localizationMessageRepository::delete);
        localizationMessageRepository.flush();
        localizationMessageService.create(new LocalizationRequest(
                CACHE_TEST_KEY,
                "en",
                "Cache EN",
                "English cache test message."
        ));
        localizationMessageService.create(new LocalizationRequest(
                CACHE_TEST_KEY,
                "es",
                "Cache ES",
                "Spanish cache test message."
        ));

        clearCaches();
    }

    @AfterEach
    void tearDown() {
        localizationMessageRepository.findByMessageKeyAndLanguage(CACHE_TEST_KEY, "en")
                .ifPresent(localizationMessageRepository::delete);
        localizationMessageRepository.findByMessageKeyAndLanguage(CACHE_TEST_KEY, "es")
                .ifPresent(localizationMessageRepository::delete);
        localizationMessageRepository.flush();
        auditLogRepository.deleteAll();
        userAccountRepository.deleteAll();
        clearAuthentication();
        clearCaches();
    }

    @Test
    void localizationLookupUsesCacheAndEvictsOnUpdate() {
        double missBefore = counterValue(CACHE_EVENTS, "cache", CacheNames.LOCALIZATION_LOOKUPS, "event", "miss");
        double hitBefore = counterValue(CACHE_EVENTS, "cache", CacheNames.LOCALIZATION_LOOKUPS, "event", "hit");
        double putBefore = counterValue(CACHE_EVENTS, "cache", CacheNames.LOCALIZATION_LOOKUPS, "event", "put");

        String first = localizationMessageService.getMessageWithFallback(CACHE_TEST_KEY, "es", "en");
        String second = localizationMessageService.getMessageWithFallback(CACHE_TEST_KEY, "es", "en");

        assertThat(first).isEqualTo("Cache ES");
        assertThat(second).isEqualTo("Cache ES");
        assertThat(cache(CacheNames.LOCALIZATION_LOOKUPS).get("%s::es::en".formatted(CACHE_TEST_KEY))).isNotNull();
        assertThat(counterValue(CACHE_EVENTS, "cache", CacheNames.LOCALIZATION_LOOKUPS, "event", "miss") - missBefore).isEqualTo(1.0d);
        assertThat(counterValue(CACHE_EVENTS, "cache", CacheNames.LOCALIZATION_LOOKUPS, "event", "hit") - hitBefore).isEqualTo(1.0d);
        assertThat(counterValue(CACHE_EVENTS, "cache", CacheNames.LOCALIZATION_LOOKUPS, "event", "put") - putBefore).isEqualTo(1.0d);

        Localization message = localizationMessageService.findByMessageKeyAndLanguage(CACHE_TEST_KEY, "es");
        localizationMessageService.update(message.getId(), new LocalizationRequest(
                CACHE_TEST_KEY,
                "es",
                "Cache ES Updated",
                "Updated Spanish cache test message."
        ));

        assertThat(cache(CacheNames.LOCALIZATION_LOOKUPS).get("%s::es::en".formatted(CACHE_TEST_KEY))).isNull();
        assertThat(localizationMessageService.getMessageWithFallback(CACHE_TEST_KEY, "es", "en"))
                .isEqualTo("Cache ES Updated");
    }

    @Test
    void categoryListUsesCacheAndEvictsOnCreate() {
        double missBefore = counterValue(CACHE_EVENTS, "cache", CacheNames.CATEGORIES, "event", "miss");
        double hitBefore = counterValue(CACHE_EVENTS, "cache", CacheNames.CATEGORIES, "event", "hit");
        double putBefore = counterValue(CACHE_EVENTS, "cache", CacheNames.CATEGORIES, "event", "put");

        List<Category> first = categoryService.findAll();
        List<Category> second = categoryService.findAll();

        assertThat(first).extracting(Category::getName)
                .containsExactly("Best Practices", "Java", "Software Engineering");
        assertThat(second).extracting(Category::getName)
                .containsExactly("Best Practices", "Java", "Software Engineering");
        assertThat(cache(CacheNames.CATEGORIES).get("all")).isNotNull();
        assertThat(counterValue(CACHE_EVENTS, "cache", CacheNames.CATEGORIES, "event", "miss") - missBefore).isEqualTo(1.0d);
        assertThat(counterValue(CACHE_EVENTS, "cache", CacheNames.CATEGORIES, "event", "hit") - hitBefore).isEqualTo(1.0d);
        assertThat(counterValue(CACHE_EVENTS, "cache", CacheNames.CATEGORIES, "event", "put") - putBefore).isEqualTo(1.0d);

        categoryService.create(new CategoryCreateRequest("Architecture"));

        assertThat(cache(CacheNames.CATEGORIES).get("all")).isNull();
        assertThat(cache(CacheNames.CATEGORY_DIRECTORY).get("byNormalizedName")).isNull();
        assertThat(categoryService.findAll()).extracting(Category::getName)
                .containsExactly("Architecture", "Best Practices", "Java", "Software Engineering");
    }

    @Test
    void serviceOperationsPublishDomainMetricsAndGauges() {
        double bookListBefore = counterValue(BOOK_OPERATIONS, "operation", "list");
        double bookGetBefore = counterValue(BOOK_OPERATIONS, "operation", "get");
        double bookCreateBefore = counterValue(BOOK_OPERATIONS, "operation", "create");
        double categoryListBefore = counterValue(CATEGORY_OPERATIONS, "operation", "list");
        double localizationMessagesBefore = counterValue(LOCALIZATION_OPERATIONS, "operation", "getAllMessages");

        BookSearchRequest request = new BookSearchRequest();
        bookService.findAll(request, PageRequest.of(0, 20));
        bookService.findById(bookRepository.findAll().getFirst().getId());
        bookService.create(new BookCreateRequest(
                "Spring in Action",
                "Craig Walls",
                "9781617297571",
                2022,
                List.of("Java", "Best Practices")
        ));
        categoryService.findAll();
        localizationMessageService.getAllMessages("en");

        assertThat(counterValue(BOOK_OPERATIONS, "operation", "list") - bookListBefore).isEqualTo(1.0d);
        assertThat(counterValue(BOOK_OPERATIONS, "operation", "get") - bookGetBefore).isEqualTo(1.0d);
        assertThat(counterValue(BOOK_OPERATIONS, "operation", "create") - bookCreateBefore).isEqualTo(1.0d);
        assertThat(counterValue(CATEGORY_OPERATIONS, "operation", "list") - categoryListBefore).isEqualTo(1.0d);
        assertThat(counterValue(LOCALIZATION_OPERATIONS, "operation", "getAllMessages") - localizationMessagesBefore)
                .isEqualTo(1.0d);

        assertThat(gaugeValue(BOOK_TOTAL)).isEqualTo((double) bookRepository.count());
        assertThat(gaugeValue(CATEGORY_TOTAL)).isEqualTo((double) categoryRepository.count());
        assertThat(gaugeValue(LOCALIZATION_TOTAL)).isEqualTo((double) localizationMessageRepository.count());
    }

    private void clearCaches() {
        CacheTestSupport.clearCaches(cacheManager, List.of(
                CacheNames.CATEGORIES,
                CacheNames.CATEGORY_DIRECTORY,
                CacheNames.LOCALIZATION_LOOKUPS,
                CacheNames.LOCALIZATION_LISTS,
                CacheNames.LOCALIZATION_MESSAGE_MAPS
        ));
    }

    private Cache cache(String cacheName) {
        return CacheTestSupport.cache(cacheManager, cacheName);
    }

    private double counterValue(String meterName, String... tags) {
        io.micrometer.core.instrument.Counter counter = meterRegistry.find(meterName).tags(tags).counter();
        return counter == null ? 0.0d : counter.count();
    }

    private double gaugeValue(String meterName) {
        io.micrometer.core.instrument.Gauge gauge = meterRegistry.find(meterName).gauge();
        assertThat(gauge).isNotNull();
        return gauge.value();
    }
}
