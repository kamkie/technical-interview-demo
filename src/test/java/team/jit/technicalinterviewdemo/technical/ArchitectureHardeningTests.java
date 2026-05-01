package team.jit.technicalinterviewdemo.technical;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PersistenceUnitUtil;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.ResolvableType;
import team.jit.technicalinterviewdemo.TechnicalInterviewDemoApplication;
import team.jit.technicalinterviewdemo.business.book.Book;
import team.jit.technicalinterviewdemo.business.book.BookController;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.book.BookResponse;
import team.jit.technicalinterviewdemo.business.book.BookSearchRequest;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryController;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.business.category.CategoryResponse;
import team.jit.technicalinterviewdemo.technical.cache.CachingConfiguration;
import team.jit.technicalinterviewdemo.testdata.BookCatalogTestData;
import team.jit.technicalinterviewdemo.testing.IntegrationSpringBootTest;

@IntegrationSpringBootTest
class ArchitectureHardeningTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private CacheManager cacheManager;

    private Book effectiveJava;

    @BeforeEach
    void setUp() {
        effectiveJava = BookCatalogTestData
                .seedDefaultCatalog(bookRepository, categoryRepository, cacheManager)
                .effectiveJava();
    }

    @Test
    void bookCategoriesMappingIsLazy() throws NoSuchFieldException {
        Field categoriesField = Book.class.getDeclaredField("categories");
        ManyToMany manyToMany = categoriesField.getAnnotation(ManyToMany.class);

        assertThat(manyToMany).isNotNull();
        assertThat(manyToMany.fetch()).isEqualTo(jakarta.persistence.FetchType.LAZY);
    }

    @Test
    void applicationEntryPointDoesNotContainEnableCachingAnnotation() {
        assertThat(TechnicalInterviewDemoApplication.class.isAnnotationPresent(EnableCaching.class)).isFalse();
        assertThat(CachingConfiguration.class.isAnnotationPresent(EnableCaching.class)).isTrue();
    }

    @Test
    void publicControllersDoNotExposeJpaEntitiesInResponseTypes() throws NoSuchMethodException {
        ResolvableType listBooksReturnType = ResolvableType.forMethodReturnType(
                BookController.class.getMethod("findAll", BookSearchRequest.class, org.springframework.data.domain.Pageable.class)
        );
        ResolvableType getBookReturnType = ResolvableType.forMethodReturnType(BookController.class.getMethod("findById", Long.class));
        ResolvableType createBookReturnType = ResolvableType.forMethodReturnType(
                BookController.class.getMethod("create", team.jit.technicalinterviewdemo.business.book.BookCreateRequest.class)
        );
        ResolvableType updateBookReturnType = ResolvableType.forMethodReturnType(
                BookController.class.getMethod("update", Long.class, team.jit.technicalinterviewdemo.business.book.BookUpdateRequest.class)
        );
        ResolvableType listCategoriesReturnType = ResolvableType.forMethodReturnType(CategoryController.class.getMethod("findAll"));
        ResolvableType createCategoryReturnType = ResolvableType.forMethodReturnType(
                CategoryController.class.getMethod("create", team.jit.technicalinterviewdemo.business.category.CategoryCreateRequest.class)
        );

        assertThat(listBooksReturnType.getGeneric(0, 0).resolve()).isEqualTo(BookResponse.class);
        assertThat(getBookReturnType.getGeneric(0).resolve()).isEqualTo(BookResponse.class);
        assertThat(createBookReturnType.getGeneric(0).resolve()).isEqualTo(BookResponse.class);
        assertThat(updateBookReturnType.getGeneric(0).resolve()).isEqualTo(BookResponse.class);
        assertThat(listCategoriesReturnType.getGeneric(0, 0).resolve()).isEqualTo(CategoryResponse.class);
        assertThat(createCategoryReturnType.getGeneric(0).resolve()).isEqualTo(CategoryResponse.class);
    }

    @Test
    void cacheManagerUsesCaffeineCaches() {
        assertThat(cacheManager).isInstanceOf(CaffeineCacheManager.class);
        org.springframework.cache.Cache categoriesCache = cacheManager.getCache(CacheNames.CATEGORIES);

        assertThat(categoriesCache).isNotNull();
        assertThat(categoriesCache.getNativeCache()).isInstanceOf(Cache.class);
    }

    @Test
    void repositoryFindByIdLoadsCategoriesViaEntityGraph() {
        PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
        Book book = bookRepository.findById(effectiveJava.getId()).orElseThrow();

        assertThat(persistenceUnitUtil.isLoaded(book, "categories")).isTrue();
        assertThat(book.getCategories()).extracting(Category::getName)
                .containsExactly("Best Practices", "Java");
    }

    @Test
    void repositorySearchLoadsCategoriesViaEntityGraph() {
        PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
        BookSearchRequest request = new BookSearchRequest();
        request.setCategory(List.of("java"));

        Page<Book> books = bookRepository.findAll(
                team.jit.technicalinterviewdemo.business.book.BookSearchSpecifications.fromSearchRequest(request),
                PageRequest.of(0, 20)
        );

        assertThat(books.getContent()).hasSize(1);
        Book book = books.getContent().getFirst();
        assertThat(book.getTitle()).isEqualTo("Effective Java");
        assertThat(persistenceUnitUtil.isLoaded(book, "categories")).isTrue();
        assertThat(book.getCategories()).extracting(Category::getName)
                .containsExactly("Best Practices", "Java");
    }
}

