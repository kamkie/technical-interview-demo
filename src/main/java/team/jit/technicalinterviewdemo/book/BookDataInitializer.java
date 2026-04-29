package team.jit.technicalinterviewdemo.book;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookDataInitializer {

    @Bean
    CommandLineRunner seedBooks(BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() > 0) {
                return;
            }

            bookRepository.save(new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008));
            bookRepository.save(new Book("Effective Java", "Joshua Bloch", "9780134685991", 2018));
        };
    }
}
