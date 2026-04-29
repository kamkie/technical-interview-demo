package team.jit.technicalinterviewdemo.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookUpdateRequest(
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "author is required")
        String author,
        @NotNull(message = "version is required")
        Long version,
        @NotNull(message = "publicationYear is required")
        Integer publicationYear
) {
}
