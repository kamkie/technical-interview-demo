package team.jit.technicalinterviewdemo.category;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
public class CategoryDataInitializer {

    @Bean
    @Order(10)
    CommandLineRunner seedCategories(CategoryRepository categoryRepository) {
        return args -> {
            for (String categoryName : defaultCategoryNames()) {
                if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
                    continue;
                }

                Category savedCategory = categoryRepository.save(new Category(categoryName));
                log.info("Seeded category id={} name={}", savedCategory.getId(), savedCategory.getName());
            }
        };
    }

    static List<String> defaultCategoryNames() {
        return List.of("Best Practices", "Java", "Software Engineering");
    }
}
