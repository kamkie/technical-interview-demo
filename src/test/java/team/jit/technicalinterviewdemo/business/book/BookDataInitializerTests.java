package team.jit.technicalinterviewdemo.business.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookDataInitializerTests {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void seedBooksSkipsWhenDemoBootstrapIsDisabled() throws Exception {
        BookDataInitializer initializer = new BookDataInitializer();

        CommandLineRunner runner = initializer.seedBooks(bookRepository, categoryRepository, bootstrapSettings(false));
        runner.run();

        verifyNoInteractions(bookRepository, categoryRepository);
    }

    @Test
    void seedBooksWritesDefaultBooksWhenDemoBootstrapIsEnabled() throws Exception {
        BookDataInitializer initializer = new BookDataInitializer();
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(categoryRepository.findAllByNormalizedNames(anyCollection()))
                .thenAnswer(invocation -> invocation.<Collection<String>>getArgument(0).stream()
                        .map(Category::new)
                        .toList());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0, Book.class));

        CommandLineRunner runner = initializer.seedBooks(bookRepository, categoryRepository, bootstrapSettings(true));
        runner.run();

        verify(categoryRepository).findAllByNormalizedNames(anyCollection());
        verify(bookRepository, times(36)).existsByIsbn(anyString());
        ArgumentCaptor<Book> savedBooks = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(36)).save(savedBooks.capture());
        assertThat(savedBooks.getAllValues())
                .extracting(Book::getTitle)
                .containsSequence("Clean Code", "Effective Java", "Clean Architecture")
                .contains(
                        "Designing Data-Intensive Applications",
                        "Observability Engineering",
                        "Monolith to Microservices");
    }

    @Test
    void seedBooksOnlyWritesMissingDefaultBooks() throws Exception {
        BookDataInitializer initializer = new BookDataInitializer();
        when(bookRepository.existsByIsbn("9780132350884")).thenReturn(true);
        when(categoryRepository.findAllByNormalizedNames(anyCollection()))
                .thenAnswer(invocation -> invocation.<Collection<String>>getArgument(0).stream()
                        .map(Category::new)
                        .toList());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0, Book.class));

        CommandLineRunner runner = initializer.seedBooks(bookRepository, categoryRepository, bootstrapSettings(true));
        runner.run();

        ArgumentCaptor<Book> savedBooks = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(35)).save(savedBooks.capture());
        assertThat(savedBooks.getAllValues())
                .extracting(Book::getTitle)
                .doesNotContain("Clean Code")
                .contains("Effective Java", "Monolith to Microservices");
    }

    private static BootstrapSettingsProperties bootstrapSettings(boolean demoDataEnabled) {
        BootstrapSettingsProperties properties = new BootstrapSettingsProperties();
        properties.getSeed().setDemoData(demoDataEnabled);
        return properties;
    }
}
