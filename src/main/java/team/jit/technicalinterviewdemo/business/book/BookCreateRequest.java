package team.jit.technicalinterviewdemo.business.book;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Payload for creating a new book.")
public record BookCreateRequest(
        @Schema(description = "Book title.", example = "Effective Java", maxLength = 255)
        @NotBlank(message = "title is required")
        @Size(max = 255, message = "title must be at most 255 characters")
        String title,

        @Schema(description = "Primary author.", example = "Joshua Bloch", maxLength = 255)
        @NotBlank(message = "author is required")
        @Size(max = 255, message = "author must be at most 255 characters")
        String author,

        @Schema(description = "Unique ISBN for the new book.", example = "9780134685991", maxLength = 32)
        @NotBlank(message = "isbn is required")
        @Size(max = 32, message = "isbn must be at most 32 characters")
        String isbn,

        @Schema(description = "Publication year.", example = "2018", minimum = "0", maximum = "3000")
        @NotNull(message = "publicationYear is required")
        @Min(value = 0, message = "publicationYear must be between 0 and 3000")
        @Max(value = 3000, message = "publicationYear must be between 0 and 3000")
        Integer publicationYear,

        @ArraySchema(
                schema = @Schema(description = "Existing category name.", example = "Java"),
                arraySchema = @Schema(description = "Optional list of existing category names."))
        List<String> categories) {}
