package team.jit.technicalinterviewdemo.business.book;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import team.jit.technicalinterviewdemo.business.category.CategoryResponse;

import java.util.Comparator;
import java.util.List;

@Schema(name = "Book", description = "Book resource returned by the API.")
public record BookResponse(
        @Schema(description = "Database identifier.", example = "1")
        Long id,

        @Schema(description = "Optimistic-lock version used for updates.", example = "0")
        Long version,

        @Schema(description = "Book title.", example = "Effective Java")
        String title,

        @Schema(description = "Primary author.", example = "Joshua Bloch")
        String author,

        @Schema(description = "Unique ISBN assigned at creation time.", example = "9780134685991")
        String isbn,

        @Schema(description = "Publication year.", example = "2018")
        Integer publicationYear,

        @ArraySchema(
                schema = @Schema(implementation = CategoryResponse.class),
                arraySchema = @Schema(description = "Assigned categories ordered by name."))
        List<CategoryResponse> categories) {

    public static BookResponse from(Book book) {
        List<CategoryResponse> categories = book.getCategories().stream()
                .map(CategoryResponse::from)
                .sorted(Comparator.comparing(CategoryResponse::name))
                .toList();
        return new BookResponse(
                book.getId(),
                book.getVersion(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationYear(),
                categories);
    }
}
