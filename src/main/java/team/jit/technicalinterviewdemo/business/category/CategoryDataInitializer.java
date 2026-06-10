package team.jit.technicalinterviewdemo.business.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class CategoryDataInitializer {

    @Bean
    @Order(10)
    CommandLineRunner seedCategories(
            CategoryRepository categoryRepository, BootstrapSettingsProperties bootstrapSettingsProperties) {
        return args -> {
            if (!bootstrapSettingsProperties.getSeed().isDemoData()) {
                log.info("Skipping demo category bootstrap because app.bootstrap.seed.demo-data is disabled.");
                return;
            }
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
        List<String> categoryNames = new ArrayList<>(coreCategoryNames());
        categoryNames.addAll(additionalCategoryNames());
        return List.copyOf(categoryNames);
    }

    public static List<String> additionalCategoryNames() {
        return List.of(
                "Algorithms",
                "Data Structures",
                "Frontend",
                "Backend",
                "Messaging",
                "Resilience",
                "Domain Modeling",
                "Cloud Security",
                "Infrastructure",
                "CI/CD",
                "Monitoring",
                "Search",
                "Machine Learning",
                "Data Engineering",
                "Leadership",
                "Product Engineering",
                "Mobile",
                "UX Engineering",
                "Release Management",
                "Incident Response");
    }

    private static List<String> coreCategoryNames() {
        return List.of(
                "Best Practices",
                "Java",
                "Software Engineering",
                "Architecture",
                "Spring",
                "Testing",
                "Security",
                "Databases",
                "Cloud Native",
                "DevOps",
                "Kotlin",
                "Observability",
                "Distributed Systems",
                "APIs",
                "Performance");
    }
}
