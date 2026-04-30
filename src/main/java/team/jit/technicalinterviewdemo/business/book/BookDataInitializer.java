package team.jit.technicalinterviewdemo.business.book;

import java.util.LinkedHashSet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;

@Slf4j
@Configuration
public class BookDataInitializer {

    @Bean
    @Order(20)
    CommandLineRunner seedBooks(BookRepository bookRepository, CategoryRepository categoryRepository) {
        return args -> {
            if (bookRepository.count() > 0) {
                return;
            }

            LinkedHashSet<Category> cleanCodeCategories = new LinkedHashSet<>(categoryRepository.findAllByNormalizedNames(
                    java.util.List.of("best practices", "software engineering")
            ));
            LinkedHashSet<Category> effectiveJavaCategories = new LinkedHashSet<>(categoryRepository.findAllByNormalizedNames(
                    java.util.List.of("best practices", "java")
            ));

            Book cleanCode = bookRepository.save(
                    new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, cleanCodeCategories)
            );
            log.info("Seeded book id={} isbn={} title={}", cleanCode.getId(), cleanCode.getIsbn(), cleanCode.getTitle());

            Book effectiveJava = bookRepository.save(
                    new Book("Effective Java", "Joshua Bloch", "9780134685991", 2018, effectiveJavaCategories)
            );
            log.info("Seeded book id={} isbn={} title={}", effectiveJava.getId(), effectiveJava.getIsbn(), effectiveJava.getTitle());
        };
    }
}
