package team.jit.technicalinterviewdemo.testing;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.cache.CacheManager;
import team.jit.technicalinterviewdemo.business.book.Book;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;

public final class BookCatalogTestData {

    private BookCatalogTestData() {
    }

    public static BookCatalog seedDefaultCatalog(
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            CacheManager cacheManager
    ) {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();

        Category bestPractices = categoryRepository.saveAndFlush(new Category("Best Practices"));
        Category javaCategory = categoryRepository.saveAndFlush(new Category("Java"));
        Category softwareEngineering = categoryRepository.saveAndFlush(new Category("Software Engineering"));
        CacheTestSupport.clearCaches(cacheManager, CacheNames.CATEGORIES, CacheNames.CATEGORY_DIRECTORY);

        Book cleanCode = bookRepository.saveAndFlush(new Book(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                2008,
                new LinkedHashSet<>(List.of(bestPractices, softwareEngineering))
        ));
        Book effectiveJava = bookRepository.saveAndFlush(new Book(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                2018,
                new LinkedHashSet<>(List.of(bestPractices, javaCategory))
        ));

        return new BookCatalog(cleanCode, effectiveJava, bestPractices, javaCategory, softwareEngineering);
    }

    public static List<Category> seedDefaultCategories(
            CategoryRepository categoryRepository,
            BookRepository bookRepository,
            CacheManager cacheManager
    ) {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();

        List<Category> categories = List.of(
                categoryRepository.saveAndFlush(new Category("Best Practices")),
                categoryRepository.saveAndFlush(new Category("Java"))
        );
        CacheTestSupport.clearCaches(cacheManager, CacheNames.CATEGORIES, CacheNames.CATEGORY_DIRECTORY);
        return categories;
    }

    public record BookCatalog(
            Book cleanCode,
            Book effectiveJava,
            Category bestPractices,
            Category javaCategory,
            Category softwareEngineering
    ) {
    }
}
