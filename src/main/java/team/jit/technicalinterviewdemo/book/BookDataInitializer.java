package team.jit.technicalinterviewdemo.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BookDataInitializer {

    @Bean
    CommandLineRunner seedBooks(BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() > 0) {
                return;
            }

            Book cleanCode = bookRepository.save(new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008));
            log.info("Seeded book id={} isbn={} title={}", cleanCode.getId(), cleanCode.getIsbn(), cleanCode.getTitle());

            Book effectiveJava = bookRepository.save(new Book("Effective Java", "Joshua Bloch", "9780134685991", 2018));
            log.info("Seeded book id={} isbn={} title={}", effectiveJava.getId(), effectiveJava.getIsbn(), effectiveJava.getTitle());
        };
    }
}
