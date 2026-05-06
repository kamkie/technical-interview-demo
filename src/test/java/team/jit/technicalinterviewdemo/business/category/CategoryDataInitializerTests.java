package team.jit.technicalinterviewdemo.business.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

@ExtendWith(MockitoExtension.class)
class CategoryDataInitializerTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void seedCategoriesSkipsWhenDemoBootstrapIsDisabled() throws Exception {
        CategoryDataInitializer initializer = new CategoryDataInitializer();

        CommandLineRunner runner = initializer.seedCategories(categoryRepository, bootstrapSettings(false));
        runner.run();

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void seedCategoriesWritesDefaultNamesWhenDemoBootstrapIsEnabled() throws Exception {
        CategoryDataInitializer initializer = new CategoryDataInitializer();
        when(categoryRepository.existsByNameIgnoreCase(org.mockito.ArgumentMatchers.anyString())).thenReturn(false);
        when(categoryRepository.save(org.mockito.ArgumentMatchers.any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0, Category.class));

        CommandLineRunner runner = initializer.seedCategories(categoryRepository, bootstrapSettings(true));
        runner.run();

        ArgumentCaptor<Category> savedCategories = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository, org.mockito.Mockito.times(3)).save(savedCategories.capture());
        assertThat(savedCategories.getAllValues()).extracting(Category::getName).containsExactlyElementsOf(List.of("Best Practices", "Java", "Software Engineering"));
    }

    private static BootstrapSettingsProperties bootstrapSettings(boolean demoDataEnabled) {
        BootstrapSettingsProperties properties = new BootstrapSettingsProperties();
        properties.getSeed().setDemoData(demoDataEnabled);
        return properties;
    }
}
