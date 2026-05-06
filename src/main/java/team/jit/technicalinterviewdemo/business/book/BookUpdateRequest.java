package team.jit.technicalinterviewdemo.business.book;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Payload for updating an existing book. ISBN remains immutable.")
public record BookUpdateRequest(
        @Schema(description = "Book title.", example = "Effective Java, Third Edition")
        @NotBlank(message = "title is required")
        String title,

        @Schema(description = "Primary author.", example = "Joshua Bloch") @NotBlank(message = "author is required")
        String author,

        @Schema(description = "Current optimistic-lock version.", example = "0")
        @NotNull(message = "version is required")
        Long version,

        @Schema(description = "Publication year.", example = "2018") @NotNull(message = "publicationYear is required")
        Integer publicationYear,

        @ArraySchema(
                schema = @Schema(description = "Existing category name.", example = "Java"),
                arraySchema = @Schema(description = "Optional replacement list of existing category names."))
        List<String> categories) {}
