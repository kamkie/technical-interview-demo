package team.jit.technicalinterviewdemo.business.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryDataInitializer;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class BookDataInitializer {

    private static final int ADDITIONAL_DEMO_BOOK_COUNT = 1_000;

    @Bean
    @Order(20)
    CommandLineRunner seedBooks(
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            BootstrapSettingsProperties bootstrapSettingsProperties) {
        return args -> {
            if (!bootstrapSettingsProperties.getSeed().isDemoData()) {
                log.info("Skipping demo book bootstrap because app.bootstrap.seed.demo-data is disabled.");
                return;
            }
            List<SeedBook> seedBooks = defaultBooks().stream()
                    .filter(seedBook -> !bookRepository.existsByIsbn(seedBook.isbn()))
                    .toList();
            if (seedBooks.isEmpty()) {
                return;
            }
            Map<String, Category> categoriesByNormalizedName =
                    categoryRepository.findAllByNormalizedNames(defaultCategoryLookupNames(seedBooks)).stream()
                            .collect(Collectors.toMap(
                                    category -> normalizeLookupName(category.getName()),
                                    category -> category,
                                    (first, second) -> first,
                                    LinkedHashMap::new));

            for (SeedBook seedBook : seedBooks) {
                LinkedHashSet<Category> categories = seedBook.categoryNames().stream()
                        .map(BookDataInitializer::normalizeLookupName)
                        .map(categoriesByNormalizedName::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                Book book = bookRepository.save(new Book(
                        seedBook.title(), seedBook.author(), seedBook.isbn(), seedBook.publicationYear(), categories));
                log.info("Seeded book id={} isbn={} title={}", book.getId(), book.getIsbn(), book.getTitle());
            }
        };
    }

    static List<SeedBook> defaultBooks() {
        List<SeedBook> books = new ArrayList<>(curatedBooks());
        for (int index = 1; index <= ADDITIONAL_DEMO_BOOK_COUNT; index++) {
            books.add(generatedBook(index));
        }
        return List.copyOf(books);
    }

    private static List<SeedBook> curatedBooks() {
        return List.of(
                new SeedBook(
                        "Clean Code",
                        "Robert C. Martin",
                        "9780132350884",
                        2008,
                        List.of("Best Practices", "Software Engineering")),
                new SeedBook(
                        "Effective Java", "Joshua Bloch", "9780134685991", 2018, List.of("Best Practices", "Java")),
                new SeedBook(
                        "Clean Architecture",
                        "Robert C. Martin",
                        "9781000000001",
                        2017,
                        List.of("Architecture", "Software Engineering")),
                new SeedBook(
                        "Refactoring",
                        "Martin Fowler",
                        "9781000000002",
                        2018,
                        List.of("Best Practices", "Software Engineering")),
                new SeedBook(
                        "Domain-Driven Design",
                        "Eric Evans",
                        "9781000000003",
                        2003,
                        List.of("Architecture", "Software Engineering")),
                new SeedBook(
                        "Designing Data-Intensive Applications",
                        "Martin Kleppmann",
                        "9781000000004",
                        2017,
                        List.of("Databases", "Distributed Systems")),
                new SeedBook("Release It!", "Michael Nygard", "9781000000005", 2018, List.of("Architecture", "DevOps")),
                new SeedBook(
                        "Building Microservices",
                        "Sam Newman",
                        "9781000000006",
                        2021,
                        List.of("Architecture", "Distributed Systems")),
                new SeedBook("Spring in Action", "Craig Walls", "9781000000007", 2022, List.of("Java", "Spring")),
                new SeedBook(
                        "Test Driven Development",
                        "Kent Beck",
                        "9781000000008",
                        2002,
                        List.of("Testing", "Best Practices")),
                new SeedBook(
                        "Growing Object-Oriented Software",
                        "Steve Freeman and Nat Pryce",
                        "9781000000009",
                        2009,
                        List.of("Testing", "Software Engineering")),
                new SeedBook(
                        "Working Effectively with Legacy Code",
                        "Michael Feathers",
                        "9781000000010",
                        2004,
                        List.of("Best Practices", "Software Engineering")),
                new SeedBook(
                        "Continuous Delivery",
                        "Jez Humble and David Farley",
                        "9781000000011",
                        2010,
                        List.of("DevOps", "Cloud Native")),
                new SeedBook(
                        "The Phoenix Project",
                        "Gene Kim, Kevin Behr, and George Spafford",
                        "9781000000012",
                        2013,
                        List.of("DevOps")),
                new SeedBook(
                        "Kubernetes in Action",
                        "Marko Luksa",
                        "9781000000013",
                        2017,
                        List.of("Cloud Native", "DevOps")),
                new SeedBook(
                        "Cloud Native Patterns",
                        "Cornelia Davis",
                        "9781000000014",
                        2019,
                        List.of("Cloud Native", "Architecture")),
                new SeedBook(
                        "Secure by Design",
                        "Dan Bergh Johnsson, Daniel Deogun, and Daniel Sawano",
                        "9781000000015",
                        2019,
                        List.of("Security", "Software Engineering")),
                new SeedBook("Web Application Security", "Andrew Hoffman", "9781000000016", 2020, List.of("Security")),
                new SeedBook(
                        "API Design Patterns", "JJ Geewax", "9781000000017", 2021, List.of("APIs", "Architecture")),
                new SeedBook(
                        "RESTful Web APIs",
                        "Leonard Richardson, Mike Amundsen, and Sam Ruby",
                        "9781000000018",
                        2013,
                        List.of("APIs")),
                new SeedBook(
                        "Observability Engineering",
                        "Charity Majors, Liz Fong-Jones, and George Miranda",
                        "9781000000019",
                        2022,
                        List.of("Observability", "DevOps")),
                new SeedBook(
                        "Site Reliability Engineering",
                        "Betsy Beyer, Chris Jones, Jennifer Petoff, and Niall Murphy",
                        "9781000000020",
                        2016,
                        List.of("Observability", "DevOps")),
                new SeedBook(
                        "High Performance Java Persistence",
                        "Vlad Mihalcea",
                        "9781000000021",
                        2016,
                        List.of("Java", "Databases", "Performance")),
                new SeedBook(
                        "Java Concurrency in Practice",
                        "Brian Goetz",
                        "9781000000022",
                        2006,
                        List.of("Java", "Performance")),
                new SeedBook(
                        "Kotlin in Action",
                        "Dmitry Jemerov and Svetlana Isakova",
                        "9781000000023",
                        2017,
                        List.of("Kotlin")),
                new SeedBook(
                        "Head First Design Patterns",
                        "Eric Freeman, Elisabeth Robson, Bert Bates, and Kathy Sierra",
                        "9781000000024",
                        2020,
                        List.of("Architecture", "Best Practices")),
                new SeedBook(
                        "The Pragmatic Programmer",
                        "David Thomas and Andrew Hunt",
                        "9781000000025",
                        2019,
                        List.of("Best Practices", "Software Engineering")),
                new SeedBook(
                        "Code Complete",
                        "Steve McConnell",
                        "9781000000026",
                        2004,
                        List.of("Best Practices", "Software Engineering")),
                new SeedBook(
                        "Fundamentals of Software Architecture",
                        "Mark Richards and Neal Ford",
                        "9781000000027",
                        2020,
                        List.of("Architecture")),
                new SeedBook("Database Internals", "Alex Petrov", "9781000000028", 2019, List.of("Databases")),
                new SeedBook(
                        "Designing Distributed Systems",
                        "Brendan Burns",
                        "9781000000029",
                        2018,
                        List.of("Distributed Systems", "Cloud Native")),
                new SeedBook(
                        "Production-Ready Microservices",
                        "Susan Fowler",
                        "9781000000030",
                        2016,
                        List.of("Distributed Systems", "Observability")),
                new SeedBook(
                        "Effective Kotlin",
                        "Marcin Moskala",
                        "9781000000031",
                        2019,
                        List.of("Kotlin", "Best Practices")),
                new SeedBook(
                        "Systems Performance",
                        "Brendan Gregg",
                        "9781000000032",
                        2020,
                        List.of("Performance", "Observability")),
                new SeedBook(
                        "Software Engineering at Google",
                        "Titus Winters, Tom Manshreck, and Hyrum Wright",
                        "9781000000033",
                        2020,
                        List.of("Software Engineering", "Best Practices")),
                new SeedBook(
                        "Monolith to Microservices",
                        "Sam Newman",
                        "9781000000034",
                        2019,
                        List.of("Architecture", "Distributed Systems")));
    }

    private static SeedBook generatedBook(int index) {
        List<String> additionalCategoryNames = CategoryDataInitializer.additionalCategoryNames();
        String categoryName = additionalCategoryNames.get((index - 1) % additionalCategoryNames.size());
        return new SeedBook(
                "Demo Load Book %04d".formatted(index),
                "Demo Author %03d".formatted(((index - 1) % 100) + 1),
                "9782000%06d".formatted(index),
                2000 + ((index - 1) % 25),
                List.of(categoryName));
    }

    private static Set<String> defaultCategoryLookupNames(Collection<SeedBook> seedBooks) {
        return seedBooks.stream()
                .flatMap(seedBook -> seedBook.categoryNames().stream())
                .map(BookDataInitializer::normalizeLookupName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static String normalizeLookupName(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    record SeedBook(String title, String author, String isbn, int publicationYear, List<String> categoryNames) {}
}
