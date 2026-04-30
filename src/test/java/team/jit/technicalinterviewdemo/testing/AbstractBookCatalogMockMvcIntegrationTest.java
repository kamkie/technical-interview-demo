package team.jit.technicalinterviewdemo.testing;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import team.jit.technicalinterviewdemo.business.book.Book;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;

public abstract class AbstractBookCatalogMockMvcIntegrationTest extends AbstractMockMvcIntegrationTest {

    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected CacheManager cacheManager;

    protected Book cleanCode;
    protected Book effectiveJava;

    @BeforeEach
    void seedBookCatalog() {
        BookCatalogTestData.BookCatalog catalog =
                BookCatalogTestData.seedDefaultCatalog(bookRepository, categoryRepository, cacheManager);
        cleanCode = catalog.cleanCode();
        effectiveJava = catalog.effectiveJava();
    }
}
