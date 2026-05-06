package team.jit.technicalinterviewdemo.business.book;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Payload for creating a new book.")
public record BookCreateRequest(
        @Schema(description = "Book title.", example = "Effective Java") @NotBlank(message = "title is required")
        String title,

        @Schema(description = "Primary author.", example = "Joshua Bloch") @NotBlank(message = "author is required")
        String author,

        @Schema(description = "Unique ISBN for the new book.", example = "9780134685991")
        @NotBlank(message = "isbn is required")
        String isbn,

        @Schema(description = "Publication year.", example = "2018") @NotNull(message = "publicationYear is required")
        Integer publicationYear,

        @ArraySchema(
                schema = @Schema(description = "Existing category name.", example = "Java"),
                arraySchema = @Schema(description = "Optional list of existing category names."))
        List<String> categories) {}
