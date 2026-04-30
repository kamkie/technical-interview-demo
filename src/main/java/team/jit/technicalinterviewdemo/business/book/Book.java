package team.jit.technicalinterviewdemo.business.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.jit.technicalinterviewdemo.business.category.Category;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "books")
@Schema(description = "Book resource returned by the API.")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Schema(description = "Database identifier.", example = "1")
    private Long id;

    @Version
    @Setter(AccessLevel.NONE)
    @Schema(description = "Optimistic-lock version used for updates.", example = "0")
    private Long version;

    @Column(nullable = false)
    @Schema(description = "Book title.", example = "Effective Java")
    private String title;

    @Column(nullable = false)
    @Schema(description = "Primary author.", example = "Joshua Bloch")
    private String author;

    @Column(nullable = false, unique = true)
    @Schema(description = "Unique ISBN assigned at creation time.", example = "9780134685991")
    private String isbn;

    @Column(nullable = false)
    @Schema(description = "Publication year.", example = "2018")
    private Integer publicationYear;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @OrderBy("name ASC")
    @Schema(description = "Assigned categories ordered by name.")
    private Set<Category> categories = new LinkedHashSet<>();

    public Book(String title, String author, String isbn, Integer publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
    }

    public Book(String title, String author, String isbn, Integer publicationYear, Set<Category> categories) {
        this(title, author, isbn, publicationYear);
        this.categories = new LinkedHashSet<>(categories);
    }
}
