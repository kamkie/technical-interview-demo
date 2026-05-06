package team.jit.technicalinterviewdemo.business.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
        when(bookRepository.count()).thenReturn(0L);
        when(categoryRepository.findAllByNormalizedNames(anyList()))
                .thenReturn(List.of(new Category("Best Practices")));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0, Book.class));

        CommandLineRunner runner = initializer.seedBooks(bookRepository, categoryRepository, bootstrapSettings(true));
        runner.run();

        verify(categoryRepository, times(2)).findAllByNormalizedNames(anyList());
        verify(bookRepository, times(2)).save(any(Book.class));
    }

    private static BootstrapSettingsProperties bootstrapSettings(boolean demoDataEnabled) {
        BootstrapSettingsProperties properties = new BootstrapSettingsProperties();
        properties.getSeed().setDemoData(demoDataEnabled);
        return properties;
    }
}
